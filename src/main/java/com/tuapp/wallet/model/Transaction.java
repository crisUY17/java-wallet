package com.tuapp.wallet.model;

import java.sql.Date;

public class Transaction {
    private int transactionID;
    private TipoTransaccion transactionType;
    private double amount;
    private Date date;
    private String description;
    private Currency currency;

    public Transaction(int transactionID, TipoTransaccion transactionType, double amount, Date date, String description, Currency currency) {
        this.transactionID = transactionID;
        this.transactionType = transactionType;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.currency = currency;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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

}
