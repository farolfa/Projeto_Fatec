package com.fatec.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.demo.model.Endereco;

public interface EnderecoRepository extends JpaRepository<Endereco, Long>{
	Optional<Endereco> findTopByUsuarioIdOrderByIdDesc(Long usuarioId);

	@Modifying
	@Transactional
	void deleteByUsuarioId(Long usuarioId);
}
