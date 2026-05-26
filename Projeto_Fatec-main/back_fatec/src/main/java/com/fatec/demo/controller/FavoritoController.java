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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.demo.model.Favorito;
import com.fatec.demo.service.FavoritoService;

@RestController
@RequestMapping("/favorito")
public class FavoritoController {

    @Autowired
    private FavoritoService favoritoService;

    @GetMapping
    public ResponseEntity<?> findByUsuario(@RequestParam Long usuarioId) {
        try {
            List<Favorito> lista = favoritoService.findByUsuarioId(usuarioId);
            return ResponseEntity.ok(lista);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao buscar favoritos: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Favorito favorito) {
        try {
            Favorito saved = favoritoService.save(favorito);
            return ResponseEntity.ok(saved);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao salvar favorito: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            favoritoService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao remover favorito: " + e.getMessage());
        }
    }
}
