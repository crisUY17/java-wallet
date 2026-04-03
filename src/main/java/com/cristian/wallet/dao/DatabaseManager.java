package com.cristian.wallet.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private DatabaseManager() {}
    
    private static final String URL = "jdbc:sqlite:wallet.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        System.out.println("Inicializando base de datos...");
        String createAccounts = """
            CREATE TABLE IF NOT EXISTS accounts (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                name        TEXT NOT NULL,
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
            System.out.println("Error al inicializar la base de datos: " + e.getMessage());
        }
        System.out.println("Base de datos inicializada.");
    }
}
