package com.fatec.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.demo.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario,Long>{
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByCpf(String cpf);
}
