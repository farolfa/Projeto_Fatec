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

import com.fatec.demo.model.Proposta;
import com.fatec.demo.service.PropostaService;

@RestController
@RequestMapping(value = "/proposta")
public class PropostaController {

    private static final Logger logger = Logger.getLogger(PropostaController.class.getName());

    @Autowired
    private PropostaService service;

    @GetMapping
    public ResponseEntity<?> findAll(){
        try {
            List<Proposta> pr = service.findAll();
            return ResponseEntity.ok().body(pr);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar propostas", e);
            return ResponseEntity.status(500).body("Erro ao buscar propostas: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido: deve ser um número positivo");
            }
            Proposta pr = service.findById(id);
            return pr != null ? ResponseEntity.ok().body(pr) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar proposta com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao buscar proposta: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Proposta proposta){
        try {
            if (proposta == null) {
                return ResponseEntity.badRequest().body("Proposta não pode ser nula");
            }
            Proposta pr = service.save(proposta);
            logger.info("Proposta salva com sucesso");
            return ResponseEntity.status(201).body(pr);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao salvar proposta", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar proposta", e);
            return ResponseEntity.status(500).body("Erro ao salvar proposta: " + e.getMessage());
        }
    }

    @GetMapping("/pedido/{pedidoId}")
    public ResponseEntity<?> findByPedido(@PathVariable Long pedidoId){
        try {
            return ResponseEntity.ok().body(service.findByPedidoId(pedidoId));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar propostas do pedido", e);
            return ResponseEntity.status(500).body("Erro: " + e.getMessage());
        }
    }

    @GetMapping("/prestador/{prestadorId}")
    public ResponseEntity<?> findByPrestador(@PathVariable Long prestadorId){
        try {
            return ResponseEntity.ok().body(service.findByPrestadorId(prestadorId));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar propostas do prestador", e);
            return ResponseEntity.status(500).body("Erro: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Proposta proposta){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido");
            }
            Proposta updated = service.update(id, proposta);
            return updated != null ? ResponseEntity.ok().body(updated) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao atualizar proposta", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao atualizar proposta com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao atualizar proposta: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/aceitar")
    public ResponseEntity<?> aceitar(@PathVariable Long id){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido");
            }
            Proposta accepted = service.aceitar(id);
            return accepted != null ? ResponseEntity.ok().body(accepted) : ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao aceitar proposta", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao aceitar proposta: " + id, e);
            return ResponseEntity.status(500).body("Erro ao aceitar proposta: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido: deve ser um número positivo");
            }
            service.delete(id);
            logger.info("Proposta deletada com ID: " + id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar proposta com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao deletar proposta: " + e.getMessage());
        }
    }
}
