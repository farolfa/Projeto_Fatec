package com.fatec.demo.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

@Entity
public class Proposta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @GeneratedValue
    @JoinColumn(name="id_pedido")
    private Pedido pedido;
    @GeneratedValue
    @JoinColumn(name="id_prestador")
    private Usuario prestador;

    private BigDecimal precoProposto;
    private String status;
    private String mensagem;

    public Proposta() {
    }
    public Proposta(Integer id, Pedido pedido, Usuario prestador, BigDecimal precoProposto, String status,
            String mensagem) {
        this.id = id;
        this.pedido = pedido;
        this.prestador = prestador;
        this.precoProposto = precoProposto;
        this.status = status;
        this.mensagem = mensagem;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Pedido getPedido() {
        return pedido;
    }
    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
    public Usuario getPrestador() {
        return prestador;
    }
    public void setPrestador(Usuario prestador) {
        this.prestador = prestador;
    }
    public BigDecimal getPrecoProposto() {
        return precoProposto;
    }
    public void setPrecoProposto(BigDecimal precoProposto) {
        this.precoProposto = precoProposto;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getMensagem() {
        return mensagem;
    }
    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    
}
