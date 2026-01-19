package com.fatec.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.demo.model.Endereco;
import com.fatec.demo.repository.EnderecoRepository;

@Service
public class EnderecoService {

    @Autowired
    private EnderecoRepository repository;
    
    public List<Endereco> findAll(){
        return repository.findAll();
    }
    public Endereco findById(Long id){
        return repository.findById(id).orElse(null);
    }
    public Endereco save(Endereco endereco){
        return repository.save(endereco);
    }
    public void delete(Long id){
        repository.deleteById(id);
    }
}
