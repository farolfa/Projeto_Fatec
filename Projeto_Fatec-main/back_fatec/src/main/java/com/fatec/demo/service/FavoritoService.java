package com.fatec.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fatec.demo.model.Favorito;
import com.fatec.demo.repository.FavoritoRepository;

@Service
public class FavoritoService {

    @Autowired
    private FavoritoRepository favoritoRepository;

    public List<Favorito> findByUsuarioId(Long usuarioId) {
        return favoritoRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public Favorito save(Favorito favorito) {
        if (favorito.getUsuario() == null || favorito.getUsuario().getId() == null) {
            throw new IllegalArgumentException("Usuário é obrigatório.");
        }
        if (favorito.getPrestadorId() == null) {
            throw new IllegalArgumentException("PrestadorId é obrigatório.");
        }
        if (favoritoRepository.existsByUsuarioIdAndPrestadorId(
                favorito.getUsuario().getId(), favorito.getPrestadorId())) {
            throw new IllegalStateException("Prestador já está nos favoritos.");
        }
        return favoritoRepository.save(favorito);
    }

    @Transactional
    public void delete(Long id) {
        if (!favoritoRepository.existsById(id)) {
            throw new IllegalArgumentException("Favorito não encontrado.");
        }
        favoritoRepository.deleteById(id);
    }
}
