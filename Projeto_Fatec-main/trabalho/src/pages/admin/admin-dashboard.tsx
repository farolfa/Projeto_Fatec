import { useEffect, useMemo, useState } from "react";
import { Link } from "react-router";
import {
  FileText,
  Search,
  ShieldCheck,
  Star,
  Tag,
  ToggleLeft,
  ToggleRight,
  Trash2,
  TrendingUp,
  Users,
} from "lucide-react";

import { Badge } from "../../components/ui/badge";
import { Button } from "../../components/ui/button";
import { Card } from "../../components/ui/card";
import { Input } from "../../components/ui/input";
import { getSessionUser } from "../../services/auth";
import {
  deleteAdminPedido,
  deleteUsuario,
  getAdminPedidos,
  getAdminStats,
  getAdminUsuarios,
  toggleUsuarioAtivo,
  type AdminStats,
} from "../../services/admin-api";
import type { PedidoFull, Usuario } from "../../services/api";

const statusColor: Record<string, string> = {
  aberto: "bg-blue-900 text-blue-300",
  em_andamento: "bg-yellow-900 text-yellow-300",
  concluido: "bg-green-900 text-green-300",
  cancelado: "bg-red-900 text-red-300",
};

const statusLabel: Record<string, string> = {
  aberto: "Aberto",
  em_andamento: "Em andamento",
  concluido: "Concluido",
  cancelado: "Cancelado",
};

function resolveUserStatus(u: Usuario): number {
  if (typeof u.status === "number") return u.status;
  if (u.tipo === "prestador") return 1;
  if (u.tipo === "cliente") return 2;
  return 10;
}

function userStatusLabel(status: number): string {
  if (status === 1) return "Prestador";
  if (status === 2) return "Cliente";
  if (status === 10) return "Admin";
  if (status === 11) return "Admin Principal";
  return `Status ${status}`;
}

function userStatusTone(status: number): string {
  if (status === 1) return "bg-orange-100 text-orange-800";
  if (status === 2) return "bg-blue-100 text-blue-800";
  if (status === 10) return "bg-red-100 text-red-800";
  if (status === 11) return "bg-fuchsia-200 text-fuchsia-900";
  return "bg-gray-700 text-gray-300";
}

export function AdminDashboard() {
  const user = getSessionUser();
  const isPrincipal = (user?.status ?? 0) === 11;

  const [stats, setStats] = useState<AdminStats | null>(null);
  const [usuarios, setUsuarios] = useState<Usuario[]>([]);
  const [pedidos, setPedidos] = useState<PedidoFull[]>([]);

  const [loading, setLoading] = useState(true);
  const [userActionId, setUserActionId] = useState<number | null>(null);
  const [pedidoActionId, setPedidoActionId] = useState<number | null>(null);

  const [userSearch, setUserSearch] = useState("");
  const [pedidoSearch, setPedidoSearch] = useState("");

  const reload = async () => {
    if (!user?.id) return;
    setLoading(true);
    try {
      const [statsData, usuariosData, pedidosData] = await Promise.all([
        getAdminStats(user.id),
        getAdminUsuarios(user.id),
        getAdminPedidos(user.id),
      ]);
      setStats(statsData);
      setUsuarios(usuariosData);
      setPedidos(pedidosData);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void reload();
  }, [user?.id]);

  const filteredUsers = useMemo(() => {
    const term = userSearch.toLowerCase();
    return usuarios
      .filter(
        (u) =>
          u.nome.toLowerCase().includes(term) ||
          u.email.toLowerCase().includes(term) ||
          u.tipo.toLowerCase().includes(term),
      )
      .slice(0, 8);
  }, [usuarios, userSearch]);

  const filteredPedidos = useMemo(() => {
    const term = pedidoSearch.toLowerCase();
    return pedidos
      .filter(
        (p) =>
          (p.titulo ?? "").toLowerCase().includes(term) ||
          String(p.id).includes(pedidoSearch) ||
          (p.status ?? "").toLowerCase().includes(term) ||
          (p.usuario?.nome ?? "").toLowerCase().includes(term),
      )
      .slice(0, 8);
  }, [pedidos, pedidoSearch]);

  const cards = stats
    ? [
        {
          label: "Total de Usuarios",
          value: stats.totalUsuarios,
          icon: Users,
          color: "bg-blue-100 text-blue-700",
          link: "/admin/usuarios",
        },
        {
          label: "Clientes",
          value: stats.totalClientes,
          icon: TrendingUp,
          color: "bg-green-100 text-green-700",
          link: "/admin/usuarios",
        },
        {
          label: "Prestadores",
          value: stats.totalPrestadores,
          icon: ShieldCheck,
          color: "bg-orange-100 text-orange-700",
          link: "/admin/usuarios",
        },
        {
          label: "Pedidos",
          value: stats.totalPedidos,
          icon: FileText,
          color: "bg-purple-100 text-purple-700",
          link: "/admin/pedidos",
        },
        {
          label: "Avaliacoes",
          value: stats.totalAvaliacoes,
          icon: Star,
          color: "bg-yellow-100 text-yellow-700",
          link: "/admin/avaliacoes",
        },
        {
          label: "Categorias",
          value: stats.totalCategorias,
          icon: Tag,
          color: "bg-red-100 text-red-700",
          link: "/admin/categorias",
        },
      ]
    : [];

  const handleToggleUser = async (uid: number) => {
    if (!user?.id) return;
    setUserActionId(uid);
    try {
      const updated = await toggleUsuarioAtivo(uid, user.id);
      setUsuarios((prev) => prev.map((u) => (u.id === uid ? { ...u, ativo: updated.ativo } : u)));
    } finally {
      setUserActionId(null);
    }
  };

  const handleDeleteUser = async (uid: number, nome: string) => {
    if (!user?.id) return;
    if (!window.confirm(`Excluir usuario \"${nome}\"? Esta acao nao pode ser desfeita.`)) return;

    setUserActionId(uid);
    try {
      await deleteUsuario(uid, user.id);
      setUsuarios((prev) => prev.filter((u) => u.id !== uid));
      setStats((prev) =>
        prev
          ? {
              ...prev,
              totalUsuarios: Math.max(0, prev.totalUsuarios - 1),
              totalClientes: prev.totalClientes - (usuarios.find((u) => u.id === uid)?.tipo === "cliente" ? 1 : 0),
              totalPrestadores:
                prev.totalPrestadores - (usuarios.find((u) => u.id === uid)?.tipo === "prestador" ? 1 : 0),
            }
          : prev,
      );
    } catch (e) {
      alert(e instanceof Error ? e.message : "Erro ao excluir usuario");
    } finally {
      setUserActionId(null);
    }
  };

  const handleDeletePedido = async (pid: number, titulo: string) => {
    if (!user?.id) return;
    if (!window.confirm(`Excluir pedido \"${titulo}\"? Esta acao nao pode ser desfeita.`)) return;

    setPedidoActionId(pid);
    try {
      await deleteAdminPedido(pid, user.id);
      setPedidos((prev) => prev.filter((p) => p.id !== pid));
      setStats((prev) => (prev ? { ...prev, totalPedidos: Math.max(0, prev.totalPedidos - 1) } : prev));
    } catch (e) {
      alert(e instanceof Error ? e.message : "Erro ao excluir pedido");
    } finally {
      setPedidoActionId(null);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-2xl font-bold text-white mb-1">Painel Administrativo</h1>
          <p className="text-gray-400 text-sm">Bloqueie, exclua usuarios e acompanhe pedidos em uma unica tela.</p>
        </div>
        <Button onClick={() => void reload()} className="bg-orange-600 hover:bg-orange-700 text-white">
          Atualizar dados
        </Button>
      </div>

      {loading ? (
        <div className="text-gray-400">Carregando dados...</div>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-5">
            {cards.map((card) => {
              const Icon = card.icon;
              return (
                <Link to={card.link} key={card.label}>
                  <Card className="p-5 bg-gray-800 border-gray-700 hover:border-gray-600 transition-all cursor-pointer">
                    <div className="flex items-center gap-4">
                      <div className={`w-12 h-12 rounded-xl flex items-center justify-center ${card.color}`}>
                        <Icon size={24} />
                      </div>
                      <div>
                        <p className="text-gray-400 text-xs mb-0.5">{card.label}</p>
                        <p className="text-white text-2xl font-bold">{card.value}</p>
                      </div>
                    </div>
                  </Card>
                </Link>
              );
            })}
          </div>

          <div className="grid grid-cols-1 xl:grid-cols-2 gap-6">
            <Card className="bg-gray-800 border-gray-700 p-4">
              <div className="mb-3 flex items-center justify-between gap-3">
                <h2 className="text-white font-semibold">Usuarios (acoes rapidas)</h2>
                <Link to="/admin/usuarios" className="text-xs text-orange-400 hover:text-orange-300">
                  Ver todos
                </Link>
              </div>

              <div className="mb-4 relative">
                <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                <Input
                  value={userSearch}
                  onChange={(e) => setUserSearch(e.target.value)}
                  placeholder="Buscar usuario por nome, email ou status..."
                  className="pl-9 bg-gray-900 border-gray-700 text-white placeholder:text-gray-500"
                />
              </div>

              <div className="space-y-2">
                {filteredUsers.map((u) => {
                  const targetStatus = resolveUserStatus(u);
                  const canManageTarget = targetStatus < 10 || isPrincipal;
                  const isSelf = u.id === user?.id;

                  return (
                  <div key={u.id} className="flex items-center justify-between rounded-lg border border-gray-700 px-3 py-2">
                    <div className="min-w-0">
                      <p className="truncate text-sm font-medium text-white">{u.nome}</p>
                      <p className="truncate text-xs text-gray-400">{u.email}</p>
                    </div>
                    <div className="flex items-center gap-2 pl-3">
                      <Badge className={`text-xs ${userStatusTone(targetStatus)}`}>
                        {targetStatus} - {userStatusLabel(targetStatus)}
                      </Badge>
                      <Badge className={u.ativo ? "bg-green-900 text-green-300" : "bg-red-900 text-red-300"}>
                        {u.ativo ? "Ativo" : "Inativo"}
                      </Badge>

                      <Button
                        size="sm"
                        variant="ghost"
                        disabled={userActionId === u.id || !canManageTarget || isSelf}
                        onClick={() => void handleToggleUser(u.id)}
                        className="text-gray-300 hover:text-white h-7 px-2"
                        title={u.ativo ? "Desativar" : "Ativar"}
                      >
                        {u.ativo ? <ToggleRight size={18} className="text-green-400" /> : <ToggleLeft size={18} />}
                      </Button>
                      <Button
                        size="sm"
                        variant="ghost"
                        disabled={userActionId === u.id || !canManageTarget || isSelf}
                        onClick={() => void handleDeleteUser(u.id, u.nome)}
                        className="text-gray-300 hover:text-red-400 h-7 px-2"
                        title="Excluir"
                      >
                        <Trash2 size={16} />
                      </Button>
                    </div>
                  </div>
                )})}
                {filteredUsers.length === 0 && <p className="text-sm text-gray-500">Nenhum usuario encontrado.</p>}
              </div>
            </Card>

            <Card className="bg-gray-800 border-gray-700 p-4">
              <div className="mb-3 flex items-center justify-between gap-3">
                <h2 className="text-white font-semibold">Pedidos (visao geral)</h2>
                <Link to="/admin/pedidos" className="text-xs text-orange-400 hover:text-orange-300">
                  Ver todos
                </Link>
              </div>

              <div className="mb-4 relative">
                <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
                <Input
                  value={pedidoSearch}
                  onChange={(e) => setPedidoSearch(e.target.value)}
                  placeholder="Buscar pedido por titulo, ID, status ou cliente..."
                  className="pl-9 bg-gray-900 border-gray-700 text-white placeholder:text-gray-500"
                />
              </div>

              <div className="space-y-2">
                {filteredPedidos.map((p) => (
                  <div key={p.id} className="flex items-center justify-between rounded-lg border border-gray-700 px-3 py-2">
                    <div className="min-w-0">
                      <p className="truncate text-sm font-medium text-white">#{p.id} - {p.titulo}</p>
                      <p className="truncate text-xs text-gray-400">
                        Cliente: {p.usuario?.nome ?? "Nao informado"}
                      </p>
                    </div>
                    <div className="flex items-center gap-2 pl-3">
                      <Badge className={`text-xs ${statusColor[p.status] ?? "bg-gray-700 text-gray-300"}`}>
                        {statusLabel[p.status] ?? p.status}
                      </Badge>
                      <Button
                        size="sm"
                        variant="ghost"
                        disabled={pedidoActionId === p.id}
                        onClick={() => void handleDeletePedido(p.id, p.titulo)}
                        className="text-gray-300 hover:text-red-400 h-7 px-2"
                        title="Excluir"
                      >
                        <Trash2 size={16} />
                      </Button>
                    </div>
                  </div>
                ))}
                {filteredPedidos.length === 0 && <p className="text-sm text-gray-500">Nenhum pedido encontrado.</p>}
              </div>
            </Card>
          </div>
        </>
      )}
    </div>
  );
}
