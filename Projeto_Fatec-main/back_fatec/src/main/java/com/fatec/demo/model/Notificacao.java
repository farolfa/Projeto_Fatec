package com.fatec.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
@Entity
@Table(name = "notificacoes")
public class Notificacao {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="id_usuario")
    private Usuario usuario;
    
    private String tipo;
    private String mensagem;
    private LocalDateTime data;
    private Boolean lida;


    public Notificacao(Long id, Usuario usuario, String tipo, String mensagem, LocalDateTime data, Boolean lida) {
        this.id = id;
        this.usuario = usuario;
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.data = data;
        this.lida = lida;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public String getMensagem() {
        return mensagem;
    }
    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
    public LocalDateTime getData() {
        return data;
    }
    public void setData(LocalDateTime data) {
        this.data = data;
    }
    public Boolean getLida() {
        return lida;
    }
    public void setLida(Boolean lida) {
        this.lida = lida;
    }
    public Notificacao() {
    }

    
}
