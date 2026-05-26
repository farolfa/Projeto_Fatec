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

import com.fatec.demo.model.Cliente;
import com.fatec.demo.service.ClienteService;


@RestController
@RequestMapping(value = "/cliente")
public class ClienteController {

    private static final Logger logger = Logger.getLogger(ClienteController.class.getName());

    @Autowired
    private ClienteService service;

    @GetMapping
    public ResponseEntity<?> findAll(){
        try {
            List<Cliente> c = service.findAll();
            return ResponseEntity.ok().body(c);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar clientes", e);
            return ResponseEntity.status(500).body("Erro ao buscar clientes: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido: deve ser um número positivo");
            }
            Cliente c = service.findById(id);
            return c != null ? ResponseEntity.ok().body(c) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar cliente com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao buscar cliente: " + e.getMessage());
        }
    }
    
    @PostMapping
    public ResponseEntity<?> save(@RequestBody Cliente cliente){
        try {
            if (cliente == null) {
                return ResponseEntity.badRequest().body("Cliente não pode ser nulo");
            }
            Cliente c = service.save(cliente);
            logger.info("Cliente salvo com sucesso");
            return ResponseEntity.status(201).body(c);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao salvar cliente", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar cliente", e);
            return ResponseEntity.status(500).body("Erro ao salvar cliente: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido: deve ser um número positivo");
            }
            service.delete(id);
            logger.info("Cliente deletado com ID: " + id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar cliente com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao deletar cliente: " + e.getMessage());
        }
    }
}
