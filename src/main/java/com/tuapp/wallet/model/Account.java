package com.tuapp.wallet.model;

import java.util.Currency;
import java.util.List;

public class Account {
    private int accountID;
    private String accountName;
    private String description;
    private List<Currency> currencys;
    private List<Transaction> transactions;

    public Account(String accountName, String description) {
        this.accountName = accountName;
        this.description = description;
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

    public List<Currency> getCurrencys() {
        return currencys;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setCurrencys(List<Currency> currencys) {
        this.currencys = currencys;
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

    public void addCurrency(Currency currency) {
        this.currencys.add(currency);
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }

}
