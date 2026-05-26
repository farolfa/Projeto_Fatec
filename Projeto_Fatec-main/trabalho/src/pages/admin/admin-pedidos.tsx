import { useEffect, useState } from "react";
import { Card } from "../../components/ui/card";
import { Badge } from "../../components/ui/badge";
import { Button } from "../../components/ui/button";
import { Input } from "../../components/ui/input";
import { Search, Trash2 } from "lucide-react";
import { getSessionUser } from "../../services/auth";
import {
  getAdminPedidos,
  deleteAdminPedido,
} from "../../services/admin-api";

interface PedidoAdmin {
  id: number;
  titulo: string;
  descricao: string;
  status: string;
  dataCriacao: string;
  clienteId: number;
  categoriaId: number;
}

const statusColor: Record<string, string> = {
  aberto: "bg-blue-900 text-blue-300",
  em_andamento: "bg-yellow-900 text-yellow-300",
  concluido: "bg-green-900 text-green-300",
  cancelado: "bg-red-900 text-red-300",
};

const statusLabel: Record<string, string> = {
  aberto: "Aberto",
  em_andamento: "Em andamento",
  concluido: "Concluído",
  cancelado: "Cancelado",
};

export function AdminPedidos() {
  const user = getSessionUser();
  const [pedidos, setPedidos] = useState<PedidoAdmin[]>([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);
  const [actionId, setActionId] = useState<number | null>(null);

  const load = () => {
    if (!user?.id) return;
    void getAdminPedidos(user.id)
      .then((data) => setPedidos(data as unknown as PedidoAdmin[]))
      .catch(() => setPedidos([]))
      .finally(() => setLoading(false));
  };

  useEffect(load, [user?.id]);

  const filtered = pedidos.filter(
    (p) =>
      p.titulo.toLowerCase().includes(search.toLowerCase()) ||
      String(p.id).includes(search) ||
      (p.status ?? "").toLowerCase().includes(search.toLowerCase())
  );

  const handleDelete = async (id: number, titulo: string) => {
    if (!user?.id) return;
    if (
      !window.confirm(
        `Excluir pedido "${titulo}"? Esta ação não pode ser desfeita.`
      )
    )
      return;

    setActionId(id);
    try {
      await deleteAdminPedido(id, user.id);
      setPedidos((prev) => prev.filter((p) => p.id !== id));
    } catch (e) {
      alert(e instanceof Error ? e.message : "Erro ao excluir");
    } finally {
      setActionId(null);
    }
  };

  return (
    <div>
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-white mb-1">Pedidos</h1>
        <p className="text-gray-400 text-sm">{pedidos.length} pedidos no sistema</p>
      </div>

      <div className="mb-4 relative">
        <Search
          size={16}
          className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400"
        />
        <Input
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Buscar por título, ID ou status..."
          className="pl-9 bg-gray-800 border-gray-700 text-white placeholder:text-gray-500"
        />
      </div>

      {loading ? (
        <p className="text-gray-400">Carregando dados...</p>
      ) : (
        <Card className="bg-gray-800 border-gray-700 overflow-hidden">
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead>
                <tr className="border-b border-gray-700 text-gray-400 text-xs uppercase">
                  <th className="text-left px-4 py-3">ID</th>
                  <th className="text-left px-4 py-3">Título</th>
                  <th className="text-left px-4 py-3">Status</th>
                  <th className="text-left px-4 py-3">Cliente ID</th>
                  <th className="text-left px-4 py-3">Data</th>
                  <th className="text-right px-4 py-3">Ações</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((p) => (
                  <tr
                    key={p.id}
                    className="border-b border-gray-700/50 hover:bg-gray-700/30 transition-colors"
                  >
                    <td className="px-4 py-3 text-gray-500">#{p.id}</td>
                    <td className="px-4 py-3 text-white font-medium max-w-xs truncate">
                      {p.titulo}
                    </td>
                    <td className="px-4 py-3">
                      <Badge
                        className={`text-xs ${
                          statusColor[p.status] ??
                          "bg-gray-700 text-gray-300"
                        }`}
                      >
                        {statusLabel[p.status] ?? p.status}
                      </Badge>
                    </td>
                    <td className="px-4 py-3 text-gray-300">{p.clienteId}</td>
                    <td className="px-4 py-3 text-gray-400 text-xs">
                      {p.dataCriacao
                        ? new Date(p.dataCriacao).toLocaleDateString("pt-BR")
                        : "—"}
                    </td>
                    <td className="px-4 py-3 text-right">
                      <Button
                        size="sm"
                        variant="ghost"
                        disabled={actionId === p.id}
                        onClick={() => handleDelete(p.id, p.titulo)}
                        className="text-gray-400 hover:text-red-400 h-7 px-2"
                        title="Excluir"
                      >
                        <Trash2 size={16} />
                      </Button>
                    </td>
                  </tr>
                ))}
                {filtered.length === 0 && (
                  <tr>
                    <td
                      colSpan={6}
                      className="px-4 py-8 text-center text-gray-500"
                    >
                      Nenhum pedido encontrado
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
