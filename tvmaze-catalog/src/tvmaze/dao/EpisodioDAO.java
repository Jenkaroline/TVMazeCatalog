package src.tvmaze.dao;

import src.tvmaze.database.DatabaseConnection;
import src.tvmaze.model.Episodio;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class EpisodioDAO {

    public boolean inserir(Episodio episodio) throws SQLException {
        String sql = "INSERT INTO episodio (id_externo, serie_id, temporada, numero, nome, data_exibicao, duracao) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, episodio.getIdExterno());
            stmt.setInt(2, episodio.getSerieId());
            stmt.setInt(3, episodio.getTemporada());
            stmt.setInt(4, episodio.getNumero());
            stmt.setString(5, episodio.getNome());
            stmt.setObject(6, episodio.getDataExibicao());
            stmt.setInt(7, episodio.getDuracao());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    episodio.setId(rs.getInt(1));
                }
                return true;
            }
            return false;
        }
    }

    public List<Episodio> listarPorSerie(int serieId) throws SQLException {
        String sql = "SELECT * FROM episodio WHERE serie_id=? ORDER BY temporada, numero";
        List<Episodio> episodios = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, serieId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                episodios.add(extrairEpisodio(rs));
            }
        }

        return episodios;
    }

    public Episodio buscarPorIdExterno(int idExterno) throws SQLException {
        String sql = "SELECT * FROM episodio WHERE id_externo=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idExterno);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extrairEpisodio(rs);
            }
            return null;
        }
    }

    private Episodio extrairEpisodio(ResultSet rs) throws SQLException {
        Episodio episodio = new Episodio();
        episodio.setId(rs.getInt("id"));
        episodio.setIdExterno(rs.getInt("id_externo"));
        episodio.setSerieId(rs.getInt("serie_id"));
        episodio.setTemporada(rs.getInt("temporada"));
        episodio.setNumero(rs.getInt("numero"));
        episodio.setNome(rs.getString("nome"));
        episodio.setDuracao(rs.getInt("duracao"));

        Date dataExibicao = rs.getDate("data_exibicao");
        episodio.setDataExibicao(dataExibicao != null ? dataExibicao.toLocalDate() : null);

        return episodio;
    }
}