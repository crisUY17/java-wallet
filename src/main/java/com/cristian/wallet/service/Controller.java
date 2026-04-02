package com.cristian.wallet.service;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.time.LocalDate;

import com.cristian.wallet.model.Transaction;
import com.cristian.wallet.model.Account;
import com.cristian.wallet.dao.TransactionDAO;
import com.cristian.wallet.dao.AccountDAO;
import com.cristian.wallet.ui.IController;
import com.cristian.wallet.model.Currency;
import com.cristian.wallet.model.TipoTransaccion;

public class Controller implements IController {
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;

    public Controller(AccountDAO accountDAO, TransactionDAO transactionDAO) {
        this.accountDAO = accountDAO;
        this.transactionDAO = transactionDAO;
    }

    @Override
    public Map<Currency, Double> getTotalBalance() {
        Map<Currency, Double> totalBalance = new HashMap<>();
        for (Account account : accountDAO.getAccounts()) {
            Currency currency = account.getCurrency();
            totalBalance.merge(currency, account.getBalance(), Double::sum);
        }
        return totalBalance;
    }

    @Override
    public List<Account> getAccounts() {
        List<Account> accounts = new ArrayList<>(accountDAO.getAccounts());
        accounts.sort(Comparator.comparing(
            Account::getLastTransactionDate,
            Comparator.nullsLast(Comparator.reverseOrder())
        ));
        return accounts;
    }

    @Override
    public List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>(transactionDAO.getTransactions());
        transactions.sort(Comparator.comparing(Transaction::getDate).reversed());
        return transactions;
    }

    @Override
    public List<Transaction> getTransactionsByAccount(int accountID) {
        Account account = accountDAO.getAccountById(accountID);
        if (account == null) {
            return Collections.emptyList();
        }
        List<Transaction> transactions = new ArrayList<>(account.getTransactions());
        transactions.sort(Comparator.comparing(Transaction::getDate).reversed());
        return transactions;
    }

    @Override
    public Transaction getTransaction(int transactionID) {
        return transactionDAO.getTransactionById(transactionID);
    }

    @Override
    public Account getAccount(int accountID) {
        return accountDAO.getAccountById(accountID);
    }

    @Override
    public void createTransaction(TipoTransaccion transactionType, double amount, String description, Currency currency, int accountID) {
        Account account = accountDAO.getAccountById(accountID);
        if (account == null) {
            throw new IllegalArgumentException("La cuenta no existe");
        }
        if (currency != account.getCurrency()) {
            throw new IllegalArgumentException("La moneda no coincide con la de la cuenta");
        }
        if (transactionType == TipoTransaccion.EGRESO && account.getBalance() < amount) {
            throw new IllegalArgumentException("Fondos insuficientes");
        }
        Transaction transaction = new Transaction(transactionType, amount, LocalDate.now(), description, currency, account);
        account.addTransaction(transaction);
        transactionDAO.addTransaction(transaction);
        
    }

    @Override
    public void createAccount(String accountName, String description, Currency currency) {
        Account account = new Account(accountName, description, currency);
        accountDAO.addAccount(account);
    }

    @Override
    public void transferFunds(int fromAccountID, int toAccountID, double amount, Currency currency, String description) {
        Account fromAccount = accountDAO.getAccountById(fromAccountID);
        Account toAccount = accountDAO.getAccountById(toAccountID);
        if (fromAccount == null || toAccount == null) {
            throw new IllegalArgumentException("Una o ambas cuentas no existen");
        }
        if (currency != fromAccount.getCurrency() || currency != toAccount.getCurrency()) {
            throw new IllegalArgumentException("La moneda no coincide con la de las cuentas");
        }
        if (fromAccount.getBalance() < amount) {
            throw new IllegalArgumentException("Fondos insuficientes en la cuenta de origen");
        }
        createTransaction(TipoTransaccion.EGRESO, amount, description + " (Transferencia a " + toAccount.getAccountName() + ")", currency, fromAccountID);
        createTransaction(TipoTransaccion.INGRESO, amount, description + " (Transferencia desde " + fromAccount.getAccountName() + ")", currency, toAccountID);
    }

    @Override
    public void deleteTransaction(int transactionID) {
        Transaction transaction = transactionDAO.getTransactionById(transactionID);
        if (transaction == null) {
            throw new IllegalArgumentException("La transacción no existe");
        }
        transaction.getAccount().removeTransaction(transaction);
        transactionDAO.deleteTransaction(transactionID);
    }

    @Override
    public void deleteAccount(int accountID) {
        Account account = accountDAO.getAccountById(accountID);
        if (account == null) {
            throw new IllegalArgumentException("La cuenta no existe");
        }
        new ArrayList<>(account.getTransactions()).forEach(transaction -> {
            transactionDAO.deleteTransaction(transaction.getTransactionID());
        });
        accountDAO.deleteAccount(accountID);
    }
}
