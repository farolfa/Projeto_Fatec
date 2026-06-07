package com.fatec.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public CommandLineRunner cleanupUsuarioAtivoColumn(JdbcTemplate jdbcTemplate) {
		return args -> {
			try {
				Integer count = jdbcTemplate.queryForObject(
					"SELECT COUNT(*) FROM sys.columns WHERE object_id = OBJECT_ID(N'dbo.usuarios') AND name = 'ativo'",
					Integer.class
				);
				if (count != null && count > 0) {
					jdbcTemplate.execute("ALTER TABLE dbo.usuarios DROP COLUMN ativo");
				}
			} catch (Exception ignored) {
				// Se a tabela ainda não existir ou se algo falhar, ignora para não impedir o app.
			}
		};
	}

}
