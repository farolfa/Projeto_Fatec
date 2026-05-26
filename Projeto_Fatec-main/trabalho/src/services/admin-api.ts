import type { AvaliacaoApi, Categoria, PedidoFull, Usuario } from "./api";

const BASE = "/api/admin";

async function request<T>(path: string, adminId: number, options: RequestInit = {}): Promise<T> {
  const sep = path.includes("?") ? "&" : "?";
  const res = await fetch(`${BASE}${path}${sep}adminId=${adminId}`, {
    headers: {
      "Content-Type": "application/json",
      ...options.headers,
    },
    ...options,
  });

  const text = await res.text();
  let body: unknown = null;
  try {
    body = JSON.parse(text);
  } catch {
    body = text;
  }

  if (!res.ok) {
    throw new Error(typeof body === "string" ? body : `Erro ${res.status}`);
  }

  return body as T;
}

export interface AdminStats {
  totalUsuarios: number;
  totalPedidos: number;
  totalAvaliacoes: number;
  totalCategorias: number;
  totalClientes: number;
  totalPrestadores: number;
}

export async function getAdminStats(adminId: number): Promise<AdminStats> {
  return request("/stats", adminId);
}

export async function getAdminUsuarios(adminId: number): Promise<Usuario[]> {
  return request("/usuarios", adminId);
}

export async function toggleUsuarioAtivo(userId: number, adminId: number): Promise<Usuario> {
  return request(`/usuario/${userId}/ativo`, adminId, {
    method: "PUT",
  });
}

export async function updateUsuarioStatus(userId: number, status: number, adminId: number): Promise<Usuario> {
  return request(`/usuario/${userId}/status`, adminId, {
    method: "PUT",
    body: JSON.stringify({ status }),
  });
}

export async function deleteUsuario(userId: number, adminId: number): Promise<void> {
  await request(`/usuario/${userId}`, adminId, {
    method: "DELETE",
  });
}

export async function getAdminPedidos(adminId: number): Promise<PedidoFull[]> {
  return request("/pedidos", adminId);
}

export async function deleteAdminPedido(pedidoId: number, adminId: number): Promise<void> {
  await request(`/pedido/${pedidoId}`, adminId, {
    method: "DELETE",
  });
}

export async function getAdminAvaliacoes(adminId: number): Promise<AvaliacaoApi[]> {
  return request("/avaliacoes", adminId);
}

export async function deleteAdminAvaliacao(avaliacaoId: number, adminId: number): Promise<void> {
  await request(`/avaliacao/${avaliacaoId}`, adminId, {
    method: "DELETE",
  });
}

export async function createAdminCategoria(nome: string, adminId: number): Promise<Categoria> {
  return request("/categoria", adminId, {
    method: "POST",
    body: JSON.stringify({ nome }),
  });
}

export async function deleteAdminCategoria(categoriaId: number, adminId: number): Promise<void> {
  await request(`/categoria/${categoriaId}`, adminId, {
    method: "DELETE",
  });
}
