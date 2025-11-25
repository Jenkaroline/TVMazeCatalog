package src.tvmaze.dao;

import src.tvmaze.database.DatabaseConnection;
import src.tvmaze.model.Genero;
import java.sql.*;
import java.util.*;

public class GeneroDAO {

    public Genero inserir(Genero genero) throws SQLException {
        // Verifica se já existe
        Genero existente = buscarPorNome(genero.getNome());
        if (existente != null) {
            return existente;
        }

        String sql = "INSERT INTO genero (nome) VALUES (?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, genero.getNome());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                genero.setId(rs.getInt(1));
            }

            return genero;
        }
    }

    public Genero buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM genero WHERE nome=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nome);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Genero genero = new Genero();
                genero.setId(rs.getInt("id"));
                genero.setNome(rs.getString("nome"));
                return genero;
            }
            return null;
        }
    }

    public List<Genero> listarTodos() throws SQLException {
        String sql = "SELECT * FROM genero ORDER BY nome";
        List<Genero> generos = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Genero genero = new Genero();
                genero.setId(rs.getInt("id"));
                genero.setNome(rs.getString("nome"));
                generos.add(genero);
            }
        }

        return generos;
    }
}
