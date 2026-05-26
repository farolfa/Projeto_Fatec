import { Link } from "react-router";
import { useEffect, useState } from "react";
import { Button } from "../components/ui/button";
import { Card } from "../components/ui/card";
import {
  LayoutDashboard,
  FileText,
  MessageSquare,
  User,
  Star,
  CheckCircle,
  AlertCircle,
} from "lucide-react";
import { getSessionUser } from "../services/auth";
import { getClientOrders, type DemoOrder } from "../services/demo-orders";
import { getUserRatingStats } from "../services/rating-stats";

export function Dashboard() {
  const user = getSessionUser();
  const [ratingStats, setRatingStats] = useState({
    rating: 0,
    count: 0,
  });
  const [clientOrders, setClientOrders] = useState<DemoOrder[]>([]);

  useEffect(() => {
    if (!user?.id) return;
    void getClientOrders(user.id).then(setClientOrders);
  }, [user?.id]);

  const recentRequests = clientOrders.slice(0, 3);

  // Pedidos prontos para finalizar
  const pendingFinalization = clientOrders.filter(
    (order) =>
      (order.status === "active" ||
        order.status === "completed" ||
        order.status === "work_completed") &&
      !order.clientFinishedConfirmed
  );

  const activeOrders = clientOrders.filter(
    (order) => order.status === "pending" || order.status === "active"
  ).length;

  const receivedQuotes = clientOrders.reduce(
    (sum, order) => sum + order.proposals,
    0
  );

  const completedServices = clientOrders.filter(
    (order) => order.status === "completed"
  ).length;

  const averageRating =
    ratingStats.rating > 0 ? ratingStats.rating.toFixed(1) : "0.0";

  useEffect(() => {
    let isMounted = true;
    if (!user?.id) {
      setRatingStats({ rating: 0, count: 0 });
      return;
    }
    void getUserRatingStats(user.id)
      .then((stats) => {
        if (isMounted) {
          setRatingStats({ rating: stats.rating, count: stats.count });
        }
      })
      .catch(() => {
        if (isMounted) {
          setRatingStats({ rating: 0, count: 0 });
        }
      });
    return () => {
      isMounted = false;
    };
  }, [user?.id]);

  const stats = [
    {
      label: "Pedidos Ativos",
      value: String(activeOrders),
      icon: FileText,
      color: "blue",
    },
    {
      label: "Orçamentos Recebidos",
      value: String(receivedQuotes),
      icon: MessageSquare,
      color: "orange",
    },
    {
      label: "Serviços Concluídos",
      value: String(completedServices),
      icon: CheckCircle,
      color: "green",
    },
    {
      label: "Avaliação Média",
      value: averageRating,
      icon: Star,
      color: "yellow",
    },
  ];

  const firstName = user?.nome?.split(" ")[0] || "Cliente";

  const getStatusBadge = (status: string) => {
    const badges = {
      pending: {
        label: "Aguardando Orçamentos",
        className: "bg-yellow-100 text-yellow-800",
      },
      active: {
        label: "Orçamentos Recebidos",
        className: "bg-blue-100 text-blue-800",
      },
      completed: {
        label: "Concluído",
        className: "bg-green-100 text-green-800",
      },
    };
    return (
      badges[status as keyof typeof badges] || badges.pending
    );
  };

  const getPrimaryActionLabel = (
    request: (typeof recentRequests)[number]
  ) => {
    const canConfirmWorkFinished =
      (request.status === "active" ||
        request.status === "completed" ||
        request.status === "work_completed") &&
      !request.clientFinishedConfirmed;
    return canConfirmWorkFinished
      ? "Ver aqui: Trabalho Finalizado"
      : "Ver Detalhes";
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-blue-700 text-white py-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex items-center gap-4 mb-4">
            <LayoutDashboard size={40} />
            <div>
              <h1 className="text-3xl font-bold">Olá, {firstName}! 👋</h1>
              <p className="text-blue-100">
                Bem-vindo de volta ao seu painel
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Quick Actions */}
        <div className="mb-8">
          <Link to="/solicitar-servico">
            <Button
              size="lg"
              className="bg-orange-600 hover:bg-orange-700 shadow-lg"
            >
              + Solicitar Novo Serviço
            </Button>
          </Link>
        </div>

        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
          {stats.map((stat, index) => (
            <Card key={index} className="p-6 hover:shadow-lg transition-shadow">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-gray-600 text-sm mb-1">{stat.label}</p>
                  <p className="text-3xl font-bold">{stat.value}</p>
                </div>
                <div
                  className={`w-12 h-12 rounded-full flex items-center justify-center ${
                    stat.color === "blue"
                      ? "bg-blue-100"
                      : stat.color === "orange"
                        ? "bg-orange-100"
                        : stat.color === "green"
                          ? "bg-green-100"
                          : "bg-yellow-100"
                  }`}
                >
                  <stat.icon
                    className={
                      stat.color === "blue"
                        ? "text-blue-600"
                        : stat.color === "orange"
                          ? "text-orange-600"
                          : stat.color === "green"
                            ? "text-green-600"
                            : "text-yellow-600"
                    }
                    size={24}
                  />
                </div>
              </div>
            </Card>
          ))}
        </div>

        {/* Pending Finalization Alert */}
        {pendingFinalization.length > 0 && (
          <div className="mb-8">
            <Card className="p-6 border-2 border-green-300 bg-green-50">
              <div className="flex items-start gap-4">
                <AlertCircle className="text-green-600 flex-shrink-0 mt-1" size={28} />
                <div className="flex-1">
                  <h3 className="text-lg font-bold text-green-900 mb-4">
                    {pendingFinalization.length} pedido{pendingFinalization.length !== 1 ? "s" : ""} pronto{pendingFinalization.length !== 1 ? "s" : ""} para finalizar!
                  </h3>
                  <div className="space-y-3">
                    {pendingFinalization.map((order) => (
                      <div
                        key={order.id}
                        className="flex items-center justify-between bg-white rounded-lg p-4 border border-green-200"
                      >
                        <div>
                          <p className="font-semibold text-gray-900">
                            {order.title}
                          </p>
                          <p className="text-sm text-gray-600">
                            O prestador finalizou. Confirme que o trabalho foi
                            concluído.
                          </p>
                        </div>
                        <Link to={`/dashboard/pedido/${order.id}`}>
                          <Button className="bg-green-600 hover:bg-green-700">
                            Finalizar Agora
                          </Button>
                        </Link>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </Card>
          </div>
        )}

        {/* Recent Requests */}
        <div className="bg-white rounded-lg shadow-lg p-6 mb-8">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold">Meus Pedidos Recentes</h2>
            <Link to="/dashboard/meus-pedidos">
              <Button variant="outline">Ver Todos</Button>
            </Link>
          </div>
          <div className="space-y-4">
            {recentRequests.length === 0 ? (
              <Card className="p-8 text-center">
                <h3 className="text-lg font-semibold mb-2">
                  Nenhum pedido recente
                </h3>
                <p className="text-gray-600 mb-4">
                  Crie seu primeiro pedido para começar a receber orçamentos.
                </p>
                <Link to="/solicitar-servico">
                  <Button>+ Solicitar Novo Serviço</Button>
                </Link>
              </Card>
            ) : (
              recentRequests.map((request) => {
                const badge = getStatusBadge(request.status);
                return (
                  <div
                    key={request.id}
                    className="border border-gray-200 rounded-lg p-4 hover:border-blue-400 transition-colors"
                  >
                    <div className="flex items-start justify-between mb-2">
                      <div className="flex-1">
                        <h3 className="font-semibold text-lg mb-1">
                          {request.title}
                        </h3>
                        <p className="text-gray-600 text-sm mb-2">
                          {request.category} • {request.date}
                        </p>
                        <span
                          className={`inline-block px-3 py-1 rounded-full text-xs font-medium ${badge.className}`}
                        >
                          {badge.label}
                        </span>
                      </div>
                      <div className="text-right">
                        <div className="bg-blue-100 text-blue-800 px-3 py-1 rounded-full text-sm font-semibold mb-2">
                          {request.proposals} orçamentos
                        </div>
                        <Link to={`/dashboard/pedido/${request.id}`}>
                          <Button variant="outline" size="sm">
                            {getPrimaryActionLabel(request)}
                          </Button>
                        </Link>
                      </div>
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </div>

        {/* Quick Links */}
        <div className="grid md:grid-cols-3 gap-6">
          <Link to="/dashboard/meus-pedidos">
            <Card className="p-6 hover:shadow-xl transition-shadow cursor-pointer border-2 border-transparent hover:border-blue-400">
              <FileText className="text-blue-600 mb-4" size={40} />
              <h3 className="text-xl font-bold mb-2">Meus Pedidos</h3>
              <p className="text-gray-600">
                Visualize e gerencie todos os seus pedidos de serviço
              </p>
            </Card>
          </Link>
          <Link to="/dashboard/orcamentos">
            <Card className="p-6 hover:shadow-xl transition-shadow cursor-pointer border-2 border-transparent hover:border-orange-400">
              <MessageSquare className="text-orange-600 mb-4" size={40} />
              <h3 className="text-xl font-bold mb-2">Orçamentos</h3>
              <p className="text-gray-600">
                Compare e escolha os melhores orçamentos recebidos
              </p>
            </Card>
          </Link>
          <Link to="/dashboard/perfil">
            <Card className="p-6 hover:shadow-xl transition-shadow cursor-pointer border-2 border-transparent hover:border-blue-400">
              <User className="text-blue-600 mb-4" size={40} />
              <h3 className="text-xl font-bold mb-2">Meu Perfil</h3>
              <p className="text-gray-600">
                Atualize seus dados e preferências
              </p>
            </Card>
          </Link>
        </div>
      </div>
    </div>
  );
}
