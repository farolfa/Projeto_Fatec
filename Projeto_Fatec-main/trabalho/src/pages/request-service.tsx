import { useState } from "react";
import { Link, Navigate, useLocation } from "react-router";
import { CheckCircle } from "lucide-react";

import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Label } from "../components/ui/label";
import { Textarea } from "../components/ui/textarea";
import { createDemoOrder } from "../services/demo-orders";
import { getSessionUser } from "../services/auth";

const categories = [
	"Reparos e Reformas",
	"Limpeza",
	"Eletricista",
	"Encanador",
	"Personal Trainer",
	"Pintura",
	"Mudanças",
	"Estética e Beleza",
	"Aulas Particulares",
	"Fotografia",
	"Jardinagem",
	"TI e Tecnologia",
];

export function RequestService() {
	const [submitted, setSubmitted] = useState(false);
	const location = useLocation();
	const user = getSessionUser();
	const savedAddress = user?.endereco?.trim() ?? "";
	const hasSavedAddress = savedAddress.length > 0;

	const [formData, setFormData] = useState({
		category: "",
		title: "",
		description: "",
		name: user?.nome ?? "",
		email: user?.email ?? "",
		phone: user?.telefone ?? "",
		address: hasSavedAddress ? savedAddress : "",
	});
	const [addressMode, setAddressMode] = useState<"saved" | "new">(
		hasSavedAddress ? "saved" : "new",
	);

	if (!user) {
		const redirect = encodeURIComponent(location.pathname);
		return <Navigate to={`/acesso?redirect=${redirect}`} replace />;
	}

	const handleSubmit = async (event: React.FormEvent) => {
		event.preventDefault();
		if (!user.id) {
			return;
		}

		await createDemoOrder({
			clientUserId: user.id,
			clientName: formData.name,
			clientEmail: formData.email,
			clientPhone: formData.phone,
			clientPhoto: user.foto,
			title: formData.title,
			category: formData.category,
			description: formData.description,
			location: formData.address,
		});

		setSubmitted(true);
	};

	const handleChange = (field: string, value: string) => {
		setFormData((prev) => ({ ...prev, [field]: value }));
	};

	const handleAddressModeChange = (mode: "saved" | "new") => {
		setAddressMode(mode);
		setFormData((prev) => ({
			...prev,
			address: mode === "saved" ? savedAddress : "",
		}));
	};

	if (submitted) {
		return (
			<div className="min-h-[80vh] flex items-center justify-center px-4">
				<div className="max-w-md w-full text-center">
					<div className="bg-green-100 mx-auto mb-6 flex h-20 w-20 items-center justify-center rounded-full">
						<CheckCircle className="text-green-600" size={48} />
					</div>
					<h1 className="mb-4 text-3xl">Solicitação Enviada!</h1>
					<p className="mb-8 text-gray-600">
						Sua solicitação foi recebida com sucesso. Em breve, profissionais qualificados
						entrarão em contato com orçamentos personalizados.
					</p>
					<div className="mb-6 rounded-lg border border-blue-200 bg-blue-50 p-6">
						<h3 className="mb-2 font-semibold">Próximos Passos:</h3>
						<ul className="space-y-2 text-left text-sm text-gray-700">
							<li>✓ Aguarde contato dos profissionais (até 24h)</li>
							<li>✓ Compare os orçamentos recebidos</li>
							<li>✓ Escolha o profissional ideal</li>
							<li>✓ Agende o serviço</li>
						</ul>
					</div>
					<Button onClick={() => setSubmitted(false)} className="bg-orange-600 hover:bg-orange-700">
						Fazer Nova Solicitação
					</Button>
					<Link to="/dashboard/meus-pedidos" className="mt-3 block">
						<Button
							variant="outline"
							className="w-full border-orange-500 text-orange-600 hover:bg-orange-50"
						>
							Voltar para Minha Conta
						</Button>
					</Link>
				</div>
			</div>
		);
	}

	return (
		<div className="py-12">
			<div className="mx-auto max-w-3xl px-4 sm:px-6 lg:px-8">
				<div className="mb-10 text-center">
					<div className="mb-4 inline-block">
						<span className="rounded-full bg-orange-600 px-4 py-2 text-sm font-semibold uppercase tracking-wide text-white shadow-lg">
							⚡ Resposta Rápida Garantida
						</span>
					</div>
					<h1 className="mb-4 text-4xl font-bold text-blue-700 md:text-5xl">Solicitar Serviço</h1>
					<p className="text-lg text-gray-600">
						Preencha o formulário e receba <span className="font-bold text-orange-600">até 5 orçamentos</span> de profissionais qualificados
					</p>
				</div>

				<form onSubmit={handleSubmit} className="space-y-6 rounded-lg bg-white p-8 shadow-lg">
					<div className="space-y-2">
						<Label htmlFor="category">Categoria do Serviço *</Label>
						<div className="relative">
							<select
								id="category"
								value={formData.category}
								onChange={(event) => handleChange("category", event.target.value)}
								className="border-input bg-input-background focus-visible:border-ring focus-visible:ring-ring/50 aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive h-10 w-full appearance-none rounded-lg border px-3.5 py-2 pr-10 text-base text-slate-700 transition-[color,box-shadow,border-color] outline-none focus-visible:ring-[3px] md:text-sm"
								required
							>
								<option value="" disabled>
									Selecione uma categoria
								</option>
								{categories.map((category) => (
									<option key={category} value={category}>
										{category}
									</option>
								))}
							</select>
							<span className="pointer-events-none absolute inset-y-0 right-3 flex items-center text-slate-400">
								▾
							</span>
						</div>
					</div>

					<div className="space-y-2">
						<Label htmlFor="title">Título do Serviço *</Label>
						<Input
							id="title"
							type="text"
							placeholder="Ex: Instalação de chuveiro elétrico"
							value={formData.title}
							onChange={(event) => handleChange("title", event.target.value)}
							required
						/>
					</div>

					<div className="space-y-2">
						<Label htmlFor="description">Descrição Detalhada *</Label>
						<Textarea
							id="description"
							placeholder="Descreva o que você precisa, quando precisa e qualquer detalhe importante..."
							value={formData.description}
							onChange={(event) => handleChange("description", event.target.value)}
							rows={6}
							required
						/>
						<p className="text-sm text-gray-500">
							Quanto mais detalhes você fornecer, melhores serão os orçamentos
						</p>
					</div>

					<div className="border-t pt-6">
						<h3 className="mb-4 text-xl">Seus Dados de Contato</h3>
					</div>

					<div className="space-y-2">
						<Label htmlFor="name">Nome Completo *</Label>
						<Input
							id="name"
							type="text"
							placeholder="Seu nome completo"
							value={formData.name}
							onChange={(event) => handleChange("name", event.target.value)}
							required
						/>
					</div>

					<div className="grid gap-4 md:grid-cols-2">
						<div className="space-y-2">
							<Label htmlFor="email">E-mail *</Label>
							<Input
								id="email"
								type="email"
								placeholder="seu@email.com"
								value={formData.email}
								onChange={(event) => handleChange("email", event.target.value)}
								required
							/>
						</div>
						<div className="space-y-2">
							<Label htmlFor="phone">Telefone *</Label>
							<Input
								id="phone"
								type="tel"
								placeholder="(11) 98765-4321"
								value={formData.phone}
								onChange={(event) => handleChange("phone", event.target.value)}
								required
							/>
						</div>
					</div>

					<div className="space-y-2">
						<Label htmlFor="address">Endereço *</Label>
						{hasSavedAddress && (
							<div className="grid grid-cols-2 gap-2 rounded-lg bg-gray-100 p-1">
								<button
									type="button"
									onClick={() => handleAddressModeChange("saved")}
									className={`rounded-md px-3 py-2 text-sm font-medium transition-all ${
										addressMode === "saved"
											? "bg-white text-blue-700 shadow-sm"
											: "text-gray-600 hover:text-gray-800"
									}`}
								>
									Usar meu endereço
								</button>
								<button
									type="button"
									onClick={() => handleAddressModeChange("new")}
									className={`rounded-md px-3 py-2 text-sm font-medium transition-all ${
										addressMode === "new"
											? "bg-white text-blue-700 shadow-sm"
											: "text-gray-600 hover:text-gray-800"
									}`}
								>
									Usar novo endereço
								</button>
							</div>
						)}
						{hasSavedAddress && addressMode === "saved" && (
							<div className="rounded-lg border border-blue-200 bg-blue-50 px-4 py-3 text-sm text-blue-900">
								{savedAddress}
							</div>
						)}
						<Input
							id="address"
							type="text"
							placeholder="Rua, número, bairro e referência"
							value={formData.address}
							onChange={(event) => handleChange("address", event.target.value)}
							disabled={hasSavedAddress && addressMode === "saved"}
							required
						/>
					</div>

					<div className="pt-4">
						<Button type="submit" className="w-full bg-blue-700 py-6 text-lg shadow-md hover:bg-blue-800">
							Enviar Solicitação
						</Button>
						<p className="mt-4 text-center text-sm text-gray-500">
							Ao enviar, você concorda com nossos Termos de Uso e Política de Privacidade
						</p>
					</div>
				</form>

				<div className="mt-8 grid gap-4 text-center md:grid-cols-3">
					<div className="rounded-lg bg-gray-50 p-4">
						<div className="mb-2 text-2xl">📝</div>
						<h4 className="mb-1 font-semibold">Grátis</h4>
						<p className="text-sm text-gray-600">Sem custo para solicitar orçamentos</p>
					</div>
					<div className="rounded-lg bg-gray-50 p-4">
						<div className="mb-2 text-2xl">⚡</div>
						<h4 className="mb-1 font-semibold">Rápido</h4>
						<p className="text-sm text-gray-600">Respostas em até 24 horas</p>
					</div>
					<div className="rounded-lg bg-gray-50 p-4">
						<div className="mb-2 text-2xl">🛡️</div>
						<h4 className="mb-1 font-semibold">Seguro</h4>
						<p className="text-sm text-gray-600">Profissionais verificados</p>
					</div>
				</div>
			</div>
		</div>
	);
}