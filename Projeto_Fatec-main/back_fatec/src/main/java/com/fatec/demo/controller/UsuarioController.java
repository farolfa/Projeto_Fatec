package com.fatec.demo.controller;

import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;

import com.fatec.demo.model.Usuario;
import com.fatec.demo.service.UsuarioService;

@RestController
@RequestMapping(value = "/usuario")
public class UsuarioController {

    private static final Logger logger = Logger.getLogger(UsuarioController.class.getName());

    @Autowired
    private UsuarioService service;

    @GetMapping
    public ResponseEntity<?> findAll(){
        try {
            List<Usuario> u = service.findAll();
            if (u == null || u.isEmpty()) {
                logger.info("Nenhum usuário encontrado");
                return ResponseEntity.ok().body(u);
            }
            return ResponseEntity.ok().body(u);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar usuários", e);
            return ResponseEntity.status(500).body("Erro ao buscar usuários: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido: deve ser um número positivo");
            }
            Usuario u = service.findById(id);
            if (u == null) {
                logger.log(Level.WARNING, "Usuário não encontrado com ID: {0}", id);
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok().body(u);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar usuário com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao buscar usuário: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Usuario usuario){
        try {
            if (usuario == null) {
                return ResponseEntity.badRequest().body("Usuário não pode ser nulo");
            }
            if (usuario.getNome() == null || usuario.getNome().isBlank()) {
                return ResponseEntity.badRequest().body("Nome é obrigatório");
            }
            if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
                return ResponseEntity.badRequest().body("E-mail é obrigatório");
            }
            Usuario u = service.save(usuario);
            if (u == null) {
                return ResponseEntity.status(500).body("Erro ao salvar usuário");
            }
            return ResponseEntity.status(201).body(u);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao salvar usuário", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar usuário", e);
            return ResponseEntity.status(500).body("Erro ao salvar usuário: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario){
        try {
            if (usuario == null) {
                return ResponseEntity.badRequest().body("Dados do usuário não podem ser nulos");
            }
            Usuario u = service.register(usuario);
            if (u == null) {
                return ResponseEntity.status(500).body("Erro ao registrar usuário");
            }
            u.setSenha(null);
            logger.log(Level.INFO, "Usuário registrado com sucesso: {0}", u.getEmail());
            return ResponseEntity.status(201).body(u);
        } catch (IllegalArgumentException ex) {
            logger.log(Level.WARNING, "Erro de validação ao registrar usuário", ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Erro ao registrar usuário", ex);
            return ResponseEntity.status(500).body("Erro ao registrar usuário: " + ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario){
        try {
            if (usuario == null) {
                return ResponseEntity.badRequest().body("Dados de login não podem ser nulos");
            }
            if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
                return ResponseEntity.badRequest().body("E-mail é obrigatório");
            }
            if (usuario.getSenha() == null || usuario.getSenha().isBlank()) {
                return ResponseEntity.badRequest().body("Senha é obrigatória");
            }

            Usuario u = service.login(usuario.getEmail(), usuario.getSenha());
            if (u == null) {
                logger.log(Level.WARNING, "Falha na tentativa de login com e-mail: {0}", usuario.getEmail());
                return ResponseEntity.status(401).body("E-mail ou senha inválidos");
            }

            u.setSenha(null);
            logger.log(Level.INFO, "Login bem-sucedido para: {0}", u.getEmail());
            return ResponseEntity.ok(u);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao fazer login", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao fazer login", e);
            return ResponseEntity.status(500).body("Erro ao fazer login: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Usuario usuario){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido: deve ser um número positivo");
            }
            if (usuario == null) {
                return ResponseEntity.badRequest().body("Dados do usuário não podem ser nulos");
            }

            Usuario u = service.update(id, usuario);
            u.setSenha(null);
            return ResponseEntity.ok(u);
        } catch (IllegalArgumentException ex) {
            logger.log(Level.WARNING, "Erro de validação ao atualizar usuário", ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Erro ao atualizar usuário", ex);
            return ResponseEntity.status(500).body("Erro ao atualizar usuário: " + ex.getMessage());
        }
    }

    @PutMapping("/{id}/perfil")
    public ResponseEntity<?> switchPerfil(@PathVariable Long id, @RequestBody Usuario usuario){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido: deve ser um número positivo");
            }
            if (usuario == null || usuario.getTipo() == null || usuario.getTipo().isBlank()) {
                return ResponseEntity.badRequest().body("Tipo é obrigatório");
            }

            Usuario atualizado = service.switchPerfil(id, usuario.getTipo());
            atualizado.setSenha(null);
            return ResponseEntity.ok(atualizado);
        } catch (IllegalArgumentException ex) {
            logger.log(Level.WARNING, "Erro de validação ao alternar perfil", ex);
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Erro ao alternar perfil", ex);
            return ResponseEntity.status(500).body("Erro ao alternar perfil: " + ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido: deve ser um número positivo");
            }
            service.delete(id);
            logger.log(Level.INFO, "Usuário deletado com ID: {0}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar usuário com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao deletar usuário: " + e.getMessage());
        }
    }
}
