package com.fatec.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fatec.demo.model.Usuario;
import com.fatec.demo.repository.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;
    
    public List<Usuario> findAll(){
        return repository.findAll();
    }
    
    public Usuario findById(Long id){
        return repository.findById(id).orElse(null);
    }
    
    public Usuario save(Usuario usuario){
        return repository.save(usuario);
    }

    public Usuario register(Usuario usuario){
        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("E-mail é obrigatório");
        }

        if (repository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado");
        }

        if (usuario.getSenha() == null || usuario.getSenha().isBlank()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }

        String tipo = usuario.getTipo() == null ? "" : usuario.getTipo().trim().toLowerCase();
        if (!tipo.equals("cliente") && !tipo.equals("prestador")) {
            throw new IllegalArgumentException("Tipo inválido: deve ser cliente ou prestador");
        }

        usuario.setTipo(tipo);
        usuario.setSenha(hashSha256(usuario.getSenha()));
        usuario.setAtivo(true);

        return repository.save(usuario);
    }

    public Usuario login(String email, String senha){
        if (email == null || senha == null) {
            return null;
        }

        return repository.findByEmail(email)
            .filter(u -> u.isAtivo())
            .filter(u -> u.getSenha().equals(hashSha256(senha)))
            .orElse(null);
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
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar hash", e);
        }
    }
    
    public void delete(Long id){
        repository.deleteById(id);
    }
}
