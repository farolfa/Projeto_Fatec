package com.fatec.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Oferta {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="id_usuario")
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name="id_servico")
    private Cliente servico;
    
    private String titulo;
    private String descricao;
    private String localizacao;
    public Oferta() {
    }
    public Oferta(Long id, Usuario usuario, Cliente servico, String titulo, String descricao, String localizacao) {
        this.id = id;
        this.usuario = usuario;
        this.servico = servico;
        this.titulo = titulo;
        this.descricao = descricao;
        this.localizacao = localizacao;
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

    
}
