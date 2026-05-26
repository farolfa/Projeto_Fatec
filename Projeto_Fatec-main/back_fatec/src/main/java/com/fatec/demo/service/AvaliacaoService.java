package com.fatec.demo.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.demo.model.Avaliacao;
import com.fatec.demo.repository.AvaliacaoRepository;

@Service
public class AvaliacaoService {

    private static final Logger logger = Logger.getLogger(AvaliacaoService.class.getName());

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    public List<Avaliacao> findAll(){
        try {
            return avaliacaoRepository.findAll();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar avaliações", e);
            throw new RuntimeException("Erro ao buscar avaliações do banco de dados", e);
        }
    }

    public Avaliacao findById(Long id){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido: deve ser um número positivo");
            }
            return avaliacaoRepository.findById(id).orElse(null);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao buscar avaliação", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar avaliação com ID: " + id, e);
            throw new RuntimeException("Erro ao buscar avaliação do banco de dados", e);
        }
    }

    @Transactional
    public Avaliacao save(Avaliacao a){
        try {
            if (a == null) {
                throw new IllegalArgumentException("Avaliação não pode ser nula");
            }
            if (a.getAvaliador() == null || a.getAvaliador().getId() == null || a.getAvaliador().getId() <= 0) {
                throw new IllegalArgumentException("Avaliador é obrigatório");
            }
            if (a.getAvaliado() == null || a.getAvaliado().getId() == null || a.getAvaliado().getId() <= 0) {
                throw new IllegalArgumentException("Avaliado é obrigatório");
            }
            if (a.getAvaliador().getId().equals(a.getAvaliado().getId())) {
                throw new IllegalArgumentException("Usuário não pode avaliar a si mesmo");
            }
            if (a.getNota() == null || a.getNota() < 1 || a.getNota() > 5) {
                throw new IllegalArgumentException("Nota deve estar entre 1 e 5");
            }

            if (a.getComentario() != null) {
                a.setComentario(a.getComentario().trim());
            }
            if (a.getData() == null) {
                a.setData(LocalDateTime.now());
            }

            Avaliacao saved = avaliacaoRepository.save(a);
            logger.info("Avaliação salva com sucesso");
            return saved;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao salvar avaliação", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar avaliação no banco de dados", e);
            throw new RuntimeException("Erro ao salvar avaliação no banco de dados", e);
        }
    }

    public List<Avaliacao> findByAvaliadoId(Long avaliadoId) {
        try {
            return avaliacaoRepository.findByAvaliadoIdOrderByDataDesc(avaliadoId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar avaliações do avaliado " + avaliadoId, e);
            throw new RuntimeException("Erro ao buscar avaliações", e);
        }
    }

    public List<Avaliacao> findByAvaliadorId(Long avaliadorId) {
        try {
            return avaliacaoRepository.findByAvaliadorIdOrderByDataDesc(avaliadorId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar avaliações do avaliador " + avaliadorId, e);
            throw new RuntimeException("Erro ao buscar avaliações", e);
        }
    }

    public boolean jaAvaliou(Long avaliadorId, Long avaliadoId) {
        return avaliacaoRepository.existsByAvaliadorIdAndAvaliadoId(avaliadorId, avaliadoId);
    }

    public Map<String, Object> getMedia(Long avaliadoId) {
        double media = avaliacaoRepository.calcularMediaByAvaliadoId(avaliadoId);
        long total = avaliacaoRepository.countByAvaliadoId(avaliadoId);
        return Map.of("media", Math.round(media * 10.0) / 10.0, "total", total);
    }

    @Transactional
    public void delete(Long id){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido: deve ser um número positivo");
            }
            if (!avaliacaoRepository.existsById(id)) {
                throw new IllegalArgumentException("Avaliação não encontrada com ID: " + id);
            }
            avaliacaoRepository.deleteById(id);
            logger.info("Avaliação deletada com ID: " + id);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao deletar avaliação", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar avaliação com ID: " + id, e);
            throw new RuntimeException("Erro ao deletar avaliação do banco de dados", e);
        }
    }
}
