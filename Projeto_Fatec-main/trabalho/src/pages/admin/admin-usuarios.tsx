import { useEffect, useMemo, useState } from "react";
import { Badge } from "../../components/ui/badge";
import { Button } from "../../components/ui/button";
import { Card } from "../../components/ui/card";
import { Input } from "../../components/ui/input";
import { getSessionUser } from "../../services/auth";
import {
  deleteUsuario,
  getAdminUsuarios,
  toggleUsuarioAtivo,
  updateUsuarioStatus,
} from "../../services/admin-api";
import type { Usuario } from "../../services/api";
import { Search, ShieldCheck, ToggleLeft, ToggleRight, Trash2 } from "lucide-react";

function resolveStatus(u: Usuario): number {
  if (typeof u.status === "number") return u.status;
  if (u.tipo === "prestador") return 1;
  if (u.tipo === "cliente") return 2;
  return 10;
}

function statusLabel(status: number): string {
  if (status === 1) return "Prestador";
  if (status === 2) return "Cliente";
  if (status === 10) return "Admin";
  if (status === 11) return "Admin Principal";
  return `Status ${status}`;
}

function statusTone(status: number): string {
  if (status === 1) return "bg-orange-100 text-orange-800";
  if (status === 2) return "bg-blue-100 text-blue-800";
  if (status === 10) return "bg-red-100 text-red-800";
  if (status === 11) return "bg-fuchsia-200 text-fuchsia-900";
  return "bg-gray-700 text-gray-300";
}

export function AdminUsuarios() {
  const user = getSessionUser();
  const isPrincipal = (user?.status ?? 0) === 11;

  const [usuarios, setUsuarios] = useState<Usuario[]>([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [actionId, setActionId] = useState<number | null>(null);

  const load = () => {
    if (!user?.id) return;
    void getAdminUsuarios(user.id)
      .then(setUsuarios)
      .catch(() => setUsuarios([]))
      .finally(() => setLoading(false));
  };

  useEffect(load, [user?.id]);

  const filtered = useMemo(() => {
    const term = search.toLowerCase();
    return usuarios.filter(
      (u) =>
        u.nome.toLowerCase().includes(term) ||
        u.email.toLowerCase().includes(term) ||
        String(resolveStatus(u)).includes(term) ||
        statusLabel(resolveStatus(u)).toLowerCase().includes(term),
    );
  }, [usuarios, search]);

  const handleToggle = async (uid: number) => {
    if (!user?.id) return;
    setActionId(uid);
    try {
      const updated = await toggleUsuarioAtivo(uid, user.id);
      setUsuarios((prev) => prev.map((u) => (u.id === uid ? { ...u, ativo: updated.ativo } : u)));
    } catch (e) {
      alert(e instanceof Error ? e.message : "Erro ao atualizar usuário");
    } finally {
      setActionId(null);
    }
  };

  const handlePromoteToAdmin = async (uid: number, nome: string) => {
    if (!user?.id) return;
    if (!window.confirm(`Tornar o usuário \"${nome}\" um ADMIN?`)) return;

    setActionId(uid);
    try {
      const updated = await updateUsuarioStatus(uid, 10, user.id);
      setUsuarios((prev) => prev.map((u) => (u.id === uid ? updated : u)));
    } catch (e) {
      alert(e instanceof Error ? e.message : "Erro ao promover usuário");
    } finally {
      setActionId(null);
    }
  };

  const handleDelete = async (uid: number, nome: string) => {
    if (!user?.id) return;
    if (!window.confirm(`Excluir usuário \"${nome}\"? Esta ação não pode ser desfeita.`)) return;

    setActionId(uid);
    try {
      await deleteUsuario(uid, user.id);
      setUsuarios((prev) => prev.filter((u) => u.id !== uid));
    } catch (e) {
      alert(e instanceof Error ? e.message : "Erro ao excluir usuário");
    } finally {
      setActionId(null);
    }
  };

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-white mb-1">Usuários</h1>
        <p className="text-gray-400 text-sm">{usuarios.length} usuários cadastrados</p>
      </div>

      <div className="mb-4 relative">
        <Search size={16} className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Buscar por nome, email ou status..."
          className="pl-9 bg-gray-800 border-gray-700 text-white placeholder:text-gray-500"
        />
      </div>

      {!isPrincipal && (
        <div className="mb-4 rounded-md border border-yellow-700 bg-yellow-950/40 p-3 text-xs text-yellow-200">
          Você está como ADMIN comum. Apenas ADMIN PRINCIPAL pode promover usuários para admin e bloquear outros admins.
        </div>
      )}

      {loading ? (
        <p className="text-gray-400">Carregando dados...</p>
      ) : (
        <Card className="bg-gray-800 border-gray-700 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-gray-700 text-gray-400 text-xs uppercase">
                  <th className="text-left px-4 py-3">ID</th>
                  <th className="text-left px-4 py-3">Nome</th>
                  <th className="text-left px-4 py-3">Email</th>
                  <th className="text-left px-4 py-3">Status</th>
                  <th className="text-left px-4 py-3">Ativo</th>
                  <th className="text-right px-4 py-3">Ações</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((u) => {
                  const status = resolveStatus(u);
                  const targetIsAdmin = status >= 10;
                  const canManageTarget = !targetIsAdmin || isPrincipal;
                  const canPromote = isPrincipal && status < 10;
                  const isSelf = u.id === user?.id;

                  return (
                    <tr key={u.id} className="border-b border-gray-700/50 hover:bg-gray-700/30 transition-colors">
                      <td className="px-4 py-3 text-gray-500">#{u.id}</td>
                      <td className="px-4 py-3 text-white font-medium">{u.nome}</td>
                      <td className="px-4 py-3 text-gray-300">{u.email}</td>
                      <td className="px-4 py-3">
                        <Badge className={`text-xs ${statusTone(status)}`}>
                          {status} - {statusLabel(status)}
                        </Badge>
                      </td>
                      <td className="px-4 py-3">
                        <Badge className={u.ativo ? "bg-green-900 text-green-300" : "bg-red-900 text-red-300"}>
                          {u.ativo ? "Ativo" : "Inativo"}
                        </Badge>
                      </td>
                      <td className="px-4 py-3 text-right">
                        <div className="flex items-center justify-end gap-2">
                          {canPromote && (
                            <Button
                              size="sm"
                              variant="ghost"
                              disabled={actionId === u.id}
                              onClick={() => void handlePromoteToAdmin(u.id, u.nome)}
                              className="text-gray-300 hover:text-blue-300 h-7 px-2"
                              title="Tornar Admin"
                            >
                              <ShieldCheck size={16} />
                            </Button>
                          )}

                          <Button
                            size="sm"
                            variant="ghost"
                            disabled={actionId === u.id || !canManageTarget || isSelf}
                            onClick={() => void handleToggle(u.id)}
                            className="text-gray-400 hover:text-white h-7 px-2"
                            title={u.ativo ? "Desativar" : "Ativar"}
                          >
                            {u.ativo ? <ToggleRight size={18} className="text-green-400" /> : <ToggleLeft size={18} />}
                          </Button>

                          <Button
                            size="sm"
                            variant="ghost"
                            disabled={actionId === u.id || !canManageTarget || isSelf}
                            onClick={() => void handleDelete(u.id, u.nome)}
                            className="text-gray-400 hover:text-red-400 h-7 px-2"
                            title="Excluir"
                          >
                            <Trash2 size={16} />
                          </Button>
                        </div>
                      </td>
                    </tr>
                  );
                })}

                {filtered.length === 0 && (
                  <tr>
                    <td colSpan={6} className="px-4 py-8 text-center text-gray-500">
                      Nenhum usuário encontrado
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </Card>
      )}
    </div>
  );
}
