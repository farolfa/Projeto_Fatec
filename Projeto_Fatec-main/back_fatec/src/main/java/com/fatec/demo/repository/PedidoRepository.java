package com.fatec.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.demo.model.Pedido;
import com.fatec.demo.model.enums.StatusPedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuarioId(Long usuarioId);
    List<Pedido> findByStatus(StatusPedido status);
    List<Pedido> findByUsuarioIdNotAndStatus(Long usuarioId, StatusPedido status);
}
