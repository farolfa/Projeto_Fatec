package com.fatec.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "avaliacoes",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_avaliacao_avaliador_avaliado", columnNames = {"id_avaliador", "id_avaliado"})
    },
    indexes = {
        @Index(name = "idx_avaliacoes_avaliado", columnList = "id_avaliado"),
        @Index(name = "idx_avaliacoes_avaliador", columnList = "id_avaliador"),
        @Index(name = "idx_avaliacoes_data", columnList = "data")
    }
)
public class Avaliacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_avaliador", nullable = false)
    private Usuario avaliador;

    @ManyToOne
    @JoinColumn(name = "id_avaliado", nullable = false)
    private Usuario avaliado;

    @Column(nullable = false)
    private Long nota;

    @Column(length = 1000)
    private String comentario;

    @Column(nullable = false)
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
