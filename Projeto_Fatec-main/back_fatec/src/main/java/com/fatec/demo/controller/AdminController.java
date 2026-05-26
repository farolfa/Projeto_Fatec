package com.fatec.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.demo.model.Avaliacao;
import com.fatec.demo.model.Categoria;
import com.fatec.demo.model.Pedido;
import com.fatec.demo.model.Usuario;
import com.fatec.demo.repository.AvaliacaoRepository;
import com.fatec.demo.repository.CategoriaRepository;
import com.fatec.demo.repository.PedidoRepository;
import com.fatec.demo.repository.UsuarioRepository;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = Logger.getLogger(AdminController.class.getName());

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    /** Verifica se o adminId fornecido é realmente um ADMIN */
    private boolean isAdmin(Long adminId) {
        if (adminId == null || adminId <= 0) return false;
        return usuarioRepository.findById(adminId)
                .map(Usuario::isAdmin)
                .orElse(false);
    }

    /** Verifica se o adminId fornecido é ADMIN PRINCIPAL (status 11) */
    private boolean isAdminPrincipal(Long adminId) {
        if (adminId == null || adminId <= 0) return false;
        return usuarioRepository.findById(adminId)
                .map(Usuario::isAdminPrincipal)
                .orElse(false);
    }

    // ─── Stats ───────────────────────────────────────────────────────────────

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@RequestParam Long adminId) {
        if (!isAdmin(adminId)) return ResponseEntity.status(403).body("Acesso negado");

        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsuarios", usuarioRepository.count());
            stats.put("totalPedidos", pedidoRepository.count());
            stats.put("totalAvaliacoes", avaliacaoRepository.count());
            stats.put("totalCategorias", categoriaRepository.count());

                long totalClientes = usuarioRepository.findAll().stream()
                    .filter(u -> u.getStatus() != null && u.getStatus() == Usuario.STATUS_CLIENTE)
                    .count();
                long totalPrestadores = usuarioRepository.findAll().stream()
                    .filter(u -> u.getStatus() != null && u.getStatus() == Usuario.STATUS_PRESTADOR)
                    .count();

            stats.put("totalClientes", totalClientes);
            stats.put("totalPrestadores", totalPrestadores);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar stats", e);
            return ResponseEntity.status(500).body("Erro ao buscar estatísticas");
        }
    }

    // ─── Usuários ─────────────────────────────────────────────────────────────

    @GetMapping("/usuarios")
    public ResponseEntity<?> getAllUsuarios(@RequestParam Long adminId) {
        if (!isAdmin(adminId)) return ResponseEntity.status(403).body("Acesso negado");

        try {
            List<Usuario> usuarios = usuarioRepository.findAll();
            usuarios.forEach(u -> u.setSenha(null)); // nunca expor senha
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar usuários", e);
            return ResponseEntity.status(500).body("Erro ao buscar usuários");
        }
    }

    @PutMapping("/usuario/{id}/ativo")
    public ResponseEntity<?> toggleAtivo(@PathVariable Long id, @RequestParam Long adminId) {
        if (!isAdmin(adminId)) return ResponseEntity.status(403).body("Acesso negado");
        if (id.equals(adminId)) return ResponseEntity.badRequest().body("Não é possível desativar a própria conta");

        return usuarioRepository.findById(id).map(u -> {
            if (u.isAdmin() && !isAdminPrincipal(adminId)) {
                return ResponseEntity.status(403).body("Somente ADMIN PRINCIPAL pode bloquear admins");
            }
            u.setAtivo(!u.isAtivo());
            usuarioRepository.save(u);
            u.setSenha(null);
            return ResponseEntity.ok(u);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/usuario/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam Long adminId, @RequestBody Map<String, Integer> payload) {
        if (!isAdminPrincipal(adminId)) return ResponseEntity.status(403).body("Somente ADMIN PRINCIPAL pode alterar status");
        if (id.equals(adminId)) return ResponseEntity.badRequest().body("Não é possível alterar o próprio status");

        Integer status = payload.get("status");
        if (status == null) return ResponseEntity.badRequest().body("Status é obrigatório");

        boolean statusValido = status == Usuario.STATUS_PRESTADOR
                || status == Usuario.STATUS_CLIENTE
                || status == Usuario.STATUS_ADMIN
                || status == Usuario.STATUS_ADMIN_PRINCIPAL;
        if (!statusValido) return ResponseEntity.badRequest().body("Status inválido");

        return usuarioRepository.findById(id).map(u -> {
            u.setStatus(status);
            usuarioRepository.save(u);
            u.setSenha(null);
            return ResponseEntity.ok(u);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/usuario/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable Long id, @RequestParam Long adminId) {
        if (!isAdmin(adminId)) return ResponseEntity.status(403).body("Acesso negado");
        if (id.equals(adminId)) return ResponseEntity.badRequest().body("Não é possível excluir a própria conta");

        Usuario alvo = usuarioRepository.findById(id).orElse(null);
        if (alvo == null) return ResponseEntity.notFound().build();
        if (alvo.isAdmin() && !isAdminPrincipal(adminId)) {
            return ResponseEntity.status(403).body("Somente ADMIN PRINCIPAL pode excluir admins");
        }

        try {
            usuarioRepository.deleteById(id);
            return ResponseEntity.ok("Usuário excluído");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao excluir usuário " + id, e);
            return ResponseEntity.status(500).body("Erro ao excluir usuário (pode ter registros vinculados)");
        }
    }

    // ─── Pedidos ──────────────────────────────────────────────────────────────

    @GetMapping("/pedidos")
    public ResponseEntity<?> getAllPedidos(@RequestParam Long adminId) {
        if (!isAdmin(adminId)) return ResponseEntity.status(403).body("Acesso negado");

        try {
            List<Pedido> pedidos = pedidoRepository.findAll();
            return ResponseEntity.ok(pedidos);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar pedidos", e);
            return ResponseEntity.status(500).body("Erro ao buscar pedidos");
        }
    }

    @DeleteMapping("/pedido/{id}")
    public ResponseEntity<?> deletePedido(@PathVariable Long id, @RequestParam Long adminId) {
        if (!isAdmin(adminId)) return ResponseEntity.status(403).body("Acesso negado");
        if (!pedidoRepository.existsById(id)) return ResponseEntity.notFound().build();
        try {
            pedidoRepository.deleteById(id);
            return ResponseEntity.ok("Pedido excluído");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao excluir pedido " + id, e);
            return ResponseEntity.status(500).body("Erro ao excluir pedido");
        }
    }

    // ─── Avaliações ───────────────────────────────────────────────────────────

    @GetMapping("/avaliacoes")
    public ResponseEntity<?> getAllAvaliacoes(@RequestParam Long adminId) {
        if (!isAdmin(adminId)) return ResponseEntity.status(403).body("Acesso negado");

        try {
            List<Avaliacao> avaliacoes = avaliacaoRepository.findAll();
            return ResponseEntity.ok(avaliacoes);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar avaliações", e);
            return ResponseEntity.status(500).body("Erro ao buscar avaliações");
        }
    }

    @DeleteMapping("/avaliacao/{id}")
    public ResponseEntity<?> deleteAvaliacao(@PathVariable Long id, @RequestParam Long adminId) {
        if (!isAdmin(adminId)) return ResponseEntity.status(403).body("Acesso negado");
        if (!avaliacaoRepository.existsById(id)) return ResponseEntity.notFound().build();
        avaliacaoRepository.deleteById(id);
        return ResponseEntity.ok("Avaliação excluída");
    }

    // ─── Categorias ───────────────────────────────────────────────────────────

    @PostMapping("/categoria")
    public ResponseEntity<?> createCategoria(@RequestBody Categoria categoria, @RequestParam Long adminId) {
        if (!isAdmin(adminId)) return ResponseEntity.status(403).body("Acesso negado");
        if (categoria.getNome() == null || categoria.getNome().isBlank()) {
            return ResponseEntity.badRequest().body("Nome da categoria é obrigatório");
        }
        Categoria saved = categoriaRepository.save(categoria);
        return ResponseEntity.status(201).body(saved);
    }

    @DeleteMapping("/categoria/{id}")
    public ResponseEntity<?> deleteCategoria(@PathVariable Long id, @RequestParam Long adminId) {
        if (!isAdmin(adminId)) return ResponseEntity.status(403).body("Acesso negado");
        if (!categoriaRepository.existsById(id)) return ResponseEntity.notFound().build();
        try {
            categoriaRepository.deleteById(id);
            return ResponseEntity.ok("Categoria excluída");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao excluir categoria " + id, e);
            return ResponseEntity.status(500).body("Erro ao excluir categoria (pode ter serviços vinculados)");
        }
    }
}
