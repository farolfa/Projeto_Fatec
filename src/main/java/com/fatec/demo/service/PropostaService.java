package com.fatec.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.demo.model.Proposta;
import com.fatec.demo.repository.PropostaRepository;

@Service
public class PropostaService {

    @Autowired
    private PropostaRepository repository;
    
    public List<Proposta> findAll(){
        return repository.findAll();
    }
    
    public Proposta findById(Long id){
        return repository.findById(id).orElse(null);
    }
    
    public Proposta save(Proposta proposta){
        return repository.save(proposta);
    }
    
    public void delete(Long id){
        repository.deleteById(id);
    }
}
