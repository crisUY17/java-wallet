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
import com.cristian.wallet.dao.ITransactionDAO;
import com.cristian.wallet.dao.IAccountDAO;
import com.cristian.wallet.model.Currency;
import com.cristian.wallet.model.TipoTransaccion;

public class Controller implements IController {
    private final IAccountDAO accountDAO;
    private final ITransactionDAO transactionDAO;

    public double getAccountBalance(int accountID) {
    return transactionDAO.getTransactionsByAccount(accountID)
        .stream()
        .mapToDouble(t -> t.getTransactionType() == TipoTransaccion.INGRESO
            ? t.getAmount() : -t.getAmount())
        .sum();
    }

    public Controller(IAccountDAO accountDAO, ITransactionDAO transactionDAO) {
        this.accountDAO = accountDAO;
        this.transactionDAO = transactionDAO;
    }

    @Override
    public Map<Currency, Double> getTotalBalance() {
        Map<Currency, Double> totalBalance = new HashMap<>();
        for (Account account : accountDAO.getAccounts()) {
            Currency currency = account.getCurrency();
            totalBalance.merge(currency, getAccountBalance(account.getAccountID()), Double::sum);
        }
        return totalBalance;
    }

    @Override
    public List<Account> getAccounts() {
        List<Account> accounts = new ArrayList<>(accountDAO.getAccounts());
        accounts.sort(Comparator.comparing((Account account) -> {
            Transaction lastTransaction = transactionDAO.getLastTransactionByAccount(account.getAccountID());
            return lastTransaction != null ? lastTransaction.getDate() : LocalDate.MIN;
        }).reversed());
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
        List<Transaction> transactions = new ArrayList<>(transactionDAO.getTransactionsByAccount(accountID));
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
        if (transactionType == TipoTransaccion.EGRESO && getAccountBalance(accountID) < amount) {
            throw new IllegalArgumentException("Fondos insuficientes");
        }
        Transaction transaction = new Transaction(transactionType, amount, LocalDate.now(), description, currency, account);
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
        if (getAccountBalance(fromAccountID) < amount) {
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
        transactionDAO.deleteTransaction(transactionID);
    }

    @Override
    public void deleteAccount(int accountID) {
        Account account = accountDAO.getAccountById(accountID);
        if (account == null) {
            throw new IllegalArgumentException("La cuenta no existe");
        }
        accountDAO.deleteAccount(accountID);
    }
}
