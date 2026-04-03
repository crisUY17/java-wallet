package com.cristian.wallet;

import com.cristian.wallet.dao.AccountDAO;
import com.cristian.wallet.dao.DatabaseManager;
import com.cristian.wallet.dao.TransactionDAO;
import com.cristian.wallet.dao.IAccountDAO;
import com.cristian.wallet.dao.ITransactionDAO;
import com.cristian.wallet.service.Controller;
import com.cristian.wallet.service.IController;
import com.cristian.wallet.ui.ConsoleUI;

/*
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Java Wallet");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setVisible(true);
        });
    }
}
 */

public class Main {
    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        IAccountDAO accountDAO = new AccountDAO();
        ITransactionDAO transactionDAO = new TransactionDAO(accountDAO);
        IController controller = new Controller(accountDAO, transactionDAO);
        ConsoleUI ui = new ConsoleUI(controller);
        ui.start();
    }
}