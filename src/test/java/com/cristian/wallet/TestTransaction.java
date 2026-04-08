package com.cristian.wallet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.cristian.wallet.model.Account;
import com.cristian.wallet.model.Currency;
import com.cristian.wallet.model.TipoTransaccion;
import com.cristian.wallet.model.Transaction;

class TestTransaction {

    @Test
    @DisplayName("Crear Transaction y verificar getters")
    void testTransactionConstructorAndGetters() {
        Account account = new Account("Ahorros", "Cuenta ahorro", Currency.DOLARES);
        account.setAccountID(1);

        Transaction tx = new Transaction(TipoTransaccion.INGRESO, 150.25, LocalDate.of(2026, 4, 3), "Sueldo", Currency.DOLARES, account);

        assertNotNull(tx);
        assertEquals(0, tx.getTransactionID());
        assertEquals(TipoTransaccion.INGRESO, tx.getTransactionType());
        assertEquals(150.25, tx.getAmount());
        assertEquals(LocalDate.of(2026, 4, 3), tx.getDate());
        assertEquals("Sueldo", tx.getDescription());
        assertEquals(Currency.DOLARES, tx.getCurrency());
        assertEquals(account, tx.getAccount());
    }

    @Test
    @DisplayName("Modificar Transaction con setters")
    void testTransactionSetters() {
        Account account = new Account("Ahorros", "Cuenta ahorro", Currency.DOLARES);
        account.setAccountID(1);

        Transaction tx = new Transaction(TipoTransaccion.INGRESO, 150.25, LocalDate.of(2026, 4, 3), "Sueldo", Currency.DOLARES, account);

        tx.setTransactionID(99);
        tx.setTransactionType(TipoTransaccion.EGRESO);
        tx.setAmount(45.75);
        tx.setDate(LocalDate.of(2026, 4, 4));
        tx.setDescription("Pago servicios");
        tx.setCurrency(Currency.PESOS_URUGUAYOS);

        Account other = new Account("Corriente", "Cuenta corriente", Currency.PESOS_URUGUAYOS);
        other.setAccountID(2);
        tx.setAccount(other);

        assertEquals(99, tx.getTransactionID());
        assertEquals(TipoTransaccion.EGRESO, tx.getTransactionType());
        assertEquals(45.75, tx.getAmount());
        assertEquals(LocalDate.of(2026, 4, 4), tx.getDate());
        assertEquals("Pago servicios", tx.getDescription());
        assertEquals(Currency.PESOS_URUGUAYOS, tx.getCurrency());
        assertEquals(other, tx.getAccount());
    }
}
