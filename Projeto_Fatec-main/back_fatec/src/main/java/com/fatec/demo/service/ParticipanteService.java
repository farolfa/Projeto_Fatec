package com.fatec.demo.service;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.demo.model.Participante;
import com.fatec.demo.repository.ParticipanteRepository;

@Service
public class ParticipanteService {

    private static final Logger logger = Logger.getLogger(ParticipanteService.class.getName());

    @Autowired
    private ParticipanteRepository repository;
    
    public List<Participante> findAll(){
        try {
            return repository.findAll();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar participantes", e);
            throw new RuntimeException("Erro ao buscar participantes do banco de dados", e);
        }
    }
    
    public Participante findById(Long id){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido: deve ser um número positivo");
            }
            return repository.findById(id).orElse(null);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao buscar participante", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar participante com ID: " + id, e);
            throw new RuntimeException("Erro ao buscar participante do banco de dados", e);
        }
    }
    
    @Transactional
    public Participante save(Participante participante){
        try {
            if (participante == null) {
                throw new IllegalArgumentException("Participante não pode ser nulo");
            }
            Participante saved = repository.save(participante);
            logger.info("Participante salvo com sucesso");
            return saved;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao salvar participante", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar participante no banco de dados", e);
            throw new RuntimeException("Erro ao salvar participante no banco de dados", e);
        }
    }
    
    @Transactional
    public void delete(Long id){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido: deve ser um número positivo");
            }
            if (!repository.existsById(id)) {
                throw new IllegalArgumentException("Participante não encontrado com ID: " + id);
            }
            repository.deleteById(id);
            logger.info("Participante deletado com ID: " + id);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao deletar participante", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar participante com ID: " + id, e);
            throw new RuntimeException("Erro ao deletar participante do banco de dados", e);
        }
    }
}
