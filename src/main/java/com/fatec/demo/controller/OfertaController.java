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

import com.fatec.demo.model.Oferta;
import com.fatec.demo.service.OfertaService;

@RestController
@RequestMapping(value = "/oferta")
public class OfertaController {

    @Autowired
    private OfertaService service;

    @GetMapping
    public ResponseEntity<List<Oferta>> findAll(){
        List<Oferta> o = service.findAll();
        return ResponseEntity.ok().body(o);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Oferta> findById(@PathVariable Long id){
        Oferta o = service.findById(id);
        return o != null ? ResponseEntity.ok().body(o) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Oferta> save(@RequestBody Oferta oferta){
        Oferta o = service.save(oferta);
        return ResponseEntity.ok().body(o);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
