import { api, type Usuario } from "./api";
import { getAverageRating } from "./demo-orders";

export interface ProviderWithRating extends Usuario {
  rating: number;
  reviewCount: number;
}

export async function getAllProviders(): Promise<ProviderWithRating[]> {
  try {
    const users = await api.getUsuarios();
    const providers = users.filter((u) => u.tipo === "prestador");

    return Promise.all(
      providers.map(async (provider) => {
        const ratingData = await getAverageRating(provider.id);
        return {
          ...provider,
          rating: ratingData.rating || 0,
          reviewCount: ratingData.count,
        };
      }),
    );
  } catch (error) {
    console.error("Erro ao buscar prestadores:", error);
    return [];
  }
}

export async function getProviderById(id: number): Promise<ProviderWithRating | null> {
  try {
    const provider = await api.getUsuario(id);
    if (provider.tipo !== "prestador") {
      return null;
    }

    const ratingData = await getAverageRating(provider.id);
    return {
      ...provider,
      rating: ratingData.rating || 0,
      reviewCount: ratingData.count,
    };
  } catch (error) {
    console.error(`Erro ao buscar prestador ${id}:`, error);
    return null;
  }
}

export async function searchProviders(
  query: string,
  filters?: {
    city?: string;
    state?: string;
    minRating?: number;
  },
): Promise<ProviderWithRating[]> {
  try {
    const allProviders = await getAllProviders();
    let results = allProviders;

    if (query.trim()) {
      const q = query.toLowerCase();
      results = results.filter(
        (p) => p.nome.toLowerCase().includes(q) || p.bio?.toLowerCase().includes(q),
      );
    }

    if (filters?.city) {
      results = results.filter((p) =>
        p.cidade?.toLowerCase().includes(filters.city!.toLowerCase()),
      );
    }

    if (filters?.state) {
      results = results.filter((p) => p.estado === filters.state);
    }

    if (typeof filters?.minRating === "number") {
      results = results.filter((p) => p.rating >= filters.minRating!);
    }

    return results;
  } catch (error) {
    console.error("Erro ao buscar prestadores:", error);
    return [];
  }
}

export async function getUniqueCities(): Promise<string[]> {
  try {
    const providers = await getAllProviders();
    const cities = new Set(
      providers.filter((p) => p.cidade && p.cidade.trim()).map((p) => p.cidade!),
    );
    return Array.from(cities).sort();
  } catch (error) {
    console.error("Erro ao buscar cidades:", error);
    return [];
  }
}

export async function getUniqueStates(): Promise<string[]> {
  try {
    const providers = await getAllProviders();
    const states = new Set(
      providers.filter((p) => p.estado && p.estado.trim()).map((p) => p.estado!),
    );
    return Array.from(states).sort();
  } catch (error) {
    console.error("Erro ao buscar estados:", error);
    return [];
  }
}

export function sortProviders(
  providers: ProviderWithRating[],
  sortBy: "rating-desc" | "rating-asc" | "name-asc" | "name-desc" = "rating-desc",
): ProviderWithRating[] {
  const sorted = [...providers];

  switch (sortBy) {
    case "rating-desc":
      return sorted.sort((a, b) => b.rating - a.rating);
    case "rating-asc":
      return sorted.sort((a, b) => a.rating - b.rating);
    case "name-asc":
      return sorted.sort((a, b) => a.nome.localeCompare(b.nome));
    case "name-desc":
      return sorted.sort((a, b) => b.nome.localeCompare(a.nome));
    default:
      return sorted;
  }
}
