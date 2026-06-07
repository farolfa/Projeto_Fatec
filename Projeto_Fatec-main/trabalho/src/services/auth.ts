import { Usuario } from "./api";

const SESSION_KEY = "faztudoja_session_user";
const SESSION_FOTO_KEY = "faztudoja_session_user_foto";
const ACTIVE_PROFILE_KEY = "faztudoja_active_profile";
// Allow profile images up to ~3.5 MB as Base64, so users can keep the photo selected at cadastro.
const SESSION_FOTO_MAX_CHARS = 3500000;

function clearLegacyPersistentSession(): void {
	localStorage.removeItem(SESSION_KEY);
}

function clearStoredSessionPhoto(): void {
	localStorage.removeItem(SESSION_FOTO_KEY);
}

export type TipoUsuario = "cliente" | "prestador" | "admin";
export type PerfilAtivo = "cliente" | "prestador";

export function normalizeTipo(tipo?: string): TipoUsuario | "" {
	const normalized = (tipo ?? "").toLowerCase().trim();
	if (normalized === "cliente" || normalized === "prestador" || normalized === "admin") {
		return normalized;
	}
	return "";
}

export function isAdmin(user: Usuario | null): boolean {
	return normalizeTipo(user?.tipo) === "admin";
}

function isQuotaExceededError(error: unknown): boolean {
	return (
		error instanceof DOMException &&
		(error.name === "QuotaExceededError" || error.name === "NS_ERROR_DOM_QUOTA_REACHED")
	);
}

function sanitizeUserForSession(user: Usuario, keepFoto: boolean): Usuario {
	const sanitized: Usuario = { ...user };

	if (!keepFoto || (sanitized.foto && sanitized.foto.length > SESSION_FOTO_MAX_CHARS)) {
		delete sanitized.foto;
	}

	return sanitized;
}

export function saveSessionUser(user: Usuario): void {
	clearLegacyPersistentSession();
	clearActiveProfile();
	clearStoredSessionPhoto();

	const preferredUser = sanitizeUserForSession(user, true);
	try {
		sessionStorage.setItem(SESSION_KEY, JSON.stringify(preferredUser));
		return;
	} catch (error) {
		if (!isQuotaExceededError(error)) {
			throw error;
		}
	}

	const fallbackUser = sanitizeUserForSession(user, false);
	sessionStorage.setItem(SESSION_KEY, JSON.stringify(fallbackUser));

	if (user.foto) {
		try {
			localStorage.setItem(SESSION_FOTO_KEY, user.foto);
		} catch {
			// Ignore secondary storage failures.
		}
	}
}

export function saveActiveProfile(profile: PerfilAtivo): void {
	sessionStorage.setItem(ACTIVE_PROFILE_KEY, profile);
}

export function getActiveProfile(): PerfilAtivo | null {
	const raw = sessionStorage.getItem(ACTIVE_PROFILE_KEY);
	return raw === "cliente" || raw === "prestador" ? raw : null;
}

export function clearActiveProfile(): void {
	sessionStorage.removeItem(ACTIVE_PROFILE_KEY);
}

export function updateSessionUser(partial: Partial<Usuario>): Usuario | null {
	const currentUser = getSessionUser();
	if (!currentUser) {
		return null;
	}

	const nextUser = { ...currentUser, ...partial };
	saveSessionUser(nextUser);
	return nextUser;
}

export function getSessionUser(): Usuario | null {
	clearLegacyPersistentSession();
	const raw = sessionStorage.getItem(SESSION_KEY);
	if (!raw) {
		return null;
	}

	try {
		const parsed = JSON.parse(raw) as Usuario;
		if (!parsed || typeof parsed !== "object") {
			return null;
		}

		const hasValidId = typeof parsed.id === "number" && Number.isFinite(parsed.id);
		const hasValidEmail = typeof parsed.email === "string" && parsed.email.trim().length > 0;
		const hasValidName = typeof parsed.nome === "string" && parsed.nome.trim().length > 0;
		const hasValidRole = normalizeTipo(parsed.tipo) !== "";

		if (!hasValidId || !hasValidEmail || !hasValidName || !hasValidRole) {
			return null;
		}

		if (!parsed.foto) {
			const storedFoto = localStorage.getItem(SESSION_FOTO_KEY);
			if (storedFoto) {
				parsed.foto = storedFoto;
			}
		}

		return parsed;
	} catch {
		return null;
	}
}

export function clearSessionUser(): void {
	sessionStorage.removeItem(SESSION_KEY);
	clearStoredSessionPhoto();
	clearActiveProfile();
	clearLegacyPersistentSession();
}

export function hasRequiredRole(user: Usuario | null, allowedRoles: TipoUsuario[]): boolean {
	if (!user) {
		return false;
	}

	const role = normalizeTipo(user.tipo);
	if (!role) {
		return false;
	}

	return allowedRoles.includes(role);
}
