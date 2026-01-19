package com.fatec.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.demo.model.ServicoOferecido;
import com.fatec.demo.repository.ServicoOferecidoRepository;

@Service
public class ServicoOferecidoService {

    @Autowired
    private ServicoOferecidoRepository repository;
    
    public List<ServicoOferecido> findAll(){
        return repository.findAll();
    }
    
    public ServicoOferecido findById(Long id){
        return repository.findById(id).orElse(null);
    }
    
    public ServicoOferecido save(ServicoOferecido servicoOferecido){
        return repository.save(servicoOferecido);
    }
    
    public void delete(Long id){
        repository.deleteById(id);
    }
}
