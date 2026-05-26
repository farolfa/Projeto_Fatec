package com.fatec.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.demo.model.Notificacao;

public interface NotificacaoRepository extends JpaRepository<Notificacao,Long>{

    List<Notificacao> findByUsuarioIdOrderByDataDesc(Long usuarioId);

    long countByUsuarioIdAndLidaFalse(Long usuarioId);
}
