package com.fatec.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.demo.model.Proposta;

public interface PropostaRepository extends JpaRepository<Proposta, Long>{

}
