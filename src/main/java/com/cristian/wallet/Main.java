package com.cristian.wallet;

import java.sql.Connection;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.cristian.wallet.dao.AccountDAO;
import com.cristian.wallet.dao.DatabaseManager;
import com.cristian.wallet.dao.TransactionDAO;
import com.cristian.wallet.dao.IAccountDAO;
import com.cristian.wallet.dao.ITransactionDAO;
import com.cristian.wallet.service.Controller;
import com.cristian.wallet.service.IController;
import com.cristian.wallet.ui.SwingUI;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseManager.initializeDatabase();
            Connection conn = DatabaseManager.getConnection();
            IAccountDAO accountDAO = new AccountDAO(conn);
            ITransactionDAO transactionDAO = new TransactionDAO(accountDAO, conn);
            IController controller = new Controller(accountDAO, transactionDAO);

            SwingUtilities.invokeLater(() -> {
                JFrame frame = new JFrame("Mi Billetera");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setContentPane(new SwingUI(controller));
                frame.setSize(800, 600);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            });
        } catch (Exception e) {
            System.out.println("Error en la aplicación: " + e.getMessage());
        }
    }
}