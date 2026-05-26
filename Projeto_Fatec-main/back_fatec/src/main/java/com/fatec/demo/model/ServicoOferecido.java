package com.fatec.demo.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "servicos_oferecidos")
public class ServicoOferecido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="id_usuario")
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name="id_servico")
    private ServicoCatalogo servico;

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
    public ServicoCatalogo getServico() {
        return servico;
    }
    public void setServico(ServicoCatalogo servico) {
        this.servico = servico;
    }
    public BigDecimal getPrecoMedio() {
        return precoMedio;
    }
    public void setPrecoMedio(BigDecimal precoMedio) {
        this.precoMedio = precoMedio;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    private BigDecimal precoMedio;
    private String descricao;
    public ServicoOferecido() {
    }
    public ServicoOferecido(Long id, Usuario usuario, ServicoCatalogo servico, BigDecimal precoMedio, String descricao) {
        this.id = id;
        this.usuario = usuario;
        this.servico = servico;
        this.precoMedio = precoMedio;
        this.descricao = descricao;
    }

    
}

