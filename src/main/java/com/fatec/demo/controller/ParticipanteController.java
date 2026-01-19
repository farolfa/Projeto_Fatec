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

import com.fatec.demo.model.Participante;
import com.fatec.demo.service.ParticipanteService;

@RestController
@RequestMapping(value = "/participante")
public class ParticipanteController {

    @Autowired
    private ParticipanteService service;

    @GetMapping
    public ResponseEntity<List<Participante>> findAll(){
        List<Participante> p = service.findAll();
        return ResponseEntity.ok().body(p);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Participante> findById(@PathVariable Long id){
        Participante p = service.findById(id);
        return p != null ? ResponseEntity.ok().body(p) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Participante> save(@RequestBody Participante participante){
        Participante p = service.save(participante);
        return ResponseEntity.ok().body(p);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
