package com.fatec.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.demo.model.NotificacaoLidaExterna;

@Repository
public interface NotificacaoLidaExternaRepository extends JpaRepository<NotificacaoLidaExterna, Long> {

    List<NotificacaoLidaExterna> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndExternalId(Long usuarioId, String externalId);

    @Modifying
    @Transactional
    @Query("DELETE FROM NotificacaoLidaExterna n WHERE n.usuarioId = :usuarioId")
    void deleteAllByUsuarioId(@Param("usuarioId") Long usuarioId);
}
