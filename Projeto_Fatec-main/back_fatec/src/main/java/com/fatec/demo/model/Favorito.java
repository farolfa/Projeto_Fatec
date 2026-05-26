package com.fatec.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "favoritos",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_favorito_usuario_prestador", columnNames = {"id_usuario", "prestador_id"})
    },
    indexes = {
        @Index(name = "idx_favorito_usuario", columnList = "id_usuario")
    }
)
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "prestador_id", nullable = false)
    private Long prestadorId;

    @Column(name = "prestador_nome", nullable = false, length = 200)
    private String prestadorNome;

    @Lob
    @Column(name = "prestador_foto", columnDefinition = "VARCHAR(MAX)")
    private String prestadorFoto;

    @Column(name = "saved_at", nullable = false)
    private LocalDateTime savedAt;

    public Favorito() {}

    @PrePersist
    protected void onCreate() {
        if (savedAt == null) {
            savedAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Long getPrestadorId() { return prestadorId; }
    public void setPrestadorId(Long prestadorId) { this.prestadorId = prestadorId; }

    public String getPrestadorNome() { return prestadorNome; }
    public void setPrestadorNome(String prestadorNome) { this.prestadorNome = prestadorNome; }

    public String getPrestadorFoto() { return prestadorFoto; }
    public void setPrestadorFoto(String prestadorFoto) { this.prestadorFoto = prestadorFoto; }

    public LocalDateTime getSavedAt() { return savedAt; }
    public void setSavedAt(LocalDateTime savedAt) { this.savedAt = savedAt; }
}
