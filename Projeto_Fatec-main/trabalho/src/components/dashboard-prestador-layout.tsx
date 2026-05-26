import { useEffect, useRef, useState } from "react";
import { Link, Navigate, Outlet, useLocation, useNavigate } from "react-router";
import {
  Bell,
  Briefcase,
  Leaf,
  LogOut,
  Menu,
  MessageSquare,
  Repeat,
  Send,
  Star,
  TrendingUp,
  User,
  X,
} from "lucide-react";

import { clearSessionUser, getSessionUser } from "../services/auth";
import { getActiveProfile } from "../services/auth";
import {
  getAvailableOrdersForProvider,
  getProviderProposals,
} from "../services/demo-orders";
import { getUnreadMessagesCount } from "../services/demo-messages";
import { getUnreadNotificationsCount } from "../services/demo-notifications";
import { Badge } from "./ui/badge";
import { Button } from "./ui/button";
import { SiteLogo } from "./site-logo";

const navItems = [
  { path: "/prestador", label: "Painel", icon: Briefcase },
  {
    path: "/prestador/servicos-disponiveis",
    label: "Servicos Disponiveis",
    icon: TrendingUp,
    badge: 0,
  },
  { path: "/prestador/minhas-propostas", label: "Minhas Propostas", icon: Send, badge: 0 },
  { path: "/prestador/mensagens", label: "Mensagens", icon: MessageSquare, badge: 0 },
  { path: "/prestador/notificacoes", label: "Notificacoes", icon: Bell, badge: 0 },
  { path: "/prestador/avaliacoes", label: "Avaliacoes", icon: Star, badge: 0 },
  { path: "/prestador/perfil", label: "Meu Perfil", icon: User },
];

export function DashboardPrestadorLayout() {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [unreadMessagesCount, setUnreadMessagesCount] = useState(0);
  const [unreadNotificationsCount, setUnreadNotificationsCount] = useState(0);
  const [availableCount, setAvailableCount] = useState(0);
  const [proposalsCount, setProposalsCount] = useState(0);

  const location = useLocation();
  const navigate = useNavigate();
  const user = getSessionUser();
  const activeProfile = getActiveProfile();
  const wasOnNotificationPage = useRef(false);

  useEffect(() => {
    if (!user?.id) {
      setAvailableCount(0);
      setProposalsCount(0);
      return;
    }

    void Promise.all([
      getAvailableOrdersForProvider(user.id),
      getProviderProposals(user.id),
    ]).then(([available, proposals]) => {
      setAvailableCount(available.length);
      setProposalsCount(proposals.filter((p) => p.status === "pending").length);
    });
  }, [user?.id]);

  useEffect(() => {
    setSidebarOpen(false);
  }, [location.pathname]);

  useEffect(() => {
    if (!user?.id) {
      setUnreadMessagesCount(0);
      setUnreadNotificationsCount(0);
      return;
    }

    let isMounted = true;

    void getUnreadMessagesCount(user.id)
      .then((count) => {
        if (isMounted) setUnreadMessagesCount(count);
      })
      .catch(() => {
        if (isMounted) setUnreadMessagesCount(0);
      });

    void getUnreadNotificationsCount(user, true)
      .then((count) => {
        if (isMounted) setUnreadNotificationsCount(count);
      })
      .catch(() => {
        if (isMounted) setUnreadNotificationsCount(0);
      });

    return () => {
      isMounted = false;
    };
  }, [user?.id, location.pathname]);

  useEffect(() => {
    const isNotificationPage = location.pathname === "/prestador/notificacoes";

    if (isNotificationPage && user?.id) {
      wasOnNotificationPage.current = true;

      let isMounted = true;
      const interval = setInterval(() => {
        void getUnreadNotificationsCount(user, true)
          .then((count) => {
            if (isMounted) setUnreadNotificationsCount(count);
          })
          .catch(() => {
            if (isMounted) setUnreadNotificationsCount(0);
          });
      }, 500);

      return () => {
        clearInterval(interval);
        isMounted = false;
      };
    }

    if (wasOnNotificationPage.current && !isNotificationPage && user?.id) {
      wasOnNotificationPage.current = false;
      void getUnreadNotificationsCount(user, true)
        .then((count) => {
          setUnreadNotificationsCount(count);
        })
        .catch(() => {
          setUnreadNotificationsCount(0);
        });
    }
  }, [location.pathname, user?.id]);

  const navItemsWithBadge = navItems.map((item) => {
    if (item.path === "/prestador/servicos-disponiveis") {
      return { ...item, badge: availableCount };
    }

    if (item.path === "/prestador/minhas-propostas") {
      return { ...item, badge: proposalsCount };
    }

    if (item.path === "/prestador/mensagens") {
      return { ...item, badge: unreadMessagesCount };
    }

    if (item.path === "/prestador/notificacoes") {
      return { ...item, badge: unreadNotificationsCount };
    }

    return item;
  });

  const isActive = (path: string) => location.pathname === path;

  const handleLogout = () => {
    clearSessionUser();
    navigate("/", { replace: true });
  };

  if (!user) {
    return <Navigate to="/" replace />;
  }

  if (activeProfile !== "prestador") {
    return <Navigate to="/selecionar-perfil" replace />;
  }

  return (
    <div className="min-h-screen bg-transparent">
      <header className="surface-glass sticky top-0 z-50 border-b shadow-[0_10px_32px_-28px_rgba(17,60,110,0.7)]">
        <div className="page-shell">
          <div className="flex min-h-[4.5rem] items-center justify-between gap-3">
            <div className="flex items-center gap-4">
              <button
                className="rounded-lg border border-orange-100 bg-white/80 p-2 lg:hidden"
                onClick={() => setSidebarOpen(!sidebarOpen)}
              >
                {sidebarOpen ? <X size={24} /> : <Menu size={24} />}
              </button>

              <Link to="/" className="flex items-center gap-2">
                <SiteLogo to="/" />
                <Badge className="border-orange-300 bg-orange-100 text-orange-800">
                  Prestador
                </Badge>
              </Link>
            </div>

            <div className="flex items-center gap-4">
              <div className="group hidden max-w-[250px] items-center rounded-full border border-orange-200 bg-orange-50 px-3 py-2 text-sm font-semibold text-orange-900 shadow-sm transition-all duration-300 hover:scale-[1.02] hover:shadow-md md:flex">
                <span className="mr-2 h-2.5 w-2.5 shrink-0 animate-pulse rounded-full bg-orange-500" />
                <User size={14} className="mr-2 shrink-0 text-orange-700" />
                <span className="truncate">{user.nome}</span>
              </div>

              <Link to="/projeto-ambiental">
                <Button className="hidden bg-orange-500 text-white shadow-md hover:bg-orange-600 sm:flex">
                  <Leaf size={16} className="mr-2" />
                  Projeto Ambiental
                </Button>
              </Link>

              <Link to="/selecionar-perfil">
                <Button variant="outline" className="hidden sm:flex">
                  <Repeat size={16} className="mr-2" />
                  Alternar Perfil
                </Button>
              </Link>

              <Button variant="ghost" onClick={handleLogout}>
                <LogOut size={20} />
                <span className="ml-2 hidden sm:inline">Sair</span>
              </Button>
            </div>
          </div>
        </div>
      </header>

      <div className="flex">
        <aside
          className={`${sidebarOpen ? "translate-x-0" : "-translate-x-full"} fixed inset-y-0 left-0 z-40 mt-[4.5rem] w-72 border-r border-orange-100 bg-white/95 shadow-[8px_0_30px_-24px_rgba(17,60,110,0.7)] transition-transform duration-300 ease-in-out lg:static lg:mt-0 lg:translate-x-0`}
        >
          <nav className="h-[calc(100vh-72px)] space-y-1 overflow-y-auto p-4">
            {navItemsWithBadge.map((item) => (
              <Link key={item.path} to={item.path} onClick={() => setSidebarOpen(false)}>
                <div
                  className={`flex items-center justify-between rounded-xl px-4 py-3 transition-all ${
                    isActive(item.path)
                      ? "bg-orange-600 text-white shadow-md"
                      : "text-gray-700 hover:bg-orange-50/80"
                  }`}
                >
                  <div className="flex items-center gap-3">
                    <item.icon size={20} />
                    <span className="font-medium">{item.label}</span>
                  </div>
                  {!!item.badge && !isActive(item.path) && (
                    <Badge className="bg-blue-500 text-white">{item.badge}</Badge>
                  )}
                </div>
              </Link>
            ))}
          </nav>
        </aside>

        {sidebarOpen && (
          <div
            className="fixed inset-0 z-30 mt-[4.5rem] bg-black/45 lg:hidden"
            onClick={() => setSidebarOpen(false)}
          />
        )}

        <main className="min-h-[calc(100vh-72px)] flex-1">
          <div key={`${location.pathname}${location.search}`} className="animate-enter min-h-[calc(100vh-72px)]">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
}
