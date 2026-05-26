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
@Table(name = "telefones", indexes = {
    @Index(name = "idx_telefones_usuario", columnList = "id_usuario")
})
public class Telefone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonBackReference
    private Usuario usuario;

    @Column(nullable = false, length = 20)
    private String numero;

    /** Ex.: "celular", "residencial", "comercial" */
    @Column(length = 20)
    private String tipo;

    /** Indica se é o telefone principal do usuário. */
    @Column(nullable = false)
    private boolean principal = true;

    public Telefone() {}

    public Telefone(Usuario usuario, String numero, String tipo, boolean principal) {
        this.usuario  = usuario;
        this.numero   = numero;
        this.tipo     = tipo;
        this.principal = principal;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public boolean isPrincipal() { return principal; }
    public void setPrincipal(boolean principal) { this.principal = principal; }
}
