const API_BASE_URL = "/api";
const API_REQUEST_TIMEOUT_MS = 15000;

const MOJIBAKE_PATTERN = /(Ã.|Â.|â[\u0080-\u00BF]|ï¿½)/;

function decodeMojibake(value: string): string {
  if (!MOJIBAKE_PATTERN.test(value)) {
    return value;
  }

  try {
    const bytes = Uint8Array.from(value, (char) => char.charCodeAt(0));
    return new TextDecoder("utf-8").decode(bytes);
  } catch {
    return value;
  }
}

function normalizeApiText<T>(value: T): T {
  if (typeof value === "string") {
    return decodeMojibake(value) as T;
  }

  if (Array.isArray(value)) {
    return value.map((item) => normalizeApiText(item)) as T;
  }

  if (value && typeof value === "object") {
    const entries = Object.entries(value as Record<string, unknown>).map(
      ([key, nestedValue]) => [key, normalizeApiText(nestedValue)],
    );
    return Object.fromEntries(entries) as T;
  }

  return value;
}

export interface Categoria {
  id: number;
  nome: string;
}

export interface Usuario {
  id: number;
  nome: string;
  email: string;
  tipo: string;
  status?: number;
  cpf?: string;
  telefone?: string;
  endereco?: string;
  estado?: string;
  cep?: string;
  bio?: string;
  foto?: string;
  cidade?: string;
  ativo: boolean;
}

export interface LoginPayload {
  email: string;
  senha: string;
}

export interface RegisterPayload {
  nome: string;
  email: string;
  senha: string;
  cpf: string;
  telefone: string;
  endereco: string;
  cidade: string;
  estado: string;
  cep: string;
  foto?: string;
  tipo: "cliente" | "prestador";
}

export interface Pedido {
  id: number;
  idUsuario: number;
  idServico: number;
  titulo: string;
  descricao: string;
  localizacao: string;
  status: string;
  idEndereco: number;
}

export interface PedidoFull {
  id: number;
  dataCriacao?: string;
  usuario?: {
    id: number;
    nome?: string;
    email?: string;
    telefone?: string;
    foto?: string;
  };
  servico?: {
    id: number;
    titulo?: string;
    categoria?: { nome?: string };
  };
  titulo: string;
  descricao: string;
  localizacao: string;
  status: string;
  contatoNome?: string;
  contatoEmail?: string;
  contatoTelefone?: string;
  clienteConfirmouConclusao?: boolean;
  prestadorConfirmouConclusao?: boolean;
}

export interface PropostaFull {
  id: number;
  pedido?: {
    id: number;
    titulo?: string;
    usuario?: { id: number; nome?: string };
  };
  prazoEntrega?: string;
  dataCriacao?: string;
  prestador?: {
    id: number;
    nome?: string;
    foto?: string;
  };
  precoProposto: number;
  status: string;
  mensagem?: string;
}

export interface PedidoBackend {
  id: number;
  usuario?: { id: number };
  servico?: { id: number };
  endereco?: { id: number };
  titulo: string;
  descricao: string;
  localizacao: string;
  status: string;
  contatoNome?: string;
  contatoEmail?: string;
  contatoTelefone?: string;
}

export interface CreatePedidoBackendPayload {
  usuario: { id: number };
  servico?: { id: number };
  endereco?: { id: number };
  titulo: string;
  descricao: string;
  localizacao: string;
  status: string;
  contatoNome?: string;
  contatoEmail?: string;
  contatoTelefone?: string;
}

export interface PropostaBackend {
  id: number;
  pedido?: { id: number };
  prestador?: { id: number };
  precoProposto: number;
  status: string;
  mensagem?: string;
}

export interface CreatePropostaBackendPayload {
  pedido: { id: number };
  prestador: { id: number };
  precoProposto: number;
  status: string;
  mensagem?: string;
  prazoEntrega?: string;
}

export interface AvaliacaoApi {
  id: number;
  avaliador?: Usuario;
  avaliado?: Usuario;
  nota: number;
  comentario?: string;
  data?: string;
}

export interface CreateAvaliacaoPayload {
  avaliador: { id: number };
  avaliado: { id: number };
  nota: number;
  comentario?: string;
  data?: string;
}

export interface FavoritoApi {
  id: number;
  usuario?: { id: number };
  prestadorId: number;
  prestadorNome: string;
  prestadorFoto?: string;
  savedAt?: string;
}

export interface CreateFavoritoPayload {
  usuario: { id: number };
  prestadorId: number;
  prestadorNome: string;
  prestadorFoto?: string;
}

class ApiService {
  private async request<T>(endpoint: string, options?: RequestInit): Promise<T> {
    const controller = new AbortController();
    const timeoutId = window.setTimeout(() => controller.abort(), API_REQUEST_TIMEOUT_MS);

    let response: Response;
    try {
      response = await fetch(`${API_BASE_URL}${endpoint}`, {
        headers: {
          "Content-Type": "application/json",
          ...options?.headers,
        },
        ...options,
        signal: options?.signal ?? controller.signal,
      });
    } catch (error) {
      if (error instanceof DOMException && error.name === "AbortError") {
        throw new Error("Tempo limite da requisicao excedido");
      }

      throw error;
    } finally {
      window.clearTimeout(timeoutId);
    }

    const responseText = await response.text();
    let parsedBody: unknown = null;

    if (responseText) {
      try {
        parsedBody = JSON.parse(responseText);
      } catch {
        parsedBody = responseText;
      }
    }

    if (!response.ok) {
      const message =
        typeof parsedBody === "string"
          ? parsedBody
          : (parsedBody as { message?: string })?.message ||
            `API Error: ${response.status} ${response.statusText}`;
      throw new Error(message);
    }

    return normalizeApiText(parsedBody as T);
  }

  async getCategorias(): Promise<Categoria[]> {
    return this.request("/categoria");
  }

  async getUsuarios(): Promise<Usuario[]> {
    return this.request("/usuario");
  }

  async getUsuario(id: number): Promise<Usuario> {
    return this.request(`/usuario/${id}`);
  }

  async login(payload: LoginPayload): Promise<Usuario> {
    return this.request("/usuario/login", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  }

  async register(payload: RegisterPayload): Promise<Usuario> {
    return this.request("/usuario/register", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  }

  async updateUsuario(id: number, payload: Partial<Usuario> & { senha?: string }): Promise<Usuario> {
    return this.request(`/usuario/${id}`, {
      method: "PUT",
      body: JSON.stringify(payload),
    });
  }

  async switchPerfil(id: number, tipo: "cliente" | "prestador"): Promise<Usuario> {
    return this.request(`/usuario/${id}/perfil`, {
      method: "PUT",
      body: JSON.stringify({ tipo }),
    });
  }

  async getPedidos(): Promise<Pedido[]> {
    return this.request("/pedido");
  }

  async getPedidosBackend(): Promise<PedidoBackend[]> {
    return this.request("/pedido");
  }

  async getPedidosByUser(usuarioId: number): Promise<PedidoFull[]> {
    return this.request(`/pedido/me?usuarioId=${usuarioId}`);
  }

  async getPedidosAbertos(prestadorId: number): Promise<PedidoFull[]> {
    return this.request(`/pedido/abertos?prestadorId=${prestadorId}`);
  }

  async getPedidoById(id: number): Promise<PedidoFull> {
    return this.request(`/pedido/${id}`);
  }

  async createPedido(pedido: Omit<Pedido, "id">): Promise<Pedido> {
    return this.request("/pedido", {
      method: "POST",
      body: JSON.stringify(pedido),
    });
  }

  async createPedidoBackend(payload: CreatePedidoBackendPayload): Promise<PedidoFull> {
    return this.request("/pedido", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  }

  async updatePedido(id: number, payload: Partial<PedidoFull>): Promise<PedidoFull> {
    return this.request(`/pedido/${id}`, {
      method: "PUT",
      body: JSON.stringify(payload),
    });
  }

  async deletePedido(id: number): Promise<void> {
    await this.request(`/pedido/${id}`, { method: "DELETE" });
  }

  async confirmarConclusao(id: number, tipo: string): Promise<PedidoFull> {
    return this.request(`/pedido/${id}/confirmar-conclusao?tipo=${tipo}`, {
      method: "POST",
    });
  }

  async getPropostasBackend(): Promise<PropostaFull[]> {
    return this.request("/proposta");
  }

  async getPropostasByPedido(pedidoId: number): Promise<PropostaFull[]> {
    return this.request(`/proposta/pedido/${pedidoId}`);
  }

  async getPropostasByPrestador(prestadorId: number): Promise<PropostaFull[]> {
    return this.request(`/proposta/prestador/${prestadorId}`);
  }

  async createPropostaBackend(payload: CreatePropostaBackendPayload): Promise<PropostaFull> {
    return this.request("/proposta", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  }

  async updateProposta(
    id: number,
    payload: { precoProposto?: number; mensagem?: string; status?: string },
  ): Promise<PropostaFull> {
    return this.request(`/proposta/${id}`, {
      method: "PUT",
      body: JSON.stringify(payload),
    });
  }

  async aceitarProposta(id: number): Promise<PropostaFull> {
    return this.request(`/proposta/${id}/aceitar`, { method: "POST" });
  }

  async deleteProposta(id: number): Promise<void> {
    await this.request(`/proposta/${id}`, { method: "DELETE" });
  }

  async getAvaliacoes(): Promise<AvaliacaoApi[]> {
    return this.request("/avaliacao");
  }

  async getAvaliacoesByAvaliado(avaliadoId: number): Promise<AvaliacaoApi[]> {
    return this.request(`/avaliacao/avaliado/${avaliadoId}`);
  }

  async getAvaliacoesByAvaliador(avaliadorId: number): Promise<AvaliacaoApi[]> {
    return this.request(`/avaliacao/avaliador/${avaliadorId}`);
  }

  async getRatingMedia(avaliadoId: number): Promise<{ media: number; total: number }> {
    return this.request(`/avaliacao/avaliado/${avaliadoId}/media`);
  }

  async jaAvaliou(avaliadorId: number, avaliadoId: number): Promise<boolean> {
    const res = await this.request<{ jaAvaliou: boolean }>(
      `/avaliacao/ja-avaliou?avaliadorId=${avaliadorId}&avaliadoId=${avaliadoId}`,
    );
    return res.jaAvaliou;
  }

  async createAvaliacao(payload: CreateAvaliacaoPayload): Promise<AvaliacaoApi> {
    return this.request("/avaliacao", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  }

  async getFavoritos(usuarioId: number): Promise<FavoritoApi[]> {
    return this.request(`/favorito?usuarioId=${usuarioId}`);
  }

  async addFavorito(payload: CreateFavoritoPayload): Promise<FavoritoApi> {
    return this.request("/favorito", {
      method: "POST",
      body: JSON.stringify(payload),
    });
  }

  async removeFavorito(id: number): Promise<void> {
    await this.request(`/favorito/${id}`, { method: "DELETE" });
  }
}

export const api = new ApiService();
