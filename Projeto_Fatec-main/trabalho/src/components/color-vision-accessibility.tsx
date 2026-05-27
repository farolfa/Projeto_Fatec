import { Eye, Palette } from "lucide-react";
import { useEffect, useMemo, useState } from "react";
import { createPortal } from "react-dom";

type ColorVisionMode = "default" | "deuteranopia" | "protanopia" | "tritanopia" | "high-contrast";

const STORAGE_KEY = "faztudoja-color-vision-mode";

const modeOptions: Array<{ mode: ColorVisionMode; label: string; description: string }> = [
  { mode: "default", label: "Padrao", description: "Cores originais do site" },
  { mode: "deuteranopia", label: "Deuteranopia", description: "Aumenta contraste entre verde e vermelho" },
  { mode: "protanopia", label: "Protanopia", description: "Melhora diferenciacao de tons vermelhos" },
  { mode: "tritanopia", label: "Tritanopia", description: "Ajusta contraste entre azul e amarelo" },
  { mode: "high-contrast", label: "Alto contraste", description: "Maximiza separacao visual dos elementos" },
];

function applyColorVisionMode(mode: ColorVisionMode) {
  const root = document.documentElement;
  const appRoot = document.getElementById("root");
  const modeClasses = [
    "color-vision-deuteranopia",
    "color-vision-protanopia",
    "color-vision-tritanopia",
    "color-vision-high-contrast",
  ];

  appRoot?.classList.remove(...modeClasses);

  if (mode === "default") {
    delete root.dataset.colorVision;
    return;
  }

  root.dataset.colorVision = mode;
  appRoot?.classList.add(`color-vision-${mode}`);
}

export function ColorVisionAccessibility() {
  const [isOpen, setIsOpen] = useState(false);
  const [mode, setMode] = useState<ColorVisionMode>("default");
  const [isMounted, setIsMounted] = useState(false);

  useEffect(() => {
    setIsMounted(true);
  }, []);

  useEffect(() => {
    const savedMode = window.localStorage.getItem(STORAGE_KEY) as ColorVisionMode | null;
    const nextMode = savedMode ?? "default";

    setMode(nextMode);
    applyColorVisionMode(nextMode);
  }, []);

  useEffect(() => {
    applyColorVisionMode(mode);
    window.localStorage.setItem(STORAGE_KEY, mode);
  }, [mode]);

  useEffect(() => {
    const closeOnEscape = (event: KeyboardEvent) => {
      if (event.key === "Escape") {
        setIsOpen(false);
      }
    };

    window.addEventListener("keydown", closeOnEscape);
    return () => window.removeEventListener("keydown", closeOnEscape);
  }, []);

  const activeOption = useMemo(
    () => modeOptions.find((option) => option.mode === mode) ?? modeOptions[0],
    [mode],
  );

  if (!isMounted) {
    return null;
  }

  return createPortal(
    <>
      {isOpen && (
        <div
          id="color-vision-panel"
          className="w-[19rem] rounded-2xl border border-blue-200/90 bg-white/95 p-4 shadow-[0_18px_40px_-20px_rgba(15,45,90,0.65)] backdrop-blur"
          style={{ position: "fixed", right: "1rem", bottom: "5.2rem", zIndex: 2147482999 }}
          role="dialog"
          aria-label="Acessibilidade para daltonismo"
        >
          <div className="mb-3 flex items-start gap-2">
            <Palette className="mt-0.5 h-4 w-4 text-blue-700" aria-hidden="true" />
            <div>
              <p className="text-sm font-semibold text-slate-900">Acessibilidade de Cores</p>
              <p className="text-xs text-slate-600">Modo atual: {activeOption.label}</p>
            </div>
          </div>

          <div className="space-y-2">
            {modeOptions.map((option) => {
              const isActive = option.mode === mode;
              return (
                <button
                  key={option.mode}
                  type="button"
                  className={`w-full rounded-xl border px-3 py-2 text-left transition ${
                    isActive
                      ? "border-blue-500 bg-blue-50 text-blue-900"
                      : "border-slate-200 bg-white text-slate-800 hover:border-blue-300 hover:bg-blue-50/60"
                  }`}
                  onClick={() => setMode(option.mode)}
                  aria-pressed={isActive}
                >
                  <p className="text-sm font-medium">{option.label}</p>
                  <p className="text-xs text-slate-600">{option.description}</p>
                </button>
              );
            })}
          </div>

          <button
            type="button"
            className="mt-3 w-full rounded-xl bg-slate-100 px-3 py-2 text-xs font-semibold text-slate-700 hover:bg-slate-200"
            onClick={() => setIsOpen(false)}
          >
            Fechar
          </button>
        </div>
      )}

      <button
        type="button"
        className="flex items-center gap-2 rounded-full border border-blue-600 bg-blue-700 px-4 py-3 text-sm font-semibold text-white shadow-lg transition hover:bg-blue-800 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-blue-300"
        style={{ position: "fixed", right: "1rem", bottom: "1rem", zIndex: 2147483000 }}
        onClick={() => setIsOpen((open) => !open)}
        aria-expanded={isOpen}
        aria-controls="color-vision-panel"
      >
        <Eye className="h-4 w-4" aria-hidden="true" />
        Acessibilidade
      </button>
    </>,
    document.body,
  );
}
