package com.cristian.wallet.ui;

import com.cristian.wallet.model.Account;
import com.cristian.wallet.model.Currency;
import com.cristian.wallet.service.IController;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CuentasPanel extends JPanel {

    private final IController controller;
    private final JTable tabla;
    private final DefaultTableModel tableModel;
    private Runnable actualizarTodo;

    public CuentasPanel(IController controller) {
        this.controller = controller;
        this.actualizarTodo = null;
        this.setLayout(new BorderLayout());

        // Tabla
        tableModel = new DefaultTableModel(new String[]{"ID", "Nombre", "Descripción", "Moneda"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabla de solo lectura
            }
        };
        tabla = new JTable(tableModel);
        tabla.getColumnModel().getColumn(0).setMinWidth(0); // ocultar columna ID
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(2).setMinWidth(0); // ocultar Descripción
        tabla.getColumnModel().getColumn(2).setMaxWidth(0);

        this.add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Botón agregar
        JButton btnNueva = new JButton("Nueva cuenta");
        btnNueva.addActionListener(e -> abrirDialogoNuevaCuenta());

        JButton btnConsultar = new JButton("Consultar cuenta");
        btnConsultar.addActionListener(e -> abrirDetalleCuenta());

        JButton btnElliminar = new JButton("Eliminar cuenta");
        btnElliminar.addActionListener(e -> abrirDialogoEliminarCuenta());

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));

        panelBotones.add(btnNueva);
        panelBotones.add(btnConsultar);
        panelBotones.add(btnElliminar);

        this.add(panelBotones, BorderLayout.SOUTH);

        cargarCuentas();
    }

    public void setRefestcartodo(Runnable actualizarTodo) {
        this.actualizarTodo = actualizarTodo;
    }

    public void cargarCuentas() {
        tableModel.setRowCount(0); // limpiar tabla
        List<Account> cuentas = controller.getAccounts();
        for (Account cuenta : cuentas) {
            tableModel.addRow(new Object[]{
                cuenta.getAccountID(),
                cuenta.getAccountName(),
                cuenta.getDescription(),
                cuenta.getCurrency().name()
            });
        }
    }

    private void abrirDialogoEliminarCuenta() {
        int selectedRow = tabla.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una cuenta para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int accountId = (int) tableModel.getValueAt(selectedRow, 0);
        String accountName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar la cuenta '" + accountName + "'?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            controller.deleteAccount(accountId);
            actualizarTodo.run();
            cargarCuentas();
        }
    }

    private void abrirDetalleCuenta() {
        int selectedRow = tabla.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una cuenta para ver", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int accountId = (int) tableModel.getValueAt(selectedRow, 0);
        Account cuenta = controller.getAccount(accountId);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), cuenta.getAccountName(), true);
        dialog.setLayout(new BorderLayout());

        // ===== PANEL SUPERIOR (Descripción + Info) =====
        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.Y_AXIS));

        // Descripción (soporta texto largo)
        JTextArea txtDescripcion = new JTextArea(cuenta.getDescription());
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setEditable(false);
        txtDescripcion.setOpaque(false);
        txtDescripcion.setBorder(BorderFactory.createTitledBorder("Descripción"));

        // Info de la cuenta
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInfo.add(new JLabel("Moneda: " + cuenta.getCurrency()));
        panelInfo.add(new JLabel("  |  "));
        panelInfo.add(new JLabel("Todo: " + String.format("%.2f", controller.getAccountBalance(accountId))));

        // Agregar al contenedor superior
        panelSuperior.add(txtDescripcion);
        panelSuperior.add(panelInfo);

        dialog.add(panelSuperior, BorderLayout.NORTH);

        // ===== HISTORIAL =====
        DefaultTableModel historialModel = new DefaultTableModel(
            new String[]{"ID", "Tipo", "Monto", "Fecha", "Descripción"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable tablaHistorial = new JTable(historialModel);
        tablaHistorial.getColumnModel().getColumn(0).setMinWidth(0);
        tablaHistorial.getColumnModel().getColumn(0).setMaxWidth(0);
        tablaHistorial.getColumnModel().getColumn(4).setMinWidth(0);
        tablaHistorial.getColumnModel().getColumn(4).setMaxWidth(0);

        controller.getTransactionsByAccount(accountId).forEach(t ->
            historialModel.addRow(new Object[]{
                t.getTransactionID(),
                t.getTransactionType().name(),
                String.format("%.2f", t.getAmount()),
                t.getDate(),
                t.getDescription()
            })
        );

        dialog.add(new JScrollPane(tablaHistorial), BorderLayout.CENTER);

    // ===== BOTÓN CERRAR =====
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dialog.dispose());

        JPanel panelBoton = new JPanel();
        panelBoton.add(btnCerrar);

        dialog.add(panelBoton, BorderLayout.SOUTH);

        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void abrirDialogoNuevaCuenta() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Nueva cuenta", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;

        // Campos
        JTextField txtNombre = new JTextField(20);
        JTextArea txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        scrollDescripcion.setPreferredSize(new Dimension(200, 70)); // tamaño fijo
        JComboBox<Currency> comboCurrency = new JComboBox<>(Currency.values());

        // Nombre
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        dialog.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        dialog.add(txtNombre, gbc);

        // Descripción — solo el scroll, no el textarea directo
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.weighty = 0;
        dialog.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        dialog.add(scrollDescripcion, gbc);

        // Moneda
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.weighty = 0;
        dialog.add(new JLabel("Moneda:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        dialog.add(comboCurrency, gbc);

        // Botón guardar
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                controller.createAccount(
                    nombre,
                    txtDescripcion.getText().trim(),
                    (Currency) comboCurrency.getSelectedItem()
                );
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error al crear la cuenta: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            cargarCuentas();
            dialog.dispose();
        });

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 0; gbc.weighty = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(btnGuardar, gbc);

        dialog.pack();
        dialog.setResizable(false); // no se puede redimensionar
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}