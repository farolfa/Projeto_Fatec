import { useState } from "react";
import { Link, Navigate, Outlet, useLocation, useNavigate } from "react-router";
import {
	FileText,
	LayoutDashboard,
	LogOut,
	Menu,
	Shield,
	Star,
	Tag,
	Users,
	X,
} from "lucide-react";

import { clearSessionUser, getSessionUser, isAdmin } from "../services/auth";
import { Button } from "./ui/button";

const navItems = [
	{ path: "/admin", label: "Dashboard", icon: LayoutDashboard, exact: true },
	{ path: "/admin/usuarios", label: "Usuários", icon: Users },
	{ path: "/admin/pedidos", label: "Pedidos", icon: FileText },
	{ path: "/admin/avaliacoes", label: "Avaliações", icon: Star },
	{ path: "/admin/categorias", label: "Categorias", icon: Tag },
];

export function AdminLayout() {
	const [sidebarOpen, setSidebarOpen] = useState(false);
	const location = useLocation();
	const navigate = useNavigate();
	const user = getSessionUser();

	if (!user || !isAdmin(user)) {
		return <Navigate to="/" replace />;
	}

	const handleLogout = () => {
		clearSessionUser();
		navigate("/", { replace: true });
	};

	const isActive = (path: string, exact?: boolean) =>
		exact ? location.pathname === path : location.pathname.startsWith(path);

	return (
		<div className="min-h-screen flex bg-transparent">
			<aside
				className={`fixed inset-y-0 left-0 z-50 flex w-72 flex-col border-r border-slate-700/80 bg-slate-900 text-white transition-transform duration-300 ${
					sidebarOpen ? "translate-x-0" : "-translate-x-full"
				} lg:static lg:flex lg:translate-x-0`}
			>
				<div className="flex items-center justify-between border-b border-slate-700/80 px-4 py-4">
					<div className="flex items-center gap-2">
						<Shield size={22} className="text-orange-400" />
						<div>
							<div className="text-sm font-black tracking-tight">
								<span className="text-orange-400">FazTudo</span>
								<span className="text-white">JA</span>
							</div>
							<div className="text-[10px] uppercase tracking-widest text-slate-300">Admin</div>
						</div>
					</div>

					<Button
						variant="ghost"
						size="sm"
						className="text-slate-300 hover:text-white lg:hidden"
						onClick={() => setSidebarOpen(false)}
					>
						<X size={20} />
					</Button>
				</div>

				<nav className="flex-1 space-y-1 px-3 py-6">
					{navItems.map((item) => {
						const Icon = item.icon;
						const active = isActive(item.path, item.exact);

						return (
							<Link
								key={item.path}
								to={item.path}
								onClick={() => setSidebarOpen(false)}
								className={`flex items-center gap-3 rounded-xl px-3 py-3 text-sm font-semibold transition-colors ${
									active
										? "bg-orange-600 text-white shadow-md"
										: "text-slate-200 hover:bg-slate-800 hover:text-white"
								}`}
							>
								<Icon size={18} />
								{item.label}
							</Link>
						);
					})}
				</nav>

				<div className="border-t border-slate-700/80 p-4">
					<div className="mb-1 truncate text-xs text-slate-200">{user.nome}</div>
					<div className="mb-3 truncate text-xs text-slate-400">{user.email}</div>
					<Button
						variant="ghost"
						className="w-full justify-start text-slate-300 hover:bg-slate-800 hover:text-white"
						onClick={handleLogout}
					>
						<LogOut size={16} className="mr-2" />
						Sair
					</Button>
				</div>
			</aside>

			{sidebarOpen && (
				<div
					className="fixed inset-0 z-40 bg-black/60 lg:hidden"
					onClick={() => setSidebarOpen(false)}
				/>
			)}

			<div className="flex min-w-0 flex-1 flex-col">
				<header className="flex items-center gap-4 border-b border-gray-700 bg-gray-900 px-4 py-3 lg:hidden">
					<Button variant="ghost" className="text-slate-200" onClick={() => setSidebarOpen(true)}>
						<Menu size={20} />
					</Button>
					<span className="text-sm font-semibold text-white">Painel Admin</span>
				</header>

				<main className="flex-1 overflow-auto p-4 sm:p-6 lg:p-8">
					<div key={`${location.pathname}${location.search}`} className="animate-enter">
						<Outlet />
					</div>
				</main>
			</div>
		</div>
	);
}
