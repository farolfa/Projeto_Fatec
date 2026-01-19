package com.fatec.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.demo.model.Notificacao;
import com.fatec.demo.repository.NotificacaoRepository;

@Service
public class NotificacaoService {

    @Autowired
    private NotificacaoRepository repository;
    
    public List<Notificacao> findAll(){
        return repository.findAll();
    }
    
    public Notificacao findById(Long id){
        return repository.findById(id).orElse(null);
    }
    
    public Notificacao save(Notificacao notificacao){
        return repository.save(notificacao);
    }
    
    public void delete(Long id){
        repository.deleteById(id);
    }
}
