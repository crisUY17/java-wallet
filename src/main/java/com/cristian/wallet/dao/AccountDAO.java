package com.cristian.wallet.dao;

import java.util.ArrayList;
import com.cristian.wallet.model.Account;
import java.util.List;

public class AccountDAO {
    private ArrayList<Account> accounts = new ArrayList<>();
    private int nextId = 1; // Para asignar IDs únicos a las cuentas

    public void addAccount(Account account) {
        account.setAccountID(nextId++);
        accounts.add(account);
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public Account getAccountById(int accountID) {
        for (Account account : accounts) {
            if (account.getAccountID() == accountID) {
                return account;
            }
        }
        return null; // Si no se encuentra la cuenta
    }

    public void saveAccounts() {
        // Aquí podrías implementar la lógica para guardar las cuentas en un archivo o base de datos
    }

    public void loadAccounts() {
        // Aquí podrías implementar la lógica para cargar las cuentas desde un archivo o base de datos
    }

    public void deleteAccount(int accountID) {
        accounts.removeIf(account -> account.getAccountID() == accountID);
    }

    public void updateAccount(Account updatedAccount) {
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getAccountID() == updatedAccount.getAccountID()) {
                accounts.set(i, updatedAccount);
                break;
            }
        }
    }
}
