import { ChangeEvent, FormEvent, useEffect, useState } from "react";
import { Link, useNavigate } from "react-router";
import { AlertTriangle, Camera, User } from "lucide-react";

import { Input } from "../components/ui/input";
import { Label } from "../components/ui/label";
import { api } from "../services/api";
import {
  isAdmin,
  saveSessionUser,
} from "../services/auth";

type RegisterField =
  | "nome"
  | "email"
  | "senha"
  | "cpf"
  | "telefone"
  | "endereco"
  | "cidade"
  | "estado"
  | "cep";
type RegisterErrors = Partial<Record<RegisterField, string>>;

const accessConfig = {
  headline: "Acesse sua conta",
  sub: "Faca login ou cadastro. Depois voce escolhe Cliente ou Prestador.",
  badge: "ACESSO DE USUARIO",
  gradient: "bg-blue-600",
  badgeBg: "bg-blue-500/30",
  btnClass: "bg-blue-600 hover:bg-blue-700 text-white",
  tabActive: "bg-white text-blue-700 shadow",
  icon: User,
} as const;

const BRAZILIAN_STATES = [
  "AC", "AL", "AP", "AM", "BA", "CE", "DF", "ES", "GO", "MA", "MT", "MS", "MG",
  "PA", "PB", "PR", "PE", "PI", "RJ", "RN", "RS", "RO", "RR", "SC", "SP", "SE", "TO",
] as const;

const CITY_FALLBACK_BY_STATE: Record<string, string[]> = {
  AC: ["Rio Branco", "Cruzeiro do Sul", "Sena Madureira"],
  AL: ["Maceio", "Arapiraca", "Palmeira dos Indios"],
  AP: ["Macapa", "Santana", "Laranjal do Jari"],
  AM: ["Manaus", "Parintins", "Itacoatiara"],
  BA: ["Salvador", "Feira de Santana", "Vitoria da Conquista", "Camaçari"],
  CE: ["Fortaleza", "Caucaia", "Juazeiro do Norte", "Maracanau"],
  DF: ["Brasilia", "Ceilandia", "Taguatinga"],
  ES: ["Vitoria", "Vila Velha", "Serra", "Cariacica"],
  GO: ["Goiania", "Aparecida de Goiania", "Anapolis"],
  MA: ["Sao Luis", "Imperatriz", "Caxias"],
  MT: ["Cuiaba", "Varzea Grande", "Rondonopolis"],
  MS: ["Campo Grande", "Dourados", "Tres Lagoas"],
  MG: ["Belo Horizonte", "Uberlandia", "Contagem", "Juiz de Fora", "Betim"],
  PA: ["Belem", "Ananindeua", "Santarem", "Maraba"],
  PB: ["Joao Pessoa", "Campina Grande", "Santa Rita"],
  PR: ["Curitiba", "Londrina", "Maringa", "Ponta Grossa"],
  PE: ["Recife", "Jaboatao dos Guararapes", "Olinda", "Caruaru"],
  PI: ["Teresina", "Parnaiba", "Picos"],
  RJ: ["Rio de Janeiro", "Sao Goncalo", "Duque de Caxias", "Nova Iguacu", "Niteroi"],
  RN: ["Natal", "Mossoro", "Parnamirim"],
  RS: ["Porto Alegre", "Caxias do Sul", "Pelotas", "Canoas"],
  RO: ["Porto Velho", "Ji-Parana", "Ariquemes"],
  RR: ["Boa Vista", "Rorainopolis", "Caracarai"],
  SC: ["Florianopolis", "Joinville", "Blumenau", "Sao Jose"],
  SP: ["Sao Paulo", "Campinas", "Guarulhos", "Santos", "Sao Bernardo do Campo", "Osasco", "Sorocaba", "Ribeirao Preto", "Sao Jose dos Campos"],
  SE: ["Aracaju", "Nossa Senhora do Socorro", "Lagarto"],
  TO: ["Palmas", "Araguaina", "Gurupi"],
};

const MAX_PROFILE_PHOTO_BYTES = 2 * 1024 * 1024;

function onlyDigits(value: string): string {
  return value.replace(/\D/g, "");
}

function formatPhone(value: string): string {
  const digits = onlyDigits(value).slice(0, 11);
  if (digits.length <= 10) {
    return digits.replace(/(\d{2})(\d)/, "($1) $2").replace(/(\d{4})(\d)/, "$1-$2");
  }
  return digits.replace(/(\d{2})(\d)/, "($1) $2").replace(/(\d{5})(\d)/, "$1-$2");
}

function formatCep(value: string): string {
  const digits = onlyDigits(value).slice(0, 8);
  return digits.replace(/(\d{5})(\d)/, "$1-$2");
}

function formatCpf(value: string): string {
  const digits = onlyDigits(value).slice(0, 11);
  return digits
    .replace(/(\d{3})(\d)/, "$1.$2")
    .replace(/(\d{3})(\d)/, "$1.$2")
    .replace(/(\d{3})(\d{1,2})$/, "$1-$2");
}

function getPasswordStrength(password: string): { label: string; tone: string } {
  if (!password) return { label: "Digite uma senha", tone: "text-gray-500" };

  let score = 0;
  if (password.length >= 6) score += 1;
  if (password.length >= 10) score += 1;
  if (/[A-Z]/.test(password) && /[a-z]/.test(password)) score += 1;
  if (/\d/.test(password)) score += 1;
  if (/[^A-Za-z0-9]/.test(password)) score += 1;

  if (score <= 1) return { label: "Forca: fraca", tone: "text-red-600" };
  if (score <= 3) return { label: "Forca: media", tone: "text-amber-600" };
  return { label: "Forca: forte", tone: "text-green-600" };
}

function mapApiError(message: string): string {
  const normalized = message.toLowerCase();
  if (normalized.includes("tempo limite") || normalized.includes("timeout")) {
    return "A API demorou para responder. Verifique se o backend e o banco estao ativos e tente novamente.";
  }
  if (normalized.includes("failed to fetch") || normalized.includes("networkerror")) {
    return "Nao foi possivel conectar com a API. Confirme se o backend esta em execucao na porta 8080.";
  }
  if (normalized.includes("bloqueado") || normalized.includes("contate o administrador")) {
    return "Usuario bloqueado. Contate o administrador para reativar sua conta.";
  }
  if (normalized.includes("cpf ja cadastrado") || normalized.includes("cpf já cadastrado")) {
    return "CPF ja cadastrado. Tente entrar ou use outro CPF.";
  }
  if (
    normalized.includes("e-mail ja cadastrado") ||
    normalized.includes("e-mail já cadastrado") ||
    normalized.includes("email ja cadastrado") ||
    normalized.includes("email já cadastrado")
  ) {
    return "E-mail ja cadastrado. Tente entrar ou use outro e-mail.";
  }
  return message;
}

function validateRegisterData(data: {
  nome: string;
  email: string;
  senha: string;
  cpf: string;
  telefone: string;
  endereco: string;
  cidade: string;
  estado: string;
  cep: string;
}): RegisterErrors {
  const errors: RegisterErrors = {};

  if (!data.nome.trim()) errors.nome = "Informe seu nome completo.";
  if (!data.email.trim() || !data.email.includes("@")) errors.email = "Informe um e-mail valido.";
  if (data.senha.length < 6) errors.senha = "A senha deve ter pelo menos 6 caracteres.";
  if (onlyDigits(data.cpf).length !== 11) errors.cpf = "CPF deve conter 11 digitos.";
  if (onlyDigits(data.telefone).length < 10) errors.telefone = "Telefone deve conter DDD + numero.";
  if (!data.endereco.trim()) errors.endereco = "Informe um endereco.";
  if (!data.cidade.trim()) errors.cidade = "Informe a cidade.";
  if (data.estado.trim().length !== 2) errors.estado = "UF deve ter 2 letras.";
  if (onlyDigits(data.cep).length !== 8) errors.cep = "CEP deve conter 8 digitos.";

  return errors;
}

export function Access() {
  const navigate = useNavigate();
  const redirectTo = "/selecionar-perfil";

  const [mode, setMode] = useState<"login" | "register">("login");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [loginData, setLoginData] = useState({ email: "", senha: "" });
  const [registerData, setRegisterData] = useState({
    nome: "",
    email: "",
    senha: "",
    cpf: "",
    telefone: "",
    endereco: "",
    cidade: "",
    estado: "",
    cep: "",
    foto: "",
    tipo: "cliente" as const,
  });
  const [registerErrors, setRegisterErrors] = useState<RegisterErrors>({});
  const [cityOptions, setCityOptions] = useState<string[]>([]);
  const [loadingCities, setLoadingCities] = useState(false);

  const passwordStrength = getPasswordStrength(registerData.senha);
  const cfg = accessConfig;
  const Icon = cfg.icon;
  const isBlockedError = error.toLowerCase().includes("bloqueado");

  useEffect(() => {
    const uf = registerData.estado.trim().toUpperCase();

    if (!uf) {
      setCityOptions([]);
      return;
    }

    let cancelled = false;
    const fallbackCities = CITY_FALLBACK_BY_STATE[uf] ?? [];

    setLoadingCities(true);

    fetch(`https://servicodados.ibge.gov.br/api/v1/localidades/estados/${uf}/municipios`)
      .then(async (response) => {
        if (!response.ok) {
          throw new Error("Falha ao carregar cidades");
        }

        const data = (await response.json()) as Array<{ nome: string }>;
        const cities = data.map((item) => item.nome).sort((first, second) => first.localeCompare(second));
        return cities.length > 0 ? cities : fallbackCities;
      })
      .catch(() => fallbackCities)
      .then((cities) => {
        if (cancelled) return;

        setCityOptions(cities);
        setRegisterData((prev) => (
          prev.cidade && !cities.includes(prev.cidade)
            ? { ...prev, cidade: "" }
            : prev
        ));
      })
      .finally(() => {
        if (!cancelled) {
          setLoadingCities(false);
        }
      });

    return () => {
      cancelled = true;
    };
  }, [registerData.estado]);

  function clearRegisterFieldError(field: RegisterField) {
    setRegisterErrors((prev) => {
      if (!prev[field]) return prev;
      const next = { ...prev };
      delete next[field];
      return next;
    });
  }

  const handleProfilePhotoChange = (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;

    if (file.size > MAX_PROFILE_PHOTO_BYTES) {
      setError("A foto deve ter no maximo 2 MB. Escolha uma imagem menor.");
      event.target.value = "";
      return;
    }

    setError("");

    const reader = new FileReader();
    reader.onload = () => {
      setRegisterData((prev) => ({
        ...prev,
        foto: typeof reader.result === "string" ? reader.result : "",
      }));
    };
    reader.readAsDataURL(file);
  };

  const handleLogin = async (event: FormEvent) => {
    event.preventDefault();
    setError("");
    setLoading(true);

    try {
      const user = await api.login({ email: loginData.email.trim(), senha: loginData.senha });

      if (isAdmin(user)) {
        saveSessionUser(user);
        navigate("/admin", { replace: true });
        return;
      }

      saveSessionUser(user);
      navigate(redirectTo, { replace: true });
    } catch (err) {
      setError(err instanceof Error ? mapApiError(err.message) : "Falha ao entrar.");
    } finally {
      setLoading(false);
    }
  };

  const handleRegister = async (event: FormEvent) => {
    event.preventDefault();
    setError("");
    setRegisterErrors({});
    setLoading(true);

    try {
      const validationErrors = validateRegisterData(registerData);
      if (Object.keys(validationErrors).length > 0) {
        setRegisterErrors(validationErrors);
        throw new Error("Revise os campos destacados.");
      }

      const payload = {
        ...registerData,
        nome: registerData.nome.trim(),
        email: registerData.email.trim(),
        endereco: registerData.endereco.trim(),
        cidade: registerData.cidade.trim(),
        estado: registerData.estado.trim().toUpperCase(),
        tipo: "cliente" as const,
      };

      await api.register(payload);
      const user = await api.login({ email: payload.email, senha: payload.senha });
      saveSessionUser(user);
      if (isAdmin(user)) {
        navigate("/admin", { replace: true });
      } else {
        navigate("/selecionar-perfil", { replace: true });
      }
    } catch (err) {
      setError(err instanceof Error ? mapApiError(err.message) : "Falha ao cadastrar.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[80vh] flex items-center justify-center py-12 px-4 bg-gray-50">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-xl overflow-hidden">
        <div className={`${cfg.gradient} px-6 py-8 text-white`}>
          <span className={`inline-block text-xs font-bold px-3 py-1 rounded-full ${cfg.badgeBg} mb-3 tracking-wider`}>
            {cfg.badge}
          </span>
          <div className="flex items-center gap-3">
            <div className="bg-white/20 rounded-xl p-2">
              <Icon size={28} />
            </div>
            <div>
              <h1 className="text-xl font-bold leading-tight">{cfg.headline}</h1>
              <p className="text-sm opacity-80 mt-0.5">{cfg.sub}</p>
            </div>
          </div>
        </div>

        <div className="p-6 space-y-5">
          <div className="grid grid-cols-2 gap-1 bg-gray-100 p-1 rounded-lg">
            <button
              type="button"
              onClick={() => {
                setMode("login");
                setError("");
              }}
              className={`py-2 text-sm font-medium rounded-md transition-all ${
                mode === "login" ? cfg.tabActive : "text-gray-500 hover:text-gray-700"
              }`}
            >
              Login de usuario
            </button>
            <button
              type="button"
              onClick={() => {
                setMode("register");
                setError("");
              }}
              className={`py-2 text-sm font-medium rounded-md transition-all ${
                mode === "register" ? cfg.tabActive : "text-gray-500 hover:text-gray-700"
              }`}
            >
              Cadastro de usuario
            </button>
          </div>

          <div aria-live="polite" className="space-y-2">
            {error && !isBlockedError && <p className="text-sm text-red-600 bg-red-50 border border-red-200 rounded-lg p-3">{error}</p>}
            {error && isBlockedError && (
              <div className="rounded-xl border border-amber-300 bg-gradient-to-r from-amber-50 to-orange-50 p-4 shadow-sm">
                <div className="flex items-start gap-3">
                  <div className="mt-0.5 rounded-full bg-amber-100 p-2 text-amber-700">
                    <AlertTriangle size={16} />
                  </div>
                  <div className="min-w-0">
                    <p className="text-sm font-semibold text-amber-900">Conta temporariamente bloqueada</p>
                    <p className="mt-1 text-sm text-amber-800">{error}</p>
                    <div className="mt-3 flex flex-wrap items-center gap-2 text-xs">
                      <Link to="/centro-ajuda" className="rounded-md bg-amber-600 px-2.5 py-1.5 font-semibold text-white hover:bg-amber-700">
                        Falar com o suporte
                      </Link>
                      <span className="text-amber-700">Tenha em maos seu e-mail e CPF para agilizar o desbloqueio.</span>
                    </div>
                  </div>
                </div>
              </div>
            )}
            {loading && <p className="text-xs text-gray-500">Processando, aguarde...</p>}
          </div>

          {mode === "login" ? (
            <form onSubmit={handleLogin} className="space-y-4">
              <div className="space-y-1.5">
                <Label htmlFor="login-email">E-mail</Label>
                <Input
                  id="login-email"
                  type="email"
                  placeholder="seu@email.com"
                  value={loginData.email}
                  onChange={(e) => setLoginData((prev) => ({ ...prev, email: e.target.value }))}
                  required
                />
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="login-password">Senha</Label>
                <Input
                  id="login-password"
                  type="password"
                  placeholder="********"
                  value={loginData.senha}
                  onChange={(e) => setLoginData((prev) => ({ ...prev, senha: e.target.value }))}
                  required
                />
              </div>
              <button type="submit" disabled={loading} className={`w-full py-2.5 rounded-lg font-semibold transition-colors ${cfg.btnClass}`}>
                {loading ? "Entrando..." : "Fazer login"}
              </button>
            </form>
          ) : (
            <form onSubmit={handleRegister} className="space-y-4">
              <div className="space-y-1.5">
                <Label htmlFor="register-name">Nome completo</Label>
                <Input
                  id="register-name"
                  type="text"
                  placeholder="Seu nome"
                  value={registerData.nome}
                  aria-invalid={!!registerErrors.nome}
                  onChange={(e) => {
                    clearRegisterFieldError("nome");
                    setRegisterData((prev) => ({ ...prev, nome: e.target.value }));
                  }}
                  required
                />
                {registerErrors.nome && <p className="text-xs text-red-600">{registerErrors.nome}</p>}
              </div>

              <div className="space-y-2">
                <Label>Foto de perfil</Label>
                <label htmlFor="register-photo" className="cursor-pointer block">
                <div className="rounded-2xl border border-dashed border-gray-300 bg-gray-50 p-4 hover:bg-gray-100 transition-colors">
                  <div className="flex items-center gap-4">
                    <div className="h-16 w-16 overflow-hidden rounded-full bg-white ring-2 ring-white shadow-sm flex items-center justify-center">
                      {registerData.foto ? (
                        <img src={registerData.foto} alt="Previa do perfil" className="h-full w-full object-cover" />
                      ) : (
                        <Camera className="text-gray-400" size={22} />
                      )}
                    </div>
                    <div className="flex-1">
                      <p className="text-sm font-medium text-blue-600 mb-1">Clique para selecionar uma foto</p>
                      <p className="text-xs text-gray-500">Essa foto aparecerá no seu perfil e nos orçamentos enviados.</p>
                    </div>
                  </div>
                </div>
                </label>
                <Input id="register-photo" type="file" accept="image/*" onChange={handleProfilePhotoChange} className="hidden" />
              </div>

              <div className="space-y-1.5">
                <Label htmlFor="register-email">E-mail</Label>
                <Input
                  id="register-email"
                  type="email"
                  placeholder="seu@email.com"
                  value={registerData.email}
                  aria-invalid={!!registerErrors.email}
                  onChange={(e) => {
                    clearRegisterFieldError("email");
                    setRegisterData((prev) => ({ ...prev, email: e.target.value }));
                  }}
                  required
                />
                {registerErrors.email && <p className="text-xs text-red-600">{registerErrors.email}</p>}
              </div>

              <div className="space-y-1.5">
                <Label htmlFor="register-password">Senha</Label>
                <Input
                  id="register-password"
                  type="password"
                  placeholder="Minimo 6 caracteres"
                  value={registerData.senha}
                  aria-invalid={!!registerErrors.senha}
                  onChange={(e) => {
                    clearRegisterFieldError("senha");
                    setRegisterData((prev) => ({ ...prev, senha: e.target.value }));
                  }}
                  required
                />
                <p className={`text-xs ${passwordStrength.tone}`}>{passwordStrength.label}</p>
                {registerErrors.senha && <p className="text-xs text-red-600">{registerErrors.senha}</p>}
              </div>

              <div className="space-y-1.5">
                <Label htmlFor="register-cpf">CPF</Label>
                <Input
                  id="register-cpf"
                  type="text"
                  placeholder="000.000.000-00"
                  value={registerData.cpf}
                  inputMode="numeric"
                  maxLength={14}
                  aria-invalid={!!registerErrors.cpf}
                  onChange={(e) => {
                    clearRegisterFieldError("cpf");
                    setRegisterData((prev) => ({ ...prev, cpf: formatCpf(e.target.value) }));
                  }}
                  required
                />
                {registerErrors.cpf && <p className="text-xs text-red-600">{registerErrors.cpf}</p>}
              </div>

              <div className="space-y-1.5">
                <Label htmlFor="register-phone">Telefone</Label>
                <Input
                  id="register-phone"
                  type="text"
                  placeholder="(11) 99999-9999"
                  value={registerData.telefone}
                  inputMode="numeric"
                  maxLength={15}
                  aria-invalid={!!registerErrors.telefone}
                  onChange={(e) => {
                    clearRegisterFieldError("telefone");
                    setRegisterData((prev) => ({ ...prev, telefone: formatPhone(e.target.value) }));
                  }}
                  required
                />
                {registerErrors.telefone && <p className="text-xs text-red-600">{registerErrors.telefone}</p>}
              </div>

              <div className="space-y-1.5">
                <Label htmlFor="register-address">Endereco</Label>
                <Input
                  id="register-address"
                  type="text"
                  placeholder="Rua, numero e complemento"
                  value={registerData.endereco}
                  aria-invalid={!!registerErrors.endereco}
                  onChange={(e) => {
                    clearRegisterFieldError("endereco");
                    setRegisterData((prev) => ({ ...prev, endereco: e.target.value }));
                  }}
                  required
                />
                {registerErrors.endereco && <p className="text-xs text-red-600">{registerErrors.endereco}</p>}
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1.5">
                  <Label htmlFor="register-city">Cidade</Label>
                  <select
                    id="register-city"
                    value={registerData.cidade}
                    aria-invalid={!!registerErrors.cidade}
                    disabled={!registerData.estado || loadingCities}
                    onChange={(e) => {
                      clearRegisterFieldError("cidade");
                      setRegisterData((prev) => ({ ...prev, cidade: e.target.value }));
                    }}
                    className="border-input bg-input-background focus-visible:border-ring focus-visible:ring-ring/50 aria-invalid:border-destructive flex h-9 w-full rounded-md border px-3 py-2 text-sm outline-none focus-visible:ring-[3px] disabled:cursor-not-allowed disabled:opacity-60"
                    required
                  >
                    <option value="">
                      {!registerData.estado
                        ? "Escolha o estado primeiro"
                        : loadingCities
                          ? "Carregando cidades..."
                          : "Selecione a cidade"}
                    </option>
                    {cityOptions.map((city) => (
                      <option key={city} value={city}>
                        {city}
                      </option>
                    ))}
                  </select>
                  {registerErrors.cidade && <p className="text-xs text-red-600">{registerErrors.cidade}</p>}
                </div>
                <div className="space-y-1.5">
                  <Label htmlFor="register-state">Estado (UF)</Label>
                  <select
                    id="register-state"
                    value={registerData.estado}
                    aria-invalid={!!registerErrors.estado}
                    onChange={(e) => {
                      clearRegisterFieldError("estado");
                      clearRegisterFieldError("cidade");
                      setRegisterData((prev) => ({ ...prev, estado: e.target.value, cidade: "" }));
                    }}
                    className="border-input bg-input-background focus-visible:border-ring focus-visible:ring-ring/50 aria-invalid:border-destructive flex h-9 w-full rounded-md border px-3 py-2 text-sm outline-none focus-visible:ring-[3px]"
                  >
                    <option value="">Selecione o estado</option>
                    {BRAZILIAN_STATES.map((uf) => (
                      <option key={uf} value={uf}>
                        {uf}
                      </option>
                    ))}
                  </select>
                  {registerErrors.estado && <p className="text-xs text-red-600">{registerErrors.estado}</p>}
                </div>
              </div>

              <div className="space-y-1.5">
                <Label htmlFor="register-cep">CEP</Label>
                <Input
                  id="register-cep"
                  type="text"
                  placeholder="00000-000"
                  value={registerData.cep}
                  inputMode="numeric"
                  maxLength={9}
                  aria-invalid={!!registerErrors.cep}
                  onChange={(e) => {
                    clearRegisterFieldError("cep");
                    setRegisterData((prev) => ({ ...prev, cep: formatCep(e.target.value) }));
                  }}
                  required
                />
                {registerErrors.cep && <p className="text-xs text-red-600">{registerErrors.cep}</p>}
              </div>

              <div className="flex items-center gap-2 rounded-lg border-2 border-blue-200 bg-blue-50 px-3 py-2.5">
                <Icon size={16} className="text-blue-600" />
                <span className="text-sm font-medium text-blue-700">
                  Conta unica: apos entrar voce escolhe Cliente ou Prestador
                </span>
              </div>

              <div className="grid grid-cols-2 gap-2">
                <button type="submit" disabled={loading} className={`w-full py-2.5 rounded-lg font-semibold transition-colors ${cfg.btnClass}`}>
                  {loading ? "Cadastrando..." : "Cadastrar usuario"}
                </button>
                <button
                  type="button"
                  disabled={loading}
                  onClick={() => {
                    setError("");
                    setRegisterErrors({});
                    setRegisterData((prev) => ({
                      ...prev,
                      nome: "",
                      email: "",
                      senha: "",
                      cpf: "",
                      telefone: "",
                      endereco: "",
                      cidade: "",
                      estado: "",
                      cep: "",
                      foto: "",
                    }));
                  }}
                  className="w-full py-2.5 rounded-lg font-semibold border border-gray-300 text-gray-700 hover:bg-gray-50 transition-colors"
                >
                  Limpar
                </button>
              </div>
            </form>
          )}

          <div className="text-center text-sm text-gray-500">
            <Link to="/" className="hover:underline">
              {"<- Voltar para o inicio"}
            </Link>
            <span className="mx-2">*</span>
            <Link to="/centro-ajuda" className="hover:underline">
              Precisa de ajuda?
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
}
