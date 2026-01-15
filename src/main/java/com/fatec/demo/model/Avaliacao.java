package com.fatec.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Avaliacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_avaliador")
    private Usuario avaliador;

    @ManyToOne
    @JoinColumn(name = "id_avaliado")
    private Usuario avaliado;

    private Long nota;
    private String comentario;
    private LocalDateTime data;

    
    public Avaliacao() {
    }
    
    public Avaliacao(Long id, Usuario avaliador, Usuario avaliado, Long nota, String comentario,
            LocalDateTime data) {
        this.id = id;
        this.avaliador = avaliador;
        this.avaliado = avaliado;
        this.nota = nota;
        this.comentario = comentario;
        this.data = data;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Usuario getAvaliador() {
        return avaliador;
    }
    public void setAvaliador(Usuario avaliador) {
        this.avaliador = avaliador;
    }
    public Usuario getAvaliado() {
        return avaliado;
    }
    public void setAvaliado(Usuario avaliado) {
        this.avaliado = avaliado;
    }
    public Long getNota() {
        return nota;
    }
    public void setNota(Long nota) {
        this.nota = nota;
    }
    public String getComentario() {
        return comentario;
    }
    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
    public LocalDateTime getData() {
        return data;
    }
    public void setData(LocalDateTime data) {
        this.data = data;
    }

    

    
}
