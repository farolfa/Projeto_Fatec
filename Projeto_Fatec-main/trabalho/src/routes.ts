import { createBrowserRouter } from "react-router";
import { lazy } from "react";
import { Layout } from "./components/layout";
import { DashboardLayout } from "./components/dashboard-layout";
import { DashboardPrestadorLayout } from "./components/dashboard-prestador-layout";
import { AdminLayout } from "./components/admin-layout";
import { PedidoDetalhes } from "./pages/pedido-detalhes";
import { AdminDashboard } from "./pages/admin/admin-dashboard";
import { AdminUsuarios } from "./pages/admin/admin-usuarios";
import { AdminPedidos } from "./pages/admin/admin-pedidos";
import { AdminAvaliacoes } from "./pages/admin/admin-avaliacoes";
import { AdminCategorias } from "./pages/admin/admin-categorias";
import { Dashboard } from "./pages/dashboard";
import { MeusPedidos } from "./pages/meus-pedidos";
import { Orcamentos } from "./pages/orcamentos";
import { Mensagens } from "./pages/mensagens";
import { Notificacoes } from "./pages/notificacoes";
import { Avaliacoes } from "./pages/avaliacoes";
import { Perfil } from "./pages/perfil";
import { BuscarPrestadores } from "./pages/buscar-prestadores";
import { MeusTickets } from "./pages/meus-tickets";
import { MeusFavoritos } from "./pages/meus-favoritos";
import { DashboardPrestador } from "./pages/prestador/dashboard-prestador";
import { ServicosDisponiveis } from "./pages/prestador/servicos-disponiveis";
import { MinhasPropostas } from "./pages/prestador/minhas-propostas";
import { HistoricoPrestador } from "./pages/prestador/historico";
import { Historico } from "./pages/historico";

const loadHome = () => import("./pages/home");
const loadRequestService = () => import("./pages/request-service");
const loadBecomeProfessional = () => import("./pages/become-professional");
const loadAccess = () => import("./pages/access");
const loadProviderPublicProfile = () => import("./pages/provider-public-profile");
const loadCentroAjuda = () => import("./pages/centro-ajuda");
const loadFaq = () => import("./pages/faq");
const loadProjetoAmbiental = () => import("./pages/projeto-ambiental");
const loadSelectProfile = () => import("./pages/select-profile");
const Home = lazy(() => loadHome().then((m) => ({ default: m.Home })));
const RequestService = lazy(() => loadRequestService().then((m) => ({ default: m.RequestService })));
const BecomeProfessional = lazy(() => loadBecomeProfessional().then((m) => ({ default: m.BecomeProfessional })));
const Access = lazy(() => loadAccess().then((m) => ({ default: m.Access })));
const ProviderPublicProfile = lazy(() =>
	loadProviderPublicProfile().then((m) => ({ default: m.ProviderPublicProfile })),
);
const CentroAjuda = lazy(() => loadCentroAjuda().then((m) => ({ default: m.CentroAjuda })));
const Faq = lazy(() => loadFaq().then((m) => ({ default: m.Faq })));
const ProjetoAmbiental = lazy(() =>
	loadProjetoAmbiental().then((m) => ({ default: m.ProjetoAmbiental })),
);
const SelectProfile = lazy(() =>
	loadSelectProfile().then((m) => ({ default: m.SelectProfile })),
);

export const router = createBrowserRouter([
	{
		path: "/",
		Component: Layout,
		children: [
			{ index: true, Component: Home },
			{ path: "acesso", Component: Access },
			{ path: "selecionar-perfil", Component: SelectProfile },
			{ path: "solicitar-servico", Component: RequestService },
			{ path: "seja-profissional", Component: BecomeProfessional },
			{ path: "buscar-prestadores", Component: BuscarPrestadores },
			{ path: "faq", Component: Faq },
			{ path: "projeto-ambiental", Component: ProjetoAmbiental },
			{ path: "centro-ajuda", Component: CentroAjuda },
			{ path: "perfil-publico/prestador/:id", Component: ProviderPublicProfile },
		],
	},
	{
		path: "/dashboard",
		Component: DashboardLayout,
		children: [
			{ index: true, Component: Dashboard },
			{ path: "meus-pedidos", Component: MeusPedidos },
			{ path: "buscar-prestadores", Component: BuscarPrestadores },
			{ path: "pedido/:id", Component: PedidoDetalhes },
			{ path: "historico", Component: Historico },
			{ path: "orcamentos", Component: Orcamentos },
			{ path: "mensagens", Component: Mensagens },
			{ path: "notificacoes", Component: Notificacoes },
			{ path: "avaliacoes", Component: Avaliacoes },
			{ path: "meus-tickets", Component: MeusTickets },
			{ path: "meus-favoritos", Component: MeusFavoritos },
			{ path: "perfil", Component: Perfil },
		],
	},
	{
		path: "/prestador",
		Component: DashboardPrestadorLayout,
		children: [
			{ index: true, Component: DashboardPrestador },
			{ path: "servicos-disponiveis", Component: ServicosDisponiveis },
			{ path: "pedido/:id", Component: PedidoDetalhes },
			{ path: "minhas-propostas", Component: MinhasPropostas },
			{ path: "historico", Component: HistoricoPrestador },
			{ path: "mensagens", Component: Mensagens },
			{ path: "notificacoes", Component: Notificacoes },
			{ path: "avaliacoes", Component: Avaliacoes },
			{ path: "perfil", Component: Perfil },
		],
	},
	{
		path: "/admin",
		Component: AdminLayout,
		children: [
			{ index: true, Component: AdminDashboard },
			{ path: "usuarios", Component: AdminUsuarios },
			{ path: "pedidos", Component: AdminPedidos },
			{ path: "avaliacoes", Component: AdminAvaliacoes },
			{ path: "categorias", Component: AdminCategorias },
		],
	},
]);