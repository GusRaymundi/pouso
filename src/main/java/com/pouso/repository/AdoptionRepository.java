package com.pouso.repository;

import com.pouso.model.AdoptionSummary;
import java.time.LocalDate;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AdoptionRepository {

    private final JdbcTemplate jdbc;

    public AdoptionRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<AdoptionSummary> listActiveAsAdopter(String cpf) {
        return list("""
                WHERE a.cpf_adotante = ?
                  AND a.status = 'EM_ANDAMENTO'
                ORDER BY a.data_inicio DESC
            """, cpf);
    }

    public List<AdoptionSummary> listActiveAsDonor(String cpf) {
        return list("""
                WHERE a.pet_dono = ?
                  AND a.status = 'EM_ANDAMENTO'
                ORDER BY a.data_inicio DESC
            """, cpf);
    }

    public List<AdoptionSummary> listHistory(String cpf) {
        return list("""
                WHERE a.pet_dono = ?
                  AND (a.status IN ('CONCLUIDA', 'CANCELADA') OR a.data_fim IS NOT NULL)
                ORDER BY COALESCE(a.data_fim, a.data_inicio) DESC
            """, cpf);
    }

    private List<AdoptionSummary> list(String where, Object... params) {
        String sql = """
                SELECT a.data_inicio, a.cpf_adotante, adotante.nome AS adotante_nome,
                       a.pet_nome, a.pet_dono, dono.nome AS dono_nome,
                       a.data_fim, a.data_solicitacao, a.status::text AS status, a.is_permanente,
                       especie.nome AS especie_nome, raca.nome AS raca_nome,
                       p.sexo::text AS sexo, p.porte::text AS porte, p.data_nasc, p.foto_pet,
                       d.adocao_inicio IS NOT NULL AS tem_devolucao
                FROM adocao a
                INNER JOIN pet p ON p.nome = a.pet_nome AND p.cpf_dono = a.pet_dono
                INNER JOIN pessoa adotante ON adotante.cpf = a.cpf_adotante
                INNER JOIN pessoa dono ON dono.cpf = a.pet_dono
                INNER JOIN tipo_pet raca ON raca.id = p.tipo_pet
                LEFT JOIN tipo_pet especie ON especie.id = raca.tipo_mae
                LEFT JOIN devolucao d ON d.adocao_inicio = a.data_inicio
                    AND d.adocao_adotante = a.cpf_adotante
                    AND d.adocao_pet = a.pet_nome
                    AND d.adocao_dono = a.pet_dono
            """ + where;

        return jdbc.query(sql, (rs, rowNum) -> new AdoptionSummary(
            rs.getObject("data_inicio", LocalDate.class),
            rs.getString("cpf_adotante"),
            rs.getString("adotante_nome"),
            rs.getString("pet_nome"),
            rs.getString("pet_dono"),
            rs.getString("dono_nome"),
            rs.getObject("data_fim", LocalDate.class),
            rs.getObject("data_solicitacao", LocalDate.class),
            rs.getString("status"),
            (Boolean) rs.getObject("is_permanente"),
            rs.getString("especie_nome"),
            rs.getString("raca_nome"),
            rs.getString("sexo"),
            rs.getString("porte"),
            rs.getObject("data_nasc", LocalDate.class),
            rs.getString("foto_pet"),
            rs.getBoolean("tem_devolucao")
        ), params);
    }
}
