package com.fatec.demo.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.demo.model.Prestador;
import com.fatec.demo.repository.PrestadorRepository;

@Service
public class PrestadorService {

    private static final Logger logger = Logger.getLogger(PrestadorService.class.getName());

    @Autowired
    private PrestadorRepository repository;

    public List<Prestador> findAll() {
        try {
            return repository.findAll();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar prestadores", e);
            throw new RuntimeException("Erro ao buscar prestadores do banco de dados", e);
        }
    }

    public Prestador findById(Long id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID invalido: deve ser um numero positivo");
            }
            return repository.findById(id).orElse(null);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validacao ao buscar prestador", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar prestador com ID: " + id, e);
            throw new RuntimeException("Erro ao buscar prestador do banco de dados", e);
        }
    }

    @Transactional
    public Prestador save(Prestador prestador) {
        try {
            if (prestador == null) {
                throw new IllegalArgumentException("Prestador nao pode ser nulo");
            }
            if (prestador.getNome() == null || prestador.getNome().isBlank()) {
                throw new IllegalArgumentException("Nome do prestador e obrigatorio");
            }
            return repository.save(prestador);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validacao ao salvar prestador", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar prestador", e);
            throw new RuntimeException("Erro ao salvar prestador no banco de dados", e);
        }
    }

    @Transactional
    public void delete(Long id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID invalido: deve ser um numero positivo");
            }
            if (!repository.existsById(id)) {
                throw new IllegalArgumentException("Prestador nao encontrado com ID: " + id);
            }
            repository.deleteById(id);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validacao ao deletar prestador", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar prestador com ID: " + id, e);
            throw new RuntimeException("Erro ao deletar prestador do banco de dados", e);
        }
    }
}
