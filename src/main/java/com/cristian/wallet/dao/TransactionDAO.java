package com.cristian.wallet.dao;

import java.util.ArrayList;
import java.util.List;

import com.cristian.wallet.model.Transaction;

public class TransactionDAO {
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private int nextId = 1; // Para asignar IDs únicos a las transacciones


    public void addTransaction(Transaction transaction) {
        transaction.setTransactionID(nextId++);
        transactions.add(transaction);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public Transaction getTransactionById(int transactionID) {
        for (Transaction transaction : transactions) {
            if (transaction.getTransactionID() == transactionID) {
                return transaction;
            }
        }
        return null; // Si no se encuentra la transacción
    }

    public void saveTransactions() {
        // Aquí podrías implementar la lógica para guardar las transacciones en un archivo o base de datos
    }

    public void loadTransactions() {
        // Aquí podrías implementar la lógica para cargar las transacciones desde un archivo o base de datos
    }

    public void deleteTransaction(int transactionID) {
        transactions.removeIf(transaction -> transaction.getTransactionID() == transactionID);
    }

    public void updateTransaction(Transaction updatedTransaction) {
        for (int i = 0; i < transactions.size(); i++) {
            if (transactions.get(i).getTransactionID() == updatedTransaction.getTransactionID()) {
                transactions.set(i, updatedTransaction);
                break;
            }
        }
    }
}
