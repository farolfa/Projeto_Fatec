package com.fatec.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.demo.model.Agenda;
import com.fatec.demo.repository.AgendaRepository;

@Service
public class AgendaService {

    @Autowired
    private AgendaRepository agendaRepository;
    //CRUD SIMPLES 
    //create
    public Agenda saveAgenda(Agenda agenda){
        return agendaRepository.save(agenda);
    }

    //read
    public List<Agenda> findAll(){
        return agendaRepository.findAll();
    }

    public Agenda findById(Long id){
        return agendaRepository.findById(id).orElse(null);
    }
    
    //delete
    public void deleteById(Long id){
        agendaRepository.deleteById(id);
    }

}
