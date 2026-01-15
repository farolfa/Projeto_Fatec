package com.fatec.demo.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Contrato {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;
    @ManyToOne
    @JoinColumn(name= "id_cliente")
    private Usuario cliente;
    @ManyToOne
    @JoinColumn(name="id_prestador")
    private Usuario prestador;
    @ManyToOne
    @JoinColumn(name="id_participante")
    private Participante participante;

    private String termos;
    private String status;
    public Contrato() {
    }
    public Contrato(Long id, Pedido pedido, Usuario cliente, Usuario prestador, Participante participante,
            String termos, String status) {
        this.id = id;
        this.pedido = pedido;
        this.cliente = cliente;
        this.prestador = prestador;
        this.participante = participante;
        this.termos = termos;
        this.status = status;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Pedido getPedido() {
        return pedido;
    }
    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
    public Usuario getCliente() {
        return cliente;
    }
    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }
    public Usuario getPrestador() {
        return prestador;
    }
    public void setPrestador(Usuario prestador) {
        this.prestador = prestador;
    }
    public Participante getParticipante() {
        return participante;
    }
    public void setParticipante(Participante participante) {
        this.participante = participante;
    }
    public String getTermos() {
        return termos;
    }
    public void setTermos(String termos) {
        this.termos = termos;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    
}
