package com.fatec.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;


@Entity
public class Pedido {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="id_usuario")
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name="id_servico")
    private Cliente servico;  
    @ManyToOne
    @JoinColumn(name="id_endereco")  
    private Endereco endereco;

    private String titulo;
    private String descricao;
    private String localizacao;
    private String status;

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
    public Cliente getServico() {
        return servico;
    }
    public void setServico(Cliente servico) {
        this.servico = servico;
    }
    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public String getLocalizacao() {
        return localizacao;
    }
    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Endereco getEndereco() {
        return endereco;
    }
    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }
    public Pedido() {
    }
    public Pedido(Long id, Usuario usuario, Cliente servico, String titulo, String descricao, String localizacao,
            String status, Endereco endereco) {
        this.id = id;
        this.usuario = usuario;
        this.servico = servico;
        this.titulo = titulo;
        this.descricao = descricao;
        this.localizacao = localizacao;
        this.status = status;
        this.endereco = endereco;
    }

    
}

