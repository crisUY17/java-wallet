package com.cristian.wallet.dao;

import java.util.List;

import com.cristian.wallet.model.Account;

public interface IAccountDAO {
    void addAccount(Account account);
    List<Account> getAccounts();
    Account getAccountById(int accountID);
    Account getAccountByTransaction(int TransactionID);
    void deleteAccount(int accountID);
    void updateAccount(Account updatedAccount);      
}
