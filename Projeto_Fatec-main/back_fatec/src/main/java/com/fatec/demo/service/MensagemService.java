package com.fatec.demo.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.demo.dto.ChatConversationDto;
import com.fatec.demo.dto.ChatMessageDto;
import com.fatec.demo.dto.EnsureConversationRequest;
import com.fatec.demo.model.Mensagem;
import com.fatec.demo.model.Participante;
import com.fatec.demo.model.Usuario;
import com.fatec.demo.repository.MensagemRepository;
import com.fatec.demo.repository.ParticipanteRepository;
import com.fatec.demo.repository.UsuarioRepository;

@Service
public class MensagemService {

    private static final Logger logger = Logger.getLogger(MensagemService.class.getName());
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    private MensagemRepository repository;

    @Autowired
    private ParticipanteRepository participanteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    public List<Mensagem> findAll(){
        try {
            return repository.findAll();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar mensagens", e);
            throw new RuntimeException("Erro ao buscar mensagens do banco de dados", e);
        }
    }
    
    public Mensagem findById(Long id){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido: deve ser um número positivo");
            }
            return repository.findById(id).orElse(null);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao buscar mensagem", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar mensagem com ID: " + id, e);
            throw new RuntimeException("Erro ao buscar mensagem do banco de dados", e);
        }
    }

    public List<ChatConversationDto> listConversationsByUser(Long userId) {
        try {
            validateUserId(userId);
            List<Participante> participantes = participanteRepository
                .findByUsuarioClienteIdOrUsuarioPrestadorIdOrderByAceiteTimestampDesc(userId, userId);

            List<ChatConversationDto> response = new ArrayList<>();
            for (Participante participante : participantes) {
                response.add(toConversationDto(participante, userId));
            }
            return response;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao listar conversas", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao listar conversas do usuário", e);
            throw new RuntimeException("Erro ao listar conversas do banco de dados", e);
        }
    }

    public List<ChatMessageDto> listMessagesByConversation(Long conversationId, Long userId) {
        try {
            Participante participante = findParticipantForUser(conversationId, userId);
            List<Mensagem> mensagens = repository.findByParticipanteIdOrderByTimestampAsc(participante.getId());
            List<ChatMessageDto> response = new ArrayList<>();
            for (Mensagem mensagem : mensagens) {
                response.add(toMessageDto(mensagem));
            }
            return response;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao listar mensagens", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao listar mensagens da conversa", e);
            throw new RuntimeException("Erro ao listar mensagens do banco de dados", e);
        }
    }

    @Transactional
    public ChatConversationDto ensureConversation(EnsureConversationRequest request) {
        try {
            if (request == null) {
                throw new IllegalArgumentException("Dados da conversa são obrigatórios");
            }
            validateUserId(request.getClientUserId());
            validateUserId(request.getProviderUserId());
            if (request.getOrderId() == null || request.getOrderId() <= 0) {
                throw new IllegalArgumentException("Pedido da conversa é obrigatório");
            }
            if (request.getServiceTitle() == null || request.getServiceTitle().isBlank()) {
                throw new IllegalArgumentException("Título do serviço é obrigatório");
            }

            Long clientUserId = Objects.requireNonNull(request.getClientUserId());
            Long providerUserId = Objects.requireNonNull(request.getProviderUserId());

            Usuario cliente = usuarioRepository.findById(clientUserId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
            Usuario prestador = usuarioRepository.findById(providerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Prestador não encontrado"));

            Participante participante = participanteRepository
                .findByPedidoReferenciaAndUsuarioClienteIdAndUsuarioPrestadorId(
                    request.getOrderId(),
                    clientUserId,
                    providerUserId
                )
                .orElseGet(() -> {
                    Participante novo = new Participante();
                    novo.setUsuarioCliente(cliente);
                    novo.setUsuarioPrestador(prestador);
                    novo.setAceiteCliente(true);
                    novo.setAceitePrestador(true);
                    novo.setAceiteTimestamp(LocalDateTime.now());
                    novo.setPedidoReferencia(request.getOrderId());
                    novo.setTituloServico(request.getServiceTitle().trim());
                    return participanteRepository.save(novo);
                });

            if (participante.getTituloServico() == null || participante.getTituloServico().isBlank()) {
                participante.setTituloServico(request.getServiceTitle().trim());
                participanteRepository.save(participante);
            }

            return toConversationDto(participante, clientUserId);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao garantir conversa", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao garantir conversa", e);
            throw new RuntimeException("Erro ao garantir conversa no banco de dados", e);
        }
    }

    @Transactional
    public ChatMessageDto sendMessage(Long conversationId, Long senderUserId, String text) {
        try {
            Participante participante = findParticipantForUser(conversationId, senderUserId);
            if (text == null || text.isBlank()) {
                throw new IllegalArgumentException("Digite uma mensagem");
            }

            Long nonNullSenderUserId = Objects.requireNonNull(senderUserId);
            Usuario remetente = usuarioRepository.findById(nonNullSenderUserId)
                .orElseThrow(() -> new IllegalArgumentException("Remetente não encontrado"));

            Mensagem mensagem = new Mensagem();
            mensagem.setParticipante(participante);
            mensagem.setRemetente(remetente);
            mensagem.setConteudo(text.trim());
            mensagem.setTipo("TEXTO");
            mensagem.setTimestamp(LocalDateTime.now());
            mensagem.setLida(false);

            participante.setAceiteTimestamp(LocalDateTime.now());
            participanteRepository.save(participante);

            return toMessageDto(repository.save(mensagem));
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao enviar mensagem", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao enviar mensagem", e);
            throw new RuntimeException("Erro ao enviar mensagem no banco de dados", e);
        }
    }

    @Transactional
    public void markConversationAsRead(Long conversationId, Long userId) {
        try {
            Participante participante = findParticipantForUser(conversationId, userId);
            List<Mensagem> unreadMessages = repository
                .findByParticipanteIdAndRemetenteIdNotAndLidaFalse(participante.getId(), userId);
            for (Mensagem mensagem : unreadMessages) {
                mensagem.setLida(true);
                repository.save(mensagem);
            }
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao marcar conversa como lida", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao marcar conversa como lida", e);
            throw new RuntimeException("Erro ao atualizar mensagens no banco de dados", e);
        }
    }

    public long getUnreadMessagesCount(Long userId) {
        try {
            validateUserId(userId);
            List<Participante> participantes = participanteRepository
                .findByUsuarioClienteIdOrUsuarioPrestadorIdOrderByAceiteTimestampDesc(userId, userId);

            long total = 0L;
            for (Participante participante : participantes) {
                total += repository.countByParticipanteIdAndRemetenteIdNotAndLidaFalse(participante.getId(), userId);
            }
            return total;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao contar mensagens não lidas", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao contar mensagens não lidas", e);
            throw new RuntimeException("Erro ao contar mensagens não lidas", e);
        }
    }
    
    @Transactional
    public Mensagem save(Mensagem mensagem){
        try {
            if (mensagem == null) {
                throw new IllegalArgumentException("Mensagem não pode ser nula");
            }
            if (mensagem.getLida() == null) {
                mensagem.setLida(false);
            }
            Mensagem saved = repository.save(mensagem);
            logger.info("Mensagem salva com sucesso");
            return saved;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao salvar mensagem", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar mensagem no banco de dados", e);
            throw new RuntimeException("Erro ao salvar mensagem no banco de dados", e);
        }
    }
    
    @Transactional
    public void delete(Long id){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido: deve ser um número positivo");
            }
            if (!repository.existsById(id)) {
                throw new IllegalArgumentException("Mensagem não encontrada com ID: " + id);
            }
            repository.deleteById(id);
            logger.info(() -> "Mensagem deletada com ID: " + Objects.toString(id));
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao deletar mensagem", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, () -> "Erro ao deletar mensagem com ID: " + Objects.toString(id));
            throw new RuntimeException("Erro ao deletar mensagem do banco de dados", e);
        }
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("ID de usuário inválido");
        }
    }

    private Participante findParticipantForUser(Long conversationId, Long userId) {
        validateUserId(userId);
        if (conversationId == null || conversationId <= 0) {
            throw new IllegalArgumentException("Conversa inválida");
        }

        Participante participante = participanteRepository.findById(conversationId)
            .orElseThrow(() -> new IllegalArgumentException("Conversa não encontrada"));

        boolean isParticipant = participante.getUsuarioCliente() != null
            && participante.getUsuarioCliente().getId() != null
            && participante.getUsuarioCliente().getId().equals(userId);
        boolean isProvider = participante.getUsuarioPrestador() != null
            && participante.getUsuarioPrestador().getId() != null
            && participante.getUsuarioPrestador().getId().equals(userId);

        if (!isParticipant && !isProvider) {
            throw new IllegalArgumentException("Usuário não participa desta conversa");
        }

        return participante;
    }

    private ChatConversationDto toConversationDto(Participante participante, Long viewerUserId) {
        ChatConversationDto dto = new ChatConversationDto();
        dto.setId(participante.getId());
        dto.setOrderId(participante.getPedidoReferencia());
        dto.setServiceTitle(participante.getTituloServico());
        dto.setClientUserId(participante.getUsuarioCliente().getId());
        dto.setClientName(participante.getUsuarioCliente().getNome());
        dto.setClientPhoto(participante.getUsuarioCliente().getFoto());
        dto.setProviderUserId(participante.getUsuarioPrestador().getId());
        dto.setProviderName(participante.getUsuarioPrestador().getNome());
        dto.setProviderPhoto(participante.getUsuarioPrestador().getFoto());

        Mensagem lastMessage = repository.findTopByParticipanteIdOrderByTimestampDesc(participante.getId());
        dto.setLastMessage(lastMessage != null ? lastMessage.getConteudo() : "Conversa iniciada.");
        dto.setLastMessageTime(lastMessage != null ? lastMessage.getTimestamp().format(TIME_FORMATTER) : "--:--");
        dto.setUnreadCount(repository.countByParticipanteIdAndRemetenteIdNotAndLidaFalse(participante.getId(), viewerUserId));
        return dto;
    }

    private ChatMessageDto toMessageDto(Mensagem mensagem) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setId(mensagem.getId());
        dto.setConversationId(mensagem.getParticipante().getId());
        dto.setSenderUserId(mensagem.getRemetente().getId());
        dto.setText(mensagem.getConteudo());
        dto.setSentAt(mensagem.getTimestamp() != null ? mensagem.getTimestamp().format(TIME_FORMATTER) : "--:--");
        return dto;
    }
}
