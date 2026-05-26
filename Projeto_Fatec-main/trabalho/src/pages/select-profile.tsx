import { useState } from "react";
import { Navigate, useNavigate } from "react-router";
import { Briefcase, User } from "lucide-react";

import { Button } from "../components/ui/button";
import { api } from "../services/api";
import {
  clearSessionUser,
  getSessionUser,
  isAdmin,
  saveActiveProfile,
  saveSessionUser,
} from "../services/auth";

export function SelectProfile() {
  const navigate = useNavigate();
  const user = getSessionUser();
  const [loading, setLoading] = useState<"cliente" | "prestador" | null>(null);
  const [error, setError] = useState("");

  if (!user) {
    return <Navigate to="/acesso" replace />;
  }

  if (isAdmin(user)) {
    return <Navigate to="/admin" replace />;
  }

  const handleChooseProfile = async (tipo: "cliente" | "prestador") => {
    setError("");
    setLoading(tipo);

    try {
      const updatedUser = await api.switchPerfil(user.id, tipo);
      saveSessionUser(updatedUser);
      saveActiveProfile(tipo);
      navigate(tipo === "prestador" ? "/prestador" : "/dashboard", {
        replace: true,
      });
    } catch (err) {
      setError(err instanceof Error ? err.message : "Nao foi possivel selecionar o perfil.");
    } finally {
      setLoading(null);
    }
  };

  return (
    <div className="min-h-[80vh] bg-gray-50 px-4 py-10">
      <div className="mx-auto max-w-3xl rounded-2xl border border-gray-200 bg-white p-6 shadow-lg md:p-8">
        <div className="mb-6 text-center">
          <h1 className="text-2xl font-bold text-gray-900">Como voce quer entrar agora?</h1>
          <p className="mt-2 text-sm text-gray-600">
            Sua conta e unica. Escolha o perfil da sessao: cliente ou prestador.
          </p>
        </div>

        {error && (
          <div className="mb-5 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-800">
            {error}
          </div>
        )}

        <div className="grid gap-4 md:grid-cols-2">
          <button
            type="button"
            onClick={() => handleChooseProfile("cliente")}
            disabled={!!loading}
            className="rounded-xl border-2 border-blue-200 bg-blue-50 p-5 text-left transition hover:border-blue-300 hover:shadow disabled:opacity-60"
          >
            <div className="mb-3 inline-flex rounded-full bg-blue-600 p-2 text-white">
              <User size={18} />
            </div>
            <h2 className="text-lg font-semibold text-blue-900">Entrar como Cliente</h2>
            <p className="mt-1 text-sm text-blue-800">
              Criar pedidos, receber orcamentos e contratar profissionais.
            </p>
          </button>

          <button
            type="button"
            onClick={() => handleChooseProfile("prestador")}
            disabled={!!loading}
            className="rounded-xl border-2 border-orange-200 bg-orange-50 p-5 text-left transition hover:border-orange-300 hover:shadow disabled:opacity-60"
          >
            <div className="mb-3 inline-flex rounded-full bg-orange-500 p-2 text-white">
              <Briefcase size={18} />
            </div>
            <h2 className="text-lg font-semibold text-orange-900">Entrar como Prestador</h2>
            <p className="mt-1 text-sm text-orange-800">
              Ver servicos disponiveis e enviar propostas para os clientes.
            </p>
          </button>
        </div>

        <div className="mt-6 flex items-center justify-between">
          <Button
            type="button"
            variant="outline"
            onClick={() => {
              clearSessionUser();
              navigate("/", { replace: true });
            }}
          >
            Sair da conta
          </Button>

          {loading && <span className="text-sm text-gray-500">Carregando perfil...</span>}
        </div>
      </div>
    </div>
  );
}
