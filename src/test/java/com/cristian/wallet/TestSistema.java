package com.cristian.wallet;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import com.cristian.wallet.dao.AccountDAO;
import com.cristian.wallet.dao.IAccountDAO;
import com.cristian.wallet.dao.ITransactionDAO;
import com.cristian.wallet.dao.TransactionDAO;
import com.cristian.wallet.model.Account;
import com.cristian.wallet.model.Currency;
import com.cristian.wallet.model.Transaction;
import com.cristian.wallet.model.TipoTransaccion;
import com.cristian.wallet.service.Controller;
import com.cristian.wallet.service.IController;

class ControllerTest {

    private IController controller;
    private Connection conn;

    @BeforeEach
    void setUp() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        Statement stmt = conn.createStatement();
        stmt.execute("PRAGMA foreign_keys = ON");
        stmt.execute("""
            CREATE TABLE accounts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT,
                currency TEXT NOT NULL
            )""");
        stmt.execute("""
            CREATE TABLE transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                account_id INTEGER NOT NULL,
                type TEXT NOT NULL,
                amount REAL NOT NULL,
                date TEXT NOT NULL,
                description TEXT,
                currency TEXT NOT NULL,
                FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
            )""");
        IAccountDAO accountDAO = new AccountDAO(conn);
        ITransactionDAO transactionDAO = new TransactionDAO(accountDAO, conn);
        controller = new Controller(accountDAO, transactionDAO);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    @Test
    void testGetAccountBalance() {
        // Crear cuenta
        controller.createAccount("Cuenta 1", "Descripción", Currency.PESOS_URUGUAYOS);
        List<Account> accounts = controller.getAccounts();
        int accountId = accounts.get(0).getAccountID();

        // Crear transacciones
        controller.createTransaction(TipoTransaccion.INGRESO, 100.0, "Ingreso", Currency.PESOS_URUGUAYOS, accountId);
        controller.createTransaction(TipoTransaccion.EGRESO, 50.0, "Egreso", Currency.PESOS_URUGUAYOS, accountId);

        double balance = controller.getAccountBalance(accountId);
        assertEquals(50.0, balance);
    }

    @Test
    void testGetTotalBalance() {
        // Crear cuentas
        controller.createAccount("Cuenta 1", "Descripción", Currency.PESOS_URUGUAYOS);
        controller.createAccount("Cuenta 2", "Descripción", Currency.DOLARES);

        List<Account> accounts = controller.getAccounts();
        int accountId1 = accounts.get(0).getAccountID();
        int accountId2 = accounts.get(1).getAccountID();

        // Transacciones
        controller.createTransaction(TipoTransaccion.INGRESO, 100.0, "Ingreso", Currency.PESOS_URUGUAYOS, accountId1);
        controller.createTransaction(TipoTransaccion.INGRESO, 200.0, "Ingreso", Currency.DOLARES, accountId2);

        Map<Currency, Double> totalBalance = controller.getTotalBalance();
        assertEquals(100.0, totalBalance.get(Currency.PESOS_URUGUAYOS));
        assertEquals(200.0, totalBalance.get(Currency.DOLARES));
    }

    @Test
    void testGetAccounts() {
        controller.createAccount("Cuenta 1", "Descripción", Currency.PESOS_URUGUAYOS);
        controller.createAccount("Cuenta 2", "Descripción", Currency.DOLARES);

        List<Account> accounts = controller.getAccounts();
        assertEquals(2, accounts.size());
        // Verificar orden por última transacción (primero la más reciente)
    }

    @Test
    void testGetTransactions() {
        controller.createAccount("Cuenta 1", "Descripción", Currency.PESOS_URUGUAYOS);
        List<Account> accounts = controller.getAccounts();
        int accountId = accounts.get(0).getAccountID();

        controller.createTransaction(TipoTransaccion.INGRESO, 100.0, "Ingreso", Currency.PESOS_URUGUAYOS, accountId);

        List<Transaction> transactions = controller.getTransactions();
        assertEquals(1, transactions.size());
        assertEquals("Ingreso", transactions.get(0).getDescription());
    }

    @Test
    void testGetTransactionsByAccount() {
        controller.createAccount("Cuenta 1", "Descripción", Currency.PESOS_URUGUAYOS);
        List<Account> accounts = controller.getAccounts();
        int accountId = accounts.get(0).getAccountID();

        controller.createTransaction(TipoTransaccion.INGRESO, 100.0, "Ingreso", Currency.PESOS_URUGUAYOS, accountId);

        List<Transaction> transactions = controller.getTransactionsByAccount(accountId);
        assertEquals(1, transactions.size());
    }

    @Test
    void testGetTransaction() {
        controller.createAccount("Cuenta 1", "Descripción", Currency.PESOS_URUGUAYOS);
        List<Account> accounts = controller.getAccounts();
        int accountId = accounts.get(0).getAccountID();

        controller.createTransaction(TipoTransaccion.INGRESO, 100.0, "Ingreso", Currency.PESOS_URUGUAYOS, accountId);
        List<Transaction> transactions = controller.getTransactions();
        int transactionId = transactions.get(0).getTransactionID();

        Transaction transaction = controller.getTransaction(transactionId);
        assertNotNull(transaction);
        assertEquals("Ingreso", transaction.getDescription());
    }

    @Test
    void testGetAccount() {
        controller.createAccount("Cuenta 1", "Descripción", Currency.PESOS_URUGUAYOS);
        List<Account> accounts = controller.getAccounts();
        int accountId = accounts.get(0).getAccountID();

        Account account = controller.getAccount(accountId);
        assertNotNull(account);
        assertEquals("Cuenta 1", account.getAccountName());
    }

    @Test
    void testCreateTransaction() {
        controller.createAccount("Cuenta 1", "Descripción", Currency.PESOS_URUGUAYOS);
        List<Account> accounts = controller.getAccounts();
        int accountId = accounts.get(0).getAccountID();

        controller.createTransaction(TipoTransaccion.INGRESO, 100.0, "Ingreso", Currency.PESOS_URUGUAYOS, accountId);

        List<Transaction> transactions = controller.getTransactions();
        assertEquals(1, transactions.size());
    }

    @Test
    void testCreateTransaction_InvalidAccount() {
        assertThrows(IllegalArgumentException.class, () -> {
            controller.createTransaction(TipoTransaccion.INGRESO, 100.0, "Ingreso", Currency.PESOS_URUGUAYOS, 999);
        });
    }

    @Test
    void testCreateTransaction_CurrencyMismatch() {
        controller.createAccount("Cuenta 1", "Descripción", Currency.PESOS_URUGUAYOS);
        List<Account> accounts = controller.getAccounts();
        int accountId = accounts.get(0).getAccountID();

        assertThrows(IllegalArgumentException.class, () -> {
            controller.createTransaction(TipoTransaccion.INGRESO, 100.0, "Ingreso", Currency.DOLARES, accountId);
        });
    }

    @Test
    void testCreateTransaction_InsufficientFunds() {
        controller.createAccount("Cuenta 1", "Descripción", Currency.PESOS_URUGUAYOS);
        List<Account> accounts = controller.getAccounts();
        int accountId = accounts.get(0).getAccountID();

        assertThrows(IllegalArgumentException.class, () -> {
            controller.createTransaction(TipoTransaccion.EGRESO, 100.0, "Egreso", Currency.PESOS_URUGUAYOS, accountId);
        });
    }

    @Test
    void testCreateAccount() {
        controller.createAccount("Cuenta 1", "Descripción", Currency.PESOS_URUGUAYOS);

        List<Account> accounts = controller.getAccounts();
        assertEquals(1, accounts.size());
        assertEquals("Cuenta 1", accounts.get(0).getAccountName());
    }

    @Test
    void testTransferFunds() {
        controller.createAccount("Cuenta 1", "Descripción", Currency.PESOS_URUGUAYOS);
        controller.createAccount("Cuenta 2", "Descripción", Currency.PESOS_URUGUAYOS);
        List<Account> accounts = controller.getAccounts();
        int accountId1 = accounts.get(0).getAccountID();
        int accountId2 = accounts.get(1).getAccountID();

        controller.createTransaction(TipoTransaccion.INGRESO, 200.0, "Ingreso", Currency.PESOS_URUGUAYOS, accountId1);

        controller.transferFunds(accountId1, accountId2, 100.0, Currency.PESOS_URUGUAYOS, "Transferencia");

        assertEquals(100.0, controller.getAccountBalance(accountId1));
        assertEquals(100.0, controller.getAccountBalance(accountId2));
    }

    @Test
    void testTransferFunds_InvalidAccount() {
        assertThrows(IllegalArgumentException.class, () -> {
            controller.transferFunds(999, 998, 100.0, Currency.PESOS_URUGUAYOS, "Transferencia");
        });
    }

    @Test
    void testTransferFunds_CurrencyMismatch() {
        controller.createAccount("Cuenta 1", "Descripción", Currency.PESOS_URUGUAYOS);
        controller.createAccount("Cuenta 2", "Descripción", Currency.DOLARES);
        List<Account> accounts = controller.getAccounts();
        int accountId1 = accounts.get(0).getAccountID();
        int accountId2 = accounts.get(1).getAccountID();

        assertThrows(IllegalArgumentException.class, () -> {
            controller.transferFunds(accountId1, accountId2, 100.0, Currency.PESOS_URUGUAYOS, "Transferencia");
        });
    }

    @Test
    void testTransferFunds_InsufficientFunds() {
        controller.createAccount("Cuenta 1", "Descripción", Currency.PESOS_URUGUAYOS);
        controller.createAccount("Cuenta 2", "Descripción", Currency.PESOS_URUGUAYOS);
        List<Account> accounts = controller.getAccounts();
        int accountId1 = accounts.get(0).getAccountID();
        int accountId2 = accounts.get(1).getAccountID();

        assertThrows(IllegalArgumentException.class, () -> {
            controller.transferFunds(accountId1, accountId2, 100.0, Currency.PESOS_URUGUAYOS, "Transferencia");
        });
    }

    @Test
    void testDeleteTransaction() {
        controller.createAccount("Cuenta 1", "Descripción", Currency.PESOS_URUGUAYOS);
        List<Account> accounts = controller.getAccounts();
        int accountId = accounts.get(0).getAccountID();

        controller.createTransaction(TipoTransaccion.INGRESO, 100.0, "Ingreso", Currency.PESOS_URUGUAYOS, accountId);
        List<Transaction> transactions = controller.getTransactions();
        int transactionId = transactions.get(0).getTransactionID();

        controller.deleteTransaction(transactionId);

        assertNull(controller.getTransaction(transactionId));
    }

    @Test
    void testDeleteTransaction_InvalidId() {
        assertThrows(IllegalArgumentException.class, () -> {
            controller.deleteTransaction(999);
        });
    }

    @Test
    void testDeleteAccount() {
        controller.createAccount("Cuenta 1", "Descripción", Currency.PESOS_URUGUAYOS);
        List<Account> accounts = controller.getAccounts();
        int accountId = accounts.get(0).getAccountID();

        controller.deleteAccount(accountId);

        assertNull(controller.getAccount(accountId));
    }

    @Test
    void testDeleteAccount_InvalidId() {
        assertThrows(IllegalArgumentException.class, () -> {
            controller.deleteAccount(999);
        });
    }
}