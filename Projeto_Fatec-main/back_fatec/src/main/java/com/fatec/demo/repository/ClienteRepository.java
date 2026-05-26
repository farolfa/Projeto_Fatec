package com.fatec.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.demo.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long>{

}
