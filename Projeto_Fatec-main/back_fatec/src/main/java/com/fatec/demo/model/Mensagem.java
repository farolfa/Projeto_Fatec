package com.fatec.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "mensagens",
    indexes = {
        @Index(name = "idx_mensagens_participante", columnList = "id_participante"),
        @Index(name = "idx_mensagens_remetente", columnList = "id_remetente"),
        @Index(name = "idx_mensagens_timestamp", columnList = "timestamp"),
        @Index(name = "idx_mensagens_lida", columnList = "lida")
    }
)
public class Mensagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="id_participante")
    private Participante participante;
    @ManyToOne
    @JoinColumn(name="id_remetente")
    private Usuario remetente;

    private String conteudo;
    private String tipo;
    private LocalDateTime timestamp;
    private Boolean lida;

    public Mensagem(Long id, Participante participante, Usuario remetente, String conteudo, String tipo,
            LocalDateTime timestamp, Boolean lida) {
        this.id = id;
        this.participante = participante;
        this.remetente = remetente;
        this.conteudo = conteudo;
        this.tipo = tipo;
        this.timestamp = timestamp;
        this.lida = lida;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Participante getParticipante() {
        return participante;
    }
    public void setParticipante(Participante participante) {
        this.participante = participante;
    }
    public Usuario getRemetente() {
        return remetente;
    }
    public void setRemetente(Usuario remetente) {
        this.remetente = remetente;
    }
    public String getConteudo() {
        return conteudo;
    }
    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public Boolean getLida() {
        return lida;
    }
    public void setLida(Boolean lida) {
        this.lida = lida;
    }
    public Mensagem() {
    }

    
}
