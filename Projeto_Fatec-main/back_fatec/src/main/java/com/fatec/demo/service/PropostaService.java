package com.fatec.demo.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.demo.model.Proposta;
import com.fatec.demo.model.enums.StatusProposta;
import com.fatec.demo.repository.PedidoRepository;
import com.fatec.demo.repository.PropostaRepository;

@Service
public class PropostaService {

    private static final Logger logger = Logger.getLogger(PropostaService.class.getName());

    @Autowired
    private PropostaRepository repository;

    @Autowired
    private PedidoRepository pedidoRepository;
    
    public List<Proposta> findAll(){
        try {
            return repository.findAll();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar propostas", e);
            throw new RuntimeException("Erro ao buscar propostas do banco de dados", e);
        }
    }
    
    public Proposta findById(Long id){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido: deve ser um número positivo");
            }
            return repository.findById(id).orElse(null);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao buscar proposta", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar proposta com ID: " + id, e);
            throw new RuntimeException("Erro ao buscar proposta do banco de dados", e);
        }
    }
    
    @Transactional
    public Proposta save(Proposta proposta){
        try {
            if (proposta == null) {
                throw new IllegalArgumentException("Proposta não pode ser nula");
            }
            if (proposta.getPedido() == null || proposta.getPedido().getId() == null || proposta.getPedido().getId() <= 0) {
                throw new IllegalArgumentException("Pedido da proposta é obrigatório");
            }
            if (proposta.getPrestador() == null || proposta.getPrestador().getId() == null || proposta.getPrestador().getId() <= 0) {
                throw new IllegalArgumentException("Prestador da proposta é obrigatório");
            }
            if (proposta.getPrecoProposto() == null || proposta.getPrecoProposto().signum() <= 0) {
                throw new IllegalArgumentException("Preço proposto deve ser maior que zero");
            }

            // Carregar o pedido completo do banco para ter acesso ao usuario
            Long pedidoId = proposta.getPedido().getId();
            if (pedidoId == null) {
                throw new IllegalArgumentException("Pedido da proposta é obrigatório");
            }
            var pedidoOpt = pedidoRepository.findById(pedidoId);
            if (pedidoOpt.isEmpty()) {
                throw new IllegalArgumentException("Pedido não encontrado");
            }
            var pedidoCompleto = pedidoOpt.get();
            proposta.setPedido(pedidoCompleto);

            // Validação: o prestador não pode ser o mesmo que o cliente do pedido
            if (pedidoCompleto.getUsuario() != null && 
                proposta.getPrestador().getId().equals(pedidoCompleto.getUsuario().getId())) {
                throw new IllegalArgumentException("Você não pode enviar proposta para seu próprio pedido");
            }

            if (proposta.getId() == null && repository.existsByPedidoIdAndPrestadorId(proposta.getPedido().getId(), proposta.getPrestador().getId())) {
                throw new IllegalArgumentException("Já existe proposta deste prestador para este pedido");
            }

            String status = proposta.getStatus();
            proposta.setStatus(status == null || status.isBlank() ? StatusProposta.AGUARDANDO.name() : status);

            if (proposta.getMensagem() != null) {
                proposta.setMensagem(proposta.getMensagem().trim());
            }

            Proposta saved = repository.save(proposta);
            logger.info("Proposta salva com sucesso");
            return saved;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao salvar proposta", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar proposta no banco de dados", e);
            throw new RuntimeException("Erro ao salvar proposta no banco de dados", e);
        }
    }
    
    @Transactional
    public void delete(Long id){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido: deve ser um número positivo");
            }
            if (!repository.existsById(id)) {
                throw new IllegalArgumentException("Proposta não encontrada com ID: " + id);
            }
            repository.deleteById(id);
            logger.log(Level.INFO, String.format("Proposta deletada com ID: %d", id));
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao deletar proposta", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar proposta com ID: " + id, e);
            throw new RuntimeException("Erro ao deletar proposta do banco de dados", e);
        }
    }

    public List<Proposta> findByPedidoId(Long pedidoId){
        try {
            return repository.findByPedidoId(pedidoId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar propostas do pedido", e);
            throw new RuntimeException("Erro ao buscar propostas", e);
        }
    }

    public List<Proposta> findByPrestadorId(Long prestadorId){
        try {
            return repository.findByPrestadorId(prestadorId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar propostas do prestador", e);
            throw new RuntimeException("Erro ao buscar propostas", e);
        }
    }

    @Transactional
    public Proposta update(Long id, Proposta proposta){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido");
            }
            Proposta existing = repository.findById(id).orElse(null);
            if (existing == null) return null;

            if (proposta.getPrecoProposto() != null && proposta.getPrecoProposto().signum() > 0) {
                existing.setPrecoProposto(proposta.getPrecoProposto());
            }
            if (proposta.getMensagem() != null) {
                existing.setMensagem(proposta.getMensagem().trim());
            }
            if (proposta.getStatus() != null && !proposta.getStatus().isBlank()) {
                existing.setStatus(proposta.getStatus());
            }
            Proposta saved = repository.save(existing);
            logger.info("Proposta atualizada com ID: " + id);
            return saved;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao atualizar proposta", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao atualizar proposta", e);
            throw new RuntimeException("Erro ao atualizar proposta", e);
        }
    }

    @Transactional
    public Proposta aceitar(Long id){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido");
            }
            Proposta proposta = repository.findById(id).orElse(null);
            if (proposta == null) return null;

            Long pedidoId = proposta.getPedido().getId();

            // Reject all other proposals for the same order
            List<Proposta> outrosPropostas = repository.findByPedidoId(pedidoId);
            for (Proposta outra : outrosPropostas) {
                if (!Long.valueOf(outra.getId()).equals(id)) {
                    outra.setStatus(StatusProposta.RECUSADA.name());
                    repository.save(outra);
                }
            }

            // Accept this proposal
            proposta.setStatus(StatusProposta.ACEITA.name());
            Proposta saved = repository.save(proposta);

            // Update the order status to EM_ANDAMENTO
            var pedidoOpt = pedidoRepository.findById(pedidoId);
            if (pedidoOpt.isPresent()) {
                var pedido = pedidoOpt.get();
                pedido.setStatus(com.fatec.demo.model.enums.StatusPedido.EM_ANDAMENTO.name());
                pedidoRepository.save(pedido);
            }

            logger.info("Proposta aceita com ID: " + id);
            return saved;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao aceitar proposta", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao aceitar proposta", e);
            throw new RuntimeException("Erro ao aceitar proposta", e);
        }
    }
}
