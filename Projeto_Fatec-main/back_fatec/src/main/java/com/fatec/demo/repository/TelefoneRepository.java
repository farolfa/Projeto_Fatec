package com.fatec.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.demo.model.Telefone;

public interface TelefoneRepository extends JpaRepository<Telefone, Long> {

    /** Retorna o telefone principal mais recente do usuário. */
    Optional<Telefone> findTopByUsuarioIdAndPrincipalTrueOrderByIdDesc(Long usuarioId);

    @Modifying
    @Transactional
    void deleteByUsuarioId(Long usuarioId);
}
