package com.fatec.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.demo.model.RespostaTicket;
import com.fatec.demo.model.TicketSuporte;
import com.fatec.demo.model.Usuario;
import com.fatec.demo.repository.RespostaTicketRepository;
import com.fatec.demo.repository.TicketSuporteRepository;
import com.fatec.demo.repository.UsuarioRepository;

@Service
public class TicketSuporteService {

    private static final Logger logger = Logger.getLogger(TicketSuporteService.class.getName());

    @Autowired
    private TicketSuporteRepository ticketRepository;

    @Autowired
    private RespostaTicketRepository respostaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<TicketSuporte> findByUsuarioId(Long usuarioId) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("ID de usuário inválido");
        }
        return ticketRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId);
    }

    public TicketSuporte findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        return ticketRepository.findById(id).orElse(null);
    }

    @Transactional
    public TicketSuporte create(Long usuarioId, String assunto, String mensagem, String categoria) {
        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException("ID de usuário inválido");
        }
        if (assunto == null || assunto.isBlank()) {
            throw new IllegalArgumentException("Assunto é obrigatório");
        }
        if (mensagem == null || mensagem.isBlank()) {
            throw new IllegalArgumentException("Mensagem é obrigatória");
        }
        if (categoria == null || categoria.isBlank()) {
            throw new IllegalArgumentException("Categoria é obrigatória");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        TicketSuporte ticket = new TicketSuporte();
        ticket.setUsuario(usuario);
        ticket.setAssunto(assunto.trim());
        ticket.setMensagem(mensagem.trim());
        ticket.setCategoria(categoria.trim());
        ticket.setStatus("aberto");
        ticket.setCriadoEm(LocalDateTime.now());

        TicketSuporte saved = ticketRepository.save(ticket);
        logger.info("Ticket criado: " + saved.getId());
        return saved;
    }

    @Transactional
    public RespostaTicket addResposta(Long ticketId, String resposta) {
        if (ticketId == null || ticketId <= 0) {
            throw new IllegalArgumentException("ID de ticket inválido");
        }
        if (resposta == null || resposta.isBlank()) {
            throw new IllegalArgumentException("Resposta é obrigatória");
        }

        TicketSuporte ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("Ticket não encontrado: " + ticketId));

        RespostaTicket r = new RespostaTicket();
        r.setTicket(ticket);
        r.setResposta(resposta.trim());
        r.setRespondidoEm(LocalDateTime.now());

        ticket.setStatus("respondido");
        ticketRepository.save(ticket);

        RespostaTicket saved = respostaRepository.save(r);
        logger.info("Resposta adicionada ao ticket " + ticketId);
        return saved;
    }

    @Transactional
    public TicketSuporte fecharTicket(Long ticketId, Long usuarioId) {
        TicketSuporte ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new IllegalArgumentException("Ticket não encontrado: " + ticketId));

        if (!ticket.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException("Sem permissão para fechar este ticket");
        }

        ticket.setStatus("fechado");
        return ticketRepository.save(ticket);
    }

    @Transactional
    public void delete(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new IllegalArgumentException("Ticket não encontrado: " + id);
        }
        try {
            ticketRepository.deleteById(id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao excluir ticket " + id, e);
            throw new RuntimeException("Erro ao excluir ticket", e);
        }
    }
}
