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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.demo.model.ServicoCatalogo;
import com.fatec.demo.service.ServicoCatalogoService;

@RestController
@RequestMapping(value = "/servico-catalogo")
public class ServicoCatalogoController {

    private static final Logger logger = Logger.getLogger(ServicoCatalogoController.class.getName());

    @Autowired
    private ServicoCatalogoService service;

    @GetMapping
    public ResponseEntity<?> findAll() {
        try {
            List<ServicoCatalogo> catalogo = service.findAll();
            return ResponseEntity.ok().body(catalogo);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar servicos de catalogo", e);
            return ResponseEntity.status(500).body("Erro ao buscar servicos de catalogo: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID invalido: deve ser um numero positivo");
            }
            ServicoCatalogo item = service.findById(id);
            return item != null ? ResponseEntity.ok().body(item) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar servico de catalogo com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao buscar servico de catalogo: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody ServicoCatalogo servicoCatalogo) {
        try {
            if (servicoCatalogo == null) {
                return ResponseEntity.badRequest().body("Servico de catalogo nao pode ser nulo");
            }
            ServicoCatalogo saved = service.save(servicoCatalogo);
            return ResponseEntity.status(201).body(saved);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validacao ao salvar servico de catalogo", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar servico de catalogo", e);
            return ResponseEntity.status(500).body("Erro ao salvar servico de catalogo: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID invalido: deve ser um numero positivo");
            }
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validacao ao deletar servico de catalogo", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar servico de catalogo com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao deletar servico de catalogo: " + e.getMessage());
        }
    }
}
