package com.fatec.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.demo.model.ServicoOferecido;

public interface ServicoOferecidoRepository extends JpaRepository<ServicoOferecido, Long>{

	@Modifying
	@Transactional
	void deleteByUsuarioId(Long usuarioId);

}
