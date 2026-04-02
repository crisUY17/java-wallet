package com.cristian.wallet.ui;
import java.util.Map;
import java.util.List;
import com.cristian.wallet.model.Currency;
import com.cristian.wallet.model.TipoTransaccion;
import com.cristian.wallet.model.Transaction;
import com.cristian.wallet.model.Account;


public interface IController {
    // Retorna un mapa con el balance total por cada moneda
    Map<Currency, Double> getTotalBalance();
    
    // Retorna una lista de todas las cuentas, ordenadas por fecha de última transacción
    List<Account> getAccounts();
    
    // Retorna una lista de todas las transacciones, ordenadas por fecha
    List<Transaction> getTransactions();
    
    // Retorna una lista de transacciones para una cuenta específica, ordenadas por fecha
    List<Transaction> getTransactionsByAccount(int accountID);
    
    // Retorna una transacción específica por su ID
    Transaction getTransaction(int transactionID);
    
    // Retorna una cuenta específica por su ID
    Account getAccount(int accountID);
    
    // Crea una nueva transacción con fecha actual
    void createTransaction(TipoTransaccion transactionType, double amount, String description, Currency currency, int accountID);
    
    // Crea una nueva cuenta
    void createAccount(String accountName, String description, Currency currency);
    
    // Transfiere fondos entre cuentas
    void transferFunds(int fromAccountID, int toAccountID, double amount,Currency currency, String description);
    
    // Elimina una transacción específica por su ID
    void deleteTransaction(int transactionID);
    
    // Elimina una cuenta específica por su ID
    void deleteAccount(int accountID);
}
