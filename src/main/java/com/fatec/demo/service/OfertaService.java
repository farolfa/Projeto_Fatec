package com.fatec.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.demo.model.Oferta;
import com.fatec.demo.repository.OfertaRepository;

@Service
public class OfertaService {

    @Autowired
    private OfertaRepository repository;
    
    public List<Oferta> findAll(){
        return repository.findAll();
    }
    
    public Oferta findById(Long id){
        return repository.findById(id).orElse(null);
    }
    
    public Oferta save(Oferta oferta){
        return repository.save(oferta);
    }
    
    public void delete(Long id){
        repository.deleteById(id);
    }
}
