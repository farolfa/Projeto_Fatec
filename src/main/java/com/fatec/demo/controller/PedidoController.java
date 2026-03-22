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

import com.fatec.demo.model.Pedido;
import com.fatec.demo.service.PedidoService;

@RestController
@RequestMapping(value = "/pedido")
public class PedidoController {

    @Autowired
    private PedidoService service;

    @GetMapping
    public ResponseEntity<List<Pedido>> findAll(){
        List<Pedido> pe = service.findAll();
        return ResponseEntity.ok().body(pe);
    }

    @GetMapping("/me")
    public ResponseEntity<List<Pedido>> findByUsuario(@RequestParam Long usuarioId){
        List<Pedido> pe = service.findByUsuarioId(usuarioId);
        return ResponseEntity.ok().body(pe);
    }

    @GetMapping("/abertos")
    public ResponseEntity<List<Pedido>> findOpenForPrestador(@RequestParam Long prestadorId){
        // Exibe apenas pedidos ABERTO de clientes diferentes do prestador
        List<Pedido> pe = service.findByUsuarioIdNotAndStatus(prestadorId, "ABERTO");
        return ResponseEntity.ok().body(pe);
    }

    @GetMapping("/status")
    public ResponseEntity<List<Pedido>> findByStatus(@RequestParam String status){
        List<Pedido> pe = service.findByStatus(status);
        return ResponseEntity.ok().body(pe);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> findById(@PathVariable Long id){
        Pedido pe = service.findById(id);
        return pe != null ? ResponseEntity.ok().body(pe) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Pedido> save(@RequestBody Pedido pedido){
        Pedido pe = service.save(pedido);
        return ResponseEntity.ok().body(pe);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
