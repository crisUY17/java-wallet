package com.cristian.wallet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.cristian.wallet.model.Account;
import com.cristian.wallet.model.Currency;

class TestAccount {

    @Test
    @DisplayName("Crear Account y verificar getters")
    void testAccountConstructorAndGetters() {
        Account account = new Account("Ahorros", "Cuenta de ahorro personal", Currency.DOLARES);

        assertNotNull(account);
        assertEquals("Ahorros", account.getAccountName());
        assertEquals("Cuenta de ahorro personal", account.getDescription());
        assertEquals(Currency.DOLARES, account.getCurrency());
        assertEquals(0, account.getAccountID());
    }

    @Test
    @DisplayName("Modificar los campos de Account con setters")
    void testAccountSetters() {
        Account account = new Account("Ahorros", "Cuenta de ahorro personal", Currency.DOLARES);

        account.setAccountID(42);
        account.setAccountName("Corriente");
        account.setDescription("Cuenta corriente empresa");
        account.setCurrencys(Currency.PESOS_URUGUAYOS);

        assertEquals(42, account.getAccountID());
        assertEquals("Corriente", account.getAccountName());
        assertEquals("Cuenta corriente empresa", account.getDescription());
        assertEquals(Currency.PESOS_URUGUAYOS, account.getCurrency());
    }
}
