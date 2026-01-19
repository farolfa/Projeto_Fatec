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

import com.fatec.demo.model.Mensagem;
import com.fatec.demo.service.MensagemService;

@RestController
@RequestMapping(value = "/mensagem")
public class MensagemController {

    @Autowired
    private MensagemService service;

    @GetMapping
    public ResponseEntity<List<Mensagem>> findAll(){
        List<Mensagem> m = service.findAll();
        return ResponseEntity.ok().body(m);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mensagem> findById(@PathVariable Long id){
        Mensagem m = service.findById(id);
        return m != null ? ResponseEntity.ok().body(m) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Mensagem> save(@RequestBody Mensagem mensagem){
        Mensagem m = service.save(mensagem);
        return ResponseEntity.ok().body(m);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
