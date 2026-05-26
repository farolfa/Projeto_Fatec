import { useEffect, useState } from "react";
import { Link, Navigate } from "react-router";
import {
  AlertCircle,
  CheckCircle,
  ChevronRight,
  Clock,
  HelpCircle,
  MessageSquare,
} from "lucide-react";

import { Button } from "../components/ui/button";
import { Card } from "../components/ui/card";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "../components/ui/dialog";
import { Badge } from "../components/ui/badge";
import { getSessionUser } from "../services/auth";
import { closeTicket, getUserTickets, type SupportTicket } from "../services/demo-support";

export function MeusTickets() {
  const user = getSessionUser();
  const [selectedTicket, setSelectedTicket] = useState<SupportTicket | null>(null);
  const [tickets, setTickets] = useState<SupportTicket[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!user) return;

    getUserTickets(user.id)
      .then(setTickets)
      .catch(() => setTickets([]))
      .finally(() => setLoading(false));
  }, [user?.id]);

  if (!user) {
    return <Navigate to="/acesso?redirect=/dashboard/meus-tickets" replace />;
  }

  const handleCloseTicket = async (ticketId: string) => {
    try {
      await closeTicket(ticketId, user.id);
      const updated = await getUserTickets(user.id);
      setTickets(updated);
      setSelectedTicket(null);
    } catch {
      // Best effort action.
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case "aberto":
        return <Clock className="text-orange-500" size={20} />;
      case "respondido":
        return <MessageSquare className="text-blue-500" size={20} />;
      case "fechado":
        return <CheckCircle className="text-green-500" size={20} />;
      default:
        return <AlertCircle size={20} />;
    }
  };

  const getStatusBadgeVariant = (
    status: string,
  ): "default" | "secondary" | "destructive" | "outline" => {
    switch (status) {
      case "aberto":
        return "destructive";
      case "respondido":
        return "default";
      case "fechado":
        return "secondary";
      default:
        return "outline";
    }
  };

  const getCategoryLabel = (category: string) => {
    const labels: Record<string, string> = {
      duvida: "Duvida",
      bug: "Problema/Bug",
      pagamento: "Pagamento",
      sugestao: "Sugestao",
      outro: "Outro",
    };

    return labels[category] || category;
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-blue-700 py-8 text-white">
        <div className="mx-auto max-w-6xl px-4 sm:px-6 lg:px-8">
          <div className="flex items-center gap-3">
            <HelpCircle size={32} />
            <div>
              <h1 className="text-3xl font-bold">Meus Tickets de Suporte</h1>
              <p className="text-blue-100">Acompanhe suas solicitacoes</p>
            </div>
          </div>
        </div>
      </div>

      <div className="mx-auto max-w-4xl px-4 py-8 sm:px-6 lg:px-8">
        {loading ? (
          <Card className="p-12 text-center">
            <p className="text-gray-500">Carregando dados...</p>
          </Card>
        ) : tickets.length === 0 ? (
          <Card className="p-12 text-center">
            <HelpCircle className="mx-auto mb-4 text-gray-300" size={48} />
            <h3 className="mb-2 text-lg font-semibold">Nenhum ticket</h3>
            <p className="mb-6 text-gray-600">
              Voce ainda nao criou nenhum ticket de suporte.
            </p>
            <Link to="/dashboard/centro-ajuda">
              <Button className="bg-blue-700 hover:bg-blue-800">Ir para Centro de Ajuda</Button>
            </Link>
          </Card>
        ) : (
          <div className="space-y-4">
            <div className="mb-6 grid gap-4 md:grid-cols-3">
              <Card className="border-l-4 border-orange-500 p-4">
                <div className="text-sm text-gray-600">Abertos</div>
                <div className="text-3xl font-bold text-orange-600">
                  {tickets.filter((t) => t.status === "aberto").length}
                </div>
              </Card>
              <Card className="border-l-4 border-blue-500 p-4">
                <div className="text-sm text-gray-600">Respondidos</div>
                <div className="text-3xl font-bold text-blue-600">
                  {tickets.filter((t) => t.status === "respondido").length}
                </div>
              </Card>
              <Card className="border-l-4 border-green-500 p-4">
                <div className="text-sm text-gray-600">Fechados</div>
                <div className="text-3xl font-bold text-green-600">
                  {tickets.filter((t) => t.status === "fechado").length}
                </div>
              </Card>
            </div>

            <div className="space-y-3">
              {tickets.map((ticket) => (
                <Card
                  key={ticket.id}
                  className="cursor-pointer border-l-4 border-blue-400 p-4 transition hover:shadow-md"
                  onClick={() => setSelectedTicket(ticket)}
                >
                  <div className="flex items-start justify-between">
                    <div className="flex-1">
                      <div className="mb-2 flex items-center gap-3">
                        {getStatusIcon(ticket.status)}
                        <div>
                          <h3 className="font-semibold text-gray-900">{ticket.subject}</h3>
                          <p className="text-sm text-gray-600">
                            #{ticket.id.slice(0, 8).toUpperCase()}
                          </p>
                        </div>
                      </div>

                      <div className="flex flex-wrap items-center gap-2">
                        <Badge variant={getStatusBadgeVariant(ticket.status)}>
                          {ticket.status.charAt(0).toUpperCase() + ticket.status.slice(1)}
                        </Badge>
                        <Badge variant="outline">{getCategoryLabel(ticket.category)}</Badge>
                        <span className="text-xs text-gray-500">
                          {new Date(ticket.createdAt).toLocaleDateString("pt-BR")}
                        </span>
                      </div>

                      <p className="mt-2 line-clamp-2 text-sm text-gray-600">{ticket.message}</p>
                    </div>
                    <ChevronRight className="flex-shrink-0 text-gray-400" />
                  </div>
                </Card>
              ))}
            </div>
          </div>
        )}
      </div>

      <Dialog open={!!selectedTicket} onOpenChange={() => setSelectedTicket(null)}>
        <DialogContent className="max-h-[80vh] max-w-2xl overflow-y-auto">
          {selectedTicket && (
            <>
              <DialogHeader>
                <DialogTitle>{selectedTicket.subject}</DialogTitle>
                <DialogDescription>
                  Ticket #{selectedTicket.id.slice(0, 8).toUpperCase()}
                </DialogDescription>
              </DialogHeader>

              <div className="space-y-4">
                <div className="flex items-center gap-4 rounded-lg bg-gray-50 p-4">
                  <div>
                    <p className="text-xs uppercase tracking-wide text-gray-600">Status</p>
                    <Badge variant={getStatusBadgeVariant(selectedTicket.status)}>
                      {selectedTicket.status.charAt(0).toUpperCase() +
                        selectedTicket.status.slice(1)}
                    </Badge>
                  </div>

                  <div>
                    <p className="text-xs uppercase tracking-wide text-gray-600">Categoria</p>
                    <p className="font-semibold">{getCategoryLabel(selectedTicket.category)}</p>
                  </div>

                  <div className="ml-auto text-right">
                    <p className="text-xs uppercase tracking-wide text-gray-600">Criado em</p>
                    <p className="font-semibold">
                      {new Date(selectedTicket.createdAt).toLocaleDateString("pt-BR")}
                    </p>
                  </div>
                </div>

                <div>
                  <p className="mb-2 text-sm font-semibold text-gray-700">Sua Mensagem</p>
                  <div className="rounded-lg border border-blue-200 bg-blue-50 p-4">
                    <p className="text-sm text-gray-700">{selectedTicket.message}</p>
                  </div>
                </div>

                {selectedTicket.responses && selectedTicket.responses.length > 0 && (
                  <div>
                    <p className="mb-2 text-sm font-semibold text-gray-700">
                      Respostas ({selectedTicket.responses.length})
                    </p>
                    <div className="space-y-3">
                      {selectedTicket.responses.map((response: any, idx: number) => (
                        <div
                          key={idx}
                          className="rounded-lg border border-green-200 bg-green-50 p-4"
                        >
                          <div className="mb-2 flex items-start justify-between">
                            <p className="font-semibold text-green-900">
                              {response.responder || "Equipe FazTudoJA"}
                            </p>
                            <span className="text-xs text-green-700">
                              {new Date(response.respondedAt).toLocaleDateString("pt-BR", {
                                hour: "2-digit",
                                minute: "2-digit",
                              })}
                            </span>
                          </div>
                          <p className="text-sm text-gray-700">{response.response}</p>
                        </div>
                      ))}
                    </div>
                  </div>
                )}

                {(!selectedTicket.responses || selectedTicket.responses.length === 0) && (
                  <div className="rounded-lg border border-yellow-200 bg-yellow-50 p-4">
                    <p className="text-sm text-yellow-800">
                      Em breve nossa equipe respondera seu ticket. Acompanhe este espaco!
                    </p>
                  </div>
                )}

                {selectedTicket.status !== "fechado" && (
                  <Button
                    onClick={() => handleCloseTicket(selectedTicket.id)}
                    variant="outline"
                    className="w-full"
                  >
                    Fechar Ticket
                  </Button>
                )}
              </div>
            </>
          )}
        </DialogContent>
      </Dialog>
    </div>
  );
}

