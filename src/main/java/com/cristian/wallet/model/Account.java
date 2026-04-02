package com.cristian.wallet.model;

import java.util.Comparator;
import java.util.List;
import java.time.LocalDate;

public class Account {
    private int accountID;
    private String accountName;
    private String description;
    private Currency currency;
    private List<Transaction> transactions;

    public Account(String accountName, String description, Currency currency) {
        this.accountName = accountName;
        this.description = description;
        this.currency = currency;
        this.transactions = new java.util.ArrayList<>();
    }

    public int getAccountID() {
        return accountID;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getDescription() {
        return description;
    }

    public Currency getCurrency() {
        return currency;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setCurrencys(Currency currency) {
        this.currency = currency;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

    public void removeTransaction(Transaction transaction) {
        this.transactions.remove(transaction);
    }

    public double getBalance() {
        double balance = 0;
        for (Transaction t : transactions) {
            if (t.getTransactionType() == TipoTransaccion.INGRESO) {
                balance += t.getAmount();
            } else {
                balance -= t.getAmount();
            }
        }
        return balance;
    }

    public LocalDate getLastTransactionDate() {
    return transactions.stream()
        .map(Transaction::getDate)
        .max(Comparator.naturalOrder())
        .orElse(null);
    }

}
