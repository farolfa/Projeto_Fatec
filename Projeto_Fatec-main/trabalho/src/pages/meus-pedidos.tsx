import { useEffect, useState } from "react";
import { Link } from "react-router";
import { Calendar, DollarSign, MapPin, Search, Trash2 } from "lucide-react";

import { Button, buttonVariants } from "../components/ui/button";
import { Card } from "../components/ui/card";
import { Input } from "../components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../components/ui/select";
import { getSessionUser } from "../services/auth";
import {
  deleteDemoOrder,
  getClientOrders,
  getGivenReviews,
  type DemoOrder,
} from "../services/demo-orders";
import { cn } from "../components/ui/utils";

export function MeusPedidos() {
  const [filterStatus, setFilterStatus] = useState("all");
  const [searchQuery, setSearchQuery] = useState("");
  const [allRequests, setAllRequests] = useState<DemoOrder[]>([]);
  const [reviewedOrderIds, setReviewedOrderIds] = useState<Set<number>>(new Set());
  const [deletingOrderId, setDeletingOrderId] = useState<number | null>(null);
  const [actionError, setActionError] = useState("");
  const [loadingRequests, setLoadingRequests] = useState(true);

  const user = getSessionUser();

  useEffect(() => {
    if (!user?.id) {
      setLoadingRequests(false);
      return;
    }

    let isMounted = true;
    setLoadingRequests(true);

    void Promise.all([getClientOrders(user.id), getGivenReviews(user.id)])
      .then(([orders, givenReviews]) => {
        if (!isMounted) {
          return;
        }

        setAllRequests(orders);
        setReviewedOrderIds(new Set(givenReviews.map((review) => review.orderId)));
      })
      .finally(() => {
        if (isMounted) {
          setLoadingRequests(false);
        }
      });

    return () => {
      isMounted = false;
    };
  }, [user?.id]);

  const handleDeleteOrder = async (order: DemoOrder) => {
    if (!user?.id) return;

    const confirmed = window.confirm(
      "Deseja excluir este pedido? Esta acao nao pode ser desfeita.",
    );
    if (!confirmed) return;

    setActionError("");
    setDeletingOrderId(order.id);

    try {
      await deleteDemoOrder(order.id, user.id);
      setAllRequests((prev) => prev.filter((item) => item.id !== order.id));
    } catch (error) {
      const message =
        error instanceof Error ? error.message : "Nao foi possivel excluir o pedido.";
      setActionError(message);
    } finally {
      setDeletingOrderId(null);
    }
  };

  const getStatusBadge = (status: string, isReviewed: boolean) => {
    if (status === "work_completed_confirmed" && isReviewed) {
      return {
        label: "Finalizado",
        className: "border-green-300 bg-green-100 text-green-800",
      };
    }

    const badges = {
      pending: {
        label: "Aguardando Orcamentos",
        className: "border-yellow-300 bg-yellow-100 text-yellow-800",
      },
      active: {
        label: "Orcamentos Recebidos",
        className: "border-blue-300 bg-blue-100 text-blue-800",
      },
      completed: {
        label: "Concluido",
        className: "border-green-300 bg-green-100 text-green-800",
      },
      work_completed: {
        label: "Aguardando Confirmacao",
        className: "border-orange-300 bg-orange-100 text-orange-800",
      },
      work_completed_confirmed: {
        label: "Pronto para Avaliar",
        className: "border-green-300 bg-green-100 text-green-800",
      },
      cancelled: {
        label: "Cancelado",
        className: "border-red-300 bg-red-100 text-red-800",
      },
    };

    return badges[status as keyof typeof badges] || badges.pending;
  };

  const filteredRequests = allRequests.filter((request) => {
    let matchesStatus = true;

    if (filterStatus !== "all") {
      if (filterStatus === "completed") {
        matchesStatus =
          request.status === "work_completed" ||
          request.status === "work_completed_confirmed";
      } else {
        matchesStatus = request.status === filterStatus;
      }
    }

    const title = request.title?.toLowerCase() ?? "";
    const category = request.category?.toLowerCase() ?? "";
    const query = searchQuery.toLowerCase();
    const matchesSearch = title.includes(query) || category.includes(query);

    return matchesStatus && matchesSearch;
  });

  const getPrimaryActionLabel = (request: DemoOrder) => {
    const canConfirmWorkFinished =
      (request.status === "active" ||
        request.status === "completed" ||
        request.status === "work_completed") &&
      !request.clientFinishedConfirmed;

    return canConfirmWorkFinished ? "Ver aqui: Trabalho Finalizado" : "Ver Detalhes";
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-blue-700 py-8 text-white">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <h1 className="mb-2 text-3xl font-bold">Meus Pedidos</h1>
          <p className="text-blue-100">Gerencie todos os seus pedidos de servico</p>
        </div>
      </div>

      <div className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
        <div className="mb-6 rounded-lg bg-white p-4 shadow-md">
          <div className="flex flex-col gap-4 md:flex-row">
            <div className="relative flex-1">
              <Search
                className="absolute left-3 top-1/2 -translate-y-1/2 transform text-gray-400"
                size={20}
              />
              <Input
                type="text"
                placeholder="Buscar pedidos..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                className="pl-10"
              />
            </div>

            <Select value={filterStatus} onValueChange={setFilterStatus}>
              <SelectTrigger className="w-full md:w-48">
                <SelectValue placeholder="Filtrar por status" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">Todos</SelectItem>
                <SelectItem value="pending">Aguardando</SelectItem>
                <SelectItem value="active">Com Orcamentos</SelectItem>
                <SelectItem value="completed">Concluidos</SelectItem>
              </SelectContent>
            </Select>

            <Link
              to="/solicitar-servico"
              className={cn(
                buttonVariants({ className: "w-full bg-orange-600 hover:bg-orange-700 md:w-auto" }),
              )}
            >
              + Novo Pedido
            </Link>
          </div>
        </div>

        <div className="mb-4">
          <p className="text-gray-600">
            {loadingRequests ? (
              "Carregando dados..."
            ) : (
              <>
                <span className="font-semibold">{filteredRequests.length}</span> pedido
                {filteredRequests.length !== 1 ? "s" : ""} encontrado
                {filteredRequests.length !== 1 ? "s" : ""}
              </>
            )}
          </p>
        </div>

        {actionError && (
          <div className="mb-4 rounded-lg border border-red-300 bg-red-50 px-4 py-3 text-sm text-red-700">
            {actionError}
          </div>
        )}

        {loadingRequests ? (
          <div className="space-y-4">
            {Array.from({ length: 3 }).map((_, index) => (
              <Card key={index} className="border-2 border-transparent p-6">
                <div className="animate-pulse space-y-4">
                  <div className="h-6 w-2/5 rounded bg-gray-200" />
                  <div className="h-4 w-1/4 rounded bg-gray-200" />
                  <div className="h-4 w-full rounded bg-gray-200" />
                  <div className="h-4 w-5/6 rounded bg-gray-200" />
                  <div className="h-10 w-40 rounded bg-gray-200" />
                </div>
              </Card>
            ))}
          </div>
        ) : (
          <div className="space-y-4">
            {filteredRequests.map((request) => {
              const badge = getStatusBadge(request.status, reviewedOrderIds.has(request.id));

              return (
                <Card
                  key={request.id}
                  className="border-2 border-transparent p-6 transition-all hover:border-blue-400 hover:shadow-xl"
                >
                  <div className="flex flex-col gap-6 lg:flex-row">
                    <div className="flex-1">
                      <div className="mb-3 flex items-start justify-between">
                        <div>
                          <h3 className="mb-1 text-xl font-bold">{request.title}</h3>
                          <p className="text-sm text-gray-600">{request.category}</p>
                        </div>
                        <span
                          className={`rounded-full border px-3 py-1 text-xs font-semibold ${badge.className}`}
                        >
                          {badge.label}
                        </span>
                      </div>

                      <p className="mb-4 text-gray-700">{request.description}</p>

                      <div className="flex flex-wrap gap-4 text-sm text-gray-600">
                        <div className="flex items-center gap-1">
                          <Calendar size={16} />
                          <span>{request.date}</span>
                        </div>
                        <div className="flex items-center gap-1">
                          <MapPin size={16} />
                          <span>{request.location}</span>
                        </div>
                        <div className="flex items-center gap-1">
                          <DollarSign size={16} />
                          <span>{request.budget}</span>
                        </div>
                      </div>
                    </div>

                    <div className="flex flex-col gap-3 lg:w-48">
                      <div className="rounded-lg border-2 border-blue-200 bg-blue-50 p-4 text-center">
                        <div className="text-3xl font-bold text-blue-700">{request.proposals}</div>
                        <div className="text-sm text-gray-600">
                          Orcamento{request.proposals !== 1 ? "s" : ""}
                        </div>
                      </div>

                      <Link
                        to={`/dashboard/pedido/${request.id}`}
                        className={cn(
                          buttonVariants({ className: "w-full bg-blue-700 hover:bg-blue-800" }),
                        )}
                      >
                        {getPrimaryActionLabel(request)}
                      </Link>

                      {request.status === "active" && (
                        <Link
                          to={`/dashboard/orcamentos?pedido=${request.id}`}
                          className={cn(buttonVariants({ variant: "outline", className: "w-full" }))}
                        >
                          Ver Orcamentos
                        </Link>
                      )}

                      {request.status === "pending" && (
                        <Button
                          type="button"
                          variant="destructive"
                          className="w-full"
                          disabled={deletingOrderId === request.id}
                          onClick={() => void handleDeleteOrder(request)}
                        >
                          <Trash2 size={16} className="mr-2" />
                          {deletingOrderId === request.id ? "Excluindo..." : "Excluir Pedido"}
                        </Button>
                      )}
                    </div>
                  </div>
                </Card>
              );
            })}
          </div>
        )}

        {!loadingRequests && filteredRequests.length === 0 && (
          <Card className="p-12 text-center">
            <div className="mb-4 text-gray-400">
              <Search size={64} className="mx-auto" />
            </div>
            <h3 className="mb-2 text-xl font-semibold">Nenhum pedido encontrado</h3>
            <p className="mb-6 text-gray-600">
              Tente ajustar os filtros ou faca um novo pedido
            </p>
            <Link
              to="/solicitar-servico"
              className={cn(buttonVariants({ className: "bg-orange-600 hover:bg-orange-700" }))}
            >
              + Solicitar Servico
            </Link>
          </Card>
        )}
      </div>
    </div>
  );
}

