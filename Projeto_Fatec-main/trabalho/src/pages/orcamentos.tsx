import { useEffect, useMemo, useState } from "react";
import { Link, Navigate, useNavigate, useSearchParams } from "react-router";
import { Award, CheckCircle, Clock, MapPin, MessageSquare, Star } from "lucide-react";

import { Badge } from "../components/ui/badge";
import { Button } from "../components/ui/button";
import { Card } from "../components/ui/card";
import { ImageWithFallback } from "../components/figma/ImageWithFallback";
import { getSessionUser } from "../services/auth";
import { ensureConversation } from "../services/demo-messages";
import {
  acceptDemoProposal,
  getClientOrders,
  getOrderProposals,
  type DemoOrder,
  type DemoProposal,
} from "../services/demo-orders";

const fallbackProfilePhoto =
  "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=640";

function parseBrazilianCurrency(value: string): number {
  const normalized = value.replace(/[^\d,]/g, "").replace(",", ".");
  const parsed = Number(normalized);
  return Number.isFinite(parsed) ? parsed : 0;
}

export function Orcamentos() {
  const [searchParams] = useSearchParams();
  const [feedback, setFeedback] = useState<{ type: "success" | "error"; message: string } | null>(
    null,
  );
  const [refreshKey, setRefreshKey] = useState(0);
  const [clientOrders, setClientOrders] = useState<DemoOrder[]>([]);
  const [proposals, setProposals] = useState<DemoProposal[]>([]);
  const [proposalsOrderId, setProposalsOrderId] = useState<number | null>(null);
  const [loadingOrders, setLoadingOrders] = useState(true);
  const [loadingProposals, setLoadingProposals] = useState(true);

  const navigate = useNavigate();
  const user = getSessionUser();

  useEffect(() => {
    let isMounted = true;

    if (!user?.id) {
      setLoadingOrders(false);
      return () => {
        isMounted = false;
      };
    }

    setLoadingOrders(true);

    void getClientOrders(user.id)
      .then((orders) => {
        if (!isMounted) {
          return;
        }

        setClientOrders(orders);
      })
      .catch(() => {
        if (!isMounted) {
          return;
        }

        setClientOrders([]);
        setFeedback({ type: "error", message: "Nao foi possivel carregar seus pedidos agora." });
      })
      .finally(() => {
        if (isMounted) {
          setLoadingOrders(false);
        }
      });

    return () => {
      isMounted = false;
    };
  }, [user?.id, refreshKey]);

  const selectedOrder = useMemo(() => {
    const pedidoParam = searchParams.get("pedido");
    const requestedOrderId = pedidoParam ? Number(pedidoParam) : Number.NaN;
    const availableOrders = clientOrders.filter(
      (order) => order.status !== "work_completed_confirmed",
    );

    const preferredOrder =
      availableOrders.find((order) => order.proposals > 0) ?? availableOrders[0] ?? null;

    if (!Number.isFinite(requestedOrderId) || requestedOrderId <= 0) {
      return preferredOrder;
    }

    return availableOrders.find((order) => order.id === requestedOrderId) ?? preferredOrder;
  }, [clientOrders, searchParams]);

  useEffect(() => {
    const pedidoParam = searchParams.get("pedido");
    const requestedOrderId = pedidoParam ? Number(pedidoParam) : Number.NaN;

    if (!Number.isFinite(requestedOrderId) || requestedOrderId <= 0) {
      return;
    }

    const requestedOrder = clientOrders.find((order) => order.id === requestedOrderId);
    if (requestedOrder?.status === "work_completed_confirmed") {
      setFeedback({
        type: "error",
        message: "Este pedido ja foi finalizado e saiu da area de orcamentos.",
      });
    }
  }, [clientOrders, searchParams]);

  useEffect(() => {
    if (!user?.id || loadingOrders) {
      return;
    }

    let isActive = true;

    if (!selectedOrder) {
      setProposals([]);
      setProposalsOrderId(null);
      setLoadingProposals(false);

      return () => {
        isActive = false;
      };
    }

    setLoadingProposals(true);

    void getOrderProposals(selectedOrder.id)
      .then((nextProposals) => {
        if (isActive) {
          setProposals(nextProposals);
          setProposalsOrderId(selectedOrder.id);
        }
      })
      .catch(() => {
        if (isActive) {
          setProposals([]);
          setProposalsOrderId(selectedOrder.id);
          setFeedback({
            type: "error",
            message: "Nao foi possivel carregar os orcamentos deste pedido.",
          });
        }
      })
      .finally(() => {
        if (isActive) {
          setLoadingProposals(false);
        }
      });

    return () => {
      isActive = false;
    };
  }, [loadingOrders, selectedOrder, user?.id]);

  if (!user?.id) {
    return <Navigate to="/acesso?redirect=/dashboard/orcamentos" replace />;
  }

  if (selectedOrder && selectedOrder.clientUserId !== user.id) {
    return <Navigate to="/dashboard/meus-pedidos" replace />;
  }

  const waitingSelectedOrderProposals =
    !!selectedOrder && (loadingProposals || proposalsOrderId !== selectedOrder.id);
  const isLoadingPage = loadingOrders || waitingSelectedOrderProposals;

  if (!isLoadingPage && !selectedOrder) {
    return (
      <div className="min-h-screen bg-gray-50">
        <div className="bg-orange-600 py-8 text-white">
          <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
            <h1 className="mb-2 text-3xl font-bold">Orcamentos Recebidos</h1>
            <p className="text-orange-100">Nenhum orcamento disponivel no momento</p>
          </div>
        </div>
        <div className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
          <Card className="p-12 text-center">
            <MessageSquare className="mx-auto mb-4 text-gray-400" size={56} />
            <h3 className="mb-2 text-xl font-semibold">Nenhum orcamento pendente</h3>
            <p className="mb-6 text-gray-600">
              Os pedidos finalizados saem desta area. Crie um novo pedido para receber mais
              orcamentos.
            </p>
            <div className="flex flex-col justify-center gap-3 sm:flex-row">
              <Link to="/solicitar-servico">
                <Button>Criar novo pedido</Button>
              </Link>
              <Link to="/dashboard/meus-pedidos">
                <Button variant="outline">Ver meus pedidos</Button>
              </Link>
            </div>
          </Card>
        </div>
      </div>
    );
  }

  const selectedOrderTitle = selectedOrder?.title ?? "seu pedido";

  const acceptedCount = proposals.filter((proposal) => proposal.status === "accepted").length;
  const averagePrice =
    proposals.length === 0
      ? "-"
      : `R$ ${(
          proposals.reduce((sum, proposal) => sum + parseBrazilianCurrency(proposal.myPrice), 0) /
          proposals.length
        ).toLocaleString("pt-BR", {
          minimumFractionDigits: 2,
          maximumFractionDigits: 2,
        })}`;

  const handleAcceptProposal = (proposalId: number) => {
    try {
      acceptDemoProposal(selectedOrder.id, proposalId);
      setFeedback({ type: "success", message: "Orcamento aceito com sucesso." });
      setRefreshKey((value) => value + 1);
    } catch (error) {
      setFeedback({
        type: "error",
        message: error instanceof Error ? error.message : "Nao foi possivel aceitar o orcamento.",
      });
    }
  };

  const handleOpenConversation = async (proposal: (typeof proposals)[number]) => {
    if (!user?.id) {
      setFeedback({
        type: "error",
        message: "Faca login novamente para iniciar a conversa.",
      });
      return;
    }

    try {
      const conversation = await ensureConversation({
        orderId: selectedOrder.id,
        serviceTitle: selectedOrder.title,
        clientUserId: user.id,
        providerUserId: proposal.providerUserId,
        clientName: selectedOrder.clientName,
        providerName: proposal.providerName,
        providerPhoto: proposal.providerPhoto,
      });

      navigate(`/dashboard/mensagens?conversa=${conversation.id}`);
    } catch (error) {
      setFeedback({
        type: "error",
        message: error instanceof Error ? error.message : "Nao foi possivel abrir a conversa.",
      });
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-orange-600 py-8 text-white">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <h1 className="mb-2 text-3xl font-bold">Orcamentos Recebidos</h1>
          <p className="text-orange-100">
            {isLoadingPage
              ? "Carregando dados do pedido e dos orcamentos..."
              : `Compare e escolha o melhor profissional para o pedido ${selectedOrderTitle}`}
          </p>
        </div>
      </div>

      <div className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
        {feedback && (
          <div
            className={`mb-6 rounded-lg border px-4 py-3 text-sm ${
              feedback.type === "success"
                ? "border-green-200 bg-green-50 text-green-900"
                : "border-red-200 bg-red-50 text-red-900"
            }`}
          >
            {feedback.message}
          </div>
        )}

        <div className="mb-6 rounded-r-lg border-l-4 border-blue-600 bg-blue-50 p-4">
          <div className="flex items-start gap-3">
            <div className="rounded-full bg-blue-600 p-2 text-white">
              <MessageSquare size={20} />
            </div>
            <div>
              <h3 className="mb-1 font-semibold text-blue-900">
                Dica: Compare os orcamentos com atencao
              </h3>
              <p className="text-sm text-blue-800">
                Analise preco, prazo, clareza da proposta e o perfil do prestador antes de
                escolher.
              </p>
            </div>
          </div>
        </div>

        <div className="mb-6 rounded-2xl border border-blue-200 bg-white p-5 shadow-sm">
          {isLoadingPage || !selectedOrder ? (
            <div className="animate-pulse">
              <div className="mb-2 h-4 w-40 rounded bg-gray-200" />
              <div className="mb-3 h-8 w-2/3 rounded bg-gray-200" />
              <div className="h-4 w-full rounded bg-gray-200" />
            </div>
          ) : (
            <div className="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
              <div>
                <p className="text-sm font-semibold uppercase tracking-wide text-blue-700">
                  Pedido selecionado
                </p>
                <h2 className="text-2xl font-bold text-gray-900">{selectedOrder.title}</h2>
                <p className="text-sm text-gray-600">{selectedOrder.description}</p>
              </div>
              <Link to={`/dashboard/pedido/${selectedOrder.id}`}>
                <Button variant="outline">Ver detalhes do pedido</Button>
              </Link>
            </div>
          )}
        </div>

        <div className="mb-8 grid gap-4 md:grid-cols-3">
          <Card className="border-2 border-blue-200 bg-blue-50 p-4">
            <div className="text-center">
              <div className="mb-1 text-3xl font-bold text-blue-600">
                {isLoadingPage ? "..." : proposals.length}
              </div>
              <div className="text-sm text-gray-700">Total de Orcamentos</div>
            </div>
          </Card>
          <Card className="border-2 border-green-200 bg-emerald-50 p-4">
            <div className="text-center">
              <div className="mb-1 text-3xl font-bold text-green-600">
                {isLoadingPage ? "..." : acceptedCount}
              </div>
              <div className="text-sm text-gray-700">Aceito</div>
            </div>
          </Card>
          <Card className="border-2 border-orange-200 bg-orange-50 p-4">
            <div className="text-center">
              <div className="mb-1 text-3xl font-bold text-orange-600">
                {isLoadingPage ? "..." : averagePrice}
              </div>
              <div className="text-sm text-gray-700">Media de Precos</div>
            </div>
          </Card>
        </div>

        {isLoadingPage ? (
          <div className="space-y-6">
            {[0, 1].map((item) => (
              <Card key={item} className="p-6" style={{ animationDelay: `${item * 120}ms` }}>
                <div className="animate-pulse">
                  <div className="mb-4 flex items-start gap-4">
                    <div className="h-16 w-16 shrink-0 rounded-full bg-gray-200" />
                    <div className="flex-1 space-y-2">
                      <div className="h-5 w-40 rounded bg-gray-200" />
                      <div className="h-4 w-56 rounded bg-gray-200" />
                      <div className="h-4 w-full rounded bg-gray-200" />
                      <div className="h-4 w-3/4 rounded bg-gray-200" />
                    </div>
                    <div className="w-48 shrink-0 space-y-3">
                      <div className="h-20 rounded-xl bg-gray-200" />
                      <div className="h-10 rounded bg-gray-200" />
                      <div className="h-10 rounded bg-gray-200" />
                    </div>
                  </div>
                </div>
              </Card>
            ))}
          </div>
        ) : proposals.length === 0 ? (
          <Card className="p-12 text-center">
            <MessageSquare className="mx-auto mb-4 text-gray-400" size={56} />
            <h3 className="mb-2 text-xl font-semibold">Nenhum orcamento recebido ainda</h3>
            <p className="text-gray-600">
              Assim que um prestador enviar uma proposta, ela aparecera aqui com foto, valor e
              descricao.
            </p>
          </Card>
        ) : (
          <div className="space-y-6">
            {proposals.map((proposal) => (
              <Card
                key={`${proposal.id}-${refreshKey}`}
                className={`p-6 transition-all hover:shadow-xl ${
                  proposal.status === "accepted"
                    ? "border-2 border-green-500 bg-green-50/30"
                    : "border-2 border-transparent hover:border-blue-400"
                }`}
              >
                {proposal.status === "accepted" && (
                  <div className="mb-4 flex items-center gap-2 rounded-lg bg-green-500 px-4 py-2 text-white">
                    <CheckCircle size={20} />
                    <span className="font-semibold">Orcamento Aceito</span>
                  </div>
                )}

                <div className="flex flex-col gap-6 lg:flex-row">
                  <div className="flex-1">
                    <div className="mb-4 flex items-start gap-4">
                      <div className="relative">
                        <div className="h-16 w-16 overflow-hidden rounded-full bg-gray-200">
                          <ImageWithFallback
                            src={proposal.providerPhoto || fallbackProfilePhoto}
                            alt={proposal.providerName}
                            className="h-full w-full object-cover"
                          />
                        </div>
                        <div className="absolute -bottom-1 -right-1 rounded-full bg-blue-600 p-1 text-white">
                          <CheckCircle size={16} />
                        </div>
                      </div>

                      <div className="flex-1">
                        <div className="mb-1 flex items-center gap-2">
                          <h3 className="text-xl font-bold">{proposal.providerName}</h3>
                          <Badge className="bg-blue-600">Prestador</Badge>
                        </div>

                        <div className="mb-2 flex items-center gap-4 text-sm text-gray-600">
                          <div className="flex items-center gap-1">
                            <Star className="fill-yellow-500 text-yellow-500" size={16} />
                            <span className="font-semibold">Novo</span>
                            <span>perfil na plataforma</span>
                          </div>
                          <div className="flex items-center gap-1">
                            <Award size={16} />
                            <span>Proposta detalhada enviada</span>
                          </div>
                        </div>

                        <p className="mb-3 text-gray-700">{proposal.description}</p>

                        <div className="flex flex-wrap gap-3 text-sm">
                          <div className="flex items-center gap-1 text-gray-600">
                            <Clock size={16} />
                            <span>{proposal.deliveryTime}</span>
                          </div>
                          <div className="flex items-center gap-1 text-gray-600">
                            <MapPin size={16} />
                            <span>{selectedOrder?.location ?? "-"}</span>
                          </div>
                          <div className="rounded-full bg-green-100 px-3 py-1 font-medium text-green-800">
                            Status: {proposal.status === "accepted"
                              ? "Selecionado"
                              : proposal.status === "rejected"
                                ? "Nao selecionado"
                                : "Aguardando decisao"}
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div className="flex flex-col gap-3 lg:w-64">
                    <div className="rounded-xl border-2 border-orange-300 bg-orange-50 p-6 text-center">
                      <div className="mb-1 text-sm text-gray-600">Valor Total</div>
                      <div className="text-3xl font-bold text-orange-600">{proposal.myPrice}</div>
                    </div>

                    {proposal.status === "pending" ? (
                      <>
                        <Button
                          onClick={() => handleAcceptProposal(proposal.id)}
                          className="w-full bg-emerald-600 hover:bg-emerald-700"
                        >
                          <CheckCircle className="mr-2" size={18} />
                          Aceitar Orcamento
                        </Button>
                        <Button
                          variant="outline"
                          className="w-full"
                          onClick={() => handleOpenConversation(proposal)}
                        >
                          <MessageSquare className="mr-2" size={18} />
                          Enviar Mensagem
                        </Button>
                      </>
                    ) : (
                      <Button
                        variant="outline"
                        className="w-full border-green-500 text-green-700"
                        disabled
                      >
                        <CheckCircle className="mr-2" size={18} />
                        {proposal.status === "accepted" ? "Selecionado" : "Encerrado"}
                      </Button>
                    )}

                    <Link to={`/perfil-publico/prestador/${proposal.providerUserId}`}>
                      <Button variant="ghost" className="w-full text-blue-600">
                        Ver Perfil Completo
                      </Button>
                    </Link>
                  </div>
                </div>

                <div className="mt-4 border-t border-gray-200 pt-4">
                  <p className="text-sm text-gray-600">
                    Orcamento para: <span className="font-semibold">{proposal.serviceTitle}</span>
                  </p>
                </div>
              </Card>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

