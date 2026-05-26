package com.fatec.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.demo.model.ServicoCatalogo;

public interface ServicoCatalogoRepository extends JpaRepository<ServicoCatalogo, Long> {
}
