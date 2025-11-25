package src.tvmaze.dao;
import src.tvmaze.database.DatabaseConnection;
import src.tvmaze.model.Serie;
import src.tvmaze.model.Genero;
import java.sql.*;
import java.sql.Date;
import java.util.*;


public class SerieDAO {

    public boolean inserir(Serie serie) throws SQLException {
        String sql = "INSERT INTO serie (id_externo, nome, linguagem, status, sinopse, nota_media, data_estreia, data_termino) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, serie.getIdExterno());
            stmt.setString(2, serie.getNome());
            stmt.setString(3, serie.getLinguagem());
            stmt.setString(4, serie.getStatus());
            stmt.setString(5, serie.getSinopse());
            stmt.setObject(6, serie.getNotaMedia());
            stmt.setObject(7, serie.getDataEstreia());
            stmt.setObject(8, serie.getDataTermino());

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    serie.setId(rs.getInt(1));
                }

                // Inserir gêneros
                for (Genero genero : serie.getGeneros()) {
                    vincularGenero(serie.getId(), genero.getId());
                }

                registrarLog("INSERT", "Serie", "Série importada: " + serie.getNome());
                return true;
            }
            return false;
        }
    }

    public boolean atualizar(Serie serie) throws SQLException {
        String sql = "UPDATE serie SET nome=?, linguagem=?, status=?, sinopse=?, nota_media=?, observacao=?, status_local=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, serie.getNome());
            stmt.setString(2, serie.getLinguagem());
            stmt.setString(3, serie.getStatus());
            stmt.setString(4, serie.getSinopse());
            stmt.setObject(5, serie.getNotaMedia());
            stmt.setString(6, serie.getObservacao());
            stmt.setString(7, serie.getStatusLocal());
            stmt.setInt(8, serie.getId());

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                registrarLog("UPDATE", "Serie", "Série atualizada: " + serie.getNome());
                return true;
            }
            return false;
        }
    }

    public boolean excluir(int id) throws SQLException {
        String sql = "DELETE FROM serie WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                registrarLog("DELETE", "Serie", "Série excluída ID: " + id);
                return true;
            }
            return false;
        }
    }

    public Serie buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM serie WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extrairSerie(rs);
            }
            return null;
        }
    }

    public Serie buscarPorIdExterno(int idExterno) throws SQLException {
        String sql = "SELECT * FROM serie WHERE id_externo=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idExterno);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extrairSerie(rs);
            }
            return null;
        }
    }

    public List<Serie> listarTodas() throws SQLException {
        String sql = "SELECT * FROM serie";
        List<Serie> series = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                series.add(extrairSerie(rs));
            }
        }

        return series;
    }

    public List<Serie> buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM serie WHERE nome LIKE ?";
        List<Serie> series = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nome + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                series.add(extrairSerie(rs));
            }
        }

        return series;
    }

    private Serie extrairSerie(ResultSet rs) throws SQLException {
        Serie serie = new Serie();
        serie.setId(rs.getInt("id"));
        serie.setIdExterno(rs.getInt("id_externo"));
        serie.setNome(rs.getString("nome"));
        serie.setLinguagem(rs.getString("linguagem"));
        serie.setStatus(rs.getString("status"));
        serie.setSinopse(rs.getString("sinopse"));

        Double nota = rs.getDouble("nota_media");
        serie.setNotaMedia(rs.wasNull() ? null : nota);

        Date dataEstreia = rs.getDate("data_estreia");
        serie.setDataEstreia(dataEstreia != null ? dataEstreia.toLocalDate() : null);

        Date dataTermino = rs.getDate("data_termino");
        serie.setDataTermino(dataTermino != null ? dataTermino.toLocalDate() : null);

        serie.setObservacao(rs.getString("observacao"));
        serie.setStatusLocal(rs.getString("status_local"));

        // Carregar gêneros
        serie.setGeneros(buscarGenerosDaSerie(serie.getId()));

        return serie;
    }

    private Set<Genero> buscarGenerosDaSerie(int serieId) throws SQLException {
        String sql = "SELECT g.* FROM genero g INNER JOIN serie_genero sg ON g.id = sg.genero_id WHERE sg.serie_id = ?";
        Set<Genero> generos = new HashSet<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, serieId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Genero genero = new Genero();
                genero.setId(rs.getInt("id"));
                genero.setNome(rs.getString("nome"));
                generos.add(genero);
            }
        }

        return generos;
    }

    private void vincularGenero(int serieId, int generoId) throws SQLException {
        String sql = "INSERT IGNORE INTO serie_genero (serie_id, genero_id) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, serieId);
            stmt.setInt(2, generoId);
            stmt.executeUpdate();
        }
    }

    private void registrarLog(String operacao, String entidade, String descricao) {
        String sql = "INSERT INTO log_operacao (operacao, entidade, descricao) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, operacao);
            stmt.setString(2, entidade);
            stmt.setString(3, descricao);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao registrar log: " + e.getMessage());
        }
    }
}
