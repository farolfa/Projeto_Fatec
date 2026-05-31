package com.fatec.demo.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.demo.model.Endereco;
import com.fatec.demo.model.Participante;
import com.fatec.demo.model.Telefone;
import com.fatec.demo.model.TicketSuporte;
import com.fatec.demo.model.Usuario;
import com.fatec.demo.repository.AvaliacaoRepository;
import com.fatec.demo.repository.ClienteRepository;
import com.fatec.demo.repository.EnderecoRepository;
import com.fatec.demo.repository.FavoritoRepository;
import com.fatec.demo.repository.MensagemRepository;
import com.fatec.demo.repository.NotificacaoLidaExternaRepository;
import com.fatec.demo.repository.NotificacaoRepository;
import com.fatec.demo.repository.ParticipanteRepository;
import com.fatec.demo.repository.PedidoRepository;
import com.fatec.demo.repository.PrestadorRepository;
import com.fatec.demo.repository.PropostaRepository;
import com.fatec.demo.repository.ServicoOferecidoRepository;
import com.fatec.demo.repository.TelefoneRepository;
import com.fatec.demo.repository.TicketSuporteRepository;
import com.fatec.demo.repository.UsuarioRepository;

import jakarta.annotation.PostConstruct;

@Service
public class UsuarioService {

    private static final Logger logger = Logger.getLogger(UsuarioService.class.getName());

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private TelefoneRepository telefoneRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Autowired
    private PropostaRepository propostaRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ParticipanteRepository participanteRepository;

    @Autowired
    private MensagemRepository mensagemRepository;

    @Autowired
    private TicketSuporteRepository ticketSuporteRepository;

    @Autowired
    private NotificacaoRepository notificacaoRepository;

    @Autowired
    private NotificacaoLidaExternaRepository notificacaoLidaExternaRepository;

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private ServicoOferecidoRepository servicoOferecidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PrestadorRepository prestadorRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void normalizeUserStatusColumn() {
        try {
            List<Usuario> usuarios = repository.findAll();
            boolean changed = false;

            for (Usuario u : usuarios) {
                Integer status = u.getStatus();
                Integer expectedFromTipo = mapTipoToStatus(u.getTipo());

                if (status == null) {
                    u.setStatus(expectedFromTipo);
                    changed = true;
                    continue;
                }

                // Keep ADMIN PRINCIPAL as-is; for others, align tipo and status.
                if (status != Usuario.STATUS_ADMIN_PRINCIPAL) {
                    String tipoAtual = u.getTipo() == null ? "" : u.getTipo().trim().toLowerCase();
                    if ((status == Usuario.STATUS_PRESTADOR && !"prestador".equals(tipoAtual))
                        || (status == Usuario.STATUS_CLIENTE && !"cliente".equals(tipoAtual))
                        || (status == Usuario.STATUS_ADMIN && !"admin".equals(tipoAtual))) {
                        u.setStatus(status);
                        changed = true;
                    }
                }
            }

            if (changed) {
                repository.saveAll(usuarios);
                logger.info("Status dos usuarios normalizado com sucesso.");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Falha ao normalizar coluna status de usuarios", e);
        }
    }

    private Integer mapTipoToStatus(String tipo) {
        String normalized = tipo == null ? "" : tipo.trim().toLowerCase();
        return switch (normalized) {
            case "prestador" -> Usuario.STATUS_PRESTADOR;
            case "admin" -> Usuario.STATUS_ADMIN;
            case "cliente" -> Usuario.STATUS_CLIENTE;
            default -> Usuario.STATUS_CLIENTE;
        };
    }
    
    public List<Usuario> findAll(){
        try {
            List<Usuario> lista = repository.findAll();
            lista.forEach(this::hydrateTransientFields);
            return lista;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar todos os usuários", e);
            throw new RuntimeException("Erro ao buscar usuários do banco de dados", e);
        }
    }
    
    public Usuario findById(Long id){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido deve ser um número positivo");
            }
            Usuario u = repository.findById(id).orElse(null);
            if (u != null) hydrateTransientFields(u);
            return u;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao buscar usuário", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao buscar usuário com ID: " + id, e);
            throw new RuntimeException("Erro ao buscar usuário do banco de dados", e);
        }
    }
    
    @Transactional
    public Usuario save(Usuario usuario){
        try {
            if (usuario == null) {
                throw new IllegalArgumentException("Usuário não pode ser nulo");
            }
            if (usuario.getNome() == null || usuario.getNome().isBlank()) {
                throw new IllegalArgumentException("Nome é obrigatório");
            }
            if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
                throw new IllegalArgumentException("E-mail é obrigatório");
            }
            return repository.save(usuario);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao salvar usuário", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao salvar usuário no banco de dados", e);
            throw new RuntimeException("Erro ao salvar usuário no banco de dados", e);
        }
    }

    @Transactional
    public Usuario register(Usuario usuario){
        try {
            if (usuario == null) {
                throw new IllegalArgumentException("Usuário não pode ser nulo");
            }
            if (usuario.getNome() == null || usuario.getNome().isBlank()) {
                throw new IllegalArgumentException("Nome é obrigatório");
            }
            if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
                throw new IllegalArgumentException("E-mail é obrigatório");
            }
            String emailNormalizado = usuario.getEmail().trim().toLowerCase();
            if (emailNormalizado.length() > 255) {
                throw new IllegalArgumentException("E-mail muito longo");
            }

            if (repository.findByEmail(emailNormalizado).isPresent()) {
                throw new IllegalArgumentException("E-mail já cadastrado");
            }

            if (usuario.getSenha() == null || usuario.getSenha().isBlank()) {
                throw new IllegalArgumentException("Senha é obrigatória");
            }
            if (usuario.getSenha().length() < 6) {
                throw new IllegalArgumentException("Senha deve ter pelo menos 6 caracteres");
            }

            if (usuario.getCpf() == null || usuario.getCpf().isBlank()) {
                throw new IllegalArgumentException("CPF é obrigatório");
            }
            String cpfNormalizado = usuario.getCpf().replaceAll("\\D", "");
            if (cpfNormalizado.length() != 11) {
                throw new IllegalArgumentException("CPF inválido: deve conter 11 dígitos");
            }
            if (repository.findByCpf(cpfNormalizado).isPresent()) {
                throw new IllegalArgumentException("CPF já cadastrado");
            }

            if (usuario.getTelefone() == null || usuario.getTelefone().isBlank()) {
                throw new IllegalArgumentException("Telefone é obrigatório");
            }
            String telefoneNumerico = usuario.getTelefone().replaceAll("\\D", "");
            if (telefoneNumerico.length() < 10 || telefoneNumerico.length() > 11) {
                throw new IllegalArgumentException("Telefone inválido: informe DDD + número");
            }

            if (usuario.getEndereco() == null || usuario.getEndereco().isBlank()) {
                throw new IllegalArgumentException("Endereço é obrigatório");
            }
            if (usuario.getCidade() == null || usuario.getCidade().isBlank()) {
                throw new IllegalArgumentException("Cidade é obrigatória");
            }
            if (usuario.getEstado() == null || usuario.getEstado().isBlank()) {
                throw new IllegalArgumentException("Estado é obrigatório");
            }

            if (usuario.getCep() == null || usuario.getCep().isBlank()) {
                throw new IllegalArgumentException("CEP é obrigatório");
            }
            String cepNumerico = usuario.getCep().replaceAll("\\D", "");
            if (cepNumerico.length() != 8) {
                throw new IllegalArgumentException("CEP inválido: deve conter 8 dígitos");
            }

            String tipo = usuario.getTipo() == null ? "cliente" : usuario.getTipo().trim().toLowerCase();
            if (!tipo.equals("cliente") && !tipo.equals("prestador")) {
                tipo = "cliente";
            }

            String senhaHash = hashSha256(usuario.getSenha());
            String estadoNormalizado = usuario.getEstado() != null ? usuario.getEstado().trim().toUpperCase() : "";
            String cidadeNormalizada = usuario.getCidade() != null ? usuario.getCidade().trim() : "";
            String enderecoNormalizado = usuario.getEndereco() != null ? usuario.getEndereco().trim() : "";

            Usuario novoUsuario = new Usuario();
            novoUsuario.setTipo(tipo);
            novoUsuario.setNome(usuario.getNome().trim());
            novoUsuario.setEmail(emailNormalizado);
            novoUsuario.setSenha(senhaHash);
            novoUsuario.setCpf(cpfNormalizado);
            novoUsuario.setTelefone(telefoneNumerico);
            novoUsuario.setEndereco(enderecoNormalizado);
            novoUsuario.setCidade(cidadeNormalizada);
            novoUsuario.setEstado(estadoNormalizado);
            novoUsuario.setCep(cepNumerico);
            novoUsuario.setBio(usuario.getBio());
            novoUsuario.setFoto(usuario.getFoto());
            novoUsuario.setAtivo(true);
            novoUsuario.setStatus(mapTipoToStatus(tipo));

            Usuario saved = repository.save(novoUsuario);
            syncEnderecoDoUsuario(saved);

            logger.log(Level.INFO, "Novo usuário registrado: {0} ({1})", new Object[]{emailNormalizado, tipo});
            return saved;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao registrar usuário", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao registrar usuário no banco de dados", e);
            throw new RuntimeException("Erro ao registrar usuário no banco de dados: " + e.getMessage(), e);
        }
    }

    public Usuario login(String email, String senha){
        try {
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("E-mail é obrigatório");
            }
            if (senha == null || senha.isBlank()) {
                throw new IllegalArgumentException("Senha é obrigatória");
            }

            String emailNormalizado = email.trim().toLowerCase();

            Usuario usuario = repository.findByEmail(emailNormalizado).orElse(null);
            if (usuario == null) {
                logger.log(Level.WARNING, "Tentativa de login falhou: {0}", emailNormalizado);
                return null;
            }

            if (!usuario.isAtivo()) {
                logger.log(Level.WARNING, "Tentativa de login com usuario bloqueado: {0}", emailNormalizado);
                throw new IllegalArgumentException("Usuario bloqueado. Contate o administrador.");
            }

            Usuario user = passwordMatches(senha, usuario.getSenha()) ? usuario : null;

            if (user != null) {
                hydrateTransientFields(user);
                logger.log(Level.INFO, "Login bem-sucedido: {0}", emailNormalizado);
            } else {
                logger.log(Level.WARNING, "Tentativa de login falhou: {0}", emailNormalizado);
            }

            return user;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao fazer login", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao fazer login", e);
            throw new RuntimeException("Erro ao fazer login", e);
        }
    }

    @Transactional
    public Usuario update(Long id, Usuario usuario){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido deve ser um número positivo");
            }
            if (usuario == null) {
                throw new IllegalArgumentException("Usuário não pode ser nulo");
            }

            Usuario existente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

            if (usuario.getNome() == null || usuario.getNome().isBlank()) {
                throw new IllegalArgumentException("Nome é obrigatório");
            }
            if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
                throw new IllegalArgumentException("E-mail é obrigatório");
            }

            String emailNormalizado = usuario.getEmail().trim().toLowerCase();
            repository.findByEmail(emailNormalizado)
                .filter(outro -> !outro.getId().equals(id))
                .ifPresent(outro -> {
                    throw new IllegalArgumentException("E-mail já cadastrado");
                });

            String cpfNormalizado = usuario.getCpf() == null
                ? existente.getCpf()
                : usuario.getCpf().replaceAll("\\D", "");
            if (cpfNormalizado == null || cpfNormalizado.isBlank()) {
                throw new IllegalArgumentException("CPF é obrigatório");
            }
            if (cpfNormalizado.length() != 11) {
                throw new IllegalArgumentException("CPF inválido: deve conter 11 dígitos");
            }
            repository.findByCpf(cpfNormalizado)
                .filter(outro -> !outro.getId().equals(id))
                .ifPresent(outro -> {
                    throw new IllegalArgumentException("CPF já cadastrado");
                });

            if (usuario.getTelefone() == null || usuario.getTelefone().isBlank()) {
                throw new IllegalArgumentException("Telefone é obrigatório");
            }
            String telefoneNumerico = usuario.getTelefone().replaceAll("\\D", "");
            if (telefoneNumerico.length() < 10 || telefoneNumerico.length() > 11) {
                throw new IllegalArgumentException("Telefone inválido: informe DDD + número");
            }

            if (usuario.getEndereco() == null || usuario.getEndereco().isBlank()) {
                throw new IllegalArgumentException("Endereço é obrigatório");
            }
            if (usuario.getCidade() == null || usuario.getCidade().isBlank()) {
                throw new IllegalArgumentException("Cidade é obrigatória");
            }
            if (usuario.getEstado() == null || usuario.getEstado().isBlank()) {
                throw new IllegalArgumentException("Estado é obrigatório");
            }
            if (usuario.getCep() == null || usuario.getCep().isBlank()) {
                throw new IllegalArgumentException("CEP é obrigatório");
            }

            String cepNumerico = usuario.getCep().replaceAll("\\D", "");
            if (cepNumerico.length() != 8) {
                throw new IllegalArgumentException("CEP inválido: deve conter 8 dígitos");
            }

            existente.setNome(usuario.getNome().trim());
            existente.setEmail(emailNormalizado);
            existente.setCpf(cpfNormalizado);
            existente.setTelefone(telefoneNumerico);
            existente.setEndereco(usuario.getEndereco().trim());
            existente.setCidade(usuario.getCidade().trim());
            existente.setEstado(usuario.getEstado().trim().toUpperCase());
            existente.setCep(cepNumerico);
            existente.setBio(usuario.getBio() == null ? null : usuario.getBio().trim());
            existente.setFoto(usuario.getFoto());

            if (usuario.getTipo() != null && !usuario.getTipo().isBlank()) {
                String tipoInformado = usuario.getTipo().trim().toLowerCase();
                if (!"cliente".equals(tipoInformado) && !"prestador".equals(tipoInformado) && !"admin".equals(tipoInformado)) {
                    throw new IllegalArgumentException("Tipo inválido: deve ser cliente, prestador ou admin");
                }
                if (existente.isAdminPrincipal() && !"admin".equals(tipoInformado)) {
                    throw new IllegalArgumentException("Não é permitido alterar o tipo do admin principal");
                }
                if (!existente.isAdmin() && "admin".equals(tipoInformado)) {
                    throw new IllegalArgumentException("Não é permitido promover usuário para admin");
                }
                existente.setTipo(tipoInformado);
            }

            if (usuario.getSenha() != null && !usuario.getSenha().isBlank()) {
                if (usuario.getSenha().length() < 6) {
                    throw new IllegalArgumentException("Senha deve ter pelo menos 6 caracteres");
                }
                existente.setSenha(hashSha256(usuario.getSenha()));
            }

            Usuario atualizado = repository.save(existente);
            syncEnderecoDoUsuario(atualizado);
            logger.log(Level.INFO, "Usuário atualizado com sucesso: {0}", atualizado.getEmail());
            return atualizado;
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao atualizar usuário", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao atualizar usuário no banco de dados", e);
            throw new RuntimeException("Erro ao atualizar usuário no banco de dados", e);
        }
    }

    @Transactional
    public Usuario switchPerfil(Long id, String tipo) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido: deve ser um número positivo");
        }
        if (tipo == null || tipo.isBlank()) {
            throw new IllegalArgumentException("Tipo é obrigatório");
        }

        String tipoNormalizado = tipo.trim().toLowerCase();
        if (!"cliente".equals(tipoNormalizado) && !"prestador".equals(tipoNormalizado)) {
            throw new IllegalArgumentException("Tipo inválido: deve ser cliente ou prestador");
        }

        Usuario existente = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (existente.isAdmin()) {
            throw new IllegalArgumentException("Perfil de admin não pode ser alternado");
        }

        existente.setTipo(tipoNormalizado);
        existente.setStatus(mapTipoToStatus(tipoNormalizado));
        Usuario atualizado = repository.save(existente);
        syncEnderecoDoUsuario(atualizado);
        return atualizado;
    }

    private void syncEnderecoDoUsuario(Usuario usuario) {
        if (usuario == null || usuario.getId() == null) {
            return;
        }

        Endereco endereco = enderecoRepository
            .findTopByUsuarioIdOrderByIdDesc(usuario.getId())
            .orElseGet(Endereco::new);

        endereco.setUsuario(usuario);
        endereco.setRua(usuario.getEndereco() != null ? usuario.getEndereco() : "");
        endereco.setNumero(endereco.getNumero() == null || endereco.getNumero().isBlank() ? "S/N" : endereco.getNumero());
        endereco.setBairro(endereco.getBairro() == null ? "" : endereco.getBairro());
        endereco.setCidade(usuario.getCidade() != null ? usuario.getCidade() : "");
        endereco.setEstado(usuario.getEstado() != null ? usuario.getEstado() : "");
        endereco.setCep(usuario.getCep() != null ? usuario.getCep() : "");
        endereco.setPrincipal(true);

        enderecoRepository.save(endereco);
        syncTelefoneDoUsuario(usuario);
    }

    private void syncTelefoneDoUsuario(Usuario usuario) {
        if (usuario == null || usuario.getId() == null || usuario.getTelefone() == null || usuario.getTelefone().isBlank()) {
            return;
        }

        Telefone telefone = telefoneRepository
            .findTopByUsuarioIdAndPrincipalTrueOrderByIdDesc(usuario.getId())
            .orElseGet(Telefone::new);

        telefone.setUsuario(usuario);
        telefone.setNumero(usuario.getTelefone());
        telefone.setTipo("celular");
        telefone.setPrincipal(true);

        telefoneRepository.save(telefone);
    }

    /**
     * Preenche os campos @Transient de endereço e telefone buscando
     * os registros principais nas tabelas FK.
     */
    private void hydrateTransientFields(Usuario usuario) {
        if (usuario == null || usuario.getId() == null) return;

        enderecoRepository.findTopByUsuarioIdOrderByIdDesc(usuario.getId()).ifPresent(e -> {
            usuario.setEndereco(e.getRua());
            usuario.setCidade(e.getCidade());
            usuario.setEstado(e.getEstado());
            usuario.setCep(e.getCep());
        });

        telefoneRepository.findTopByUsuarioIdAndPrincipalTrueOrderByIdDesc(usuario.getId()).ifPresent(t ->
            usuario.setTelefone(t.getNumero())
        );
    }

    private String hashSha256(String value){
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar hash", e);
        }
    }

    private boolean passwordMatches(String senhaInformada, String senhaBanco) {
        if (senhaInformada == null || senhaInformada.isBlank()) {
            return false;
        }
        if (senhaBanco == null || senhaBanco.isBlank()) {
            return false;
        }

        // Compatibilidade com dados legados: alguns usuários foram persistidos em texto puro.
        if (senhaBanco.matches("^[a-f0-9]{64}$")) {
            return senhaBanco.equals(hashSha256(senhaInformada));
        }

        return senhaBanco.equals(senhaInformada);
    }

    @Transactional
    public void delete(Long id){
        try {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException("ID inválido: deve ser um número positivo");
            }
            if (!repository.existsById(id)) {
                throw new IllegalArgumentException("Usuário não encontrado com ID: " + id);
            }

            // Remove mensagens ligadas ao usuário (como remetente e nas conversas em que participa).
            List<Participante> participantes = participanteRepository
                .findByUsuarioClienteIdOrUsuarioPrestadorId(id, id);
            List<Long> participanteIds = participantes.stream()
                .map(Participante::getId)
                .collect(Collectors.toList());

            if (!participanteIds.isEmpty()) {
                mensagemRepository.deleteByParticipanteIdIn(participanteIds);
            }
            mensagemRepository.deleteByRemetenteId(id);
            participanteRepository.deleteByUsuarioClienteIdOrUsuarioPrestadorId(id, id);

            // Remove propostas que o usuário recebeu em pedidos próprios e as propostas enviadas por ele.
            propostaRepository.deleteByPedidoUsuarioId(id);
            propostaRepository.deleteByPrestadorId(id);

            // Remove entidades relacionadas diretamente ao usuário.
            pedidoRepository.deleteByUsuarioId(id);
            avaliacaoRepository.deleteByAvaliadorIdOrAvaliadoId(id, id);
            favoritoRepository.deleteByUsuarioIdOrPrestadorId(id, id);
            notificacaoRepository.deleteByUsuarioId(id);
            notificacaoLidaExternaRepository.deleteAllByUsuarioId(id);
            servicoOferecidoRepository.deleteByUsuarioId(id);

            List<TicketSuporte> tickets = ticketSuporteRepository.findByUsuarioId(id);
            if (!tickets.isEmpty()) {
                ticketSuporteRepository.deleteAll(tickets);
            }

            telefoneRepository.deleteByUsuarioId(id);
            enderecoRepository.deleteByUsuarioId(id);

            if (prestadorRepository.existsById(id)) {
                prestadorRepository.deleteById(id);
            }
            if (clienteRepository.existsById(id)) {
                clienteRepository.deleteById(id);
            }

            repository.deleteById(id);
            logger.log(Level.INFO, "Usuário deletado com ID: {0}", id);
        } catch (IllegalArgumentException e) {
            logger.log(Level.WARNING, "Erro de validação ao deletar usuário", e);
            throw e;
        } catch (Exception e) {
            logger.log(Level.WARNING, "Falha na exclusão padrão do usuário. Tentando fallback SQL. ID=" + id, e);
            try {
                deleteWithSqlFallback(id);
                logger.log(Level.INFO, "Usuário deletado com fallback SQL. ID={0}", id);
            } catch (Exception fallbackEx) {
                logger.log(Level.SEVERE, "Erro ao deletar usuário com ID: " + id + " mesmo com fallback SQL", fallbackEx);
                throw new RuntimeException("Erro ao deletar usuário do banco de dados", fallbackEx);
            }
        }
    }

    private void deleteWithSqlFallback(Long id) {
        final String fkRefsSql = """
            SELECT t.name AS table_name, c.name AS column_name
            FROM sys.foreign_key_columns fkc
            INNER JOIN sys.tables rt ON rt.object_id = fkc.referenced_object_id
            INNER JOIN sys.tables t ON t.object_id = fkc.parent_object_id
            INNER JOIN sys.columns c ON c.object_id = fkc.parent_object_id AND c.column_id = fkc.parent_column_id
            WHERE rt.name = 'usuarios'
            ORDER BY t.name, c.name
            """;

        // Executa em múltiplas passagens para resolver cadeias de FKs (ex.: propostas -> pedidos -> usuarios).
        for (int pass = 1; pass <= 5; pass++) {
            List<Map<String, Object>> refs = jdbcTemplate.queryForList(fkRefsSql);

            for (Map<String, Object> ref : refs) {
                String table = String.valueOf(ref.get("table_name"));
                String column = String.valueOf(ref.get("column_name"));

                String deleteSql = "DELETE FROM [" + table + "] WHERE [" + column + "] = ?";
                try {
                    jdbcTemplate.update(deleteSql, id);
                } catch (Exception ex) {
                    // Ignora nesta passagem e tenta novamente após outras dependências serem removidas.
                }
            }

            jdbcTemplate.update("DELETE FROM notificacoes_lidas_externas WHERE usuario_id = ?", id);
            jdbcTemplate.update("DELETE FROM usuarios WHERE id = ?", id);

            Integer remaining = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM usuarios WHERE id = ?",
                Integer.class,
                id
            );

            if (remaining == null || remaining == 0) {
                return;
            }
        }

        throw new IllegalStateException("Não foi possível remover vínculos restantes do usuário");
    }
}
