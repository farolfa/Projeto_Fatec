package com.fatec.demo.controller;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.demo.dto.EnsureConversationRequest;
import com.fatec.demo.dto.SendMessageRequest;
import com.fatec.demo.model.Mensagem;
import com.fatec.demo.service.MensagemService;

@RestController
@RequestMapping(value = "/mensagem")
public class MensagemController {

    private static final Logger logger = Logger.getLogger(MensagemController.class.getName());

    @Autowired
    private MensagemService service;

    @GetMapping
    public ResponseEntity<?> findAll(){
        try {
            List<Mensagem> m = service.findAll();
            return ResponseEntity.ok().body(m);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar mensagens", e);
            return ResponseEntity.status(500).body("Erro ao buscar mensagens: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido: deve ser um número positivo");
            }
            Mensagem m = service.findById(id);
            return m != null ? ResponseEntity.ok().body(m) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar mensagem com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao buscar mensagem: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Mensagem mensagem){
        try {
            if (mensagem == null) {
                return ResponseEntity.badRequest().body("Mensagem não pode ser nula");
            }
            Mensagem m = service.save(mensagem);
            logger.info("Mensagem salva com sucesso");
            return ResponseEntity.status(201).body(m);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao salvar mensagem", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar mensagem", e);
            return ResponseEntity.status(500).body("Erro ao salvar mensagem: " + e.getMessage());
        }
    }

    @PostMapping("/conversas/garantir")
    public ResponseEntity<?> ensureConversation(@RequestBody EnsureConversationRequest request) {
        try {
            return ResponseEntity.ok(service.ensureConversation(request));
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao garantir conversa", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao garantir conversa", e);
            return ResponseEntity.status(500).body("Erro ao garantir conversa: " + e.getMessage());
        }
    }

    @GetMapping("/conversas/usuario/{userId}")
    public ResponseEntity<?> getConversations(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(service.listConversationsByUser(userId));
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao buscar conversas", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar conversas", e);
            return ResponseEntity.status(500).body("Erro ao buscar conversas: " + e.getMessage());
        }
    }

    @GetMapping("/conversas/{conversationId}/mensagens")
    public ResponseEntity<?> getMessages(@PathVariable Long conversationId, @RequestParam Long userId) {
        try {
            return ResponseEntity.ok(service.listMessagesByConversation(conversationId, userId));
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao buscar mensagens da conversa", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar mensagens da conversa", e);
            return ResponseEntity.status(500).body("Erro ao buscar mensagens: " + e.getMessage());
        }
    }

    @PostMapping("/conversas/{conversationId}/mensagens")
    public ResponseEntity<?> sendMessage(@PathVariable Long conversationId, @RequestBody SendMessageRequest request) {
        try {
            return ResponseEntity.status(201).body(service.sendMessage(conversationId, request.getSenderUserId(), request.getText()));
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao enviar mensagem", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao enviar mensagem", e);
            return ResponseEntity.status(500).body("Erro ao enviar mensagem: " + e.getMessage());
        }
    }

    @PostMapping("/conversas/{conversationId}/ler")
    public ResponseEntity<?> markAsRead(@PathVariable Long conversationId, @RequestParam Long userId) {
        try {
            service.markConversationAsRead(conversationId, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao marcar conversa como lida", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao marcar conversa como lida", e);
            return ResponseEntity.status(500).body("Erro ao atualizar leitura: " + e.getMessage());
        }
    }

    @GetMapping("/conversas/usuario/{userId}/nao-lidas")
    public ResponseEntity<?> getUnreadCount(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(service.getUnreadMessagesCount(userId));
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao contar mensagens não lidas", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao contar mensagens não lidas", e);
            return ResponseEntity.status(500).body("Erro ao contar mensagens não lidas: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido: deve ser um número positivo");
            }
            service.delete(id);
            logger.info(() -> "Mensagem deletada com ID: " + Objects.toString(id));
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, () -> "Erro ao deletar mensagem com ID: " + Objects.toString(id));
            return ResponseEntity.status(500).body("Erro ao deletar mensagem: " + e.getMessage());
        }
    }
}
