package com.cristian.wallet.dao;

import java.util.List;
import com.cristian.wallet.model.Transaction;

public interface ITransactionDAO {
    void addTransaction(Transaction transaction);
    List<Transaction> getTransactions();
    List<Transaction> getTransactionsByAccount(int accountID);
    Transaction getTransactionById(int transactionID);
    Transaction getLastTransactionByAccount(int accountID);
    void deleteTransaction(int transactionID);
    void updateTransaction(Transaction updatedTransaction);
}
