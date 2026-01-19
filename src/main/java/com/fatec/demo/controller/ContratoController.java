package com.fatec.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.demo.model.Contrato;
import com.fatec.demo.service.ContratoService;

@RestController
@RequestMapping(value = "/contrato")
public class ContratoController {
    
    @Autowired
    private ContratoService service;

    @GetMapping
    public ResponseEntity<List<Contrato>> findAll(){
        List<Contrato> c = service.findAll();
        return ResponseEntity.ok().body(c);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contrato> findById(@PathVariable Long id){
        Contrato c = service.findById(id);
        return c != null ? ResponseEntity.ok().body(c) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Contrato> save(@RequestBody Contrato contrato){
        Contrato c = service.save(contrato);
        return ResponseEntity.ok().body(c);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
