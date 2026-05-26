package com.fatec.demo.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

import com.fatec.demo.model.Notificacao;
import com.fatec.demo.model.NotificacaoLidaExterna;
import com.fatec.demo.repository.NotificacaoLidaExternaRepository;
import com.fatec.demo.service.NotificacaoService;

@RestController
@RequestMapping(value = "/notificacao")
@CrossOrigin(originPatterns = "*")
public class NotificacaoController {

    private static final Logger logger = Logger.getLogger(NotificacaoController.class.getName());

    @Autowired
    private NotificacaoService service;

    @Autowired
    private NotificacaoLidaExternaRepository lidaExternaRepository;

    @GetMapping
    public ResponseEntity<?> findAll(){
        try {
            List<Notificacao> n = service.findAll();
            return ResponseEntity.ok().body(n);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar notificações", e);
            return ResponseEntity.status(500).body("Erro ao buscar notificações: " + e.getMessage());
        }
    }

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<?> findByUsuario(@PathVariable Long userId){
        try {
            if (userId == null || userId <= 0) {
                return ResponseEntity.badRequest().body("ID inválido");
            }
            return ResponseEntity.ok(service.findByUsuarioId(userId));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar notificações do usuário " + userId, e);
            return ResponseEntity.status(500).body("Erro ao buscar notificações: " + e.getMessage());
        }
    }

    @GetMapping("/usuario/{userId}/nao-lidas")
    public ResponseEntity<?> countUnread(@PathVariable Long userId){
        try {
            return ResponseEntity.ok(Map.of("count", service.countUnread(userId)));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao contar não lidas");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido: deve ser um número positivo");
            }
            Notificacao n = service.findById(id);
            return n != null ? ResponseEntity.ok().body(n) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar notificação com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao buscar notificação: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Notificacao notificacao){
        try {
            if (notificacao == null) {
                return ResponseEntity.badRequest().body("Notificação não pode ser nula");
            }
            if (notificacao.getData() == null) {
                notificacao.setData(LocalDateTime.now());
            }
            if (notificacao.getLida() == null) {
                notificacao.setLida(false);
            }
            Notificacao n = service.save(notificacao);
            logger.info("Notificação salva com sucesso");
            return ResponseEntity.status(201).body(n);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao salvar notificação", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar notificação", e);
            return ResponseEntity.status(500).body("Erro ao salvar notificação: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/lida")
    public ResponseEntity<?> markAsRead(@PathVariable Long id){
        try {
            return ResponseEntity.ok(service.markAsRead(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao marcar notificação como lida: " + id, e);
            return ResponseEntity.status(500).body("Erro ao marcar como lida");
        }
    }

    @PutMapping("/usuario/{userId}/lidas")
    public ResponseEntity<?> markAllAsRead(@PathVariable Long userId){
        try {
            service.markAllAsRead(userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao marcar todas como lidas: " + userId, e);
            return ResponseEntity.status(500).body("Erro ao marcar como lidas");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        try {
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body("ID inválido: deve ser um número positivo");
            }
            service.delete(id);
            logger.info("Notificação deletada com ID: " + id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar notificação com ID: " + id, e);
            return ResponseEntity.status(500).body("Erro ao deletar notificação: " + e.getMessage());
        }
    }

    // ──────────── Read state for dynamically-derived notifications ──────────

    @GetMapping("/usuario/{userId}/lidas-externas")
    public ResponseEntity<?> getReadExternalIds(@PathVariable Long userId) {
        try {
            List<String> ids = lidaExternaRepository.findByUsuarioId(userId)
                .stream()
                .map(NotificacaoLidaExterna::getExternalId)
                .collect(Collectors.toList());
            return ResponseEntity.ok(ids);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar IDs lidos para usuário " + userId, e);
            return ResponseEntity.status(500).body("Erro ao buscar notificações lidas");
        }
    }

    @PostMapping("/usuario/{userId}/lidas-externas")
    public ResponseEntity<?> markExternalAsRead(@PathVariable Long userId,
                                                 @RequestBody Map<String, String> body) {
        try {
            String externalId = body.get("externalId");
            if (externalId == null || externalId.isBlank()) {
                return ResponseEntity.badRequest().body("externalId é obrigatório");
            }
            if (!lidaExternaRepository.existsByUsuarioIdAndExternalId(userId, externalId)) {
                lidaExternaRepository.save(new NotificacaoLidaExterna(userId, externalId));
            }
            Map<String, String> result = new HashMap<>();
            result.put("mensagem", "Marcada como lida");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao marcar externalId como lido", e);
            return ResponseEntity.status(500).body("Erro ao marcar como lida");
        }
    }

    @PutMapping("/usuario/{userId}/lidas-externas")
    public ResponseEntity<?> setAllExternalAsRead(@PathVariable Long userId,
                                                   @RequestBody List<String> externalIds) {
        try {
            lidaExternaRepository.deleteAllByUsuarioId(userId);
            if (externalIds != null && !externalIds.isEmpty()) {
                List<NotificacaoLidaExterna> toSave = externalIds.stream()
                    .filter(id -> id != null && !id.isBlank())
                    .map(id -> new NotificacaoLidaExterna(userId, id))
                    .collect(Collectors.toList());
                lidaExternaRepository.saveAll(toSave);
            }
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao atualizar IDs lidos para usuário " + userId, e);
            return ResponseEntity.status(500).body("Erro ao atualizar notificações lidas");
        }
    }
}
