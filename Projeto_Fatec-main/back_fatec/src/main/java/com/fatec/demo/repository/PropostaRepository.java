package com.fatec.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.demo.model.Proposta;

public interface PropostaRepository extends JpaRepository<Proposta, Long>{
	boolean existsByPedidoIdAndPrestadorId(Long pedidoId, Long prestadorId);
	List<Proposta> findByPedidoId(Long pedidoId);
	List<Proposta> findByPrestadorId(Long prestadorId);

	@Modifying
	@Transactional
	void deleteByPrestadorId(Long prestadorId);

	@Modifying
	@Transactional
	@Query("DELETE FROM Proposta p WHERE p.pedido.usuario.id = :usuarioId")
	void deleteByPedidoUsuarioId(@Param("usuarioId") Long usuarioId);
}
