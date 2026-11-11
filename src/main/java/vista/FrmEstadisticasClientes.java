package vista;

import dao.ClienteDAO;
import modelo.EstadisticasClienteDTO;
import javax.swing.*;
import java.awt.*;

public class FrmEstadisticasClientes extends javax.swing.JInternalFrame  {

    private JTextField txtRut;
    private JLabel lblNombre, lblSaldo;
    private JLabel lblVecesCompro, lblTotalCompro, lblPromCompro, lblUltimaCompra, lblMaxCompro;
    private JLabel lblVecesPago, lblTotalPago, lblPromPago, lblUltimoPago, lblMaxPago;
    private ClienteDAO clienteDAO = new ClienteDAO();

    public FrmEstadisticasClientes() {
        super("ESTADÍSTICAS Y CUENTA CORRIENTE - CLIENTES");
        setSize(800, 560);
        //setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        Color fondoAzul = new Color(0, 0, 170);
        Color azulEncabezado = new Color(0, 128, 128);

        // --- ENCABEZADO Y BUSCADOR DE RUT ---
        JPanel panelTop = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelTop.setBackground(azulEncabezado);

        JLabel lblTitulo = new JLabel("ESTADÍSTICAS DEL CLIENTE");
        lblTitulo.setFont(new Font("Monospaced", Font.BOLD, 18));
        lblTitulo.setForeground(Color.YELLOW);

        panelTop.add(lblTitulo);

        // Input para buscar RUT directamente
        JLabel lblRutTitle = new JLabel(" | RUT:");
        lblRutTitle.setFont(new Font("Monospaced", Font.BOLD, 16));
        lblRutTitle.setForeground(Color.WHITE);
        panelTop.add(lblRutTitle);

        txtRut = new JTextField(12);
        txtRut.setFont(new Font("Monospaced", Font.BOLD, 16));
        panelTop.add(txtRut);

        JButton btnBuscar = new JButton("BUSCAR");
        btnBuscar.setFont(new Font("Monospaced", Font.BOLD, 16));
        panelTop.add(btnBuscar);

        add(panelTop, BorderLayout.NORTH);

        // --- PANEL CENTRAL (ESTILO TERMINAL RETRO) ---
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBackground(fondoAzul);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Nombre del Cliente
        JPanel panelNombre = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelNombre.setBackground(fondoAzul);
        JLabel lblNomTitle = new JLabel("NOMBRE : ");
        lblNomTitle.setFont(new Font("Monospaced", Font.BOLD, 16));
        lblNomTitle.setForeground(Color.CYAN);

        lblNombre = new JLabel("INGRESE UN RUT Y PRESIONE BUSCAR...");
        lblNombre.setFont(new Font("Monospaced", Font.BOLD, 16));
        lblNombre.setForeground(Color.WHITE);
        panelNombre.add(lblNomTitle);
        panelNombre.add(lblNombre);
        panelCentral.add(panelNombre);
        panelCentral.add(Box.createVerticalStrut(10));

        // Saldo Actual
        JPanel panelSaldo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSaldo.setBackground(fondoAzul);
        JLabel lblSalTitle = new JLabel("Saldo Actual             : $");
        lblSalTitle.setFont(new Font("Monospaced", Font.BOLD, 16));
        lblSalTitle.setForeground(Color.CYAN);

        lblSaldo = new JLabel("0");
        lblSaldo.setFont(new Font("Monospaced", Font.BOLD, 16));
        lblSaldo.setForeground(Color.GREEN);
        panelSaldo.add(lblSalTitle);
        panelSaldo.add(lblSaldo);
        panelCentral.add(panelSaldo);
        panelCentral.add(Box.createVerticalStrut(14));

        // SECCIÓN COMPRAS
        JPanel panelCompras = new JPanel(new GridLayout(5, 2, 5, 5));
        panelCompras.setBackground(fondoAzul);
        panelCompras.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.CYAN), " COMPRAS ", 0, 0,
                new Font("Monospaced", Font.BOLD, 16), Color.YELLOW));

        lblVecesCompro = crearLabelDato(panelCompras, "Veces que ha comprado    : ");
        lblTotalCompro = crearLabelDatoMoneda(panelCompras, "Total de compras        : $");
        lblPromCompro = crearLabelDatoMoneda(panelCompras, "Promedio de compra      : $");
        lblUltimaCompra = crearLabelDato(panelCompras, "Última compra           : ");
        lblMaxCompro = crearLabelDatoMoneda(panelCompras, "Máximo comprado         : $");

        panelCentral.add(panelCompras);
        panelCentral.add(Box.createVerticalStrut(10));

        // SECCIÓN PAGOS
        JPanel panelPagos = new JPanel(new GridLayout(5, 2, 5, 5));
        panelPagos.setBackground(fondoAzul);
        panelPagos.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.CYAN), " PAGOS ", 0, 0,
                new Font("Monospaced", Font.BOLD, 16), Color.YELLOW));

        lblVecesPago = crearLabelDato(panelPagos, "Veces que ha pagado     : ");
        lblTotalPago = crearLabelDatoMoneda(panelPagos, "Total de pagos          : $");
        lblPromPago = crearLabelDatoMoneda(panelPagos, "Promedio de pago        : $");
        lblUltimoPago = crearLabelDato(panelPagos, "Último pago             : ");
        lblMaxPago = crearLabelDatoMoneda(panelPagos, "Máximo pagado           : $");

        panelCentral.add(panelPagos);
        add(panelCentral, BorderLayout.CENTER);

        // --- BARRA INFERIOR ---
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelInferior.setBackground(new Color(180, 0, 0));
        JLabel lblInfo = new JLabel("[ ESC ] Salir / Cerrar Ventana");
        lblInfo.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblInfo.setForeground(Color.WHITE);
        panelInferior.add(lblInfo);
        add(panelInferior, BorderLayout.SOUTH);

        // --- ACCIONES DE BUSCAR Y TECLA ESC ---
        btnBuscar.addActionListener(e -> realizarBusqueda());
        txtRut.addActionListener(e -> realizarBusqueda()); // Enter en el input

        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    // 2. Constructor que recibe el RUT (para cuando viene desde el listado de clientes)
    public FrmEstadisticasClientes(String rut) {
        this();
        cargarDatosCliente(rut);
    }

    private JLabel crearLabelDato(JPanel panel, String textoEtiqueta) {
        JLabel lblT = new JLabel(textoEtiqueta);
        lblT.setFont(new Font("Monospaced", Font.BOLD, 15));
        lblT.setForeground(Color.WHITE);
        panel.add(lblT);

        JLabel lblV = new JLabel("0");
        lblV.setFont(new Font("Monospaced", Font.BOLD, 15));
        lblV.setForeground(Color.GREEN);
        panel.add(lblV);
        return lblV;
    }

    private JLabel crearLabelDatoMoneda(JPanel panel, String textoEtiqueta) {
        return crearLabelDato(panel, textoEtiqueta);
    }

    private void realizarBusqueda() {
        String rutBuscado = txtRut.getText().trim();
        if (rutBuscado.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese un RUT.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            ClienteDAO dao = new ClienteDAO();
            EstadisticasClienteDTO stats = dao.obtenerEstadisticasPorRut(rutBuscado);

            if (stats != null) {
                lblNombre.setText(stats.getNombre() != null ? stats.getNombre() : "SIN NOMBRE");
                lblSaldo.setText(String.format("%,.2f", stats.getSaldoActual()));
                lblVecesCompro.setText(String.valueOf(stats.getVecesCompro()));
                lblTotalCompro.setText(String.format("%,.2f", stats.getTotalCompro()));
                lblPromCompro.setText(String.format("%,.2f", stats.getPromCompro()));
                lblUltimaCompra.setText(stats.getUltimaCompra() != null ? stats.getUltimaCompra() : "N/A");
                lblMaxCompro.setText(String.format("%,.2f", stats.getMaxCompro()));
                lblVecesPago.setText(String.valueOf(stats.getVecesPago()));
                lblTotalPago.setText(String.format("%,.2f", stats.getTotalPago()));
                lblPromPago.setText(String.format("%,.2f", stats.getPromPago()));
                lblUltimoPago.setText(stats.getUltimoPago() != null ? stats.getUltimoPago() : "N/A");
                lblMaxPago.setText(String.format("%,.2f", stats.getMaxPago()));
            } else {
                lblNombre.setText("CLIENTE NO ENCONTRADO");
                lblSaldo.setText("0");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al consultar la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarDatosCliente(String rut) {
        // Aseguramos que el campo de texto del RUT muestre el RUT que llegó por parámetro
        if (txtRut != null) {
            txtRut.setText(rut);
        }

        EstadisticasClienteDTO stats = clienteDAO.obtenerEstadisticasPorRut(rut);

        if (stats != null) {
            // Pintamos el nombre y saldo principal
            if (lblNombre != null) {
                lblNombre.setText(stats.getNombre());
            }
            if (lblSaldo != null) {
                lblSaldo.setText("$ " + String.format("%,.2f", stats.getSaldoActual()));
            }

            // --- BLOQUE DE COMPRAS ---
            if (lblVecesCompro != null) {
                lblVecesCompro.setText(String.valueOf(stats.getVecesCompro()));
            }
            if (lblTotalCompro != null) {
                lblTotalCompro.setText("$ " + String.format("%,.2f", stats.getTotalCompro()));
            }
            if (lblPromCompro != null) {
                lblPromCompro.setText("$ " + String.format("%,.2f", stats.getPromCompro()));
            }
            if (lblUltimaCompra != null) {
                lblUltimaCompra.setText(stats.getUltimaCompra());
            }
            if (lblMaxCompro != null) {
                lblMaxCompro.setText("$ " + String.format("%,.2f", stats.getMaxCompro()));
            }

            // --- BLOQUE DE PAGOS ---
            if (lblVecesPago != null) {
                lblVecesPago.setText(String.valueOf(stats.getVecesPago()));
            }
            if (lblTotalPago != null) {
                lblTotalPago.setText("$ " + String.format("%,.2f", stats.getTotalPago()));
            }
            if (lblPromPago != null) {
                lblPromPago.setText("$ " + String.format("%,.2f", stats.getPromPago()));
            }
            if (lblUltimoPago != null) {
                lblUltimoPago.setText(stats.getUltimoPago());
            }
            if (lblMaxPago != null) {
                lblMaxPago.setText("$ " + String.format("%,.2f", stats.getMaxPago()));
            }
        } else {
            JOptionPane.showMessageDialog(this, "No se encontraron estadísticas para el RUT: " + rut);
        }
    }
}
