package com.fatec.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.demo.model.Cliente;
import com.fatec.demo.repository.ClienteRepository;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository repository;

    public List<Cliente> findAll(){
        return repository.findAll();
    }
    public Cliente findById(Long id){
        return repository.findById(id).orElse(null);
    }
    public Cliente save(Cliente c){
        return repository.save(c);
    }
    public void delete(Long id){
        repository.deleteById(id);
    }
}
