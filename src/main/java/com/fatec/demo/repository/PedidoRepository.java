package com.fatec.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fatec.demo.model.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

}
