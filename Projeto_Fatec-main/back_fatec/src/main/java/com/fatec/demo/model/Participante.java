package com.fatec.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "participantes")
public class Participante {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="id_user_cliente")
    private Usuario usuarioCliente;

    @ManyToOne
    @JoinColumn(name="id_user_prestador")
    private Usuario usuarioPrestador;

    private Boolean aceiteCliente;
    private Boolean aceitePrestador;
    private LocalDateTime aceiteTimestamp;
    private Long pedidoReferencia;
    private String tituloServico;
    
    public Participante() {
    }
    public Participante(Long id, Usuario usuarioCliente, Usuario usuarioPrestador, Boolean aceiteCliente,
            Boolean aceitePrestador, LocalDateTime aceiteTimestamp, Long pedidoReferencia, String tituloServico) {
        this.id = id;
        this.usuarioCliente = usuarioCliente;
        this.usuarioPrestador = usuarioPrestador;
        this.aceiteCliente = aceiteCliente;
        this.aceitePrestador = aceitePrestador;
        this.aceiteTimestamp = aceiteTimestamp;
        this.pedidoReferencia = pedidoReferencia;
        this.tituloServico = tituloServico;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Usuario getUsuarioCliente() {
        return usuarioCliente;
    }
    public void setUsuarioCliente(Usuario usuarioCliente) {
        this.usuarioCliente = usuarioCliente;
    }
    public Usuario getUsuarioPrestador() {
        return usuarioPrestador;
    }
    public void setUsuarioPrestador(Usuario usuarioPrestador) {
        this.usuarioPrestador = usuarioPrestador;
    }
    public Boolean getAceiteCliente() {
        return aceiteCliente;
    }
    public void setAceiteCliente(Boolean aceiteCliente) {
        this.aceiteCliente = aceiteCliente;
    }
    public Boolean getAceitePrestador() {
        return aceitePrestador;
    }
    public void setAceitePrestador(Boolean aceitePrestador) {
        this.aceitePrestador = aceitePrestador;
    }
    public LocalDateTime getAceiteTimestamp() {
        return aceiteTimestamp;
    }
    public void setAceiteTimestamp(LocalDateTime aceiteTimestamp) {
        this.aceiteTimestamp = aceiteTimestamp;
    }

    public Long getPedidoReferencia() {
        return pedidoReferencia;
    }

    public void setPedidoReferencia(Long pedidoReferencia) {
        this.pedidoReferencia = pedidoReferencia;
    }

    public String getTituloServico() {
        return tituloServico;
    }

    public void setTituloServico(String tituloServico) {
        this.tituloServico = tituloServico;
    }

    
}
