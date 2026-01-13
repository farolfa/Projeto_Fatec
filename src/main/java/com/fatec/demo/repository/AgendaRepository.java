package com.fatec.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.demo.model.Agenda;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {

}
