package com.pouso.repository;

import com.pouso.model.PetSolicitacao;
import java.time.LocalDate;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PetRepository {

    private final JdbcTemplate jdbc;

    public PetRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<PetSolicitacao> listarPendentes() {
    String sql = """
            SELECT pt.nome, pt.cpf_dono, dono.nome AS dono_nome,
                   especie.nome AS especie_nome, raca.nome AS raca_nome,
                   pt.sexo, pt.porte, pt.bio, pt.is_castrado, pt.is_permanente,
                   pt.data_nasc, pt.data_cadastro, pt.foto_pet
            FROM pet pt
            INNER JOIN pessoa dono ON dono.cpf = pt.cpf_dono
            INNER JOIN tipo_pet raca ON raca.id = pt.tipo_pet
            LEFT JOIN tipo_pet especie ON especie.id = raca.tipo_mae
            WHERE pt.status_aprovacao = 'PENDENTE'
            ORDER BY pt.data_cadastro ASC
        """;

    return jdbc.query(sql, (rs, rowNum) -> new PetSolicitacao(
        rs.getString("nome"),
        rs.getString("cpf_dono"),
        rs.getString("dono_nome"),
        rs.getString("especie_nome"),
        rs.getString("raca_nome"),
        rs.getString("sexo"),
        rs.getString("porte"),
        rs.getString("bio"),
        (Boolean) rs.getObject("is_castrado"),
        (Boolean) rs.getObject("is_permanente"),
        rs.getObject("data_nasc", LocalDate.class),
        rs.getObject("data_cadastro", LocalDate.class),
        "PENDENTE",
        null,
        rs.getString("foto_pet")
    ));
}

public List<PetSolicitacao> listarHistorico() {
    String sql = """
            SELECT pt.nome, pt.cpf_dono, dono.nome AS dono_nome,
                   especie.nome AS especie_nome, raca.nome AS raca_nome,
                   pt.sexo, pt.porte, pt.bio, pt.is_castrado, pt.is_permanente,
                   pt.data_nasc, pt.data_cadastro, pt.foto_pet,
                   pt.status_aprovacao, adm.nome AS admin_nome
            FROM pet pt
            INNER JOIN pessoa dono ON dono.cpf = pt.cpf_dono
            INNER JOIN tipo_pet raca ON raca.id = pt.tipo_pet
            LEFT JOIN tipo_pet especie ON especie.id = raca.tipo_mae
            LEFT JOIN pessoa adm ON adm.cpf = pt.adm_aprovou
            WHERE pt.status_aprovacao IN ('APROVADO', 'REJEITADO')
            ORDER BY pt.data_cadastro DESC
        """;

    return jdbc.query(sql, (rs, rowNum) -> new PetSolicitacao(
        rs.getString("nome"),
        rs.getString("cpf_dono"),
        rs.getString("dono_nome"),
        rs.getString("especie_nome"),
        rs.getString("raca_nome"),
        rs.getString("sexo"),
        rs.getString("porte"),
        rs.getString("bio"),
        (Boolean) rs.getObject("is_castrado"),
        (Boolean) rs.getObject("is_permanente"),
        rs.getObject("data_nasc", LocalDate.class),
        rs.getObject("data_cadastro", LocalDate.class),
        rs.getString("status_aprovacao"),
        rs.getString("admin_nome"),
        rs.getString("foto_pet")
    ));
}

    public void aprovar(String nomePet, String cpfDono, String cpfAdmin) {
        String sql = """
                UPDATE pet
                SET status_aprovacao = 'APROVADO', adm_aprovou = ?
                WHERE nome = ? AND cpf_dono = ?
            """;
        jdbc.update(sql, cpfAdmin, nomePet, cpfDono);
    }

    public void rejeitar(String nomePet, String cpfDono, String cpfAdmin) {
        String sql = """
                UPDATE pet
                SET status_aprovacao = 'REJEITADO', adm_aprovou = ?
                WHERE nome = ? AND cpf_dono = ?
            """;
        jdbc.update(sql, cpfAdmin, nomePet, cpfDono);
    }
}