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

import com.fatec.demo.model.Participante;
import com.fatec.demo.service.ParticipanteService;

@RestController
@RequestMapping(value = "/participante")
public class ParticipanteController {

    private static final Logger logger = Logger.getLogger(ParticipanteController.class.getName());

    @Autowired
    private ParticipanteService service;

    @GetMapping
    public ResponseEntity<?> findAll(){
        try {
            List<Participante> p = service.findAll();
            return ResponseEntity.ok().body(p);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar participantes", e);
            return ResponseEntity.status(500).body("Erro ao buscar participantes: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido: deve ser um número positivo");
            }
            Participante p = service.findById(id);
            return p != null ? ResponseEntity.ok().body(p) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar participante com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao buscar participante: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Participante participante){
        try {
            if (participante == null) {
                return ResponseEntity.badRequest().body("Participante não pode ser nulo");
            }
            Participante p = service.save(participante);
            logger.info("Participante salvo com sucesso");
            return ResponseEntity.status(201).body(p);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao salvar participante", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar participante", e);
            return ResponseEntity.status(500).body("Erro ao salvar participante: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido: deve ser um número positivo");
            }
            service.delete(id);
            logger.info("Participante deletado com ID: " + id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar participante com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao deletar participante: " + e.getMessage());
        }
    }}
