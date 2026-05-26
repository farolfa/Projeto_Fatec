package com.fatec.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.demo.model.RespostaTicket;

public interface RespostaTicketRepository extends JpaRepository<RespostaTicket, Long> {
}
