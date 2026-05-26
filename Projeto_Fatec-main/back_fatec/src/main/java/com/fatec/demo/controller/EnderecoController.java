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

import com.fatec.demo.model.Endereco;
import com.fatec.demo.service.EnderecoService;

@RestController
@RequestMapping(value = "/endereco")
public class EnderecoController {

    private static final Logger logger = Logger.getLogger(EnderecoController.class.getName());

    @Autowired
    private EnderecoService service;

    @GetMapping
    public ResponseEntity<?> findAll(){
        try {
            List<Endereco> e = service.findAll();
            return ResponseEntity.ok().body(e);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Erro ao buscar endereços", ex);
            return ResponseEntity.status(500).body("Erro ao buscar endereços: " + ex.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido: deve ser um número positivo");
            }
            Endereco e = service.findById(id);
            return e != null ? ResponseEntity.ok().body(e) : ResponseEntity.notFound().build();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Erro ao buscar endereço com ID: " + id, ex);
            return ResponseEntity.status(500).body("Erro ao buscar endereço: " + ex.getMessage());
        }
    }
    
    @PostMapping
    public ResponseEntity<?> save(@RequestBody Endereco endereco){
        try {
            if (endereco == null) {
                return ResponseEntity.badRequest().body("Endereço não pode ser nulo");
            }
            Endereco e = service.save(endereco);
            logger.info("Endereço salvo com sucesso");
            return ResponseEntity.status(201).body(e);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao salvar endereço", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar endereço", e);
            return ResponseEntity.status(500).body("Erro ao salvar endereço: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido: deve ser um número positivo");
            }
            service.delete(id);
            logger.info("Endereço deletado com ID: " + id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar endereço com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao deletar endereço: " + e.getMessage());
        }
    }}
