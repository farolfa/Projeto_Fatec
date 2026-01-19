package com.fatec.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.demo.model.Usuario;
import com.fatec.demo.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;
    
    public List<Usuario> findAll(){
        return repository.findAll();
    }
    
    public Usuario findById(Long id){
        return repository.findById(id).orElse(null);
    }
    
    public Usuario save(Usuario usuario){
        return repository.save(usuario);
    }
    
    public void delete(Long id){
        repository.deleteById(id);
    }
}
