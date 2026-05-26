package com.fatec.demo.service;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.demo.model.Categoria;
import com.fatec.demo.repository.CategoriaRepository;

@Service
public class CategoriaService {

    private static final Logger logger = Logger.getLogger(CategoriaService.class.getName());

    @Autowired
    private CategoriaRepository repository;

    public List<Categoria> findAll(){
        try {
            return repository.findAll();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar categorias", e);
            throw new RuntimeException("Erro ao buscar categorias do banco de dados", e);
        }
    }

    public Categoria findById(Long id){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido: deve ser um número positivo");
            }
            return repository.findById(id).orElse(null);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao buscar categoria", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar categoria com ID: " + id, e);
            throw new RuntimeException("Erro ao buscar categoria do banco de dados", e);
        }
    }

    @Transactional
    public Categoria save(Categoria c){
        try {
            if (c == null) {
                throw new IllegalArgumentException("Categoria não pode ser nula");
            }
            Categoria saved = repository.save(c);
            logger.info("Categoria salva com sucesso");
            return saved;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao salvar categoria", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar categoria no banco de dados", e);
            throw new RuntimeException("Erro ao salvar categoria no banco de dados", e);
        }
    }

    @Transactional
    public void delete(Long id){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido: deve ser um número positivo");
            }
            if (!repository.existsById(id)) {
                throw new IllegalArgumentException("Categoria não encontrada com ID: " + id);
            }
            repository.deleteById(id);
            logger.info("Categoria deletada com ID: " + id);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao deletar categoria", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar categoria com ID: " + id, e);
            throw new RuntimeException("Erro ao deletar categoria do banco de dados", e);
        }
    }
}
