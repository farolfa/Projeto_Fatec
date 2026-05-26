package com.fatec.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.demo.model.Prestador;

public interface PrestadorRepository extends JpaRepository<Prestador, Long> {
}
