package com.fatec.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.demo.model.Mensagem;

public interface MensagemRepository extends JpaRepository<Mensagem,Long> {

}
