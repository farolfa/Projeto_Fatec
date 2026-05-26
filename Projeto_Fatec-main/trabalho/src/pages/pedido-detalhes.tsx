import { useState, useEffect, useMemo } from "react";
import { Link, Navigate, useLocation, useNavigate, useParams } from "react-router";
import { Button } from "../components/ui/button";
import { Card } from "../components/ui/card";
import { Badge } from "../components/ui/badge";
import {
  Calendar,
  CheckCircle,
  DollarSign,
  MapPin,
  MessageSquare,
  AlertCircle,
  FileText,
  MessageSquareText,
  Wrench,
} from "lucide-react";
import { getSessionUser } from "../services/auth";
import { ImageWithFallback } from "../components/figma/ImageWithFallback";
import {
  acceptDemoProposal,
  getOrderById,
  getOrderProposals,
  confirmWorkFinished,
  getOrderReviewsStatus,
  type DemoOrder,
  type DemoProposal,
} from "../services/demo-orders";

const fallbackProfilePhoto =
  "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=640";

export function PedidoDetalhes() {
  const params = useParams();
  const [message, setMessage] = useState("");
  const [refreshKey, setRefreshKey] = useState(0);
  const [order, setOrder] = useState<DemoOrder | null>(null);
  const [proposals, setProposals] = useState<DemoProposal[]>([]);
  const [reviewsStatus, setReviewsStatus] = useState({
    clientReviewGiven: false,
    providerReviewGiven: false,
  });
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const location = useLocation();
  const sessionUser = useMemo(() => getSessionUser(), []);
  const userId = sessionUser?.id ?? null;
  const isProviderView = location.pathname.startsWith("/prestador");
  const fallbackPath = isProviderView
    ? "/prestador/servicos-disponiveis"
    : "/dashboard/meus-pedidos";

  useEffect(() => {
    let isMounted = true;
    const loadOrderDetails = async () => {
      setLoading(true);
      if (!userId || !params.id) {
        if (!userId) navigate("/", { replace: true });
        if (isMounted) setLoading(false);
        return;
      }

      const orderId = Number(params.id);
      if (!Number.isFinite(orderId) || orderId <= 0) {
        navigate(fallbackPath, { replace: true });
        if (isMounted) setLoading(false);
        return;
      }

      try {
        const loadedOrder = await getOrderById(orderId);
        const isClientOwner = loadedOrder?.clientUserId === userId;
        const canAccess = isProviderView
          ? !!loadedOrder && !isClientOwner
          : !!loadedOrder && isClientOwner;

        if (!canAccess) {
          navigate(fallbackPath, { replace: true });
          if (isMounted) setLoading(false);
          return;
        }

        const [loadedProposals, loadedReviewsStatus] = await Promise.all([
          getOrderProposals(loadedOrder.id),
          getOrderReviewsStatus(loadedOrder.id).catch(() => ({
            clientReviewGiven: false,
            providerReviewGiven: false,
          })),
        ]);

        if (!isMounted) return;
        setOrder(loadedOrder);
        setProposals(loadedProposals);
        setReviewsStatus(loadedReviewsStatus);
      } finally {
        if (isMounted) setLoading(false);
      }
    };

    void loadOrderDetails();
    return () => {
      isMounted = false;
    };
  }, [userId, params.id, refreshKey, navigate, fallbackPath, isProviderView]);

  const handleAcceptProposal = (proposalId: number) => {
    if (!order) return;
    try {
      void acceptDemoProposal(order.id, proposalId).then(() => {
        setMessage("Orçamento aceito com sucesso.");
        setRefreshKey((value) => value + 1);
      });
    } catch (error) {
      setMessage(
        error instanceof Error
          ? error.message
          : "Não foi possível aceitar o orçamento."
      );
    }
  };

  const handleConfirmWorkFinished = () => {
    if (!order || !userId) return;
    try {
      void confirmWorkFinished(order.id, userId).then((updatedOrder) => {
        const bothConfirmed =
          updatedOrder.status === "work_completed_confirmed" ||
          (updatedOrder.clientFinishedConfirmed === true &&
            updatedOrder.providerFinishedConfirmed === true);
        if (bothConfirmed) {
          setMessage(
            "Trabalho confirmado por ambos. Redirecionando para avaliação..."
          );
          setTimeout(
            () => navigate(`/dashboard/avaliacoes?pedido=${order.id}`),
            1500
          );
          return;
        }
        setMessage("Você confirmou que o trabalho foi finalizado!");
        setRefreshKey((value) => value + 1);
      });
    } catch (error) {
      setMessage(
        error instanceof Error
          ? error.message
          : "Não foi possível confirmar o trabalho."
      );
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <div className="bg-blue-700 text-white py-8">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="h-9 w-56 rounded bg-blue-600 animate-pulse mb-2" />
            <div className="h-4 w-72 rounded bg-blue-600/50 animate-pulse" />
          </div>
        </div>
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-4">
          <Card className="p-5 animate-pulse">
            <div className="flex justify-between gap-4">
              {[1, 2, 3, 4].map((i) => (
                <div key={i} className="h-10 w-20 rounded-full bg-gray-200" />
              ))}
            </div>
          </Card>
          <Card className="p-6 animate-pulse">
            <div className="h-8 w-64 rounded bg-gray-200 mb-3" />
            <div className="h-4 w-full rounded bg-gray-200 mb-2" />
            <div className="h-4 w-5/6 rounded bg-gray-200 mb-4" />
            <div className="flex gap-6">
              <div className="h-4 w-28 rounded bg-gray-200" />
              <div className="h-4 w-28 rounded bg-gray-200" />
            </div>
          </Card>
        </div>
      </div>
    );
  }

  if (!order) {
    return <Navigate to={fallbackPath} replace />;
  }

  const visibleProposals = isProviderView
    ? proposals.filter((proposal) => proposal.providerUserId === userId)
    : proposals;

  const statusMap = {
    pending: "Aguardando Orçamentos",
    active: "Orçamentos Recebidos",
    completed: "Trabalho em Andamento",
    work_completed: "Aguardando Confirmação",
    work_completed_confirmed: "Pronto para Avaliar",
  } as const;

  const canClientConfirmWorkFinished =
    !isProviderView &&
    (order.status === "active" ||
      order.status === "completed" ||
      order.status === "work_completed") &&
    !order.clientFinishedConfirmed;

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="bg-blue-700 text-white py-8">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <h1 className="text-3xl font-bold mb-2">Detalhes do Pedido</h1>
          <p className="text-blue-100">
            {isProviderView
              ? "Veja as informações do pedido antes de enviar ou acompanhar seu orçamento"
              : "Acompanhe o andamento do seu pedido e os orçamentos recebidos"}
          </p>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
        {message && (
          <div className="rounded-lg border border-green-200 bg-green-50 px-4 py-3 text-sm text-green-900">
            {message}
          </div>
        )}

        {/* Progress Stepper */}
        {(() => {
          const steps = [
            { key: "pending", label: "Pedido Criado", Icon: FileText },
            {
              key: "active",
              label: "Orçamentos Recebidos",
              Icon: MessageSquareText,
            },
            { key: "completed", label: "Em Andamento", Icon: Wrench },
            {
              key: "work_completed_confirmed",
              label: "Concluído",
              Icon: CheckCircle,
            },
          ];
          const stepOrder = [
            "pending",
            "active",
            "completed",
            "work_completed",
            "work_completed_confirmed",
          ];
          const currentIndex = stepOrder.indexOf(order.status);
          const activeStepIndex = [0, 1, 2, 2, 3][Math.max(0, currentIndex)];

          return (
            <Card className="p-5 shadow-sm">
              <div className="flex items-center justify-between">
                {steps.map((step, i) => (
                  <div key={step.key} className="flex items-center flex-1">
                    <div className="flex flex-col items-center">
                      <div
                        className={`w-10 h-10 rounded-full flex items-center justify-center text-lg font-bold transition-colors ${
                          i <= activeStepIndex
                            ? "bg-blue-600 text-white shadow-md"
                            : "bg-gray-200 text-gray-400"
                        }`}
                      >
                        {i < activeStepIndex ? (
                          <CheckCircle size={20} />
                        ) : (
                          <step.Icon size={20} />
                        )}
                      </div>
                      <span
                        className={`text-xs mt-1 text-center max-w-[80px] leading-tight ${
                          i <= activeStepIndex
                            ? "text-blue-700 font-semibold"
                            : "text-gray-400"
                        }`}
                      >
                        {step.label}
                      </span>
                    </div>
                    {i < steps.length - 1 && (
                      <div
                        className={`flex-1 h-1 mx-2 rounded ${
                          i < activeStepIndex ? "bg-blue-500" : "bg-gray-200"
                        }`}
                      />
                    )}
                  </div>
                ))}
              </div>
            </Card>
          );
        })()}

        <Card className="p-6 border-2 border-blue-200 shadow-md">
          <div className="flex flex-col lg:flex-row gap-6 lg:items-start lg:justify-between">
            <div className="flex-1">
              <div className="flex items-start justify-between gap-4 mb-4">
                <div>
                  <h2 className="text-3xl font-bold mb-1">{order.title}</h2>
                  <p className="text-gray-600">{order.category}</p>
                </div>
                <Badge className="bg-yellow-100 text-yellow-800 border border-yellow-300 hover:bg-yellow-100">
                  {statusMap[order.status]}
                </Badge>
              </div>
              <p className="text-gray-700 text-lg mb-5">{order.description}</p>
              <div className="flex flex-wrap gap-4 text-sm text-gray-600">
                <div className="flex items-center gap-2">
                  <Calendar size={16} />
                  {order.date}
                </div>
                <div className="flex items-center gap-2">
                  <MapPin size={16} />
                  {order.location}
                </div>
                <div className="flex items-center gap-2">
                  <DollarSign size={16} />
                  {order.budget}
                </div>
              </div>
            </div>
            <div className="lg:w-64 flex flex-col gap-3">
              <div className="rounded-xl border-2 border-blue-200 bg-blue-50 p-5 text-center">
                <div className="text-4xl font-bold text-blue-700">
                  {order.proposals}
                </div>
                <div className="text-sm text-gray-600 mt-1">
                  Orçamento{order.proposals !== 1 ? "s" : ""} recebido
                  {order.proposals !== 1 ? "s" : ""}
                </div>
              </div>
              <Link to={fallbackPath}>
                <Button variant="outline" className="w-full">
                  {isProviderView
                    ? "Voltar para Serviços Disponíveis"
                    : "Voltar para Meus Pedidos"}
                </Button>
              </Link>
            </div>
          </div>
        </Card>

        {visibleProposals.length === 0 ? (
          <Card className="p-10 text-center">
            <MessageSquare className="mx-auto mb-4 text-gray-400" size={56} />
            <h3 className="text-2xl font-semibold mb-2">
              {isProviderView
                ? "Você ainda não enviou orçamento para este pedido"
                : "Nenhum orçamento recebido ainda"}
            </h3>
            <p className="text-gray-600">
              {isProviderView
                ? "Volte para a lista de serviços e use o botão de enviar orçamento neste pedido."
                : "Seu pedido já está visível para os prestadores. Assim que eles enviarem propostas, elas aparecerão aqui."}
            </p>
          </Card>
        ) : (
          <div className="space-y-4">
            {visibleProposals.map((proposal) => (
              <Card
                key={`${proposal.id}-${refreshKey}`}
                className="p-6 border-2 border-transparent hover:border-blue-300 hover:shadow-lg transition-all"
              >
                <div className="flex flex-col lg:flex-row gap-6">
                  <div className="flex-1">
                    <div className="flex items-center gap-3 mb-3">
                      <div className="h-12 w-12 overflow-hidden rounded-full bg-blue-100 text-blue-700 flex items-center justify-center">
                        <ImageWithFallback
                          src={
                            proposal.providerPhoto || fallbackProfilePhoto
                          }
                          alt={proposal.providerName}
                          className="h-full w-full object-cover"
                        />
                      </div>
                      <div>
                        <h3 className="text-xl font-bold">
                          {proposal.providerName}
                        </h3>
                        <p className="text-sm text-gray-500">
                          Orçamento{" "}
                          {proposal.status === "accepted"
                            ? "aceito"
                            : "enviado"}{" "}
                          {proposal.sentDate}
                        </p>
                      </div>
                    </div>
                    <p className="text-gray-700 mb-4">
                      {proposal.description}
                    </p>
                    <div className="flex flex-wrap gap-4 text-sm text-gray-600">
                      <div className="flex items-center gap-2">
                        <CheckCircle size={16} />
                        Prazo: {proposal.deliveryTime}
                      </div>
                      <div className="flex items-center gap-2">
                        <MessageSquare size={16} />
                        Concorrência:{" "}
                        {proposal.competitors} orçamento
                        {proposal.competitors !== 1 ? "s" : ""}
                      </div>
                    </div>
                  </div>
                  <div className="lg:w-60 flex flex-col gap-3">
                    <div className="rounded-xl border-2 border-orange-300 bg-orange-50 p-5 text-center">
                      <div className="text-sm text-gray-600 mb-1">
                        Valor enviado
                      </div>
                      <div className="text-3xl font-bold text-orange-600">
                        {proposal.myPrice}
                      </div>
                    </div>
                    {!isProviderView && proposal.status !== "accepted" ? (
                      <Button
                        onClick={() => handleAcceptProposal(proposal.id)}
                        className="w-full bg-emerald-600 hover:bg-emerald-700"
                      >
                        Aceitar Orçamento
                      </Button>
                    ) : !isProviderView ? (
                      <>
                        <Badge className="w-full text-center py-2 bg-green-100 text-green-800 border border-green-300 hover:bg-green-100 justify-center">
                          <CheckCircle size={16} className="mr-1" /> Aceito
                        </Badge>
                        {canClientConfirmWorkFinished && (
                          <Button
                            onClick={handleConfirmWorkFinished}
                            variant="outline"
                            className="w-full text-blue-600 border-blue-300"
                          >
                            <CheckCircle className="mr-2" size={18} />
                            Trabalho Finalizado
                          </Button>
                        )}
                      </>
                    ) : (
                      <Badge className="w-full text-center py-2 bg-blue-100 text-blue-800 border border-blue-300 hover:bg-blue-100 justify-center">
                        {proposal.status === "accepted"
                          ? "✓ Seu orçamento foi aceito"
                          : "Orçamento enviado"}
                      </Badge>
                    )}
                  </div>
                </div>
              </Card>
            ))}
          </div>
        )}

        {/* Status: Waiting for client confirmation */}
        {canClientConfirmWorkFinished && (
          <Card className="p-6 border-2 border-yellow-200 bg-yellow-50">
            <div className="flex items-start gap-4">
              <AlertCircle
                className="text-yellow-600 flex-shrink-0 mt-1"
                size={24}
              />
              <div className="flex-1">
                <h3 className="text-lg font-bold text-yellow-900 mb-2">
                  O trabalho foi finalizado?
                </h3>
                <p className="text-yellow-800 mb-4">
                  {order.providerFinishedConfirmed
                    ? "O prestador já confirmou a finalização. Confirme abaixo para liberar as avaliações."
                    : "Confirme ao concluir o serviço. Quando ambos confirmarem, as avaliações serão liberadas."}
                </p>
                <Button
                  onClick={handleConfirmWorkFinished}
                  className="bg-orange-600 hover:bg-orange-700"
                >
                  Confirmar Trabalho Finalizado
                </Button>
              </div>
            </div>
          </Card>
        )}

        {/* Status: Work Completed - Waiting for other user confirmation */}
        {!isProviderView &&
          order.status === "work_completed" &&
          order.clientFinishedConfirmed && (
            <Card className="p-6 border-2 border-blue-200 bg-blue-50">
              <div className="flex items-start gap-4">
                <AlertCircle
                  className="text-blue-600 flex-shrink-0 mt-1"
                  size={24}
                />
                <div className="flex-1">
                  <h3 className="text-lg font-bold text-blue-900 mb-2">
                    Aguardando confirmação do prestador
                  </h3>
                  <p className="text-blue-800">
                    Você confirmou que o trabalho foi finalizado. Assim que o
                    prestador também confirmar, você poderá avaliar.
                  </p>
                </div>
              </div>
            </Card>
          )}

        {/* Status: Work Completed Confirmed - Ready to rate */}
        {!isProviderView &&
          order.status === "work_completed_confirmed" &&
          !reviewsStatus?.clientReviewGiven && (
            <Card className="p-6 border-2 border-green-200 bg-green-50">
              <div className="flex items-start gap-4">
                <CheckCircle
                  className="text-green-600 flex-shrink-0 mt-1"
                  size={24}
                />
                <div className="flex-1">
                  <h3 className="text-lg font-bold text-green-900 mb-2">
                    Pronto para avaliar!
                  </h3>
                  <p className="text-green-800 mb-4">
                    O trabalho foi finalizado e confirmado por ambos. Agora
                    você pode avaliar o prestador.
                  </p>
                  <Link to="/dashboard/avaliacoes">
                    <Button className="bg-emerald-600 hover:bg-emerald-700">
                      Ir para Avaliações
                    </Button>
                  </Link>
                </div>
              </div>
            </Card>
          )}
      </div>
    </div>
  );
}
