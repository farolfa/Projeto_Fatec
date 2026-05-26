package com.fatec.demo.service;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.demo.model.Pedido;
import com.fatec.demo.model.enums.StatusPedido;
import com.fatec.demo.repository.PedidoRepository;

@Service
public class PedidoService {

    private static final Logger logger = Logger.getLogger(PedidoService.class.getName());

    @Autowired
    private PedidoRepository repository;
    
    public List<Pedido> findAll(){
        try {
            return repository.findAll();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar todos os pedidos", e);
            throw new RuntimeException("Erro ao buscar pedidos do banco de dados", e);
        }
    }
    
    public List<Pedido> findByUsuarioId(Long usuarioId){
        try {
            if (usuarioId == null || usuarioId <= 0) {
                throw new IllegalArgumentException("ID de usuário inválido");
            }
            return repository.findByUsuarioId(usuarioId);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao buscar pedidos do usuário", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar pedidos do usuário", e);
            throw new RuntimeException("Erro ao buscar pedidos do banco de dados", e);
        }
    }

    public List<Pedido> findByStatus(String status){
        try {
            if (status == null || status.isBlank()) {
                throw new IllegalArgumentException("Status é obrigatório");
            }
            return repository.findByStatus(StatusPedido.from(status));
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao buscar pedidos por status", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar pedidos por status", e);
            throw new RuntimeException("Erro ao buscar pedidos do banco de dados", e);
        }
    }

    public List<Pedido> findByUsuarioIdNotAndStatus(Long usuarioId, String status){
        try {
            if (usuarioId == null || usuarioId <= 0) {
                throw new IllegalArgumentException("ID de usuário inválido");
            }
            if (status == null || status.isBlank()) {
                throw new IllegalArgumentException("Status é obrigatório");
            }
            return repository.findByUsuarioIdNotAndStatus(usuarioId, StatusPedido.from(status));
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao buscar pedidos", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar pedidos", e);
            throw new RuntimeException("Erro ao buscar pedidos do banco de dados", e);
        }
    }
    
    public Pedido findById(Long id){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido: deve ser um número positivo");
            }
            return repository.findById(id).orElse(null);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao buscar pedido", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar pedido com ID: " + id, e);
            throw new RuntimeException("Erro ao buscar pedido do banco de dados", e);
        }
    }
    
    @Transactional
    public Pedido save(Pedido pedido){
        try {
            if (pedido == null) {
                throw new IllegalArgumentException("Pedido não pode ser nulo");
            }
            if (pedido.getUsuario() == null || pedido.getUsuario().getId() == null || pedido.getUsuario().getId() <= 0) {
                throw new IllegalArgumentException("ID de usuário inválido");
            }
            if (pedido.getTitulo() == null || pedido.getTitulo().isBlank()) {
                throw new IllegalArgumentException("Título do pedido é obrigatório");
            }
            if (pedido.getDescricao() == null || pedido.getDescricao().isBlank()) {
                throw new IllegalArgumentException("Descrição do pedido é obrigatória");
            }
            if (pedido.getLocalizacao() == null || pedido.getLocalizacao().isBlank()) {
                throw new IllegalArgumentException("Localização do pedido é obrigatória");
            }

            String status = pedido.getStatus();
            pedido.setStatus(status == null || status.isBlank() ? StatusPedido.ABERTO.name() : status);
            pedido.setTitulo(pedido.getTitulo().trim());
            pedido.setDescricao(pedido.getDescricao().trim());
            pedido.setLocalizacao(pedido.getLocalizacao().trim());

            if (pedido.getContatoNome() != null) {
                pedido.setContatoNome(pedido.getContatoNome().trim());
            }
            if (pedido.getContatoEmail() != null) {
                pedido.setContatoEmail(pedido.getContatoEmail().trim().toLowerCase());
            }
            if (pedido.getContatoTelefone() != null) {
                pedido.setContatoTelefone(pedido.getContatoTelefone().replaceAll("\\D", ""));
            }

            Pedido saved = repository.save(pedido);
            logger.info("Pedido salvo com sucesso");
            return saved;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao salvar pedido", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar pedido no banco de dados", e);
            throw new RuntimeException("Erro ao salvar pedido no banco de dados", e);
        }
    }
    
    @Transactional
    public Pedido update(Long id, Pedido pedido){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido");
            }
            Pedido existing = repository.findById(id).orElse(null);
            if (existing == null) return null;

            if (pedido.getStatus() != null) {
                existing.setStatus(pedido.getStatus());
            }
            if (pedido.getTitulo() != null && !pedido.getTitulo().isBlank()) {
                existing.setTitulo(pedido.getTitulo().trim());
            }
            if (pedido.getDescricao() != null && !pedido.getDescricao().isBlank()) {
                existing.setDescricao(pedido.getDescricao().trim());
            }
            if (pedido.getLocalizacao() != null && !pedido.getLocalizacao().isBlank()) {
                existing.setLocalizacao(pedido.getLocalizacao().trim());
            }
            existing.setClienteConfirmouConclusao(pedido.isClienteConfirmouConclusao());
            existing.setPrestadorConfirmouConclusao(pedido.isPrestadorConfirmouConclusao());

            Pedido saved = repository.save(existing);
            logger.info("Pedido atualizado com ID: " + id);
            return saved;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao atualizar pedido", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao atualizar pedido", e);
            throw new RuntimeException("Erro ao atualizar pedido", e);
        }
    }

    @Transactional
    public Pedido confirmarConclusao(Long id, String tipo){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido");
            }
            Pedido pedido = repository.findById(id).orElse(null);
            if (pedido == null) return null;

            if ("CLIENTE".equalsIgnoreCase(tipo)) {
                pedido.setClienteConfirmouConclusao(true);
            } else if ("PRESTADOR".equalsIgnoreCase(tipo)) {
                pedido.setPrestadorConfirmouConclusao(true);
            } else {
                throw new IllegalArgumentException("Tipo deve ser CLIENTE ou PRESTADOR");
            }

            if (pedido.isClienteConfirmouConclusao() && pedido.isPrestadorConfirmouConclusao()) {
                pedido.setStatus(StatusPedido.CONCLUIDO.name());
            }

            Pedido saved = repository.save(pedido);
            logger.info("Conclusão confirmada para pedido: " + id + " por " + tipo);
            return saved;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao confirmar conclusão", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao confirmar conclusão", e);
            throw new RuntimeException("Erro ao confirmar conclusão", e);
        }
    }

    @Transactional
    public void delete(Long id){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido: deve ser um número positivo");
            }
            if (!repository.existsById(id)) {
                throw new IllegalArgumentException("Pedido não encontrado com ID: " + id);
            }
            repository.deleteById(id);
            logger.info("Pedido deletado com ID: " + id);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao deletar pedido", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao deletar pedido com ID: " + id, e);
            throw new RuntimeException("Erro ao deletar pedido do banco de dados", e);
        }
    }
}
