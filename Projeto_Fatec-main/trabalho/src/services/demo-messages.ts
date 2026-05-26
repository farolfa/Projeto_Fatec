const API_BASE_URL = "/api/mensagem";

export interface DemoConversation {
	id: number;
	orderId: number;
	serviceTitle: string;
	clientUserId: number;
	clientName: string;
	clientPhoto?: string;
	providerUserId: number;
	providerName: string;
	providerPhoto?: string;
	lastMessage: string;
	lastMessageTime: string;
	unreadCount: number;
}

export interface DemoChatMessage {
	id: number;
	conversationId: number;
	senderUserId: number;
	text: string;
	sentAt: string;
}

interface EnsureConversationInput {
	orderId: number;
	serviceTitle: string;
	clientUserId: number;
	providerUserId: number;
	clientName?: string;
	providerName?: string;
	clientPhoto?: string;
	providerPhoto?: string;
}

class RequestError extends Error {
	status: number;

	constructor(message: string, status: number) {
		super(message);
		this.status = status;
		this.name = "RequestError";
	}
}

function extractErrorMessage(parsedBody: unknown): string | null {
	if (typeof parsedBody === "string" && parsedBody.trim()) {
		return parsedBody;
	}

	if (!parsedBody || typeof parsedBody !== "object") {
		return null;
	}

	const errorBody = parsedBody as {
		message?: string;
		error?: string;
		detail?: string;
		title?: string;
	};

	return errorBody.message || errorBody.error || errorBody.detail || errorBody.title || null;
}

async function request<T>(endpoint: string, options?: RequestInit): Promise<T> {
	const hasBody = options?.body !== undefined;

	const response = await fetch(`${API_BASE_URL}${endpoint}`, {
		headers: {
			...options?.headers,
			...(hasBody ? { "Content-Type": "application/json" } : {}),
		},
		...options,
	});

	if (response.status === 204) {
		return undefined as T;
	}

	const responseText = await response.text();
	let parsedBody: unknown = null;

	if (responseText) {
		const contentType = response.headers.get("content-type") || "";

		if (contentType.includes("application/json")) {
			try {
				parsedBody = JSON.parse(responseText);
			} catch {
				parsedBody = responseText;
			}
		} else {
			parsedBody = responseText;
		}
	}

	if (!response.ok) {
		const message =
			extractErrorMessage(parsedBody) ||
			`Erro ao processar mensagens (${response.status} ${response.statusText}).`;
		throw new RequestError(message, response.status);
	}

	return parsedBody as T;
}

export async function ensureConversation(input: EnsureConversationInput): Promise<DemoConversation> {
	const normalizedInput = {
		orderId: Number(input.orderId),
		serviceTitle: String(input.serviceTitle || "").trim(),
		clientUserId: Number(input.clientUserId),
		providerUserId: Number(input.providerUserId),
	};

	const hasInvalidId =
		!Number.isFinite(normalizedInput.orderId) ||
		normalizedInput.orderId <= 0 ||
		!Number.isFinite(normalizedInput.clientUserId) ||
		normalizedInput.clientUserId <= 0 ||
		!Number.isFinite(normalizedInput.providerUserId) ||
		normalizedInput.providerUserId <= 0;

	if (hasInvalidId || !normalizedInput.serviceTitle) {
		throw new Error("Dados invalidos para abrir conversa.");
	}

	return request<DemoConversation>("/conversas/garantir", {
		method: "POST",
		body: JSON.stringify(normalizedInput),
	});
}

export async function getUserConversations(userId: number): Promise<DemoConversation[]> {
	return request<DemoConversation[]>(`/conversas/usuario/${userId}`);
}

export async function getConversationMessages(
	conversationId: number,
	userId: number,
): Promise<DemoChatMessage[]> {
	return request<DemoChatMessage[]>(`/conversas/${conversationId}/mensagens?userId=${userId}`);
}

export async function sendDemoMessage(
	conversationId: number,
	senderUserId: number,
	text: string,
): Promise<DemoChatMessage> {
	return request<DemoChatMessage>(`/conversas/${conversationId}/mensagens`, {
		method: "POST",
		body: JSON.stringify({ senderUserId, text }),
	});
}

export async function markConversationAsRead(
	conversationId: number,
	viewerUserId: number,
): Promise<void> {
	return request<void>(`/conversas/${conversationId}/ler?userId=${viewerUserId}`, {
		method: "POST",
	});
}

export async function getUnreadMessagesCount(userId: number): Promise<number> {
	return request<number>(`/conversas/usuario/${userId}/nao-lidas`);
}