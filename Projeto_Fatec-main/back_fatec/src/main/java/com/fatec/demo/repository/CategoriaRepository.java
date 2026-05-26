package com.fatec.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.demo.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria,Long>{

}
