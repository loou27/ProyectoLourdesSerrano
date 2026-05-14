package src.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static final String HOST = System.getenv().getOrDefault("DB_HOST", "localhost");
    private static final String PORT = System.getenv().getOrDefault("DB_PORT", "3308");
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/cafeteria_ayala";
    private static final String USER = "root";
    private static final String PASS = "admin";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS); // Necesita el jar en referenced libraries
    }
}
