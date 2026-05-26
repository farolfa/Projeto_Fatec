package com.fatec.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.demo.model.RespostaTicket;
import com.fatec.demo.model.TicketSuporte;
import com.fatec.demo.service.TicketSuporteService;

@RestController
@RequestMapping("/ticket")
@CrossOrigin(originPatterns = "*")
public class TicketSuporteController {

    @Autowired
    private TicketSuporteService service;

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<?> getByUsuario(@PathVariable Long userId) {
        try {
            List<TicketSuporte> tickets = service.findByUsuarioId(userId);
            return ResponseEntity.ok(tickets);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        TicketSuporte ticket = service.findById(id);
        if (ticket == null) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", "Ticket não encontrado");
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ticket);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        try {
            Long usuarioId = null;
            Object usuarioObj = body.get("usuarioId");
            if (usuarioObj instanceof Number n) {
                usuarioId = n.longValue();
            }
            String assunto = (String) body.get("assunto");
            String mensagem = (String) body.get("mensagem");
            String categoria = (String) body.get("categoria");

            TicketSuporte ticket = service.create(usuarioId, assunto, mensagem, categoria);
            return ResponseEntity.ok(ticket);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PostMapping("/{id}/resposta")
    public ResponseEntity<?> addResposta(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String resposta = body.get("resposta");
            RespostaTicket r = service.addResposta(id, resposta);
            return ResponseEntity.ok(r);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}/fechar")
    public ResponseEntity<?> fechar(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        try {
            Long usuarioId = null;
            Object u = body.get("usuarioId");
            if (u instanceof Number n) {
                usuarioId = n.longValue();
            }
            TicketSuporte ticket = service.fecharTicket(id, usuarioId);
            return ResponseEntity.ok(ticket);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            Map<String, String> result = new HashMap<>();
            result.put("mensagem", "Ticket excluído com sucesso");
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("erro", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
