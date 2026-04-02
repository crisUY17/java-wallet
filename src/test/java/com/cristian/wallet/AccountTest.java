package com.cristian.wallet;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cristian.wallet.model.Account;
import com.cristian.wallet.model.Currency;
import com.cristian.wallet.model.Transaction;
import com.cristian.wallet.model.TipoTransaccion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class AccountTest {

    private Account account;
    private Currency currency;
    private List<Transaction> transactions;

    @BeforeEach
    void setUp() {
        currency = Currency.PESOS_URUGUAYOS;
        account = new Account("Cuenta Principal", "Cuenta de ahorros", currency);
        transactions = new ArrayList<>();
        account.setTransactions(transactions);
        account.setAccountID(1);
    }

    @Test
    void testConstructor() {
        assertEquals("Cuenta Principal", account.getAccountName());
        assertEquals("Cuenta de ahorros", account.getDescription());
        assertEquals(currency, account.getCurrency());
        assertNotNull(account.getTransactions());
        assertTrue(account.getTransactions().isEmpty());
    }

    @Test
    void testGetAccountID() {
        assertEquals(1, account.getAccountID());
    }

    @Test
    void testSetAccountID() {
        account.setAccountID(2);
        assertEquals(2, account.getAccountID());
    }

    @Test
    void testGetAccountName() {
        assertEquals("Cuenta Principal", account.getAccountName());
    }

    @Test
    void testSetAccountName() {
        account.setAccountName("Nueva Cuenta");
        assertEquals("Nueva Cuenta", account.getAccountName());
    }

    @Test
    void testGetDescription() {
        assertEquals("Cuenta de ahorros", account.getDescription());
    }

    @Test
    void testSetDescription() {
        account.setDescription("Nueva descripción");
        assertEquals("Nueva descripción", account.getDescription());
    }

    @Test
    void testGetCurrency() {
        assertEquals(currency, account.getCurrency());
    }

    @Test
    void testSetCurrencys() {
        Currency newCurrency = Currency.DOLARES;
        account.setCurrencys(newCurrency);
        assertEquals(newCurrency, account.getCurrency());
    }

    @Test
    void testGetTransactions() {
        assertEquals(transactions, account.getTransactions());
    }

    @Test
    void testSetTransactions() {
        List<Transaction> newTransactions = new ArrayList<>();
        account.setTransactions(newTransactions);
        assertEquals(newTransactions, account.getTransactions());
    }

    @Test
    void testAddTransaction() {
        Transaction transaction = new Transaction(TipoTransaccion.INGRESO, 100.0, LocalDate.now(), "Depósito", currency, account);
        account.addTransaction(transaction);
        assertEquals(1, account.getTransactions().size());
        assertTrue(account.getTransactions().contains(transaction));
    }

    @Test
    void testRemoveTransaction() {
        Transaction transaction = new Transaction(TipoTransaccion.INGRESO, 100.0, LocalDate.now(), "Depósito", currency, account);
        account.addTransaction(transaction);
        assertEquals(1, account.getTransactions().size());
        account.removeTransaction(transaction);
        assertTrue(account.getTransactions().isEmpty());
    }

    @Test
    void testGetBalanceWithNoTransactions() {
        assertEquals(0.0, account.getBalance());
    }

    @Test
    void testGetBalanceWithIngreso() {
        Transaction ingreso = new Transaction(TipoTransaccion.INGRESO, 200.0, LocalDate.now(), "Depósito", currency, account);
        account.addTransaction(ingreso);
        assertEquals(200.0, account.getBalance());
    }

    @Test
    void testGetBalanceWithEgreso() {
        Transaction egreso = new Transaction(TipoTransaccion.EGRESO, 50.0, LocalDate.now(), "Retiro", currency, account);
        account.addTransaction(egreso);
        assertEquals(-50.0, account.getBalance());
    }

    @Test
    void testGetBalanceWithMultipleTransactions() {
        Transaction ingreso1 = new Transaction(TipoTransaccion.INGRESO, 100.0, LocalDate.now(), "Depósito 1", currency, account);
        Transaction ingreso2 = new Transaction(TipoTransaccion.INGRESO, 50.0, LocalDate.now(), "Depósito 2", currency, account);
        Transaction egreso = new Transaction(TipoTransaccion.EGRESO, 30.0, LocalDate.now(), "Compra", currency, account);
        account.addTransaction(ingreso1);
        account.addTransaction(ingreso2);
        account.addTransaction(egreso);
        assertEquals(120.0, account.getBalance());
    }

    @Test
    void testGetLastTransactionDateWithNoTransactions() {
        assertNull(account.getLastTransactionDate());
    }

    @Test
    void testGetLastTransactionDateWithOneTransaction() {
        LocalDate date = LocalDate.of(2023, 4, 1);
        Transaction transaction = new Transaction(TipoTransaccion.INGRESO, 100.0, date, "Depósito", currency, account);
        account.addTransaction(transaction);
        assertEquals(date, account.getLastTransactionDate());
    }

    @Test
    void testGetLastTransactionDateWithMultipleTransactions() {
        LocalDate date1 = LocalDate.of(2023, 4, 1);
        LocalDate date2 = LocalDate.of(2023, 4, 3);
        LocalDate date3 = LocalDate.of(2023, 4, 2);
        Transaction t1 = new Transaction(TipoTransaccion.INGRESO, 100.0, date1, "Depósito 1", currency, account);
        Transaction t2 = new Transaction(TipoTransaccion.INGRESO, 50.0, date2, "Depósito 2", currency, account);
        Transaction t3 = new Transaction(TipoTransaccion.EGRESO, 30.0, date3, "Compra", currency, account);
        account.addTransaction(t1);
        account.addTransaction(t2);
        account.addTransaction(t3);
        assertEquals(date2, account.getLastTransactionDate()); // date2 es la más reciente
    }
}
