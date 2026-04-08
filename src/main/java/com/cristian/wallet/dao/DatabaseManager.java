package com.cristian.wallet.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private DatabaseManager() {}
    
    private static String URL = "jdbc:sqlite:wallet.db";
    private static Connection memoryConnection = null; // conexión persistente para tests

    // Permite cambiar la URL (solo para tests)
    public static void setUrl(String url) {
        URL = url;
    }

    // Resetea y crea una nueva conexión en memoria limpia
    public static void resetMemoryConnection() throws SQLException {
        if (memoryConnection != null && !memoryConnection.isClosed()) {
            memoryConnection.close();
        }
        memoryConnection = DriverManager.getConnection(URL);
    }

    public static Connection getConnection() throws SQLException {
        if (memoryConnection != null && !memoryConnection.isClosed()) {
            return memoryConnection;
        }
        Connection conn = DriverManager.getConnection(URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }
    
    public static void initializeDatabase() {
        System.out.println("Inicializando base de datos...");
        String createAccounts = """
            CREATE TABLE IF NOT EXISTS accounts (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                name        TEXT NOT NULL UNIQUE,
                description TEXT,
                currency    TEXT NOT NULL
            )""";

        String createTransactions = """
            CREATE TABLE IF NOT EXISTS transactions (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                account_id  INTEGER NOT NULL,
                type        TEXT NOT NULL,
                amount      REAL NOT NULL,
                date        TEXT NOT NULL,
                description TEXT,
                currency    TEXT NOT NULL,
                FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
            )""";

        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.execute(createAccounts);
            stmt.execute(createTransactions);
        } catch (SQLException e) {
             throw new RuntimeException("Error al inicializar la base de datos: " + e.getMessage());
        }
        System.out.println("Base de datos inicializada.");
    }
    
    // Solo para tests — limpia toda la base
    public static void clearDatabase() throws SQLException {
        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS transactions");
            stmt.execute("DROP TABLE IF EXISTS accounts");
        }
    }
}
