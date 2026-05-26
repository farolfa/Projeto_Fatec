package com.fatec.demo.model;

import java.time.LocalDateTime;

import com.fatec.demo.model.enums.StatusPedido;

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


@Entity
@Table(
    name = "pedidos",
    indexes = {
        @Index(name = "idx_pedidos_usuario", columnList = "id_usuario"),
        @Index(name = "idx_pedidos_status", columnList = "status"),
        @Index(name = "idx_pedidos_servico", columnList = "id_servico")
    }
)
public class Pedido {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="id_usuario", nullable = false)
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name="id_servico")
    private ServicoCatalogo servico;  
    @ManyToOne
    @JoinColumn(name="id_endereco")  
    private Endereco endereco;

    @Column(nullable = false, length = 160)
    private String titulo;

    @Column(nullable = false, length = 2000)
    private String descricao;

    @Column(nullable = false, length = 160)
    private String localizacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatusPedido status;

    @Column(length = 120)
    private String contatoNome;

    @Column(length = 160)
    private String contatoEmail;

    @Column(length = 20)
    private String contatoTelefone;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    private boolean clienteConfirmouConclusao = false;

    @Column(nullable = false, columnDefinition = "BIT DEFAULT 0")
    private boolean prestadorConfirmouConclusao = false;

    @Column(updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        if (dataCriacao == null) dataCriacao = LocalDateTime.now();
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
    public ServicoCatalogo getServico() {
        return servico;
    }
    public void setServico(ServicoCatalogo servico) {
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
    public String getStatus() {
        return status == null ? null : status.name();
    }
    public void setStatus(String status) {
        this.status = StatusPedido.from(status);
    }
    public StatusPedido getStatusEnum() {
        return status;
    }
    public void setStatusEnum(StatusPedido status) {
        this.status = status;
    }

    public String getContatoNome() {
        return contatoNome;
    }

    public void setContatoNome(String contatoNome) {
        this.contatoNome = contatoNome;
    }

    public String getContatoEmail() {
        return contatoEmail;
    }

    public void setContatoEmail(String contatoEmail) {
        this.contatoEmail = contatoEmail;
    }

    public String getContatoTelefone() {
        return contatoTelefone;
    }

    public void setContatoTelefone(String contatoTelefone) {
        this.contatoTelefone = contatoTelefone;
    }

    public boolean isClienteConfirmouConclusao() {
        return clienteConfirmouConclusao;
    }

    public void setClienteConfirmouConclusao(boolean clienteConfirmouConclusao) {
        this.clienteConfirmouConclusao = clienteConfirmouConclusao;
    }

    public boolean isPrestadorConfirmouConclusao() {
        return prestadorConfirmouConclusao;
    }

    public void setPrestadorConfirmouConclusao(boolean prestadorConfirmouConclusao) {
        this.prestadorConfirmouConclusao = prestadorConfirmouConclusao;
    }

    public Endereco getEndereco() {
        return endereco;
    }
    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public Pedido() {
    }
    public Pedido(Long id, Usuario usuario, ServicoCatalogo servico, String titulo, String descricao, String localizacao,
            String status, Endereco endereco, String contatoNome, String contatoEmail, String contatoTelefone) {
        this.id = id;
        this.usuario = usuario;
        this.servico = servico;
        this.titulo = titulo;
        this.descricao = descricao;
        this.localizacao = localizacao;
        setStatus(status);
        this.endereco = endereco;
        this.contatoNome = contatoNome;
        this.contatoEmail = contatoEmail;
        this.contatoTelefone = contatoTelefone;
    }

    
}

