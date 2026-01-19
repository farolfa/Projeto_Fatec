package com.fatec.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.demo.model.Contrato;
import com.fatec.demo.repository.ContratoRepository;

@Service
public class ContratoService {

    @Autowired
    private ContratoRepository repository;

    public List<Contrato> findAll(){
        return repository.findAll();
    }
    public Contrato findById(Long id){
        return repository.findById(id).orElse(null);
    }
    public Contrato save (Contrato contrato){
        return repository.save(contrato);
    }
    public void delete(Long id){
        repository.deleteById(id);
    }
}
