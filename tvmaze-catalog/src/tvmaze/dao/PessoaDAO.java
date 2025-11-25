package src.tvmaze.dao;

import src.tvmaze.database.DatabaseConnection;
import src.tvmaze.model.Pessoa;
import src.tvmaze.model.Participacao;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class PessoaDAO {

    public Pessoa inserir(Pessoa pessoa) throws SQLException {
        // Verifica se já existe
        Pessoa existente = buscarPorIdExterno(pessoa.getIdExterno());
        if (existente != null) {
            return existente;
        }

        String sql = "INSERT INTO pessoa (id_externo, nome, pais, data_nascimento) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, pessoa.getIdExterno());
            stmt.setString(2, pessoa.getNome());
            stmt.setString(3, pessoa.getPais());
            stmt.setObject(4, pessoa.getDataNascimento());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                pessoa.setId(rs.getInt(1));
            }

            return pessoa;
        }
    }

    public Pessoa buscarPorIdExterno(int idExterno) throws SQLException {
        String sql = "SELECT * FROM pessoa WHERE id_externo=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idExterno);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extrairPessoa(rs);
            }
            return null;
        }
    }

    public boolean inserirParticipacao(Participacao participacao) throws SQLException {
        String sql = "INSERT IGNORE INTO participacao (serie_id, pessoa_id, personagem) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, participacao.getSerieId());
            stmt.setInt(2, participacao.getPessoaId());
            stmt.setString(3, participacao.getPersonagem());

            return stmt.executeUpdate() > 0;
        }
    }

    public List<Participacao> listarElenco(int serieId) throws SQLException {
        String sql = "SELECT p.*, pe.* FROM participacao p INNER JOIN pessoa pe ON p.pessoa_id = pe.id WHERE p.serie_id=?";
        List<Participacao> elenco = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, serieId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Participacao participacao = new Participacao();
                participacao.setId(rs.getInt("p.id"));
                participacao.setSerieId(rs.getInt("p.serie_id"));
                participacao.setPessoaId(rs.getInt("p.pessoa_id"));
                participacao.setPersonagem(rs.getString("p.personagem"));

                Pessoa pessoa = new Pessoa();
                pessoa.setId(rs.getInt("pe.id"));
                pessoa.setIdExterno(rs.getInt("pe.id_externo"));
                pessoa.setNome(rs.getString("pe.nome"));
                pessoa.setPais(rs.getString("pe.pais"));

                Date dataNasc = rs.getDate("pe.data_nascimento");
                pessoa.setDataNascimento(dataNasc != null ? dataNasc.toLocalDate() : null);

                participacao.setPessoa(pessoa);
                elenco.add(participacao);
            }
        }

        return elenco;
    }

    private Pessoa extrairPessoa(ResultSet rs) throws SQLException {
        Pessoa pessoa = new Pessoa();
        pessoa.setId(rs.getInt("id"));
        pessoa.setIdExterno(rs.getInt("id_externo"));
        pessoa.setNome(rs.getString("nome"));
        pessoa.setPais(rs.getString("pais"));

        Date dataNasc = rs.getDate("data_nascimento");
        pessoa.setDataNascimento(dataNasc != null ? dataNasc.toLocalDate() : null);

        return pessoa;
    }
}
