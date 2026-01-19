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

import com.fatec.demo.model.Notificacao;
import com.fatec.demo.service.NotificacaoService;

@RestController
@RequestMapping(value = "/notificacao")
public class NotificacaoController {

    @Autowired
    private NotificacaoService service;

    @GetMapping
    public ResponseEntity<List<Notificacao>> findAll(){
        List<Notificacao> n = service.findAll();
        return ResponseEntity.ok().body(n);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacao> findById(@PathVariable Long id){
        Notificacao n = service.findById(id);
        return n != null ? ResponseEntity.ok().body(n) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Notificacao> save(@RequestBody Notificacao notificacao){
        Notificacao n = service.save(notificacao);
        return ResponseEntity.ok().body(n);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
