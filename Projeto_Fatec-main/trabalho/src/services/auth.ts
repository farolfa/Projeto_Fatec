import { Usuario } from "./api";

const SESSION_KEY = "faztudoja_session_user";
const ACTIVE_PROFILE_KEY = "faztudoja_active_profile";

function clearLegacyPersistentSession(): void {
	localStorage.removeItem(SESSION_KEY);
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

export function saveSessionUser(user: Usuario): void {
	clearLegacyPersistentSession();
	clearActiveProfile();
	sessionStorage.setItem(SESSION_KEY, JSON.stringify(user));
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

		return parsed;
	} catch {
		return null;
	}
}

export function clearSessionUser(): void {
	sessionStorage.removeItem(SESSION_KEY);
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
