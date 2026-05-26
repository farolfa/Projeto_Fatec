package com.fatec.demo.service;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.demo.model.ServicoOferecido;
import com.fatec.demo.repository.ServicoOferecidoRepository;

@Service
public class ServicoOferecidoService {

    private static final Logger logger = Logger.getLogger(ServicoOferecidoService.class.getName());

    @Autowired
    private ServicoOferecidoRepository repository;
    
    public List<ServicoOferecido> findAll(){
        try {
            return repository.findAll();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar serviços oferecidos", e);
            throw new RuntimeException("Erro ao buscar serviços do banco de dados", e);
        }
    }
    
    public ServicoOferecido findById(Long id){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido: deve ser um número positivo");
            }
            return repository.findById(id).orElse(null);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao buscar serviço", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar serviço com ID: " + id, e);
            throw new RuntimeException("Erro ao buscar serviço do banco de dados", e);
        }
    }
    
    @Transactional
    public ServicoOferecido save(ServicoOferecido servicoOferecido){
        try {
            if (servicoOferecido == null) {
                throw new IllegalArgumentException("Serviço não pode ser nulo");
            }
            ServicoOferecido saved = repository.save(servicoOferecido);
            logger.info("Serviço salvo com sucesso");
            return saved;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao salvar serviço", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar serviço no banco de dados", e);
            throw new RuntimeException("Erro ao salvar serviço no banco de dados", e);
        }
    }
    
    @Transactional
    public void delete(Long id){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido: deve ser um número positivo");
            }
            if (!repository.existsById(id)) {
                throw new IllegalArgumentException("Serviço não encontrado com ID: " + id);
            }
            repository.deleteById(id);
            logger.info("Serviço deletado com ID: " + id);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao deletar serviço", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar serviço com ID: " + id, e);
            throw new RuntimeException("Erro ao deletar serviço do banco de dados", e);
        }
    }
}
