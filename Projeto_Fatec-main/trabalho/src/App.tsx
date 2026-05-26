import { RouterProvider } from "react-router";
import { router } from "./routes";
import { useEffect, Suspense } from "react";
import { clearSessionUser, getSessionUser } from "./services/auth";

export default function App() {
  useEffect(() => {
    const controller = new AbortController();
    const timeoutId = window.setTimeout(() => controller.abort(), 5000);

    const validateSession = async () => {
      const storedUser = getSessionUser();
      if (!storedUser?.id) return;

      try {
        const userRes = await fetch(`/api/usuario/${storedUser.id}`, {
          signal: controller.signal,
        });
        // Only clear session on explicit auth/not-found errors
        if (userRes.status === 401 || userRes.status === 403 || userRes.status === 404) {
          clearSessionUser();
          const isProtected =
            window.location.pathname.startsWith("/dashboard") ||
            window.location.pathname.startsWith("/prestador");
          if (isProtected) window.location.replace("/");
        }
      } catch {
        // Network error or timeout — keep session, do not log out
      } finally {
        window.clearTimeout(timeoutId);
      }
    };

    void validateSession();
    return () => {
      window.clearTimeout(timeoutId);
      controller.abort();
    };
  }, []);

  return (
    <Suspense fallback={null}>
      <RouterProvider router={router} />
    </Suspense>
  );
}

