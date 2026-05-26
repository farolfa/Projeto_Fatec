package com.fatec.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "notificacoes_lidas_externas",
       uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "external_id"}))
public class NotificacaoLidaExterna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "external_id", nullable = false, length = 255)
    private String externalId;

    public NotificacaoLidaExterna() {}

    public NotificacaoLidaExterna(Long usuarioId, String externalId) {
        this.usuarioId = usuarioId;
        this.externalId = externalId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
}
