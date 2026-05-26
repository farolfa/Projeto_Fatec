package com.fatec.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "prestador",
    indexes = {
        @Index(name = "idx_prestador_especialidade", columnList = "especialidade")
    }
)
@PrimaryKeyJoinColumn(name = "id_usuario")
public class Prestador extends Usuario {

    @Column(name = "nome_profissional", nullable = false, length = 120)
    private String nomeProfissional;

    @Column(nullable = false, length = 160)
    private String especialidade;

    @Column(length = 500)
    private String descricao;

    public Prestador() {
    }

    public Prestador(Long id, String nome, String email, String senha, boolean ativo, String tipo, String cpf,
            String telefone, String endereco, String estado, String cep, String bio, String foto, String cidade,
            String nomeProfissional, String especialidade, String descricao) {
        super(id, nome, email, senha, ativo, tipo, cpf, telefone, endereco, estado, cep, bio, foto, cidade);
        this.nomeProfissional = nomeProfissional;
        this.especialidade = especialidade;
        this.descricao = descricao;
    }

    public String getNomeProfissional() {
        return nomeProfissional;
    }

    public void setNomeProfissional(String nomeProfissional) {
        this.nomeProfissional = nomeProfissional;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
