package com.fatec.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "respostas_ticket")
public class RespostaTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_ticket", nullable = false)
    private TicketSuporte ticket;

    @Column(nullable = false, length = 100)
    private String respondente = "Equipe de Suporte FazTudoJA";

    @Column(nullable = false, length = 2000)
    private String resposta;

    @Column(nullable = false)
    private LocalDateTime respondidoEm = LocalDateTime.now();

    public RespostaTicket() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TicketSuporte getTicket() { return ticket; }
    public void setTicket(TicketSuporte ticket) { this.ticket = ticket; }

    public String getRespondente() { return respondente; }
    public void setRespondente(String respondente) { this.respondente = respondente; }

    public String getResposta() { return resposta; }
    public void setResposta(String resposta) { this.resposta = resposta; }

    public LocalDateTime getRespondidoEm() { return respondidoEm; }
    public void setRespondidoEm(LocalDateTime respondidoEm) { this.respondidoEm = respondidoEm; }
}
