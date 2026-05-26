package com.fatec.demo.controller;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.demo.model.Categoria;
import com.fatec.demo.service.CategoriaService;

@RestController
@RequestMapping(value = "/categoria")
public class CategoriaController {

    private static final Logger logger = Logger.getLogger(CategoriaController.class.getName());

    @Autowired
    private CategoriaService service;

    @GetMapping
    public ResponseEntity<?> findAll() {
        try {
            List<Categoria> c = service.findAll();
            return ResponseEntity.ok().body(c);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar categorias", e);
            return ResponseEntity.status(500).body("Erro ao buscar categorias: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido: deve ser um número positivo");
            }
            Categoria c = service.findById(id);
            return c != null ? ResponseEntity.ok().body(c) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar categoria com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao buscar categoria: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Categoria categoria) {
        try {
            if (categoria == null) {
                return ResponseEntity.badRequest().body("Categoria não pode ser nula");
            }
            Categoria c = service.save(categoria);
            logger.info("Categoria salva com sucesso");
            return ResponseEntity.status(201).body(c);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao salvar categoria", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar categoria", e);
            return ResponseEntity.status(500).body("Erro ao salvar categoria: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido: deve ser um número positivo");
            }
            service.delete(id);
            logger.info("Categoria deletada com ID: " + id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar categoria com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao deletar categoria: " + e.getMessage());
        }
    }
}
