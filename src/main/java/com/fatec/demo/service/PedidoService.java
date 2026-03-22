package com.fatec.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.demo.model.Pedido;
import com.fatec.demo.repository.PedidoRepository;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository repository;
    
    public List<Pedido> findAll(){
        return repository.findAll();
    }
    
    public List<Pedido> findByUsuarioId(Long usuarioId){
        return repository.findByUsuarioId(usuarioId);
    }

    public List<Pedido> findByStatus(String status){
        return repository.findByStatus(status);
    }

    public List<Pedido> findByUsuarioIdNotAndStatus(Long usuarioId, String status){
        return repository.findByUsuarioIdNotAndStatus(usuarioId, status);
    }
    
    public Pedido findById(Long id){
        return repository.findById(id).orElse(null);
    }
    
    public Pedido save(Pedido pedido){
        return repository.save(pedido);
    }
    
    public void delete(Long id){
        repository.deleteById(id);
    }
}
