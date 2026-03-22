package com.fatec.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.demo.model.Usuario;
import com.fatec.demo.service.UsuarioService;

@RestController
@RequestMapping(value = "/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping
    public ResponseEntity<List<Usuario>> findAll(){
        List<Usuario> u = service.findAll();
        return ResponseEntity.ok().body(u);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> findById(@PathVariable Long id){
        Usuario u = service.findById(id);
        return u != null ? ResponseEntity.ok().body(u) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Usuario> save(@RequestBody Usuario usuario){
        Usuario u = service.save(usuario);
        return ResponseEntity.ok().body(u);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Usuario usuario){
        try {
            Usuario u = service.register(usuario);
            u.setSenha(null);
            return ResponseEntity.status(201).body(u);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Usuario usuario){
        if (usuario.getEmail() == null || usuario.getEmail().isBlank() ||
            usuario.getSenha() == null || usuario.getSenha().isBlank()) {
            return ResponseEntity.badRequest().body("E-mail e senha são obrigatórios");
        }

        Usuario u = service.login(usuario.getEmail(), usuario.getSenha());
        if (u == null) {
            return ResponseEntity.status(401).body("E-mail ou senha inválidos");
        }

        u.setSenha(null);
        return ResponseEntity.ok(u);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
