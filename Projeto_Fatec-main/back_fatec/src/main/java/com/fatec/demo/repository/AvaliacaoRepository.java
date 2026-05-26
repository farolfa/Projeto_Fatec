package com.fatec.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fatec.demo.model.Avaliacao;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {

    List<Avaliacao> findByAvaliadoIdOrderByDataDesc(Long avaliadoId);

    List<Avaliacao> findByAvaliadorIdOrderByDataDesc(Long avaliadorId);

    boolean existsByAvaliadorIdAndAvaliadoId(Long avaliadorId, Long avaliadoId);

    @Query("SELECT COALESCE(AVG(a.nota), 0) FROM Avaliacao a WHERE a.avaliado.id = :avaliadoId")
    Double calcularMediaByAvaliadoId(@Param("avaliadoId") Long avaliadoId);

    long countByAvaliadoId(Long avaliadoId);
}
