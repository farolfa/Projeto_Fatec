import { Outlet, Link, useLocation } from "react-router";
import { Button } from "./ui/button";
import { LogIn, Menu, User, X, HelpCircle, Leaf } from "lucide-react";
import { useEffect, useState } from "react";
import { getSessionUser } from "../services/auth";
import { SiteLogo } from "./site-logo";

export function Layout() {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const location = useLocation();
  const user = getSessionUser();

  useEffect(() => {
    setMobileMenuOpen(false);
  }, [location.pathname]);

  const loginLink = user ? "/selecionar-perfil" : "/acesso";

  return (
    <div className="min-h-screen flex flex-col">
      {/* Header */}
      <header className="surface-glass sticky top-0 z-50 border-b shadow-[0_10px_32px_-28px_rgba(17,60,110,0.7)]">
        <div className="page-shell">
          <div className="flex min-h-[4.5rem] justify-between items-center gap-3">
            {/* Logo */}
            <SiteLogo to="/" />

            {/* Desktop Navigation */}
            <nav className="hidden md:flex items-center gap-8">
              <p className="rounded-full border border-blue-100 bg-white/75 px-4 py-1.5 text-xs font-semibold tracking-[0.16em] text-blue-700 uppercase">
                Trabalho guiado por confiança, respeito e precisão
              </p>
            </nav>

            {/* Desktop CTA */}
            <div className="hidden md:flex items-center gap-3">
              {user ? (
                <Link to="/selecionar-perfil">
                  <Button className="bg-blue-700 hover:bg-blue-800 text-white">
                    <User size={16} className="mr-1" />
                    {user.nome?.split(" ")[0] ?? "Minha Conta"}
                  </Button>
                </Link>
              ) : (
                <Link to="/acesso">
                  <Button className="bg-blue-700 hover:bg-blue-800 text-white">
                    <LogIn size={16} className="mr-1" />
                    Login
                  </Button>
                </Link>
              )}
              <Link to="/faq">
                <Button className="bg-blue-700 hover:bg-blue-800 text-white shadow-md">
                  <HelpCircle size={16} className="mr-2" />
                  FAQ
                </Button>
              </Link>
              <Link to="/projeto-ambiental">
                <Button className="bg-orange-500 hover:bg-orange-600 text-white shadow-md">
                  <Leaf size={16} className="mr-2" />
                  Projeto Ambiental
                </Button>
              </Link>
            </div>

            {/* Mobile Menu Button */}
            <button
              className="md:hidden rounded-lg border border-blue-100 bg-white/80 p-2"
              onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            >
              {mobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
            </button>
          </div>

          {/* Mobile Menu */}
          {mobileMenuOpen && (
            <div className="animate-enter md:hidden py-4 space-y-4">
              {user && (
                <div className="flex items-center rounded-lg border border-blue-200 bg-blue-50 px-4 py-3 text-sm font-semibold text-blue-900 shadow-sm">
                  <span className="mr-2 h-2.5 w-2.5 shrink-0 rounded-full bg-blue-500 animate-pulse" />
                  <User size={16} className="mr-2 shrink-0 text-blue-700" />
                  <span className="truncate">{user.nome}</span>
                </div>
              )}
              <div className="rounded-lg border border-blue-100 bg-blue-50/70 px-4 py-3 text-xs font-semibold uppercase tracking-[0.12em] text-blue-700">
                Trabalho guiado por confiança, respeito e precisão
              </div>
              <div className="flex flex-col gap-2 pt-4">
                {user ? (
                  <Link to="/selecionar-perfil" onClick={() => setMobileMenuOpen(false)}>
                    <Button variant="outline" className="w-full border-blue-600 text-blue-700 hover:bg-blue-50">
                      <User size={16} className="mr-2" />
                      {user.nome?.split(" ")[0] ?? "Minha Conta"}
                    </Button>
                  </Link>
                ) : (
                  <Link to="/acesso" onClick={() => setMobileMenuOpen(false)}>
                    <Button className="w-full bg-blue-700 hover:bg-blue-800 text-white">
                      <LogIn size={16} className="mr-2" />
                      Login
                    </Button>
                  </Link>
                )}
                <Link to="/faq" onClick={() => setMobileMenuOpen(false)}>
                  <Button className="w-full bg-blue-700 hover:bg-blue-800 text-white">
                    <HelpCircle size={16} className="mr-2" />
                    FAQ
                  </Button>
                </Link>
                <Link to="/projeto-ambiental" onClick={() => setMobileMenuOpen(false)}>
                  <Button className="w-full bg-orange-500 hover:bg-orange-600 text-white">
                    <Leaf size={16} className="mr-2" />
                    Projeto Ambiental
                  </Button>
                </Link>
              </div>
            </div>
          )}
        </div>
      </header>

      {/* Main Content */}
      <main className="flex-1 animate-enter">
        <Outlet />
      </main>

      {/* Footer */}
      <footer className="mt-8 border-t border-blue-100 bg-slate-900 text-white py-12">
        <div className="page-shell">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
            <div>
              <SiteLogo to="/" />
              <p className="mt-3 max-w-xs text-slate-300 text-sm leading-relaxed">
                Conectando você aos melhores profissionais
              </p>
            </div>
            <div>
              <h3 className="font-semibold mb-4 text-slate-100">Para Clientes</h3>
              <ul className="space-y-2 text-slate-300">
                <li>
                  <Link to="/acesso?tipo=cliente&redirect=/dashboard/meus-pedidos" className="hover:text-orange-300">
                    Solicitar Serviço
                  </Link>
                </li>
                <li>
                  <Link to="/buscar-prestadores" className="hover:text-orange-300">
                    Buscar Prestadores
                  </Link>
                </li>
                <li>
                  <a href="#" className="hover:text-orange-300">Como Funciona</a>
                </li>
              </ul>
            </div>
            <div>
              <h3 className="font-semibold mb-4 text-slate-100">Para Profissionais</h3>
              <ul className="space-y-2 text-slate-300">
                <li>
                  <Link to="/acesso?tipo=prestador&redirect=/prestador" className="hover:text-orange-300">
                    Cadastre-se
                  </Link>
                </li>
                <li>
                  <Link to="/faq" className="hover:text-orange-300">FAQ</Link>
                </li>
              </ul>
            </div>
            <div>
              <h3 className="font-semibold mb-4 text-slate-100">Contato</h3>
              <ul className="space-y-2 text-slate-300">
                <li>
                  <Link to="/centro-ajuda" className="hover:text-orange-300">Centro de Ajuda</Link>
                </li>
                <li>
                  <a href="#" className="hover:text-orange-300">Termos de Uso</a>
                </li>
              </ul>
            </div>
          </div>
          <div className="border-t border-slate-700/70 mt-8 pt-8 text-center text-slate-300 text-sm">
            <p>&copy; 2026 FazTudoJA. Todos os direitos reservados.</p>
          </div>
        </div>
      </footer>
    </div>
  );
}