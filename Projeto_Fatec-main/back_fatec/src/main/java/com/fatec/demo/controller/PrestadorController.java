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

import com.fatec.demo.model.Prestador;
import com.fatec.demo.service.PrestadorService;

@RestController
@RequestMapping(value = "/prestador")
public class PrestadorController {

    private static final Logger logger = Logger.getLogger(PrestadorController.class.getName());

    @Autowired
    private PrestadorService service;

    @GetMapping
    public ResponseEntity<?> findAll() {
        try {
            List<Prestador> prestadores = service.findAll();
            return ResponseEntity.ok().body(prestadores);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar prestadores", e);
            return ResponseEntity.status(500).body("Erro ao buscar prestadores: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID invalido: deve ser um numero positivo");
            }
            Prestador prestador = service.findById(id);
            return prestador != null ? ResponseEntity.ok().body(prestador) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar prestador com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao buscar prestador: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Prestador prestador) {
        try {
            if (prestador == null) {
                return ResponseEntity.badRequest().body("Prestador nao pode ser nulo");
            }
            Prestador saved = service.save(prestador);
            return ResponseEntity.status(201).body(saved);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validacao ao salvar prestador", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar prestador", e);
            return ResponseEntity.status(500).body("Erro ao salvar prestador: " + e.getMessage());
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
            logger.log(Level.WARNING, "Erro de validacao ao deletar prestador", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar prestador com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao deletar prestador: " + e.getMessage());
        }
    }
}
