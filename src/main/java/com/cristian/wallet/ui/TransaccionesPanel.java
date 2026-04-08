package com.cristian.wallet.ui;

import com.cristian.wallet.model.Account;
import com.cristian.wallet.model.Transaction;
import com.cristian.wallet.model.Currency;
import com.cristian.wallet.model.TipoTransaccion;
import com.cristian.wallet.service.IController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TransaccionesPanel extends JPanel {
    private final IController controller;
    private final JTable tabla;
    private final DefaultTableModel tableModel;
    private Runnable actualizarTodo;

    public TransaccionesPanel(IController controller) {
        this.controller = controller;
        this.actualizarTodo = null;
        this.setLayout(new java.awt.BorderLayout());

        // Tabla
        tableModel = new DefaultTableModel(new String[]{"ID", "Cuenta", "Tipo", "Monto", "Moneda", "Fecha"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabla de solo lectura
                }
        };
        tabla = new JTable(tableModel);
        tabla.getColumnModel().getColumn(0).setMinWidth(0); // ocultar columna ID
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);

        this.add(new javax.swing.JScrollPane(tabla), BorderLayout.CENTER);
            // Botón
        JButton btnCrear = new JButton("Nueva Transacción");
        btnCrear.addActionListener(e -> mostrarDialogoCrear());
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotones.add(btnCrear);
        this.add(panelBotones, BorderLayout.SOUTH);

        
        JButton btnTransferir = new JButton("Transferir Fondos");
        btnTransferir.addActionListener(e -> mostrarDialogoTransferir());
        panelBotones.add(btnTransferir, BorderLayout.SOUTH);

        JButton btnConsultar = new JButton("Consultar Transacción");
        btnConsultar.addActionListener(e -> abrirDetalleTransaccion());
        panelBotones.add(btnConsultar, BorderLayout.SOUTH);

        JButton btnEliminar = new JButton("Eliminar Transacción");
        btnEliminar.addActionListener(e -> mostrarDialogoEliminar());
        panelBotones.add(btnEliminar, BorderLayout.SOUTH);

        cargarTransacciones();
    }
    
    public void setRefestcartodo(Runnable actualizarTodo) {
        this.actualizarTodo = actualizarTodo;
    }

    public void cargarTransacciones() {
        tableModel.setRowCount(0); // limpiar tabla
        List<Transaction> transacciones = controller.getTransactions();
        for (Transaction transaccion : transacciones) {
            Account cuenta = controller.getAccountByTransaction(transaccion.getTransactionID());
            String nombreCuenta = cuenta != null ? cuenta.getAccountName() : "Desconocida";
            tableModel.addRow(new Object[]{
                transaccion.getTransactionID(),
                nombreCuenta,
                transaccion.getTransactionType().name(),
                transaccion.getAmount(),
                transaccion.getCurrency().name(),
                transaccion.getDate()
            });
        }
    }

    private void mostrarDialogoCrear() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nueva Transacción", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        // Cuenta
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Cuenta:"), gbc);
        gbc.gridx = 1;
        List<Account> cuentas = controller.getAccounts();
        if (cuentas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe crear una cuenta primero", "Sin cuentas", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JComboBox<Account> comboCuenta = new JComboBox<>(cuentas.toArray(new Account[0]));
        comboCuenta.setRenderer((list, value, index, isSelected, cellHasFocus) -> 
            new JLabel(value != null ? value.getAccountName() : ""));
        dialog.add(comboCuenta, gbc);

        // Tipo
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        JComboBox<TipoTransaccion> comboTipo = new JComboBox<>(TipoTransaccion.values());
        dialog.add(comboTipo, gbc);

        // Monto
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Monto:"), gbc);
        gbc.gridx = 1;
        JTextField txtMonto = new JTextField(10);
        dialog.add(txtMonto, gbc);

        // Descripción
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        JTextArea txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        dialog.add(new JScrollPane(txtDescripcion), gbc);

        // Botón confirmar
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JButton btnConfirmar = new JButton("Crear");
        btnConfirmar.addActionListener(e -> {
            try {
                Account cuenta = (Account) comboCuenta.getSelectedItem();
                TipoTransaccion tipo = (TipoTransaccion) comboTipo.getSelectedItem();
                double monto = Double.parseDouble(txtMonto.getText());
                if (monto <= 0) {
                    JOptionPane.showMessageDialog(dialog, "El monto debe ser mayor a cero", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }      
                String descripcion = txtDescripcion.getText();
                controller.createTransaction(tipo, monto, descripcion, cuenta.getCurrency(), cuenta.getAccountID());
                cargarTransacciones();
                actualizarTodo.run();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Monto inválido", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(btnConfirmar, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void abrirDetalleTransaccion() {
        int selectedRow = tabla.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una transacción para consultar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int transactionID = (int) tableModel.getValueAt(selectedRow, 0);
        Transaction transaccion = controller.getTransaction(transactionID);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Detalle de Transacción", true);
        dialog.setLayout(new BorderLayout(10, 10));

        // ===== PANEL INFO =====
        JPanel panelInfo = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panelInfo.add(new JLabel("Cuenta:"), gbc);
        gbc.gridx = 1;
        panelInfo.add(new JLabel(transaccion.getAccount().getAccountName()), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelInfo.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        panelInfo.add(new JLabel(transaccion.getTransactionType().name()), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panelInfo.add(new JLabel("Monto:"), gbc);
        gbc.gridx = 1;
        panelInfo.add(new JLabel(String.format("%.2f", transaccion.getAmount())), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panelInfo.add(new JLabel("Fecha:"), gbc);
        gbc.gridx = 1;
        panelInfo.add(new JLabel(transaccion.getDate().toString()), gbc);

        dialog.add(panelInfo, BorderLayout.NORTH);

        // ===== DESCRIPCIÓN =====
        JTextArea txtDescripcion = new JTextArea(transaccion.getDescription());
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setEditable(false);
        txtDescripcion.setBorder(BorderFactory.createTitledBorder("Descripción"));

        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        scrollDescripcion.setPreferredSize(new Dimension(350, 120));

        dialog.add(scrollDescripcion, BorderLayout.CENTER);

        // ===== BOTÓN CERRAR =====
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dialog.dispose());

        JPanel panelBoton = new JPanel();
        panelBoton.add(btnCerrar);

        dialog.add(panelBoton, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void mostrarDialogoEliminar() {
        int selectedRow = tabla.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una transacción para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int transactionID = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "¿Confirma que desea eliminar esta transacción?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteTransaction(transactionID);
            actualizarTodo.run();
            cargarTransacciones();
        }
    }

    private void mostrarDialogoTransferir() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Transferir Fondos", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        List<Account> cuentas = controller.getAccounts();
        if (cuentas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe crear una cuenta primero", "Sin cuentas", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Account[] cuentasArray = cuentas.toArray(new Account[0]);

        // Renderer para mostrar nombre de cuenta
        ListCellRenderer<Account> renderer = (list, value, index, isSelected, cellHasFocus) ->
            new JLabel(value != null ? value.getAccountName() + " (" + value.getCurrency() + ")" : "");

        // Cuenta origen
        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("Cuenta origen:"), gbc);
        gbc.gridx = 1;
        JComboBox<Account> comboOrigen = new JComboBox<>(cuentasArray);
        comboOrigen.setRenderer(renderer);
        dialog.add(comboOrigen, gbc);

        // Cuenta destino
        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("Cuenta destino:"), gbc);
        gbc.gridx = 1;
        JComboBox<Account> comboDestino = new JComboBox<>(cuentasArray);
        comboDestino.setRenderer(renderer);
        dialog.add(comboDestino, gbc);

        // Monto
        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("Monto:"), gbc);
        gbc.gridx = 1;
        JTextField txtMonto = new JTextField(10);
        dialog.add(txtMonto, gbc);

        // Descripción
        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        JTextArea txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        scrollDesc.setPreferredSize(new Dimension(200, 70));
        dialog.add(scrollDesc, gbc);

        // Botón confirmar
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton btnConfirmar = new JButton("Transferir");
        btnConfirmar.addActionListener(e -> {
            try {
                Account origen = (Account) comboOrigen.getSelectedItem();
                Account destino = (Account) comboDestino.getSelectedItem();
                if (origen.getAccountID() == destino.getAccountID()) {
                    JOptionPane.showMessageDialog(dialog, "Las cuentas deben ser distintas", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                double monto = Double.parseDouble(txtMonto.getText());
                if (monto <= 0) {
                    JOptionPane.showMessageDialog(dialog, "El monto debe ser mayor a cero", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String descripcion = txtDescripcion.getText().trim();
                controller.transferFunds(origen.getAccountID(), destino.getAccountID(), monto, origen.getCurrency(), descripcion);
                cargarTransacciones();
                actualizarTodo.run();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Monto inválido", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(btnConfirmar, gbc);

        dialog.pack();
        dialog.setResizable(false);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
}