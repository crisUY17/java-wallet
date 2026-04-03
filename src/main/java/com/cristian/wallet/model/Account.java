package com.cristian.wallet.model;

public class Account {
    private int accountID;
    private String accountName;
    private String description;
    private Currency currency;

    public Account(String accountName, String description, Currency currency) {
        this.accountName = accountName;
        this.description = description;
        this.currency = currency;
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

    public void setCurrencys(Currency currency) {
        this.currency = currency;
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

}
