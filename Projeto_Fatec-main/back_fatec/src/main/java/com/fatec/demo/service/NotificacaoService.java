package com.fatec.demo.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.demo.model.Notificacao;
import com.fatec.demo.repository.NotificacaoRepository;

@Service
public class NotificacaoService {

    private static final Logger logger = Logger.getLogger(NotificacaoService.class.getName());

    @Autowired
    private NotificacaoRepository repository;
    
    public List<Notificacao> findAll(){
        try {
            return repository.findAll();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar notificações", e);
            throw new RuntimeException("Erro ao buscar notificações do banco de dados", e);
        }
    }

    public List<Notificacao> findByUsuarioId(Long usuarioId){
        try {
            if (usuarioId == null || usuarioId <= 0) {
                throw new IllegalArgumentException("ID de usuário inválido");
            }
            return repository.findByUsuarioIdOrderByDataDesc(usuarioId);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar notificações do usuário " + usuarioId, e);
            throw new RuntimeException("Erro ao buscar notificações", e);
        }
    }

    public long countUnread(Long usuarioId){
        try {
            return repository.countByUsuarioIdAndLidaFalse(usuarioId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao contar notificações não lidas", e);
            return 0;
        }
    }
    
    public Notificacao findById(Long id){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido: deve ser um número positivo");
            }
            return repository.findById(id).orElse(null);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao buscar notificação", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar notificação com ID: " + id, e);
            throw new RuntimeException("Erro ao buscar notificação do banco de dados", e);
        }
    }
    
    @Transactional
    public Notificacao save(Notificacao notificacao){
        try {
            if (notificacao == null) {
                throw new IllegalArgumentException("Notificação não pode ser nula");
            }
            Notificacao saved = repository.save(notificacao);
            logger.info("Notificação salva com sucesso");
            return saved;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao salvar notificação", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar notificação no banco de dados", e);
            throw new RuntimeException("Erro ao salvar notificação no banco de dados", e);
        }
    }

    @Transactional
    public Notificacao markAsRead(Long id){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido");
            }
            Notificacao n = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notificação não encontrada: " + id));
            n.setLida(true);
            return repository.save(n);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao marcar notificação como lida: " + id, e);
            throw new RuntimeException("Erro ao marcar notificação como lida", e);
        }
    }

    @Transactional
    public void markAllAsRead(Long usuarioId){
        try {
            List<Notificacao> pending = repository.findByUsuarioIdOrderByDataDesc(usuarioId)
                .stream().filter(n -> !Boolean.TRUE.equals(n.getLida())).toList();
            pending.forEach(n -> n.setLida(true));
            repository.saveAll(pending);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao marcar todas como lidas para usuário " + usuarioId, e);
            throw new RuntimeException("Erro ao marcar notificações como lidas", e);
        }
    }
    
    @Transactional
    public void delete(Long id){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido: deve ser um número positivo");
            }
            if (!repository.existsById(id)) {
                throw new IllegalArgumentException("Notificação não encontrada com ID: " + id);
            }
            repository.deleteById(id);
            logger.info("Notificação deletada com ID: " + id);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao deletar notificação", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar notificação com ID: " + id, e);
            throw new RuntimeException("Erro ao deletar notificação do banco de dados", e);
        }
    }
}
