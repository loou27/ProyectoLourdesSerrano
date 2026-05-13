package src.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionDB {
    private static final String URL = "jdbc:mysql://localhost:3308/cafeteria_ayala";
    private static final String USER = "root";
    private static final String PASS = "admin";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS); // Necesita el jar en referenced libraries
    }
}
