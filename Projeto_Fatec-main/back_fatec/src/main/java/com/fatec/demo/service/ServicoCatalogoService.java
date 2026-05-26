package com.fatec.demo.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.demo.model.ServicoCatalogo;
import com.fatec.demo.repository.ServicoCatalogoRepository;

@Service
public class ServicoCatalogoService {

    private static final Logger logger = Logger.getLogger(ServicoCatalogoService.class.getName());

    @Autowired
    private ServicoCatalogoRepository repository;

    public List<ServicoCatalogo> findAll() {
        try {
            return repository.findAll();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar servicos de catalogo", e);
            throw new RuntimeException("Erro ao buscar servicos de catalogo no banco de dados", e);
        }
    }

    public ServicoCatalogo findById(Long id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID invalido: deve ser um numero positivo");
            }
            return repository.findById(id).orElse(null);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validacao ao buscar servico de catalogo", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar servico de catalogo com ID: " + id, e);
            throw new RuntimeException("Erro ao buscar servico de catalogo no banco de dados", e);
        }
    }

    @Transactional
    public ServicoCatalogo save(ServicoCatalogo servicoCatalogo) {
        try {
            if (servicoCatalogo == null) {
                throw new IllegalArgumentException("Servico de catalogo nao pode ser nulo");
            }
            return repository.save(servicoCatalogo);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validacao ao salvar servico de catalogo", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar servico de catalogo", e);
            throw new RuntimeException("Erro ao salvar servico de catalogo no banco de dados", e);
        }
    }

    @Transactional
    public void delete(Long id) {
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID invalido: deve ser um numero positivo");
            }
            if (!repository.existsById(id)) {
                throw new IllegalArgumentException("Servico de catalogo nao encontrado com ID: " + id);
            }
            repository.deleteById(id);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validacao ao deletar servico de catalogo", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar servico de catalogo com ID: " + id, e);
            throw new RuntimeException("Erro ao deletar servico de catalogo do banco de dados", e);
        }
    }
}
