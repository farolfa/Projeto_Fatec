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

import com.fatec.demo.model.Proposta;
import com.fatec.demo.service.PropostaService;

@RestController
@RequestMapping(value = "/proposta")
public class PropostaController {

    @Autowired
    private PropostaService service;

    @GetMapping
    public ResponseEntity<List<Proposta>> findAll(){
        List<Proposta> pr = service.findAll();
        return ResponseEntity.ok().body(pr);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proposta> findById(@PathVariable Long id){
        Proposta pr = service.findById(id);
        return pr != null ? ResponseEntity.ok().body(pr) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Proposta> save(@RequestBody Proposta proposta){
        Proposta pr = service.save(proposta);
        return ResponseEntity.ok().body(pr);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
