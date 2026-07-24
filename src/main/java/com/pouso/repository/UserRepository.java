package com.pouso.repository;
import com.pouso.model.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbc;

    public UserRepository(JdbcTemplate jdbc) { //
        this.jdbc = jdbc;
    }

    private static final RowMapper<User> USER_MAPPER = (rs, rowNum) -> new User(
        rs.getString("cpf"),
        rs.getString("nome"),
        rs.getString("email"),
        rs.getString("senha"),
        rs.getString("username"),
        rs.getString("bio"),
        rs.getString("genero"),
        rs.getString("telefone"),
        rs.getString("foto_perfil")
    );

    public User buscarPorCpf(String cpf) {
        String sql = """
                SELECT p.cpf, p.nome, p.email, p.senha, 
                       u.username, u.bio, u.genero, u.telefone, u.foto_perfil
                FROM pessoa p
                INNER JOIN usuario u ON p.cpf = u.cpf
                WHERE p.cpf = ?
            """;

        List<User> users = jdbc.query(
            sql,
            USER_MAPPER,
            cpf
        );

        if (users.isEmpty()) {
            return null;
        }

        return users.get(0); //tirar lista
    }

    public User buscarPorUsername(String username) {
        String sql = """
                SELECT p.cpf, p.nome, p.email, p.senha,
                       u.username, u.bio, u.genero, u.telefone, u.foto_perfil
                FROM pessoa p
                INNER JOIN usuario u ON p.cpf = u.cpf
                WHERE u.username = ?
            """;

        List<User> users = jdbc.query(sql, USER_MAPPER, username);

        if (users.isEmpty()) {
            return null;
        }

        return users.get(0);
    }

    public void atualizar(User user) {
   
        String sqlPessoa = """
                UPDATE pessoa 
                SET nome = ?, email = ? 
                WHERE cpf = ?
            """;
        
        jdbc.update(sqlPessoa, user.getNome(), user.getEmail(), user.getCpf());

        // o cast '?::genero_enum' garante que o banco reconheça a String como o enum do banco
        String sqlUsuario = """
                UPDATE usuario 
                SET username = ?, bio = ?, genero = ?::genero_enum, telefone = ?, foto_perfil = ? 
                WHERE cpf = ?
            """;

        jdbc.update(
            sqlUsuario, 
            user.getUsername(), 
            user.getBio(), 
            user.getGenero(), 
            user.getTelefone(), 
            user.getFotoPerfil(), 
            user.getCpf()
        );
    }

    public String mediaAvaliacoesRecebidas(String cpf) {
        String sql = """
                SELECT ROUND(AVG(nota)::numeric, 1)
                FROM avaliacao
                WHERE adocao_dono = ?
            """;

        BigDecimal media = jdbc.queryForObject(sql, BigDecimal.class, cpf);
        return media == null ? "--" : media.toPlainString();
    }

    public Integer contarAvaliacoesRecebidas(String cpf) {
        String sql = """
                SELECT COUNT(*)
                FROM avaliacao
                WHERE adocao_dono = ?
            """;

        return jdbc.queryForObject(sql, Integer.class, cpf);
    }

    public Integer contarAdocoesDosPets(String cpf) {
        String sql = """
                SELECT COUNT(*)
                FROM adocao
                WHERE pet_dono = ?
            """;

        return jdbc.queryForObject(sql, Integer.class, cpf);
    }

    public String buscarLocalizacao(String cpf) {
        String sql = """
                SELECT CONCAT_WS('/', NULLIF(cidade, ''), NULLIF(uf, ''))
                FROM endereco
                WHERE usuario_cpf = ?
            """;

        List<String> locations = jdbc.queryForList(sql, String.class, cpf);
        if (locations.isEmpty() || locations.get(0) == null || locations.get(0).isBlank()) {
            return "Localizacao nao informada";
        }

        return locations.get(0);
    }

    public List<Map<String, Object>> listarAvaliacoesRecebidas(String cpf) {
        String sql = """
                SELECT a.nota, a.comentario, a.data, adotante.nome AS autor_nome, a.adocao_pet AS pet_nome
                FROM avaliacao a
                INNER JOIN pessoa adotante ON adotante.cpf = a.adocao_adotante
                WHERE a.adocao_dono = ?
                ORDER BY a.data DESC
            """;

        return jdbc.query(sql, (rs, rowNum) -> {
            Map<String, Object> review = new HashMap<>();
            review.put("nota", rs.getInt("nota"));
            review.put("comentario", rs.getString("comentario"));
            review.put("data", rs.getObject("data", LocalDate.class));
            review.put("autor_nome", rs.getString("autor_nome"));
            review.put("pet_nome", rs.getString("pet_nome"));
            return review;
        }, cpf);
    }
}
