package com.tuapp.wallet.model;

import java.time.LocalDate;

public class Transaction {
    private int transactionID;
    private TipoTransaccion transactionType;
    private double amount;
    private LocalDate date;
    private String description;
    private Currency currency;
    private Account account;

    public Transaction(TipoTransaccion transactionType, double amount, LocalDate date, String description, Currency currency, Account account) {
        this.transactionType = transactionType;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.currency = currency;
        this.account = account;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public TipoTransaccion getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TipoTransaccion transactionType) {
        this.transactionType = transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

}
