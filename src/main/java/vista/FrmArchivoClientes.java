package vista;

import dao.ClienteDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import modelo.ClienteDTO;

public class FrmArchivoClientes extends javax.swing.JInternalFrame {

    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private JLabel lblRegistros;
    private ClienteDAO clienteDAO;

    public FrmArchivoClientes() {
        clienteDAO = new ClienteDAO();
        configurarVentana();
        inicializarComponentes();
        cargarClientes();
    }

    private void configurarVentana() {
        setTitle("ARCHIVO DE CLIENTES");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(0, 0, 128)); // Azul clásico terminal
        setLayout(new BorderLayout());
    }

    private void inicializarComponentes() {
        // --- PANEL SUPERIOR (Título y contador de registros) ---
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(new Color(0, 0, 128));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        JLabel lblTitulo = new JLabel("ARCHIVO DE CLIENTES");
        lblTitulo.setFont(new Font("Monospaced", Font.BOLD, 18));
        lblTitulo.setForeground(Color.YELLOW);

        lblRegistros = new JLabel("Registros: 0");
        lblRegistros.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblRegistros.setForeground(Color.GREEN);

        panelSuperior.add(lblTitulo, BorderLayout.WEST);
        panelSuperior.add(lblRegistros, BorderLayout.EAST);
        add(panelSuperior, BorderLayout.NORTH);

        // --- TABLA CENTRAL (R.U.T. y NOMBRE DEL CLIENTE) ---
        String[] columnas = {"R.U.T.", "NOMBRE DEL CLIENTE"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Solo lectura tipo listado
            }
        };

        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setFont(new Font("Monospaced", Font.BOLD, 16));
        tablaClientes.setRowHeight(25);
        tablaClientes.setBackground(new Color(0, 0, 170)); // Azul brillante estilo consola
        tablaClientes.setForeground(Color.WHITE);
        tablaClientes.setSelectionBackground(Color.CYAN);
        tablaClientes.setSelectionForeground(Color.BLACK);

        // Cabecera de la tabla
        tablaClientes.getTableHeader().setFont(new Font("Monospaced", Font.BOLD, 16));
        tablaClientes.getTableHeader().setBackground(new Color(0, 0, 100));
        tablaClientes.getTableHeader().setForeground(Color.YELLOW);

        JScrollPane scrollPane = new JScrollPane(tablaClientes);
        scrollPane.getViewport().setBackground(new Color(0, 0, 170));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.CYAN, 1));

        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBackground(new Color(0, 0, 128));
        panelCentro.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        panelCentro.add(scrollPane, BorderLayout.CENTER);
        add(panelCentro, BorderLayout.CENTER);

        // --- PANEL INFERIOR (Barra de Acciones / Teclas rápidas) ---
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelInferior.setBackground(new Color(180, 0, 0)); // Barra roja clásica inferior
        panelInferior.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel lblAcciones = new JLabel("(Esc) Salir   |   (Ins) Insertar   |   (Supr) Eliminar   |   (M) Modificar   |   (B) Buscar   |   (V) Ver Ficha");
        lblAcciones.setFont(new Font("Monospaced", Font.BOLD, 13));
        lblAcciones.setForeground(Color.WHITE);
        panelInferior.add(lblAcciones);
        add(panelInferior, BorderLayout.SOUTH);

        // --- EVENTOS DE TECLADO (Atajos idénticos al sistema original) ---
        KeyAdapter listenerTeclado = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE:
                        dispose();
                        break;
                    case KeyEvent.VK_INSERT:
                        insertarCliente();
                        break;
                    case KeyEvent.VK_DELETE:
                        eliminarCliente();
                        break;
                    case KeyEvent.VK_B:
                        buscarCliente();
                        break;
                    case KeyEvent.VK_M:
                        modificarCliente();
                        break;
                    case KeyEvent.VK_V:
                    case KeyEvent.VK_ENTER:
                        verEstadisticasCliente();
                        break;
                    default:
                        break;
                }
            }
        };

        this.addKeyListener(listenerTeclado);
        tablaClientes.addKeyListener(listenerTeclado);
        setFocusable(true);
    }

    private void cargarClientes() {
        modeloTabla.setRowCount(0);
        List<ClienteDTO> lista = clienteDAO.obtenerTodosLosClientes();
        for (ClienteDTO c : lista) {
            // Mostramos el RUT y el nombre completo formateado por apellido
            modeloTabla.addRow(new Object[]{c.getRut(), c.getNombreCompletoFormato()});
        }
        lblRegistros.setText("Registros: " + lista.size());
    }

    private void insertarCliente() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 8, 12));
        panel.setBackground(new Color(0, 0, 128));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // FUENTE GRANDE Y CLARA PARA TODO (Tamaño 16)
        Font fuenteGrande = new Font("Monospaced", Font.BOLD, 16);
        Color colorTexto = Color.YELLOW;

        JTextField txtRutIns = new JTextField();
        txtRutIns.setFont(fuenteGrande);

        JTextField txtPaternoIns = new JTextField();
        txtPaternoIns.setFont(fuenteGrande);

        JTextField txtMaternoIns = new JTextField();
        txtMaternoIns.setFont(fuenteGrande);

        JTextField txtNombresIns = new JTextField();
        txtNombresIns.setFont(fuenteGrande);

        JTextField txtSaldoIns = new JTextField("0");
        txtSaldoIns.setFont(fuenteGrande);

        JTextField txtCupoIns = new JTextField("1000000");
        txtCupoIns.setFont(fuenteGrande);

        JLabel lblR = new JLabel(" R.U.T. :");
        lblR.setForeground(colorTexto);
        lblR.setFont(fuenteGrande);

        JLabel lblPat = new JLabel(" APE. PATERNO :");
        lblPat.setForeground(colorTexto);
        lblPat.setFont(fuenteGrande);

        JLabel lblMat = new JLabel(" APE. MATERNO :");
        lblMat.setForeground(colorTexto);
        lblMat.setFont(fuenteGrande);

        JLabel lblNom = new JLabel(" NOMBRES :");
        lblNom.setForeground(colorTexto);
        lblNom.setFont(fuenteGrande);

        JLabel lblS = new JLabel(" SALDO :");
        lblS.setForeground(colorTexto);
        lblS.setFont(fuenteGrande);

        JLabel lblC = new JLabel(" CUPO :");
        lblC.setForeground(colorTexto);
        lblC.setFont(fuenteGrande);

        panel.add(lblR);
        panel.add(txtRutIns);
        panel.add(lblPat);
        panel.add(txtPaternoIns);
        panel.add(lblMat);
        panel.add(txtMaternoIns);
        panel.add(lblNom);
        panel.add(txtNombresIns);
        panel.add(lblS);
        panel.add(txtSaldoIns);
        panel.add(lblC);
        panel.add(txtCupoIns);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(600, 420));
        scrollPane.setBorder(null);

        int resultado = JOptionPane.showConfirmDialog(
                this, scrollPane, "INGRESAR NUEVO CLIENTE",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (resultado == JOptionPane.OK_OPTION) {
            String rut = txtRutIns.getText().trim();
            String paterno = txtPaternoIns.getText().trim().toUpperCase();
            String materno = txtMaternoIns.getText().trim().toUpperCase();
            String nombres = txtNombresIns.getText().trim().toUpperCase();

            if (rut.isEmpty() || paterno.isEmpty() || nombres.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El RUT, el Apellido Paterno y los Nombres son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                java.math.BigDecimal saldo = new java.math.BigDecimal(txtSaldoIns.getText().trim());
                java.math.BigDecimal cupo = new java.math.BigDecimal(txtCupoIns.getText().trim());

                ClienteDTO nuevoCliente = new ClienteDTO(rut, paterno, materno, nombres);
                nuevoCliente.setSaldoActual(saldo);
                nuevoCliente.setCupo(cupo);

                boolean exito = clienteDAO.insertarCliente(nuevoCliente);

                if (exito) {
                    JOptionPane.showMessageDialog(this, "¡Cliente registrado correctamente!");
                    cargarClientes();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo guardar el cliente en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Los campos de Saldo y Cupo deben ser numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila >= 0) {
            String rut = (String) modeloTabla.getValueAt(fila, 0);
            int opcion = JOptionPane.showConfirmDialog(this, "¿Desea eliminar al cliente RUT: " + rut + "?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
            if (opcion == JOptionPane.YES_OPTION) {
                boolean exito = clienteDAO.eliminarCliente(rut);
                if (exito) {
                    cargarClientes();
                    JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente.");
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar el cliente.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente de la lista.");
        }
    }

    private void modificarCliente() {
        int filaSeleccionada = tablaClientes.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente de la lista para modificar, compadre.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String rutSeleccionado = tablaClientes.getValueAt(filaSeleccionada, 0).toString();
        ClienteDTO clienteActual = clienteDAO.obtenerClientePorRut(rutSeleccionado);

        if (clienteActual == null) {
            JOptionPane.showMessageDialog(this, "No se encontró la información del cliente en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(6, 2, 8, 12));
        panel.setBackground(new Color(0, 0, 128));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // FUENTE GRANDE Y CLARA PARA TODO (Tamaño 16)
        Font fuenteGrande = new Font("Monospaced", Font.BOLD, 16);
        Color colorTexto = Color.YELLOW;

        JTextField txtRutMod = new JTextField(clienteActual.getRut());
        txtRutMod.setFont(fuenteGrande);
        txtRutMod.setEditable(false);
        txtRutMod.setBackground(new Color(220, 220, 220));

        JTextField txtPaternoMod = new JTextField(clienteActual.getPaterno());
        txtPaternoMod.setFont(fuenteGrande);

        JTextField txtMaternoMod = new JTextField(clienteActual.getMaterno());
        txtMaternoMod.setFont(fuenteGrande);

        JTextField txtNombresMod = new JTextField(clienteActual.getNombres());
        txtNombresMod.setFont(fuenteGrande);

        JTextField txtSaldoMod = new JTextField(clienteActual.getSaldoActual() != null ? clienteActual.getSaldoActual().toString() : "0");
        txtSaldoMod.setFont(fuenteGrande);

        JTextField txtCupoMod = new JTextField(clienteActual.getCupo() != null ? clienteActual.getCupo().toString() : "1000000");
        txtCupoMod.setFont(fuenteGrande);

        JLabel lblR = new JLabel(" R.U.T. (Fijo) :");
        lblR.setForeground(colorTexto);
        lblR.setFont(fuenteGrande);

        JLabel lblPat = new JLabel(" APE. PATERNO :");
        lblPat.setForeground(colorTexto);
        lblPat.setFont(fuenteGrande);

        JLabel lblMat = new JLabel(" APE. MATERNO :");
        lblMat.setForeground(colorTexto);
        lblMat.setFont(fuenteGrande);

        JLabel lblNom = new JLabel(" NOMBRES :");
        lblNom.setForeground(colorTexto);
        lblNom.setFont(fuenteGrande);

        JLabel lblS = new JLabel(" SALDO :");
        lblS.setForeground(colorTexto);
        lblS.setFont(fuenteGrande);

        JLabel lblC = new JLabel(" CUPO :");
        lblC.setForeground(colorTexto);
        lblC.setFont(fuenteGrande);

        panel.add(lblR);
        panel.add(txtRutMod);
        panel.add(lblPat);
        panel.add(txtPaternoMod);
        panel.add(lblMat);
        panel.add(txtMaternoMod);
        panel.add(lblNom);
        panel.add(txtNombresMod);
        panel.add(lblS);
        panel.add(txtSaldoMod);
        panel.add(lblC);
        panel.add(txtCupoMod);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(600, 420)); // Un poco más amplio para que calce bien la letra grande
        scrollPane.setBorder(null);

        int resultado = JOptionPane.showConfirmDialog(
                this, scrollPane, "MODIFICAR CLIENTE",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (resultado == JOptionPane.OK_OPTION) {
            String paterno = txtPaternoMod.getText().trim().toUpperCase();
            String materno = txtMaternoMod.getText().trim().toUpperCase();
            String nombres = txtNombresMod.getText().trim().toUpperCase();

            if (paterno.isEmpty() || nombres.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El Apellido Paterno y los Nombres no pueden quedar vacíos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                java.math.BigDecimal saldo = new java.math.BigDecimal(txtSaldoMod.getText().trim());
                java.math.BigDecimal cupo = new java.math.BigDecimal(txtCupoMod.getText().trim());

                ClienteDTO clienteModificado = new ClienteDTO(rutSeleccionado, paterno, materno, nombres);
                clienteModificado.setSaldoActual(saldo);
                clienteModificado.setCupo(cupo);

                boolean exito = clienteDAO.actualizarCliente(clienteModificado);

                if (exito) {
                    JOptionPane.showMessageDialog(this, "¡Cliente modificado correctamente!");
                    cargarClientes();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo actualizar el registro en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Los campos de Saldo y Cupo deben ser numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buscarCliente() {
        String rutBuscado = JOptionPane.showInputDialog(this, "Ingrese R.U.T. a buscar:");
        if (rutBuscado != null && !rutBuscado.trim().isEmpty()) {
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                String rutTabla = (String) modeloTabla.getValueAt(i, 0);
                if (rutTabla.equalsIgnoreCase(rutBuscado.trim())) {
                    tablaClientes.setRowSelectionInterval(i, i);
                    tablaClientes.scrollRectToVisible(tablaClientes.getCellRect(i, 0, true));
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Cliente no encontrado.");
        }
    }

    private void verEstadisticasCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila >= 0) {
            String rut = (String) modeloTabla.getValueAt(fila, 0);
            // Abre la ventana de estadísticas que armamos antes para ese RUT
            FrmEstadisticasClientes frmEstadisticas = new FrmEstadisticasClientes(rut);
            frmEstadisticas.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para ver su ficha/estadísticas.");
        }
    }
}
