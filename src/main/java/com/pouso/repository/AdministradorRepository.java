package com.pouso.repository;

import com.pouso.model.Administrador;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AdministradorRepository {

    private final JdbcTemplate jdbc;

    public AdministradorRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Administrador buscarPorCpf(String cpf) {
        String sql = """
                SELECT a.cpf, p.nome, a.nivel
                FROM administrador a
                INNER JOIN pessoa p ON p.cpf = a.cpf
                WHERE a.cpf = ?
            """;

        List<Administrador> admins = jdbc.query(
            sql,
            (rs, rowNum) -> new Administrador(
                rs.getString("cpf"),
                rs.getString("nome"),
                rs.getString("nivel")
            ),
            cpf
        );

        return admins.isEmpty() ? null : admins.get(0);
    }

    public boolean isAdministrador(String cpf) {
        return cpf != null && buscarPorCpf(cpf) != null;
    }
}