package com.fatec.demo.service;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Service;

@Service
public class AcademicoDbService {

    private final JdbcTemplate jdbcTemplate;

    public AcademicoDbService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> criarPedidoComProcedure(Map<String, Object> payload) {
        Long idUsuario = getLong(payload, "idUsuario", true);
        String titulo = getString(payload, "titulo", true);
        String descricao = getString(payload, "descricao", true);
        String localizacao = getString(payload, "localizacao", true);
        String rua = getString(payload, "rua", true);
        String cidade = getString(payload, "cidade", true);
        String estado = getString(payload, "estado", true);
        String cep = getString(payload, "cep", true);

        String contatoNome = getString(payload, "contatoNome", false);
        String contatoEmail = getString(payload, "contatoEmail", false);
        String contatoTelefone = getString(payload, "contatoTelefone", false);
        Long idServico = getLong(payload, "idServico", false);
        String numero = getString(payload, "numero", false);
        String bairro = getString(payload, "bairro", false);

        String callSql = "{call dbo.sp_criar_pedido_com_endereco(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        List<SqlParameter> parametros = new ArrayList<>();

        parametros.add(new SqlParameter(Types.BIGINT));
        parametros.add(new SqlParameter(Types.VARCHAR));
        parametros.add(new SqlParameter(Types.VARCHAR));
        parametros.add(new SqlParameter(Types.VARCHAR));
        parametros.add(new SqlParameter(Types.VARCHAR));
        parametros.add(new SqlParameter(Types.VARCHAR));
        parametros.add(new SqlParameter(Types.VARCHAR));
        parametros.add(new SqlParameter(Types.BIGINT));
        parametros.add(new SqlParameter(Types.VARCHAR));
        parametros.add(new SqlParameter(Types.VARCHAR));
        parametros.add(new SqlParameter(Types.VARCHAR));
        parametros.add(new SqlParameter(Types.VARCHAR));
        parametros.add(new SqlParameter(Types.VARCHAR));
        parametros.add(new SqlParameter(Types.VARCHAR));
        parametros.add(new SqlOutParameter("id_pedido_gerado", Types.BIGINT));
        parametros.add(new SqlOutParameter("id_endereco_gerado", Types.BIGINT));

        Map<String, Object> out = jdbcTemplate.call(conn -> {
            var cs = conn.prepareCall(callSql);
            cs.setLong(1, idUsuario);
            cs.setString(2, titulo);
            cs.setString(3, descricao);
            cs.setString(4, localizacao);
            cs.setString(5, contatoNome);
            cs.setString(6, contatoEmail);
            cs.setString(7, contatoTelefone);
            if (idServico == null) {
                cs.setNull(8, Types.BIGINT);
            } else {
                cs.setLong(8, idServico);
            }
            cs.setString(9, rua);
            cs.setString(10, numero);
            cs.setString(11, cidade);
            cs.setString(12, bairro);
            cs.setString(13, estado);
            cs.setString(14, cep);
            cs.registerOutParameter(15, Types.BIGINT);
            cs.registerOutParameter(16, Types.BIGINT);
            return cs;
        }, parametros);

        return Map.of(
                "pedidoId", out.get("id_pedido_gerado"),
                "enderecoId", out.get("id_endereco_gerado")
        );
    }

    public List<Map<String, Object>> listarDashboardPedidosView() {
        return jdbcTemplate.queryForList("SELECT * FROM dbo.vw_dashboard_pedidos ORDER BY data_criacao DESC");
    }

    public List<Map<String, Object>> listarPrestadoresPorCategoria(Long categoriaId) {
        return jdbcTemplate.queryForList(
                "SELECT * FROM dbo.fn_prestadores_por_categoria(?) ORDER BY media_avaliacao DESC, total_servicos_oferecidos DESC",
                categoriaId
        );
    }

    private Long getLong(Map<String, Object> payload, String key, boolean required) {
        Object raw = payload.get(key);
        if (raw == null) {
            if (required) {
                throw new IllegalArgumentException("Campo obrigatório ausente: " + key);
            }
            return null;
        }
        if (raw instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(raw.toString().trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Campo inválido (esperado número): " + key);
        }
    }

    private String getString(Map<String, Object> payload, String key, boolean required) {
        Object raw = payload.get(key);
        if (raw == null) {
            if (required) {
                throw new IllegalArgumentException("Campo obrigatório ausente: " + key);
            }
            return null;
        }
        String value = raw.toString().trim();
        if (required && value.isEmpty()) {
            throw new IllegalArgumentException("Campo obrigatório vazio: " + key);
        }
        return value.isEmpty() ? null : value;
    }
}
