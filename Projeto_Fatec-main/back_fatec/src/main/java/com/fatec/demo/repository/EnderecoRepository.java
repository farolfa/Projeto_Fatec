package com.fatec.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.demo.model.Endereco;

public interface EnderecoRepository extends JpaRepository<Endereco, Long>{
	Optional<Endereco> findTopByUsuarioIdOrderByIdDesc(Long usuarioId);
}
