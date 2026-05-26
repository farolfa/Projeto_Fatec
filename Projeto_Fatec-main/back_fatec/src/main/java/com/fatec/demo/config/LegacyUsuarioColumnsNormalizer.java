package com.fatec.demo.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class LegacyUsuarioColumnsNormalizer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(LegacyUsuarioColumnsNormalizer.class);

    private static final Map<String, String> LEGACY_COLUMNS = new LinkedHashMap<>();

    static {
        LEGACY_COLUMNS.put("telefone", "NVARCHAR(20)");
        LEGACY_COLUMNS.put("endereco", "NVARCHAR(200)");
        LEGACY_COLUMNS.put("cidade", "NVARCHAR(120)");
        LEGACY_COLUMNS.put("estado", "NVARCHAR(2)");
        LEGACY_COLUMNS.put("cep", "NVARCHAR(10)");
    }

    private final JdbcTemplate jdbcTemplate;

    public LegacyUsuarioColumnsNormalizer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        for (Map.Entry<String, String> entry : LEGACY_COLUMNS.entrySet()) {
            ensureColumnIsNullable(entry.getKey(), entry.getValue());
        }
    }

    private void ensureColumnIsNullable(String columnName, String sqlType) {
        String nullableFlag = jdbcTemplate.query(
            "SELECT IS_NULLABLE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'usuarios' AND COLUMN_NAME = ?",
            ps -> ps.setString(1, columnName),
            rs -> rs.next() ? rs.getString(1) : null
        );

        if (nullableFlag == null) {
            return;
        }

        if ("YES".equalsIgnoreCase(nullableFlag)) {
            return;
        }

        String alterSql = "ALTER TABLE usuarios ALTER COLUMN " + columnName + " " + sqlType + " NULL";
        jdbcTemplate.execute(alterSql);
        log.warn("Coluna legada usuarios.{} alterada para NULL para compatibilidade com modelo normalizado.", columnName);
    }
}
