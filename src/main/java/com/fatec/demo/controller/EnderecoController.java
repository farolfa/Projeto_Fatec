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

import com.fatec.demo.model.Endereco;
import com.fatec.demo.service.EnderecoService;

@RestController
@RequestMapping(value = "/endereco")
public class EnderecoController {

    @Autowired
    private EnderecoService service;

    @GetMapping
    public ResponseEntity<List<Endereco>> findAll(){
       List<Endereco> e = service.findAll();
        return ResponseEntity.ok().body(e);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Endereco> findById(@PathVariable Long id){
        Endereco e = service.findById(id);
        return e != null ? ResponseEntity.ok().body(e) : ResponseEntity.notFound().build();
    }
    @PostMapping
    public ResponseEntity<Endereco> save(@RequestBody Endereco endereco){
        Endereco e = service.save(endereco);
        return ResponseEntity.ok().body(e);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
