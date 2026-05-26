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

import com.fatec.demo.model.ServicoOferecido;
import com.fatec.demo.service.ServicoOferecidoService;

@RestController
@RequestMapping(value = "/servico-oferecido")
public class ServicoOferecidoController {

    private static final Logger logger = Logger.getLogger(ServicoOferecidoController.class.getName());

    @Autowired
    private ServicoOferecidoService service;

    @GetMapping
    public ResponseEntity<?> findAll(){
        try {
            List<ServicoOferecido> s = service.findAll();
            return ResponseEntity.ok().body(s);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar serviços oferecidos", e);
            return ResponseEntity.status(500).body("Erro ao buscar serviços: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido: deve ser um número positivo");
            }
            ServicoOferecido s = service.findById(id);
            return s != null ? ResponseEntity.ok().body(s) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar serviço com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao buscar serviço: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody ServicoOferecido servicoOferecido){
        try {
            if (servicoOferecido == null) {
                return ResponseEntity.badRequest().body("Serviço não pode ser nulo");
            }
            ServicoOferecido s = service.save(servicoOferecido);
            logger.info("Serviço salvo com sucesso");
            return ResponseEntity.status(201).body(s);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao salvar serviço", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar serviço", e);
            return ResponseEntity.status(500).body("Erro ao salvar serviço: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido: deve ser um número positivo");
            }
            service.delete(id);
            logger.info("Serviço deletado com ID: " + id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar serviço com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao deletar serviço: " + e.getMessage());
        }
    }}
