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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.demo.model.Pedido;
import com.fatec.demo.service.PedidoService;

@RestController
@RequestMapping(value = "/pedido")
public class PedidoController {

    private static final Logger logger = Logger.getLogger(PedidoController.class.getName());

    @Autowired
    private PedidoService service;

    @GetMapping
    public ResponseEntity<?> findAll(){
        try {
            List<Pedido> pe = service.findAll();
            return ResponseEntity.ok().body(pe);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar pedidos", e);
            return ResponseEntity.status(500).body("Erro ao buscar pedidos: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> findByUsuario(@RequestParam Long usuarioId){
        try {
            if (usuarioId == null || usuarioId <= 0) {
                return ResponseEntity.badRequest().body("ID de usuário inválido");
            }
            List<Pedido> pe = service.findByUsuarioId(usuarioId);
            return ResponseEntity.ok().body(pe);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar pedidos do usuário", e);
            return ResponseEntity.status(500).body("Erro ao buscar pedidos: " + e.getMessage());
        }
    }

    @GetMapping("/abertos")
    public ResponseEntity<?> findOpenForPrestador(@RequestParam Long prestadorId){
        try {
            if (prestadorId == null || prestadorId <= 0) {
                return ResponseEntity.badRequest().body("ID de prestador inválido");
            }
            // Exibe apenas pedidos ABERTO de clientes diferentes do prestador
            List<Pedido> pe = service.findByUsuarioIdNotAndStatus(prestadorId, "ABERTO");
            return ResponseEntity.ok().body(pe);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar pedidos abertos", e);
            return ResponseEntity.status(500).body("Erro ao buscar pedidos: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> findByStatus(@RequestParam String status){
        try {
            if (status == null || status.isBlank()) {
                return ResponseEntity.badRequest().body("Status é obrigatório");
            }
            List<Pedido> pe = service.findByStatus(status);
            return ResponseEntity.ok().body(pe);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar pedidos por status", e);
            return ResponseEntity.status(500).body("Erro ao buscar pedidos: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido: deve ser um número positivo");
            }
            Pedido pe = service.findById(id);
            return pe != null ? ResponseEntity.ok().body(pe) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar pedido com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao buscar pedido: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Pedido pedido){
        try {
            if (pedido == null) {
                return ResponseEntity.badRequest().body("Pedido não pode ser nulo");
            }
            Pedido pe = service.save(pedido);
            logger.info("Pedido salvo com sucesso");
            return ResponseEntity.status(201).body(pe);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao salvar pedido", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar pedido", e);
            return ResponseEntity.status(500).body("Erro ao salvar pedido: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Pedido pedido){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido");
            }
            Pedido updated = service.update(id, pedido);
            return updated != null ? ResponseEntity.ok().body(updated) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao atualizar pedido", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao atualizar pedido com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao atualizar pedido: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/confirmar-conclusao")
    public ResponseEntity<?> confirmarConclusao(@PathVariable Long id, @RequestParam String tipo){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido");
            }
            Pedido updated = service.confirmarConclusao(id, tipo);
            return updated != null ? ResponseEntity.ok().body(updated) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao confirmar conclusão", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao confirmar conclusão do pedido: " + id, e);
            return ResponseEntity.status(500).body("Erro ao confirmar conclusão: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido: deve ser um número positivo");
            }
            service.delete(id);
            logger.info("Pedido deletado com ID: " + id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar pedido com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao deletar pedido: " + e.getMessage());
        }
    }
}
