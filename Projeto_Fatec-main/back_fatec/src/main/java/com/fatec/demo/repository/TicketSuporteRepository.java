package com.fatec.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.demo.model.TicketSuporte;

public interface TicketSuporteRepository extends JpaRepository<TicketSuporte, Long> {

    List<TicketSuporte> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId);
}
