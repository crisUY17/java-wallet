package com.cristian.wallet.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.cristian.wallet.model.Account;
import com.cristian.wallet.model.Currency;

public class AccountDAO implements IAccountDAO {
    @Override
    public void addAccount(Account account) {
        String sql = "INSERT INTO accounts (name, description, currency) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
                stmt.setString(1, account.getAccountName());
                stmt.setString(2, account.getDescription());
                stmt.setString(3, account.getCurrency().name());
                stmt.executeUpdate();
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    account.setAccountID(rs.getInt(1));
                }
            } catch (SQLException e) {
                System.out.println("Error al agregar cuenta: " + e.getMessage());
            }
    }

    @Override
    public List<Account> getAccounts() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Account account = new Account(
                    rs.getString("name"),
                    rs.getString("description"),
                    Currency.valueOf(rs.getString("currency"))
                );
                account.setAccountID(rs.getInt("id"));
                accounts.add(account);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener cuentas: " + e.getMessage());
        }
        return accounts;
    }

    @Override
    public Account getAccountById(int accountID) {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, accountID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Account account = new Account(
                        rs.getString("name"),
                        rs.getString("description"),
                        Currency.valueOf(rs.getString("currency"))
                    );
                    account.setAccountID(rs.getInt("id"));
                    return account;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener cuenta por ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteAccount(int accountID) {
        String sql = "DELETE FROM accounts WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, accountID);
                stmt.executeUpdate();   
            }   catch (SQLException e) {
                System.out.println("Error al eliminar cuenta: " + e.getMessage());
            }
    }

    @Override
    public void updateAccount(Account updatedAccount) {
        String sql = "UPDATE accounts SET name = ?, description = ?, currency = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, updatedAccount.getAccountName());
            stmt.setString(2, updatedAccount.getDescription());
            stmt.setString(3, updatedAccount.getCurrency().name());
            stmt.setInt(4, updatedAccount.getAccountID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al actualizar cuenta: " + e.getMessage());
        }
    }
}
