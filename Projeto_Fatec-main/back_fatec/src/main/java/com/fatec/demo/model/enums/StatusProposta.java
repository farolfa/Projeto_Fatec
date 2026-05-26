package com.fatec.demo.model.enums;

public enum StatusProposta {
    AGUARDANDO,
    ACEITA,
    RECUSADA;

    public static StatusProposta from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Status da proposta é obrigatório");
        }

        String normalized = value.trim().toUpperCase();
        return switch (normalized) {
            case "AGUARDANDO", "PENDING" -> AGUARDANDO;
            case "ACEITA", "ACCEPTED" -> ACEITA;
            case "RECUSADA", "REJECTED" -> RECUSADA;
            default -> throw new IllegalArgumentException("Status da proposta inválido");
        };
    }
}