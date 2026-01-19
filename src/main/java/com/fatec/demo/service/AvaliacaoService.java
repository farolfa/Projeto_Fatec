package com.fatec.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.demo.model.Avaliacao;
import com.fatec.demo.repository.AvaliacaoRepository;

@Service
public class AvaliacaoService {

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    public List<Avaliacao> findAll(){
        return avaliacaoRepository.findAll();
    }

    public Avaliacao findById(Long id){
        return avaliacaoRepository.findById(id).orElse(null);
    }

    public Avaliacao save(Avaliacao a){
        return avaliacaoRepository.save(a);
    }

    public void delete(Long id){
        avaliacaoRepository.deleteById(id);
    }

}
