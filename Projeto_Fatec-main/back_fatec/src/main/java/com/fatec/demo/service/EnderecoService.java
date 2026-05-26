package com.fatec.demo.service;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.demo.model.Endereco;
import com.fatec.demo.repository.EnderecoRepository;

@Service
public class EnderecoService {

   private static final Logger logger = Logger.getLogger(EnderecoService.class.getName());

    @Autowired
    private EnderecoRepository repository;
    
    public List<Endereco> findAll(){
        try {
            return repository.findAll();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar endereços", e);
            throw new RuntimeException("Erro ao buscar endereços do banco de dados", e);
        }
    }
    
    public Endereco findById(Long id){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido: deve ser um número positivo");
            }
            return repository.findById(id).orElse(null);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao buscar endereço", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar endereço com ID: " + id, e);
            throw new RuntimeException("Erro ao buscar endereço do banco de dados", e);
        }
    }
    
    @Transactional
    public Endereco save(Endereco endereco){
        try {
            if (endereco == null) {
                throw new IllegalArgumentException("Endereço não pode ser nulo");
            }
            Endereco saved = repository.save(endereco);
            logger.info("Endereço salvo com sucesso");
            return saved;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao salvar endereço", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar endereço no banco de dados", e);
            throw new RuntimeException("Erro ao salvar endereço no banco de dados", e);
        }
    }
    
    @Transactional
    public void delete(Long id){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido: deve ser um número positivo");
            }
            if (!repository.existsById(id)) {
                throw new IllegalArgumentException("Endereço não encontrado com ID: " + id);
            }
            repository.deleteById(id);
            logger.info("Endereço deletado com ID: " + id);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao deletar endereço", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar endereço com ID: " + id, e);
            throw new RuntimeException("Erro ao deletar endereço do banco de dados", e);
        }
    }
}
