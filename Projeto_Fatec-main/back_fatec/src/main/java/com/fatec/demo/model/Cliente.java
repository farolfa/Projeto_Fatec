package com.fatec.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "cliente")
@PrimaryKeyJoinColumn(name = "id_usuario")
public class Cliente extends Usuario {
    @Column(length = 120)
    private String apelido;

    @Column(length = 500)
    private String observacao;
    
    public Cliente() {
    }
    
    public Cliente(Long id, String nome, String email, String senha, boolean ativo, String tipo, String cpf,
            String telefone, String endereco, String estado, String cep, String bio, String foto, String cidade,
            String apelido, String observacao) {
        super(id, nome, email, senha, ativo, tipo, cpf, telefone, endereco, estado, cep, bio, foto, cidade);
        this.apelido = apelido;
        this.observacao = observacao;
    }

    public String getApelido() {
        return apelido;
    }

    public void setApelido(String apelido) {
        this.apelido = apelido;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}
