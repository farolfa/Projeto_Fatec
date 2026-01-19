package com.fatec.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.demo.model.Categoria;
import com.fatec.demo.repository.CategoriaRepository;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository repository;

    public List<Categoria> findAll(){
        return repository.findAll();
    }

    public Categoria findById(Long id){
        return repository.findById(id).orElse(null);
    }

    public Categoria save(Categoria c){
        return repository.save(c);
    }

    public void delete(Long id){
        repository.deleteById(id);
    }
}
