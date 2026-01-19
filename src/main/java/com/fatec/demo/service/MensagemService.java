package com.fatec.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.demo.model.Mensagem;
import com.fatec.demo.repository.MensagemRepository;

@Service
public class MensagemService {

    @Autowired
    private MensagemRepository repository;
    
    public List<Mensagem> findAll(){
        return repository.findAll();
    }
    
    public Mensagem findById(Long id){
        return repository.findById(id).orElse(null);
    }
    
    public Mensagem save(Mensagem mensagem){
        return repository.save(mensagem);
    }
    
    public void delete(Long id){
        repository.deleteById(id);
    }
}
