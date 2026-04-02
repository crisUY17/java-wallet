package com.cristian.wallet;

import com.cristian.wallet.dao.AccountDAO;
import com.cristian.wallet.dao.TransactionDAO;
import com.cristian.wallet.service.Controller;
import com.cristian.wallet.ui.ConsoleUI;
import com.cristian.wallet.ui.IController;

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
        AccountDAO accountDAO = new AccountDAO();
        TransactionDAO transactionDAO = new TransactionDAO();
        IController controller = new Controller(accountDAO, transactionDAO);
        ConsoleUI ui = new ConsoleUI(controller);
        ui.start();
    }
}