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

import com.fatec.demo.model.Agenda;
import com.fatec.demo.service.AgendaService;

@RestController
@RequestMapping(value = "/agenda")
public class AgendaController {
    @Autowired
    private AgendaService agendaService;

    @GetMapping
    public ResponseEntity<List<Agenda>> listAll(){
        List<Agenda> list = agendaService.findAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Agenda> findById(@PathVariable Long id){
        Agenda a = agendaService.findById(id);
        return ResponseEntity.ok().body(a);
    }

    @PostMapping
    public ResponseEntity<Agenda> save(@RequestBody Agenda agenda){
        Agenda a = agendaService.saveAgenda(agenda);
        return ResponseEntity.ok(a);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        agendaService.deleteById(id);
        return ResponseEntity.noContent().build();

    }

}
