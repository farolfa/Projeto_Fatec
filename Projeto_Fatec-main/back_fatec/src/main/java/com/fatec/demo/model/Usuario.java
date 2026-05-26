package com.fatec.demo.model;

import com.fatec.demo.model.enums.TipoUsuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "usuarios",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_usuarios_email", columnNames = "email"),
        @UniqueConstraint(name = "uk_usuarios_cpf", columnNames = "cpf")
    },
    indexes = {
        @Index(name = "idx_usuarios_tipo", columnList = "tipo"),
        @Index(name = "idx_usuarios_ativo", columnList = "ativo")
    }
)
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario {
    public static final int STATUS_PRESTADOR = 1;
    public static final int STATUS_CLIENTE = 2;
    public static final int STATUS_ADMIN = 10;
    public static final int STATUS_ADMIN_PRINCIPAL = 11;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, length = 160)
    private String email;

    @Column(nullable = false, length = 128)
    private String senha;

    @Column(nullable = false)
    private boolean ativo = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoUsuario tipo;

    @Column(name = "status")
    private Integer status;

    @Column(nullable = false, length = 14)
    private String cpf;

    /** Telefone principal — não persistido aqui; vem da tabela telefones (FK). */
    @Transient
    private String telefone;

    /** Endereço (logradouro) principal — não persistido aqui; vem da tabela enderecos (FK). */
    @Transient
    private String endereco;

    /** Estado — não persistido aqui; vem da tabela enderecos (FK). */
    @Transient
    private String estado;

    /** CEP — não persistido aqui; vem da tabela enderecos (FK). */
    @Transient
    private String cep;

    @Column(length = 500)
    private String bio;

    @Lob
    @Column(columnDefinition = "VARCHAR(MAX)")
    private String foto;

    /** Cidade — não persistida aqui; vem da tabela enderecos (FK). */
    @Transient
    private String cidade;

    public Usuario() {
    }

    public Usuario(Long id, String nome, String email, String senha, boolean ativo, String tipo, String cpf, String telefone, String endereco, String estado, String cep, String bio, String foto, String cidade) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.ativo = ativo;
        setTipo(tipo);
        this.cpf = cpf;
        this.telefone = telefone;
        this.endereco = endereco;
        this.estado = estado;
        this.cep = cep;
        this.bio = bio;
        this.foto = foto;
        this.cidade = cidade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getTipo() {
        return tipo == null ? null : tipo.getApiValue();
    }

    public void setTipo(String tipo) {
        this.tipo = TipoUsuario.from(tipo);
        this.status = inferStatusFromTipo(this.tipo);
    }

    public TipoUsuario getTipoEnum() {
        return tipo;
    }

    public void setTipoEnum(TipoUsuario tipo) {
        this.tipo = tipo;
        this.status = inferStatusFromTipo(tipo);
    }

    public Integer getStatus() {
        if (status == null) {
            status = inferStatusFromTipo(tipo);
        }
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
        if (status == null) {
            return;
        }

        if (status == STATUS_PRESTADOR) {
            this.tipo = TipoUsuario.PRESTADOR;
        } else if (status == STATUS_CLIENTE) {
            this.tipo = TipoUsuario.CLIENTE;
        } else if (status == STATUS_ADMIN || status == STATUS_ADMIN_PRINCIPAL) {
            this.tipo = TipoUsuario.ADMIN;
        }
    }

    public boolean isAdmin() {
        Integer resolvedStatus = getStatus();
        return resolvedStatus != null && resolvedStatus >= STATUS_ADMIN;
    }

    public boolean isAdminPrincipal() {
        Integer resolvedStatus = getStatus();
        return resolvedStatus != null && resolvedStatus == STATUS_ADMIN_PRINCIPAL;
    }

    private Integer inferStatusFromTipo(TipoUsuario tipo) {
        if (tipo == null) {
            return STATUS_CLIENTE;
        }

        return switch (tipo) {
            case PRESTADOR -> STATUS_PRESTADOR;
            case CLIENTE -> STATUS_CLIENTE;
            case ADMIN -> STATUS_ADMIN;
        };
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    @PrePersist
    @PreUpdate
    private void normalize() {
        if (status == null) {
            status = inferStatusFromTipo(tipo);
        }
        if (tipo == null && status != null) {
            if (status == STATUS_PRESTADOR) {
                tipo = TipoUsuario.PRESTADOR;
            } else if (status == STATUS_CLIENTE) {
                tipo = TipoUsuario.CLIENTE;
            } else {
                tipo = TipoUsuario.ADMIN;
            }
        }

        if (email != null) {
            email = email.trim().toLowerCase();
        }
        if (cpf != null) {
            cpf = cpf.replaceAll("\\D", "");
        }
        if (telefone != null) {
            telefone = telefone.replaceAll("\\D", "");
        }
        if (cep != null) {
            cep = cep.replaceAll("\\D", "");
        }
        if (estado != null) {
            estado = estado.trim().toUpperCase();
        }
        if (nome != null) {
            nome = nome.trim();
        }
        if (cidade != null) {
            cidade = cidade.trim();
        }
        if (endereco != null) {
            endereco = endereco.trim();
        }
        if (bio != null) {
            bio = bio.trim();
        }
    }



  
}
