package com.fatec.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "enderecos", indexes = {
    @Index(name = "idx_enderecos_usuario", columnList = "id_usuario")
})
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonBackReference
    private Usuario usuario;

    @Column(nullable = false, length = 200)
    private String rua;

    @Column(length = 10)
    private String numero;

    @Column(nullable = false, length = 120)
    private String cidade;

    @Column(length = 100)
    private String bairro;

    @Column(nullable = false, length = 2)
    private String estado;

    @Column(nullable = false, length = 8)
    private String cep;

    /** Indica se é o endereço principal do usuário. */
    @Column(nullable = false)
    private boolean principal = true;

    public Endereco(Long id, Usuario usuario, String rua, String numero, String cidade,String bairro, String estado, String cep) {
        this.id = id;
        this.usuario = usuario;
        this.rua = rua;
        this.numero = numero;
        this.cidade = cidade;
        this.bairro = bairro;
        this.estado = estado;
        this.cep = cep;
    }
    public Endereco() {
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
    public String getRua() {
        return rua;
    }
    public void setRua(String rua) {
        this.rua = rua;
    }
    public String getNumero() {
        return numero;
    }
    public void setNumero(String numero) {
        this.numero = numero;
    }
    public String getCidade() {
        return cidade;
    }
    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
    public String getBairro() {
        return bairro;
    }
    public void setBairro(String bairro) {
        this.bairro = bairro;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public String getCep() {
        return cep;
    }
    public void setCep(String cep) {
        this.cep = cep;
    }

    public boolean isPrincipal() { return principal; }
    public void setPrincipal(boolean principal) { this.principal = principal; }
}
