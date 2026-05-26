package com.fatec.demo.service;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.demo.model.Cliente;
import com.fatec.demo.repository.ClienteRepository;

@Service
public class ClienteService {

    private static final Logger logger = Logger.getLogger(ClienteService.class.getName());

    @Autowired
    private ClienteRepository repository;

    public List<Cliente> findAll(){
        try {
            return repository.findAll();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar todos os clientes", e);
            throw new RuntimeException("Erro ao buscar clientes do banco de dados", e);
        }
    }
    
    public Cliente findById(Long id){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido: deve ser um número positivo");
            }
            return repository.findById(id).orElse(null);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao buscar cliente", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar cliente com ID: " + id, e);
            throw new RuntimeException("Erro ao buscar cliente do banco de dados", e);
        }
    }
    
    @Transactional
    public Cliente save(Cliente c){
        try {
            if (c == null) {
                throw new IllegalArgumentException("Cliente não pode ser nulo");
            }
            Cliente saved = repository.save(c);
            logger.info("Cliente salvo com sucesso");
            return saved;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao salvar cliente", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar cliente no banco de dados", e);
            throw new RuntimeException("Erro ao salvar cliente no banco de dados", e);
        }
    }
    
    @Transactional
    public void delete(Long id){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido: deve ser um número positivo");
            }
            if (!repository.existsById(id)) {
                throw new IllegalArgumentException("Cliente não encontrado com ID: " + id);
            }
            repository.deleteById(id);
            logger.info("Cliente deletado com ID: " + id);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao deletar cliente", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar cliente com ID: " + id, e);
            throw new RuntimeException("Erro ao deletar cliente do banco de dados", e);
        }
    }
}
