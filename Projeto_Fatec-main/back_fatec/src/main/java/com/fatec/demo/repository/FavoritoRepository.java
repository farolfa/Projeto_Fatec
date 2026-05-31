package com.fatec.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.demo.model.Favorito;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {

    List<Favorito> findByUsuarioId(Long usuarioId);

    Optional<Favorito> findByUsuarioIdAndPrestadorId(Long usuarioId, Long prestadorId);

    boolean existsByUsuarioIdAndPrestadorId(Long usuarioId, Long prestadorId);

    @Modifying
    @Transactional
    void deleteByUsuarioIdOrPrestadorId(Long usuarioId, Long prestadorId);
}
