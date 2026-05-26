package com.fatec.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.demo.model.Proposta;

public interface PropostaRepository extends JpaRepository<Proposta, Long>{
	boolean existsByPedidoIdAndPrestadorId(Long pedidoId, Long prestadorId);
	List<Proposta> findByPedidoId(Long pedidoId);
	List<Proposta> findByPrestadorId(Long prestadorId);
}
