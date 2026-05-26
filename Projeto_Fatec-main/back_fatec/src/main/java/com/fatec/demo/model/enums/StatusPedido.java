package com.fatec.demo.model.enums;

public enum StatusPedido {
    ABERTO,
    EM_ANDAMENTO,
    CONCLUIDO,
    CANCELADO;

    public static StatusPedido from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Status do pedido é obrigatório");
        }

        String normalized = value.trim().toUpperCase();
        return switch (normalized) {
            case "ABERTO", "PENDING", "ACTIVE" -> ABERTO;
            case "EM_ANDAMENTO", "COMPLETED", "WORK_COMPLETED" -> EM_ANDAMENTO;
            case "CONCLUIDO", "WORK_COMPLETED_CONFIRMED" -> CONCLUIDO;
            case "CANCELADO", "CANCELLED" -> CANCELADO;
            default -> throw new IllegalArgumentException("Status do pedido inválido");
        };
    }
}