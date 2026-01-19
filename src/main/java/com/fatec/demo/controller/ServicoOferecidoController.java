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

import com.fatec.demo.model.ServicoOferecido;
import com.fatec.demo.service.ServicoOferecidoService;

@RestController
@RequestMapping(value = "/servico-oferecido")
public class ServicoOferecidoController {

    @Autowired
    private ServicoOferecidoService service;

    @GetMapping
    public ResponseEntity<List<ServicoOferecido>> findAll(){
        List<ServicoOferecido> s = service.findAll();
        return ResponseEntity.ok().body(s);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicoOferecido> findById(@PathVariable Long id){
        ServicoOferecido s = service.findById(id);
        return s != null ? ResponseEntity.ok().body(s) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ServicoOferecido> save(@RequestBody ServicoOferecido servicoOferecido){
        ServicoOferecido s = service.save(servicoOferecido);
        return ResponseEntity.ok().body(s);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
