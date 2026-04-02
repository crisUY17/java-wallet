package com.cristian.wallet.ui;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.cristian.wallet.model.Currency;
import com.cristian.wallet.model.TipoTransaccion;
import com.cristian.wallet.model.Transaction;
import com.cristian.wallet.model.Account;

public class ConsoleUI {
    private final IController controller;
    private final Scanner scanner;

    public ConsoleUI(IController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    private void showTotalBalance() {
        Map<Currency, Double> balances = controller.getTotalBalance();
        if (balances.isEmpty()) {
            System.out.println("No hay fondos.");
            return;
        }
        System.out.println("Balance total por moneda:");
        for (Map.Entry<Currency, Double> entry : balances.entrySet()) {
                System.out.printf("%s: %.2f%n", entry.getKey(), entry.getValue());
        }
        
    }

    private void showAccounts(){
        List<Account> accounts = controller.getAccounts();
        if (accounts.isEmpty()) {
            System.out.println("No hay cuentas disponibles.");
            return;
        }
        System.out.println("Cuentas:");
        for (Account account : accounts) {
            System.out.printf("Nombre: %s, Descripción: %s, Moneda: %s, Balance: %.2f%n",
                    account.getAccountName(), account.getDescription(),
                    account.getCurrency(), account.getBalance());
        }
    }

    private void showTransactions(){
        List<Transaction> transactions = controller.getTransactions();
        if (transactions.isEmpty()) {
            System.out.println("No hay transacciones disponibles.");
            return;
        }
        System.out.println("Transacciones:");
        for (Transaction transaction : transactions) {
            System.out.printf("Tipo: %s, Monto: %.2f, Moneda: %s, Fecha: %s%n",
                    transaction.getTransactionType(),
                    transaction.getAmount(), transaction.getCurrency(), transaction.getDate());
        }
    }

    private void createAccount(){
        System.out.println("Ingrese el nombre de la cuenta:");
        String accountName = scanner.nextLine();
        System.out.println("Ingrese la descripción de la cuenta:");
        String description = scanner.nextLine();
        Currency currency = readCurrency(); // Validar la moneda antes de solicitar la descripción
        if (currency == null) {
            return;
        }
        try {
            controller.createAccount(accountName, description, currency);
            System.out.println("Cuenta creada exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error al crear la cuenta: " + e.getMessage());
        }
    }

    private void createTransaction(){
        System.out.println("Ingrese el ID de la cuenta:");
        int accountID = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        System.out.println("Ingrese el tipo de transacción: \n1. Ingreso\n2. Egreso");
        int transactionType = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        if (transactionType != 1 && transactionType != 2) {
            System.out.println("Tipo de transacción no válido. Transacción cancelada.");
            return;
        }
        TipoTransaccion tipo = (transactionType == 1) ? TipoTransaccion.INGRESO : TipoTransaccion.EGRESO;
        System.out.println("Ingrese el monto:");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consumir el salto de línea
        if (amount <= 0) {
            System.out.println("Monto no válido. Transacción cancelada.");
            return;
        }
        System.out.println("Ingrese la descripción:");
        String description = scanner.nextLine();
        Currency currency = readCurrency(); // Validar la moneda antes de solicitar la descripción
        if (currency == null) {
            return;
        }
        try {
            controller.createTransaction(tipo, amount, description, currency, accountID);
            System.out.println("Transacción creada exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error al crear la transacción: " + e.getMessage());
        }
    }

    private void transferFunds(){
        System.out.println("Ingrese el ID de la cuenta de origen:");
        int fromAccountID = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        System.out.println("Ingrese el ID de la cuenta de destino:");
        int toAccountID = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        System.out.println("Ingrese el monto a transferir:");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consumir el salto de línea
        if (amount <= 0) {
            System.out.println("Monto no válido. Transferencia cancelada.");
            return;
        }
        Currency currency = readCurrency(); // Validar la moneda antes de solicitar la descripción
        if (currency == null) {
            return;
        }
        System.out.println("Ingrese la descripción:");
        String description = scanner.nextLine();
        try {
            controller.transferFunds(fromAccountID, toAccountID, amount, currency, description);
            System.out.println("Transferencia realizada exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error al realizar la transferencia: " + e.getMessage());
        }
    }
    private void deleteTransaction(){
        System.out.println("Ingrese el ID de la transacción a eliminar:");
        int transactionID = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        try {
            controller.deleteTransaction(transactionID);
            System.out.println("Transacción eliminada exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error al eliminar la transacción: " + e.getMessage());
        }
    }
    private void deleteAccount(){
        System.out.println("Ingrese el ID de la cuenta a eliminar:");
        int accountID = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea
        try {
            controller.deleteAccount(accountID);
            System.out.println("Cuenta eliminada exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error al eliminar la cuenta: " + e.getMessage());
        }
    }

    private Currency readCurrency() {
        System.out.println("Ingrese la moneda: \n1. Pesos Uruguayos\n2. Dólares\n3. UI");
        int currencyChoice = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea   
        if (currencyChoice != 1 && currencyChoice != 2 && currencyChoice != 3) {
            System.out.println("Moneda no válida. Operación cancelada.");
            return null;
        }
        return switch (currencyChoice) {
            case 1 -> Currency.PESOS_URUGUAYOS;
            case 2 -> Currency.DOLARES;
            case 3 -> Currency.UI;
            default -> null; // Esto nunca debería ocurrir debido a la validación anterior
        };
    }

    public void start() {
        int opcion;
        System.out.println("Bienvenido a Java Wallet!");
        do {
            System.out.println("Elija una opción:");
            System.out.println("1. Ver balance total");
            System.out.println("2. Ver cuentas");
            System.out.println("3. Ver transacciones");
            System.out.println("4. Crear cuenta");
            System.out.println("5. Crear transacción");
            System.out.println("6. Transferir fondos");
            System.out.println("7. Eliminar transacción");
            System.out.println("8. Eliminar cuenta");
            System.out.println("0. Salir");
            opcion = scanner.nextInt();
            scanner.nextLine(); // Consumir el salto de línea
            switch (opcion) {
                case 1 -> showTotalBalance();
                case 2 -> showAccounts();
                case 3 -> showTransactions();
                case 4 -> createAccount();
                case 5 -> createTransaction();
                case 6 -> transferFunds();
                case 7 -> deleteTransaction();
                case 8 -> deleteAccount();    
                case 0 -> {
                    System.out.println("Gracias por usar Java Wallet. ¡Hasta luego!");
                    return;
                }
                default -> System.out.println("Opción no válida. Intente de nuevo.");
            }
        }   while (true);
    }

}
