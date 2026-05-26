import type { Usuario } from "./api";
import {
  getAvailableOrdersForProvider,
  getClientOrders,
  getProviderProposals,
} from "./demo-orders";
import { getUnreadMessagesCount } from "./demo-messages";

export type DemoNotificationColor = "blue" | "orange" | "yellow" | "green" | "red";

export interface DemoNotification {
  id: string;
  type: "offer" | "message" | "proposal" | "status" | "review";
  title: string;
  message: string;
  time: string;
  read: boolean;
  color: DemoNotificationColor;
  link?: string;
}

const READ_IDS_CACHE = new Map<number, Set<string>>();

async function fetchReadIds(userId: number): Promise<Set<string>> {
  if (READ_IDS_CACHE.has(userId)) {
    return READ_IDS_CACHE.get(userId)!;
  }

  try {
    const res = await fetch(`/api/notificacao/usuario/${userId}/lidas-externas`);
    if (!res.ok) return new Set();

    const ids: string[] = await res.json();
    const set = new Set(ids);
    READ_IDS_CACHE.set(userId, set);
    return set;
  } catch {
    return new Set();
  }
}

function invalidateCache(userId: number): void {
  READ_IDS_CACHE.delete(userId);
}

function isRead(readSet: Set<string>, id: string): boolean {
  return readSet.has(id);
}

function getBaseOffers(
  userId: number,
  isPrestadorArea: boolean,
  readSet: Set<string>,
): DemoNotification[] {
  const targetLink = isPrestadorArea
    ? "/prestador/servicos-disponiveis"
    : "/solicitar-servico";

  const raw: Omit<DemoNotification, "read">[] = [
    {
      id: `offer-weekly-${userId}`,
      type: "offer",
      title: "Oferta da semana",
      message:
        "Ganhe destaque extra no perfil ao manter respostas rapidas nas proximas 24 horas.",
      time: "Hoje",
      color: "orange",
      link: targetLink,
    },
    {
      id: `offer-referral-${userId}`,
      type: "offer",
      title: "Convite especial",
      message:
        "Convide um amigo para o FazTudoJA e desbloqueie beneficios no proximo servico.",
      time: "Hoje",
      color: "blue",
      link: "/",
    },
  ];

  return raw.map((item) => ({ ...item, read: isRead(readSet, item.id) }));
}

async function getSyncNotifications(
  user: Usuario,
  isPrestadorArea: boolean,
  readSet: Set<string>,
): Promise<DemoNotification[]> {
  if (isPrestadorArea) {
    const proposals = await getProviderProposals(user.id);
    const accepted = proposals.filter((proposal) => proposal.status === "accepted");

    const availableOrders = await getAvailableOrdersForProvider(user.id);
    const opportunities = availableOrders.length;

    const acceptedItems = accepted.map((proposal) => {
      const id = `provider-accepted-${proposal.id}`;
      return {
        id,
        type: "proposal" as const,
        title: "Proposta aceita",
        message: `Sua proposta para ${proposal.serviceTitle} foi aceita por ${proposal.clientName}.`,
        time: proposal.sentDate || "Recente",
        read: isRead(readSet, id),
        color: "green" as const,
        link: "/prestador/minhas-propostas",
      };
    });

    const opportunityItem: DemoNotification[] =
      opportunities > 0
        ? [
            {
              id: `provider-opportunities-${user.id}`,
              type: "status",
              title: "Novas oportunidades disponiveis",
              message: `Voce tem ${opportunities} servico(s) aguardando orcamento no momento.`,
              time: "Agora",
              read: isRead(readSet, `provider-opportunities-${user.id}`),
              color: "yellow",
              link: "/prestador/servicos-disponiveis",
            },
          ]
        : [];

    return [...acceptedItems, ...opportunityItem];
  }

  const orders = await getClientOrders(user.id);
  const withProposals = orders.filter((order) => order.proposals > 0);
  const completed = orders.filter(
    (order) =>
      order.status === "completed" || order.status === "work_completed_confirmed",
  );

  const proposalItems = withProposals.map((order) => {
    const id = `client-proposals-${order.id}`;
    return {
      id,
      type: "proposal" as const,
      title: "Orcamentos recebidos",
      message: `Seu pedido ${order.title} recebeu ${order.proposals} orcamento(s).`,
      time: order.date || "Recente",
      read: isRead(readSet, id),
      color: "orange" as const,
      link: `/dashboard/orcamentos?pedido=${order.id}`,
    };
  });

  const completedItems = completed.map((order) => {
    const id = `client-completed-${order.id}`;
    return {
      id,
      type: "review" as const,
      title: "Servico concluido",
      message: `O pedido ${order.title} foi concluido. Nao esqueca de avaliar o profissional.`,
      time: order.date || "Recente",
      read: isRead(readSet, id),
      color: "green" as const,
      link: `/dashboard/pedido/${order.id}`,
    };
  });

  return [...proposalItems, ...completedItems];
}

export async function getUserNotifications(
  user: Usuario,
  isPrestadorArea: boolean,
): Promise<DemoNotification[]> {
  const readSet = await fetchReadIds(user.id);
  const notifications = await getSyncNotifications(user, isPrestadorArea, readSet);
  const offers = getBaseOffers(user.id, isPrestadorArea, readSet);

  try {
    const unreadMessages = await getUnreadMessagesCount(user.id);
    if (unreadMessages > 0) {
      const messageId = `messages-unread-${user.id}`;
      notifications.unshift({
        id: messageId,
        type: "message",
        title: "Novas mensagens",
        message: `Voce tem ${unreadMessages} mensagem(ns) nao lida(s).`,
        time: "Agora",
        read: isRead(readSet, messageId),
        color: "blue",
        link: isPrestadorArea ? "/prestador/mensagens" : "/dashboard/mensagens",
      });
    }
  } catch {
    // Keep notifications available even if message endpoint fails.
  }

  return [...notifications, ...offers];
}

export async function getUnreadNotificationsCount(
  user: Usuario,
  isPrestadorArea: boolean,
): Promise<number> {
  const notifications = await getUserNotifications(user, isPrestadorArea);
  return notifications.filter((item) => !item.read).length;
}

export async function markNotificationAsRead(
  userId: number,
  notificationId: string,
): Promise<void> {
  try {
    await fetch(`/api/notificacao/usuario/${userId}/lidas-externas`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ externalId: notificationId }),
    });
    invalidateCache(userId);
  } catch {
    // Best effort, not critical.
  }
}

export async function markAllNotificationsAsRead(
  user: Usuario,
  isPrestadorArea: boolean,
): Promise<void> {
  const notifications = await getUserNotifications(user, isPrestadorArea);
  const allIds = notifications.map((item) => item.id);

  try {
    await fetch(`/api/notificacao/usuario/${user.id}/lidas-externas`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(allIds),
    });
    invalidateCache(user.id);
  } catch {
    // Best effort, not critical.
  }
}
