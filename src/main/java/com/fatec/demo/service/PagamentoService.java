package com.fatec.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.demo.model.Pagamento;
import com.fatec.demo.repository.PagamentoRepository;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository repository;
    
    public List<Pagamento> findAll(){
        return repository.findAll();
    }
    
    public Pagamento findById(Long id){
        return repository.findById(id).orElse(null);
    }
    
    public Pagamento save(Pagamento pagamento){
        return repository.save(pagamento);
    }
    
    public void delete(Long id){
        repository.deleteById(id);
    }
}
