package com.fatec.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.demo.model.Participante;

public interface ParticipanteRepository extends JpaRepository<Participante, Long>{
	Optional<Participante> findByPedidoReferenciaAndUsuarioClienteIdAndUsuarioPrestadorId(
		Long pedidoReferencia,
		Long usuarioClienteId,
		Long usuarioPrestadorId
	);

	List<Participante> findByUsuarioClienteIdOrUsuarioPrestadorIdOrderByAceiteTimestampDesc(
		Long usuarioClienteId,
		Long usuarioPrestadorId
	);

}
