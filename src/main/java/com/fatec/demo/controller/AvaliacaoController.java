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

import com.fatec.demo.model.Avaliacao;
import com.fatec.demo.service.AvaliacaoService;

@RestController
@RequestMapping(value = "/avaliacao")
public class AvaliacaoController {
    
    @Autowired
    private AvaliacaoService avaliacaoService;

    @GetMapping
    public ResponseEntity<List<Avaliacao>> findAll(){
        List <Avaliacao> a = avaliacaoService.findAll();
        return ResponseEntity.ok().body(a);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Avaliacao> findById(@PathVariable Long id){
        Avaliacao a = avaliacaoService.findById(id);
        return ResponseEntity.ok().body(a);
    }

    @PostMapping
    public ResponseEntity<Avaliacao> save(@RequestBody Avaliacao avaliacao){
        Avaliacao a = avaliacaoService.save(avaliacao);
        return ResponseEntity.ok().body(a);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        avaliacaoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
