package src.tvmaze.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/tvmaze_catalog";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL não encontrado", e);
        }
    }

    public static void testConnection() {
        try (Connection conn = getConnection()) {
            System.out.println("✓ Conexão com banco de dados estabelecida com sucesso!");
        } catch (SQLException e) {
            System.err.println("✗ Erro ao conectar ao banco de dados: " + e.getMessage());
            System.err.println("Certifique-se de que o MySQL está rodando e o schema foi criado.");
        }
    }
}
