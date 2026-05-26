import { useEffect, useState } from "react";
import { Link, Navigate, useParams } from "react-router";
import { BadgeCheck, Briefcase, Mail, MapPin, Phone, Star } from "lucide-react";

import { Button } from "../components/ui/button";
import { Card } from "../components/ui/card";
import { ImageWithFallback } from "../components/figma/ImageWithFallback";
import { api, type Usuario } from "../services/api";
import { getSessionUser } from "../services/auth";

const fallbackProfilePhoto =
	"https://images.unsplash.com/photo-1521572267360-ee0c2909d518?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=640";

export function ProviderPublicProfile() {
	const sessionUser = getSessionUser();
	const params = useParams();
	const [provider, setProvider] = useState<Usuario | null>(null);
	const [loading, setLoading] = useState(true);
	const [error, setError] = useState("");

	useEffect(() => {
		const providerId = Number(params.id);

		if (!Number.isFinite(providerId)) {
			setError("Prestador invalido.");
			setLoading(false);
			return;
		}

		const loadProvider = async () => {
			try {
				const user = await api.getUsuario(providerId);
				setProvider(user);
			} catch (err) {
				setError(err instanceof Error ? err.message : "Nao foi possivel carregar o perfil.");
			} finally {
				setLoading(false);
			}
		};

		void loadProvider();
	}, [params.id]);

	if (!sessionUser) {
		return <Navigate to="/acesso?redirect=/perfil-publico" replace />;
	}

	return (
		<div className="min-h-screen bg-gray-50">
			<div className="bg-blue-700 py-10 text-white">
				<div className="mx-auto max-w-5xl px-4 sm:px-6 lg:px-8">
					<p className="mb-3 text-sm uppercase tracking-[0.2em] text-blue-100">Perfil Profissional</p>
					<h1 className="text-4xl font-bold">Conheca melhor o prestador</h1>
					<p className="mt-2 text-blue-100">
						Veja foto, biografia, regiao de atendimento e os dados publicos do profissional.
					</p>
				</div>
			</div>

			<div className="mx-auto max-w-5xl px-4 py-8 sm:px-6 lg:px-8">
				<Link to="/dashboard/orcamentos">
					<Button variant="outline" className="mb-6">
						Voltar
					</Button>
				</Link>

				{loading && <Card className="p-10 text-center text-gray-600">Carregando dados...</Card>}

				{!loading && error && (
					<Card className="border-red-200 bg-red-50 p-10 text-center text-red-700">{error}</Card>
				)}

				{!loading && provider && (
					<div className="space-y-6">
						<Card className="overflow-hidden border-2 border-blue-100 shadow-lg">
							<div className="grid lg:grid-cols-[280px,1fr]">
								<div className="flex items-center justify-center bg-blue-50 p-8">
									<div className="h-48 w-48 overflow-hidden rounded-[2rem] shadow-lg ring-4 ring-white">
										<ImageWithFallback
											src={provider.foto || fallbackProfilePhoto}
											alt={provider.nome}
											className="h-full w-full object-cover"
										/>
									</div>
								</div>

								<div className="p-8">
									<div className="mb-4 flex flex-wrap items-center gap-3">
										<h2 className="text-3xl font-bold text-gray-900">{provider.nome}</h2>
										<span className="inline-flex items-center gap-1 rounded-full bg-green-100 px-3 py-1 text-sm font-medium text-green-800">
											<BadgeCheck size={16} />
											Perfil verificado
										</span>
									</div>

									<p className="mb-6 text-lg text-gray-600">
										{provider.bio?.trim() ||
											"Profissional cadastrado na plataforma e disponivel para novos servicos."}
									</p>

									<div className="grid gap-4 text-sm md:grid-cols-2">
										<div className="rounded-xl border border-gray-200 bg-gray-50 p-4">
											<div className="mb-1 flex items-center gap-2 font-semibold text-gray-900">
												<Briefcase size={16} />
												Atuacao
											</div>
											<p className="capitalize text-gray-600">{provider.tipo || "prestador"}</p>
										</div>

										<div className="rounded-xl border border-gray-200 bg-gray-50 p-4">
											<div className="mb-1 flex items-center gap-2 font-semibold text-gray-900">
												<MapPin size={16} />
												Regiao
											</div>
											<p className="text-gray-600">
												{[provider.cidade, provider.estado].filter(Boolean).join(" - ") ||
													"Regiao nao informada"}
											</p>
										</div>

										<div className="rounded-xl border border-gray-200 bg-gray-50 p-4">
											<div className="mb-1 flex items-center gap-2 font-semibold text-gray-900">
												<Phone size={16} />
												Telefone
											</div>
											<p className="text-gray-600">{provider.telefone || "Nao informado"}</p>
										</div>

										<div className="rounded-xl border border-gray-200 bg-gray-50 p-4">
											<div className="mb-1 flex items-center gap-2 font-semibold text-gray-900">
												<Mail size={16} />
												E-mail
											</div>
											<p className="text-gray-600">{provider.email}</p>
										</div>
									</div>
								</div>
							</div>
						</Card>

						<div className="grid gap-4 md:grid-cols-3">
							<Card className="border-yellow-200 bg-orange-50 p-6">
								<div className="mb-3 flex items-center gap-2 text-yellow-600">
									<Star className="fill-yellow-500" size={20} />
									<span className="font-semibold">Qualidade</span>
								</div>
								<p className="text-sm text-gray-700">
									Perfil pronto para receber novos servicos e negociar diretamente com o cliente.
								</p>
							</Card>

							<Card className="border-blue-200 bg-blue-50 p-6">
								<div className="mb-3 flex items-center gap-2 text-blue-600">
									<Briefcase size={20} />
									<span className="font-semibold">Apresentacao</span>
								</div>
								<p className="text-sm text-gray-700">
									A biografia e a foto ajudam o cliente a confiar mais no orcamento enviado.
								</p>
							</Card>

							<Card className="border-green-200 bg-emerald-50 p-6">
								<div className="mb-3 flex items-center gap-2 text-green-600">
									<BadgeCheck size={20} />
									<span className="font-semibold">Contato</span>
								</div>
								<p className="text-sm text-gray-700">
									As informacoes publicas ficam visiveis para facilitar o alinhamento do servico.
								</p>
							</Card>
						</div>
					</div>
				)}
			</div>
		</div>
	);
}
