package com.fatec.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.demo.model.Participante;
import com.fatec.demo.repository.ParticipanteRepository;

@Service
public class ParticipanteService {

    @Autowired
    private ParticipanteRepository repository;
    
    public List<Participante> findAll(){
        return repository.findAll();
    }
    
    public Participante findById(Long id){
        return repository.findById(id).orElse(null);
    }
    
    public Participante save(Participante participante){
        return repository.save(participante);
    }
    
    public void delete(Long id){
        repository.deleteById(id);
    }
}
