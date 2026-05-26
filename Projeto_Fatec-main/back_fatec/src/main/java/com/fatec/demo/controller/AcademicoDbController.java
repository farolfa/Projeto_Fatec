package com.fatec.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fatec.demo.service.AcademicoDbService;

@RestController
@RequestMapping("/academico-db")
public class AcademicoDbController {

    private final AcademicoDbService academicoDbService;

    public AcademicoDbController(AcademicoDbService academicoDbService) {
        this.academicoDbService = academicoDbService;
    }

    @PostMapping("/procedure/pedido-completo")
    public ResponseEntity<?> criarPedidoCompleto(@RequestBody Map<String, Object> payload) {
        try {
            return ResponseEntity.ok(academicoDbService.criarPedidoComProcedure(payload));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
        }
    }

    @GetMapping("/view/dashboard-pedidos")
    public ResponseEntity<List<Map<String, Object>>> dashboardPedidos() {
        return ResponseEntity.ok(academicoDbService.listarDashboardPedidosView());
    }

    @GetMapping("/function/prestadores-por-categoria")
    public ResponseEntity<List<Map<String, Object>>> prestadoresPorCategoria(
            @RequestParam(required = false) Long categoriaId) {
        return ResponseEntity.ok(academicoDbService.listarPrestadoresPorCategoria(categoriaId));
    }
}
