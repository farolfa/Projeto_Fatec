package com.fatec.demo.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fatec.demo.model.enums.StatusProposta;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "propostas",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_propostas_pedido_prestador", columnNames = {"id_pedido", "id_prestador"})
    },
    indexes = {
        @Index(name = "idx_propostas_pedido", columnList = "id_pedido"),
        @Index(name = "idx_propostas_prestador", columnList = "id_prestador"),
        @Index(name = "idx_propostas_status", columnList = "status")
    }
)
public class Proposta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="id_pedido", nullable = false)
    private Pedido pedido;
    @ManyToOne
    @JoinColumn(name="id_prestador", nullable = false)
    private Usuario prestador;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precoProposto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusProposta status;

    @Column(length = 1000)
    private String mensagem;

    @Column(length = 100)
    private String prazoEntrega;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        if (dataCriacao == null) dataCriacao = LocalDateTime.now();
    }

    public Proposta() {
    }
    public Proposta(Integer id, Pedido pedido, Usuario prestador, BigDecimal precoProposto, String status,
            String mensagem) {
        this.id = id;
        this.pedido = pedido;
        this.prestador = prestador;
        this.precoProposto = precoProposto;
        setStatus(status);
        this.mensagem = mensagem;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Pedido getPedido() {
        return pedido;
    }
    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
    public Usuario getPrestador() {
        return prestador;
    }
    public void setPrestador(Usuario prestador) {
        this.prestador = prestador;
    }
    public BigDecimal getPrecoProposto() {
        return precoProposto;
    }
    public void setPrecoProposto(BigDecimal precoProposto) {
        this.precoProposto = precoProposto;
    }
    public String getStatus() {
        return status == null ? null : status.name();
    }
    public void setStatus(String status) {
        this.status = StatusProposta.from(status);
    }
    public StatusProposta getStatusEnum() {
        return status;
    }
    public void setStatusEnum(StatusProposta status) {
        this.status = status;
    }
    public String getMensagem() {
        return mensagem;
    }
    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getPrazoEntrega() {
        return prazoEntrega;
    }
    public void setPrazoEntrega(String prazoEntrega) {
        this.prazoEntrega = prazoEntrega;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    
}
