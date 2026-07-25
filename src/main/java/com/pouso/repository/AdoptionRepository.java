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
                WHERE (a.pet_dono = ? OR a.cpf_adotante = ?)
                  AND (a.status IN ('CONCLUIDA', 'CANCELADA', 'RECUSADA') OR a.data_fim IS NOT NULL)
                ORDER BY COALESCE(a.data_fim, a.data_inicio) DESC
            """, cpf, cpf);
    }

    public List<AdoptionSummary> listPendentesAsDonor(String cpf) {
        return list("""
                WHERE a.pet_dono = ?
                  AND a.status = 'PENDENTE'
                ORDER BY a.data_solicitacao DESC
            """, cpf);
    }

    public void solicitar(String cpfAdotante, String petNome, String petDono, boolean permanente, LocalDate dataFim) {
        String sql = """
                INSERT INTO adocao (
                    data_inicio, cpf_adotante, pet_nome, pet_dono,
                    data_solicitacao, status, is_permanente, data_fim
                ) VALUES (CURRENT_DATE, ?, ?, ?, CURRENT_DATE, 'PENDENTE', ?, ?)
            """;
        jdbc.update(sql, cpfAdotante, petNome, petDono, permanente, dataFim);
    }

    public void aceitar(LocalDate dataInicio, String cpfAdotante, String petNome, String petDono) {
        String sql = """
                UPDATE adocao SET status = 'EM_ANDAMENTO'
                WHERE data_inicio = ? AND cpf_adotante = ? AND pet_nome = ? AND pet_dono = ?
                  AND status = 'PENDENTE'
            """;
        jdbc.update(sql, dataInicio, cpfAdotante, petNome, petDono);
    }

    public void recusar(LocalDate dataInicio, String cpfAdotante, String petNome, String petDono) {
        String sql = """
                UPDATE adocao SET status = 'RECUSADA'
                WHERE data_inicio = ? AND cpf_adotante = ? AND pet_nome = ? AND pet_dono = ?
                  AND status = 'PENDENTE'
            """;
        jdbc.update(sql, dataInicio, cpfAdotante, petNome, petDono);
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
