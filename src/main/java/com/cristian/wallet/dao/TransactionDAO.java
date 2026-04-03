package com.cristian.wallet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.cristian.wallet.model.Account;
import com.cristian.wallet.model.Currency;
import com.cristian.wallet.model.TipoTransaccion;
import com.cristian.wallet.model.Transaction;

public class TransactionDAO implements ITransactionDAO {
    
    private final IAccountDAO accountDAO;

    private void addTransaction(Transaction transaction, Connection conn) throws SQLException {
        String sql = "INSERT INTO transactions (account_id, type, amount, date, description, currency) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, transaction.getAccount().getAccountID());
            stmt.setString(2, transaction.getTransactionType().name());
            stmt.setDouble(3, transaction.getAmount());
            stmt.setString(4, transaction.getDate().toString());
            stmt.setString(5, transaction.getDescription());
            stmt.setString(6, transaction.getCurrency().name());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                transaction.setTransactionID(rs.getInt(1));
            }
        }
    }

    public TransactionDAO(IAccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    @Override
    public void addTransaction(Transaction transaction) {
        try (Connection conn = DatabaseManager.getConnection()) {
            addTransaction(transaction, conn);
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }

    @Override
    public void transferFunds(Transaction egreso, Transaction ingreso) {
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                addTransaction(egreso, conn);
                addTransaction(ingreso, conn);
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Error en la transferencia: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }

    @Override
    public List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Account account = accountDAO.getAccountById(rs.getInt("account_id"));
                Transaction transaction = new Transaction(
                    TipoTransaccion.valueOf(rs.getString("type")),
                    rs.getDouble("amount"),
                    LocalDate.parse(rs.getString("date")),
                    rs.getString("description"),
                    Currency.valueOf(rs.getString("currency")),
                    account
                );
                transaction.setTransactionID(rs.getInt("id"));
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener transacciones: " + e.getMessage());
        }
        return transactions;
    }

    @Override
    public List<Transaction> getTransactionsByAccount(int accountID) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountID);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Account account = accountDAO.getAccountById(rs.getInt("account_id"));
                    Transaction transaction = new Transaction(
                        TipoTransaccion.valueOf(rs.getString("type")),
                        rs.getDouble("amount"),
                        LocalDate.parse(rs.getString("date")),
                        rs.getString("description"),
                        Currency.valueOf(rs.getString("currency")),
                        account
                    );
                    transaction.setTransactionID(rs.getInt("id"));
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener transacciones por cuenta y fecha: " + e.getMessage());
        }
        return transactions;
    }

    @Override
    public Transaction getTransactionById(int transactionID) {
        String sql = "SELECT * FROM transactions WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transactionID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Account account = accountDAO.getAccountById(rs.getInt("account_id"));
                    Transaction transaction = new Transaction(
                        TipoTransaccion.valueOf(rs.getString("type")),
                        rs.getDouble("amount"),
                        LocalDate.parse(rs.getString("date")),
                        rs.getString("description"),
                        Currency.valueOf(rs.getString("currency")),
                        account
                    );
                    transaction.setTransactionID(rs.getInt("id"));
                    return transaction;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener transacción por ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Transaction getLastTransactionByAccount(int accountID) {
        String sql = "SELECT * FROM transactions WHERE account_id = ? ORDER BY date DESC LIMIT 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Account account = accountDAO.getAccountById(rs.getInt("account_id"));
                    Transaction transaction = new Transaction(
                        TipoTransaccion.valueOf(rs.getString("type")),
                        rs.getDouble("amount"),
                        LocalDate.parse(rs.getString("date")),
                        rs.getString("description"),
                        Currency.valueOf(rs.getString("currency")),
                        account
                    );
                    transaction.setTransactionID(rs.getInt("id"));
                    return transaction;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener última transacción por cuenta: " + e.getMessage());
        }
        return null;
    }


    @Override
    public void deleteTransaction(int transactionID) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, transactionID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al eliminar transacción: " + e.getMessage());
        }
    }

    @Override
    public void updateTransaction(Transaction updatedTransaction) {
        String sql = "UPDATE transactions SET account_id = ?, type = ?, amount = ?, date = ?, description = ?, currency = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, updatedTransaction.getAccount().getAccountID());
            stmt.setString(2, updatedTransaction.getTransactionType().name());
            stmt.setDouble(3, updatedTransaction.getAmount());
            stmt.setString(4, updatedTransaction.getDate().toString());
            stmt.setString(5, updatedTransaction.getDescription());
            stmt.setString(6, updatedTransaction.getCurrency().name());
            stmt.setInt(7, updatedTransaction.getTransactionID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar transacción: " + e.getMessage());
        }
    }
    
}
