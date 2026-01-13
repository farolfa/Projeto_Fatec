package com.fatec.demo.model;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;

@Entity
public class Pagamento {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @GeneratedValue
    @JoinColumn(name="id_usuario")
    private Usuario usuario;
    @GeneratedValue
    @JoinColumn(name="id_pedido")
    private Pedido pedido;

    private BigDecimal valor;
    private String status;
    private String metodo;
    public Pagamento() {
    }
    public Pagamento(Long id, Usuario usuario, Pedido pedido, BigDecimal valor, String status, String metodo) {
        this.id = id;
        this.usuario = usuario;
        this.pedido = pedido;
        this.valor = valor;
        this.status = status;
        this.metodo = metodo;
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
    public Pedido getPedido() {
        return pedido;
    }
    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
    public BigDecimal getValor() {
        return valor;
    }
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getMetodo() {
        return metodo;
    }
    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }
    



    
}
