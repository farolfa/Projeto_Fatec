package com.fatec.demo.model.enums;

public enum TipoUsuario {
    CLIENTE("cliente"),
    PRESTADOR("prestador"),
    ADMIN("admin");

    private final String apiValue;

    TipoUsuario(String apiValue) {
        this.apiValue = apiValue;
    }

    public String getApiValue() {
        return apiValue;
    }

    public static TipoUsuario from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Tipo inválido: deve ser cliente, prestador ou admin");
        }

        String normalized = value.trim().toUpperCase();
        return switch (normalized) {
            case "CLIENTE" -> CLIENTE;
            case "PRESTADOR" -> PRESTADOR;
            case "ADMIN" -> ADMIN;
            default -> throw new IllegalArgumentException("Tipo inválido: deve ser cliente, prestador ou admin");
        };
    }
}