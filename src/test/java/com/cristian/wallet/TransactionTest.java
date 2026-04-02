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

class TransactionTest {

    private Transaction transaction;
    private Account account;
    private Currency currency;
    private TipoTransaccion tipoTransaccion;
    private LocalDate date;
    private double amount;
    private String description;

    @BeforeEach
    void setUp() {
        currency = Currency.PESOS_URUGUAYOS;
        account = new Account("Cuenta de Prueba", "Descripción", currency);
        account.setTransactions(new ArrayList<>());
        tipoTransaccion = TipoTransaccion.INGRESO;
        date = LocalDate.of(2023, 4, 15);
        amount = 500.0;
        description = "Depósito inicial";
        transaction = new Transaction(tipoTransaccion, amount, date, description, currency, account);
    }

    // Tests del constructor
    @Test
    void testConstructor() {
        assertEquals(TipoTransaccion.INGRESO, transaction.getTransactionType());
        assertEquals(500.0, transaction.getAmount());
        assertEquals(LocalDate.of(2023, 4, 15), transaction.getDate());
        assertEquals("Depósito inicial", transaction.getDescription());
        assertEquals(Currency.PESOS_URUGUAYOS, transaction.getCurrency());
        assertEquals(account, transaction.getAccount());
    }

    // Tests de getTransactionID y setTransactionID
    @Test
    void testGetTransactionID() {
        transaction.setTransactionID(1);
        assertEquals(1, transaction.getTransactionID());
    }

    @Test
    void testSetTransactionID() {
        transaction.setTransactionID(100);
        assertEquals(100, transaction.getTransactionID());
    }

    @Test
    void testSetTransactionIDNegative() {
        transaction.setTransactionID(-5);
        assertEquals(-5, transaction.getTransactionID()); // Sin validación, se permite
    }

    @Test
    void testSetTransactionIDZero() {
        transaction.setTransactionID(0);
        assertEquals(0, transaction.getTransactionID());
    }

    // Tests de getTransactionType y setTransactionType
    @Test
    void testGetTransactionType() {
        assertEquals(TipoTransaccion.INGRESO, transaction.getTransactionType());
    }

    @Test
    void testSetTransactionTypeToEgreso() {
        transaction.setTransactionType(TipoTransaccion.EGRESO);
        assertEquals(TipoTransaccion.EGRESO, transaction.getTransactionType());
    }

    @Test
    void testSetTransactionTypeToIngreso() {
        transaction.setTransactionType(TipoTransaccion.EGRESO);
        transaction.setTransactionType(TipoTransaccion.INGRESO);
        assertEquals(TipoTransaccion.INGRESO, transaction.getTransactionType());
    }

    // Tests de getAmount y setAmount
    @Test
    void testGetAmount() {
        assertEquals(500.0, transaction.getAmount());
    }

    @Test
    void testSetAmount() {
        transaction.setAmount(1000.0);
        assertEquals(1000.0, transaction.getAmount());
    }

    @Test
    void testSetAmountZero() {
        transaction.setAmount(0.0);
        assertEquals(0.0, transaction.getAmount());
    }

    @Test
    void testSetAmountNegative() {
        transaction.setAmount(-100.0);
        assertEquals(-100.0, transaction.getAmount()); // Sin validación, se permite
    }

    @Test
    void testSetAmountDecimal() {
        transaction.setAmount(99.99);
        assertEquals(99.99, transaction.getAmount());
    }

    // Tests de getDate y setDate
    @Test
    void testGetDate() {
        assertEquals(LocalDate.of(2023, 4, 15), transaction.getDate());
    }

    @Test
    void testSetDate() {
        LocalDate newDate = LocalDate.of(2024, 1, 1);
        transaction.setDate(newDate);
        assertEquals(newDate, transaction.getDate());
    }

    @Test
    void testSetDateToday() {
        LocalDate today = LocalDate.now();
        transaction.setDate(today);
        assertEquals(today, transaction.getDate());
    }

    @Test
    void testSetDatePastDate() {
        LocalDate pastDate = LocalDate.of(2000, 1, 1);
        transaction.setDate(pastDate);
        assertEquals(pastDate, transaction.getDate());
    }

    @Test
    void testSetDateFutureDate() {
        LocalDate futureDate = LocalDate.of(2050, 12, 31);
        transaction.setDate(futureDate);
        assertEquals(futureDate, transaction.getDate());
    }

    // Tests de getDescription y setDescription
    @Test
    void testGetDescription() {
        assertEquals("Depósito inicial", transaction.getDescription());
    }

    @Test
    void testSetDescription() {
        transaction.setDescription("Nuevo depósito");
        assertEquals("Nuevo depósito", transaction.getDescription());
    }

    @Test
    void testSetDescriptionEmpty() {
        transaction.setDescription("");
        assertEquals("", transaction.getDescription());
    }

    @Test
    void testSetDescriptionLong() {
        String longDescription = "Esta es una descripción muy larga que contiene muchos caracteres para probar si el sistema acepta descripciones largas";
        transaction.setDescription(longDescription);
        assertEquals(longDescription, transaction.getDescription());
    }

    @Test
    void testSetDescriptionWithSpecialChars() {
        transaction.setDescription("Pago #123 - 50% descuento @ tienda");
        assertEquals("Pago #123 - 50% descuento @ tienda", transaction.getDescription());
    }

    // Tests de getCurrency y setCurrency
    @Test
    void testGetCurrency() {
        assertEquals(Currency.PESOS_URUGUAYOS, transaction.getCurrency());
    }

    @Test
    void testSetCurrencyToDolares() {
        transaction.setCurrency(Currency.DOLARES);
        assertEquals(Currency.DOLARES, transaction.getCurrency());
    }

    @Test
    void testSetCurrencyToUI() {
        transaction.setCurrency(Currency.UI);
        assertEquals(Currency.UI, transaction.getCurrency());
    }

    @Test
    void testSetCurrencyChanges() {
        assertEquals(Currency.PESOS_URUGUAYOS, transaction.getCurrency());
        transaction.setCurrency(Currency.DOLARES);
        assertEquals(Currency.DOLARES, transaction.getCurrency());
        transaction.setCurrency(Currency.UI);
        assertEquals(Currency.UI, transaction.getCurrency());
    }

    // Tests de getAccount y setAccount
    @Test
    void testGetAccount() {
        assertEquals(account, transaction.getAccount());
    }

    @Test
    void testSetAccount() {
        Account newAccount = new Account("Nueva Cuenta", "Otra descripción", Currency.DOLARES);
        transaction.setAccount(newAccount);
        assertEquals(newAccount, transaction.getAccount());
    }

    @Test
    void testSetAccountNull() {
        transaction.setAccount(null);
        assertNull(transaction.getAccount());
    }

    @Test
    void testSetAccountMultipleTimes() {
        Account account2 = new Account("Cuenta 2", "Desc 2", Currency.DOLARES);
        Account account3 = new Account("Cuenta 3", "Desc 3", Currency.UI);
        transaction.setAccount(account2);
        assertEquals(account2, transaction.getAccount());
        transaction.setAccount(account3);
        assertEquals(account3, transaction.getAccount());
    }

    // Tests de casos de uso de negocio
    @Test
    void testCrearTransaccionIngreso() {
        Transaction ingreso = new Transaction(
            TipoTransaccion.INGRESO,
            1500.0,
            LocalDate.now(),
            "Salario",
            Currency.PESOS_URUGUAYOS,
            account
        );
        assertEquals(TipoTransaccion.INGRESO, ingreso.getTransactionType());
        assertEquals(1500.0, ingreso.getAmount());
        assertTrue(ingreso.getAmount() > 0);
    }

    @Test
    void testCrearTransaccionEgreso() {
        Transaction egreso = new Transaction(
            TipoTransaccion.EGRESO,
            200.0,
            LocalDate.now(),
            "Compra",
            Currency.PESOS_URUGUAYOS,
            account
        );
        assertEquals(TipoTransaccion.EGRESO, egreso.getTransactionType());
        assertEquals(200.0, egreso.getAmount());
        assertTrue(egreso.getAmount() > 0);
    }

    @Test
    void testActualizarTransaccion() {
        transaction.setTransactionID(1);
        transaction.setAmount(750.0);
        transaction.setDescription("Depósito modificado");
        transaction.setDate(LocalDate.of(2023, 5, 1));

        assertEquals(1, transaction.getTransactionID());
        assertEquals(750.0, transaction.getAmount());
        assertEquals("Depósito modificado", transaction.getDescription());
        assertEquals(LocalDate.of(2023, 5, 1), transaction.getDate());
    }

    @Test
    void testComparaTransacciones() {
        Transaction t1 = new Transaction(TipoTransaccion.INGRESO, 500.0, LocalDate.now(), "T1", Currency.PESOS_URUGUAYOS, account);
        Transaction t2 = new Transaction(TipoTransaccion.INGRESO, 500.0, LocalDate.now(), "T1", Currency.PESOS_URUGUAYOS, account);

        // Sin un método equals, comparamos los atributos manualmente
        assertEquals(t1.getAmount(), t2.getAmount());
        assertEquals(t1.getTransactionType(), t2.getTransactionType());
        assertEquals(t1.getDate(), t2.getDate());
    }

    @Test
    void testTransaccionConDiferentesCurrencies() {
        Transaction t1 = new Transaction(TipoTransaccion.INGRESO, 100.0, LocalDate.now(), "USD", Currency.DOLARES, account);
        Transaction t2 = new Transaction(TipoTransaccion.INGRESO, 100.0, LocalDate.now(), "UYU", Currency.PESOS_URUGUAYOS, account);

        assertNotEquals(t1.getCurrency(), t2.getCurrency());
        assertEquals(t1.getAmount(), t2.getAmount());
    }
}
