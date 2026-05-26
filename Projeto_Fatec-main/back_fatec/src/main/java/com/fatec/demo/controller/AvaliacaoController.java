package com.fatec.demo.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.demo.model.Avaliacao;
import com.fatec.demo.service.AvaliacaoService;

@RestController
@RequestMapping(value = "/avaliacao")
public class AvaliacaoController {
    
    private static final Logger logger = Logger.getLogger(AvaliacaoController.class.getName());

    @Autowired
    private AvaliacaoService avaliacaoService;

    @GetMapping
    public ResponseEntity<?> findAll(){
        try {
            List <Avaliacao> a = avaliacaoService.findAll();
            return ResponseEntity.ok().body(a);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar avaliações", e);
            return ResponseEntity.status(500).body("Erro ao buscar avaliações: " + e.getMessage());
        }
    }

    @GetMapping("/avaliado/{avaliadoId}")
    public ResponseEntity<?> findByAvaliadoId(@PathVariable Long avaliadoId) {
        try {
            return ResponseEntity.ok(avaliacaoService.findByAvaliadoId(avaliadoId));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar avaliações do avaliado " + avaliadoId, e);
            return ResponseEntity.status(500).body("Erro ao buscar avaliações");
        }
    }

    @GetMapping("/avaliador/{avaliadorId}")
    public ResponseEntity<?> findByAvaliadorId(@PathVariable Long avaliadorId) {
        try {
            return ResponseEntity.ok(avaliacaoService.findByAvaliadorId(avaliadorId));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar avaliações do avaliador " + avaliadorId, e);
            return ResponseEntity.status(500).body("Erro ao buscar avaliações");
        }
    }

    @GetMapping("/avaliado/{avaliadoId}/media")
    public ResponseEntity<?> getMedia(@PathVariable Long avaliadoId) {
        try {
            return ResponseEntity.ok(avaliacaoService.getMedia(avaliadoId));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao calcular média do avaliado " + avaliadoId, e);
            return ResponseEntity.status(500).body("Erro ao calcular média");
        }
    }

    @GetMapping("/ja-avaliou")
    public ResponseEntity<?> jaAvaliou(@RequestParam Long avaliadorId, @RequestParam Long avaliadoId) {
        try {
            boolean jaAvaliou = avaliacaoService.jaAvaliou(avaliadorId, avaliadoId);
            return ResponseEntity.ok(Map.of("jaAvaliou", jaAvaliou));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao verificar avaliação", e);
            return ResponseEntity.status(500).body("Erro ao verificar avaliação");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido: deve ser um número positivo");
            }
            Avaliacao a = avaliacaoService.findById(id);
            return a != null ? ResponseEntity.ok().body(a) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar avaliação com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao buscar avaliação: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Avaliacao avaliacao){
        try {
            if (avaliacao == null) {
                return ResponseEntity.badRequest().body("Avaliação não pode ser nula");
            }
            Avaliacao a = avaliacaoService.save(avaliacao);
            logger.info("Avaliação salva com sucesso");
            return ResponseEntity.status(201).body(a);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao salvar avaliação", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar avaliação", e);
            return ResponseEntity.status(500).body("Erro ao salvar avaliação: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido: deve ser um número positivo");
            }
            avaliacaoService.delete(id);
            logger.info("Avaliação deletada com ID: " + id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar avaliação com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao deletar avaliação: " + e.getMessage());
        }
    }}
