package com.fatec.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.demo.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario,Long>{

}
