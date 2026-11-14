package vista.ventas;

import dao.ClienteDAO;
import dao.ProductoVentaDAO;
import dao.VendedorDAO;
import dao.VentaDAO;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import modelo.ItemVentaDTO;

/**
 * @author caill
 */
public class FrmVenta extends JInternalFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmVenta.class.getName());

    private JTextField txtVendedor;
    private JLabel lblNombreVendedor;

    private JTextField txtIngresoRapido;
    private JTextField txtCantidad;

    private JTable tablaVenta;
    private DefaultTableModel modeloTablaVenta;

    // Tabla flotante superpuesta para elegir el producto
    private JTable tablaBuscadorFlotante;
    private DefaultTableModel modeloBuscadorFlotante;
    private JScrollPane scrollBuscadorFlotante;

    private JLabel lblTotalGeneral;

    // Datos temporales del producto seleccionado
    private String codigoBarraSeleccionado = "";
    private String descripcionSeleccionada = "";
    private double precioUnitarioSeleccionado = 0.0;

    // Campos de texto para los datos del cliente
    private JTextField txtRutCliente = new JTextField(15);
    private JTextField txtNombreCliente = new JTextField(25);
    private JTextField txtDireccion = new JTextField(25);
    private JTextField txtComuna = new JTextField(15);
    private JTextField txtSector = new JTextField(15);
    private JTextField txtCelular = new JTextField(12);
    private JComboBox<String> cmbCondicionPago = new JComboBox<>(new String[]{"Contado", "Crédito"});
    private JTextField txtDias = new JTextField(5);
    private JTextField txtPlazo = new JTextField(10);
    private JTextField txtCiudad = new JTextField(15);
    private JTextField txtCodigoProducto = new JTextField(15);

    public FrmVenta() {
        // JInternalFrame: título, resizable, closable, maximizable, iconifiable
        super("Ingreso de Venta", true, true, true, true);
        putClientProperty("JInternalFrame.isPalette", Boolean.FALSE);

        initComponentsManual();
    }

    private void initComponentsManual() {
        setSize(1100, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        setContentPane(layeredPane);

        // ==========================================
        // PALETA DE COLORES Y FUENTES ESTILO MS-DOS
        // ==========================================
        Color dosBlue = new Color(0, 0, 128);       // Azul Clipper clásico
        Color dosDarkPanel = new Color(16, 16, 16);   // Fondo negro terminal
        Color dosYellow = Color.YELLOW;              // Amarillo brillante estilo DOS
        Color dosWhite = Color.WHITE;

        Font fuenteConsola = new Font("Consolas", Font.BOLD, 15);
        if (fuenteConsola.getFamily().equals("Dialog")) {
            fuenteConsola = new Font("Monospaced", Font.BOLD, 15);
        }

        layeredPane.setBackground(dosBlue);

        // ==========================================
        // 1. PANEL SUPERIOR (Cabecera)
        // ==========================================
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBounds(0, 0, 1100, 80);
        panelSuperior.setBackground(dosBlue);
        panelSuperior.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE));

        // Vendedor
        JPanel panelVendedor = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 12));
        panelVendedor.setOpaque(false);

        JLabel lblVende = new JLabel("Vende:");
        lblVende.setForeground(dosWhite);
        lblVende.setFont(fuenteConsola);

        txtVendedor = new JTextField(6);
        txtVendedor.setFont(fuenteConsola);
        txtVendedor.setBackground(Color.BLACK);
        txtVendedor.setForeground(dosYellow);
        txtVendedor.setCaretColor(dosYellow);
        txtVendedor.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        txtVendedor.setText("");
        txtVendedor.addActionListener(e -> {
            buscarVendedor();
            txtIngresoRapido.requestFocusInWindow();
            txtIngresoRapido.selectAll();
        });

        lblNombreVendedor = new JLabel("");
        lblNombreVendedor.setForeground(dosYellow);
        lblNombreVendedor.setFont(fuenteConsola);

        panelVendedor.add(lblVende);
        panelVendedor.add(txtVendedor);
        panelVendedor.add(lblNombreVendedor);

        // Título central
        JPanel panelTitulo = new JPanel();
        panelTitulo.setOpaque(false);
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel lblTituloPrincipal = new JLabel("INGRESO DE VENTA");
        lblTituloPrincipal.setForeground(dosYellow);
        lblTituloPrincipal.setFont(new Font(fuenteConsola.getFamily(), Font.BOLD, 17));
        lblTituloPrincipal.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblEmpresa = new JLabel("— CASA VICTORIA —");
        lblEmpresa.setForeground(dosWhite);
        lblEmpresa.setFont(fuenteConsola);
        lblEmpresa.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelTitulo.add(lblTituloPrincipal);
        panelTitulo.add(lblEmpresa);

        // Derecha (Fecha y Total)
        JPanel panelDerecha = new JPanel(new GridLayout(2, 1, 0, 2));
        panelDerecha.setOpaque(false);
        panelDerecha.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 25));

        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        JLabel lblFecha = new JLabel("Fecha: " + fechaHoy, JLabel.RIGHT);
        lblFecha.setForeground(dosWhite);
        lblFecha.setFont(fuenteConsola);

        JPanel panelTotalBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panelTotalBox.setOpaque(false);

        JLabel lblTextoTotal = new JLabel("TOTAL: $");
        lblTextoTotal.setForeground(dosWhite);
        lblTextoTotal.setFont(fuenteConsola);

        lblTotalGeneral = new JLabel("0");
        lblTotalGeneral.setForeground(dosYellow);
        lblTotalGeneral.setFont(new Font(fuenteConsola.getFamily(), Font.BOLD, 18));

        panelTotalBox.add(lblTextoTotal);
        panelTotalBox.add(lblTotalGeneral);

        panelDerecha.add(lblFecha);
        panelDerecha.add(panelTotalBox);

        panelSuperior.add(panelVendedor, BorderLayout.WEST);
        panelSuperior.add(panelTitulo, BorderLayout.CENTER);
        panelSuperior.add(panelDerecha, BorderLayout.EAST);

        layeredPane.add(panelSuperior, JLayeredPane.DEFAULT_LAYER);

        // ==========================================
        // 2. CENTRO (Tabla Principal de Venta)
        // ==========================================
        String[] columnasVenta = {"N°", "#Barra", "Descripción", "Cantidad", "Neto", "%", "Total"};
        modeloTablaVenta = new DefaultTableModel(columnasVenta, 0);

        tablaVenta = new JTable(modeloTablaVenta);
        tablaVenta.setFont(fuenteConsola);
        tablaVenta.setBackground(dosDarkPanel);
        tablaVenta.setForeground(dosWhite);
        tablaVenta.setGridColor(Color.DARK_GRAY);
        tablaVenta.setRowHeight(26);

        tablaVenta.getTableHeader().setFont(fuenteConsola);
        tablaVenta.getTableHeader().setBackground(dosBlue);
        tablaVenta.getTableHeader().setForeground(dosYellow);

        JScrollPane scrollTabla = new JScrollPane(tablaVenta);
        scrollTabla.setBounds(0, 80, 1100, 515);
        scrollTabla.getViewport().setBackground(dosDarkPanel);
        scrollTabla.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        layeredPane.add(scrollTabla, JLayeredPane.DEFAULT_LAYER);

        // ==========================================
        // 3. TABLA FLOTANTE DE BÚSQUEDA (Superpuesta y Ampliada)
        // ==========================================
        String[] columnasBuscador = {"Barra", "Descripción del Producto", "Precio Público"};
        modeloBuscadorFlotante = new DefaultTableModel(columnasBuscador, 0);

        tablaBuscadorFlotante = new JTable(modeloBuscadorFlotante);
        tablaBuscadorFlotante.setFont(fuenteConsola);
        tablaBuscadorFlotante.setBackground(dosDarkPanel);
        tablaBuscadorFlotante.setForeground(dosWhite);
        tablaBuscadorFlotante.setGridColor(Color.DARK_GRAY);
        tablaBuscadorFlotante.setRowHeight(26);

        tablaBuscadorFlotante.getTableHeader().setFont(fuenteConsola);
        tablaBuscadorFlotante.getTableHeader().setBackground(dosBlue);
        tablaBuscadorFlotante.getTableHeader().setForeground(dosYellow);

        scrollBuscadorFlotante = new JScrollPane(tablaBuscadorFlotante);
        scrollBuscadorFlotante.setBounds(150, 120, 800, 350);// Más ancho y más alto
        scrollBuscadorFlotante.getViewport().setBackground(dosDarkPanel);
        scrollBuscadorFlotante.setBorder(BorderFactory.createLineBorder(dosYellow, 2));
        scrollBuscadorFlotante.setVisible(false);
        layeredPane.add(scrollBuscadorFlotante, JLayeredPane.PALETTE_LAYER);

        tablaBuscadorFlotante.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    seleccionarProductoFlotante();
                }
            }
        });

        // ==========================================
        // 4. PANEL INFERIOR (Barra de comandos y atajos)
        // ==========================================
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBounds(0, 595, 1100, 105);
        panelInferior.setBackground(dosBlue);
        panelInferior.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.WHITE));

        JPanel panelInputsLinea = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        panelInputsLinea.setOpaque(false);

        JLabel lblIngreso = new JLabel("Barra/Prod:");
        lblIngreso.setForeground(dosWhite);
        lblIngreso.setFont(fuenteConsola);

        txtIngresoRapido = new JTextField(20);
        txtIngresoRapido.setFont(fuenteConsola);
        txtIngresoRapido.setBackground(Color.BLACK);
        txtIngresoRapido.setForeground(dosYellow);
        txtIngresoRapido.setCaretColor(dosYellow);
        txtIngresoRapido.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        txtIngresoRapido.addActionListener(e -> filtrarYMostrarBuscador());

        JLabel lblCant = new JLabel("uni:");
        lblCant.setForeground(dosWhite);
        lblCant.setFont(fuenteConsola);

        txtCantidad = new JTextField(5);
        txtCantidad.setFont(fuenteConsola);
        txtCantidad.setBackground(Color.BLACK);
        txtCantidad.setForeground(dosYellow);
        txtCantidad.setCaretColor(dosYellow);
        txtCantidad.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        txtCantidad.setText("1");
        txtCantidad.setEnabled(false);
        txtCantidad.addActionListener(e -> confirmarYAgregarItemTabla());

        panelInputsLinea.add(lblIngreso);
        panelInputsLinea.add(txtIngresoRapido);
        panelInputsLinea.add(lblCant);
        panelInputsLinea.add(txtCantidad);

        JLabel lblAtajos = new JLabel("  (Esc)apar    (F4): Eliminar    (F8): Cliente    (F10): Descto    (F11): PDF    (F12): Guardar");
        lblAtajos.setForeground(dosYellow);
        lblAtajos.setFont(new Font(fuenteConsola.getFamily(), Font.BOLD, 13));
        lblAtajos.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        panelInferior.add(panelInputsLinea, BorderLayout.NORTH);
        panelInferior.add(lblAtajos, BorderLayout.SOUTH);

        layeredPane.add(panelInferior, JLayeredPane.DEFAULT_LAYER);

        // ==========================================
        // LISTENER RESPONSIVO MÁGICO
        // ==========================================
        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = layeredPane.getWidth();
                int h = layeredPane.getHeight();

                if (w > 0 && h > 0) {
                    panelSuperior.setBounds(0, 0, w, 80);
                    scrollTabla.setBounds(0, 80, w, h - 185);
                    panelInferior.setBounds(0, h - 105, w, 105);

                    int buscadorW = Math.min(850, w - 150);
                    scrollBuscadorFlotante.setBounds((w - buscadorW) / 2, 100, buscadorW, Math.min(400, h - 180));
                }
            }
        });

        // ==========================================
        // ATAJOS GLOBALES DE TECLADO
        // ==========================================
        getRootPane().registerKeyboardAction(ev -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        atajof8();
        atajof4();

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), "guardarVentaF12");

        getRootPane().getActionMap().put("guardarVentaF12", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                finalizarYGuardarVenta();
            }
        });

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "exportarPDFF11");

        getRootPane().getActionMap().put("exportarPDFF11", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                exportarVentaPDF();
            }
        });

        buscarVendedor();
        txtIngresoRapido.requestFocusInWindow();
    }

    private void atajof8() {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0), "abrirBuscarCliente");

        getRootPane().getActionMap().put("abrirBuscarCliente", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                Window parentWindow = SwingUtilities.getWindowAncestor(FrmVenta.this);
                DlgClientes buscador = new DlgClientes(parentWindow);
                try {
                    ClienteDAO dao = new ClienteDAO();
                    List<Object[]> listaClientes = dao.cargarClientesDAO("");
                    buscador.cargarClientesEnTabla(listaClientes);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FrmVenta.this,
                            "Error al cargar los clientes: " + ex.getMessage(),
                            "Error de Base de Datos",
                            JOptionPane.ERROR_MESSAGE);
                }

                buscador.setVisible(true);

                String[] datosCliente = buscador.getClienteSeleccionado();
                if (datosCliente != null) {
                    if (datosCliente.length > 0) {
                        txtRutCliente.setText(datosCliente[0]);
                    }
                    if (datosCliente.length > 1) {
                        txtNombreCliente.setText(datosCliente[1]);
                    }
                    if (datosCliente.length > 2) {
                        txtDireccion.setText(datosCliente[2]);
                    }
                    if (datosCliente.length > 3) {
                        txtComuna.setText(datosCliente[3]);
                    }
                    if (datosCliente.length > 4) {
                        txtSector.setText(datosCliente[4]);
                    }
                    if (datosCliente.length > 5) {
                        txtCelular.setText(datosCliente[5]);
                    }
                    if (datosCliente.length > 6) {
                        txtCiudad.setText(datosCliente[6]);
                    }
                    txtIngresoRapido.requestFocusInWindow();
                }
            }
        });
    }

    private void atajof4() {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), "eliminarItemF4");

        getRootPane().getActionMap().put("eliminarItemF4", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int filaSeleccionada = tablaVenta.getSelectedRow();
                if (filaSeleccionada != -1) {
                    modeloTablaVenta.removeRow(filaSeleccionada);
                    for (int i = 0; i < modeloTablaVenta.getRowCount(); i++) {
                        modeloTablaVenta.setValueAt(i + 1, i, 0);
                    }
                    actualizarTotalGeneral();
                    JOptionPane.showMessageDialog(FrmVenta.this, "Ítem eliminado correctamente.");
                } else {
                    JOptionPane.showMessageDialog(FrmVenta.this, "Seleccione un ítem de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    private void exportarVentaPDF() {
        if (modeloTablaVenta.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay productos en la venta actual para exportar.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String vendedor = lblNombreVendedor.getText() != null ? lblNombreVendedor.getText() : "Desconocido";
        String total = lblTotalGeneral.getText();

        utilidades.PDFUtils.generarComprobanteVenta(vendedor, total, modeloTablaVenta);
    }

    private void buscarVendedor() {
        try {
            String textoCodigo = txtVendedor.getText().trim();
            if (textoCodigo.isEmpty()) {
                lblNombreVendedor.setText("INGRESE CÓDIGO");
                return;
            }

            int codigoVendedor = Integer.parseInt(textoCodigo);
            VendedorDAO dao = new VendedorDAO();
            String nombreVendedor = dao.obtenerNombreVendedor(codigoVendedor);

            if (nombreVendedor != null && !nombreVendedor.isEmpty()) {
                lblNombreVendedor.setText(nombreVendedor);
                txtIngresoRapido.requestFocusInWindow();
                txtIngresoRapido.selectAll();
            } else {
                lblNombreVendedor.setText("VENDEDOR NO ENCONTRADO");
                txtVendedor.requestFocusInWindow();
                txtVendedor.selectAll();
            }

        } catch (NumberFormatException e) {
            lblNombreVendedor.setText("CÓDIGO INVÁLIDO");
        } catch (Exception e) {
            lblNombreVendedor.setText("ERROR AL BUSCAR");
            System.err.println("Error en buscarVendedor: " + e.getMessage());
        }
    }

    private void confirmarYAgregarItemTabla() {
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) {
                cantidad = 1;
            }

            double totalItem = precioUnitarioSeleccionado * cantidad;
            double netoItem = totalItem / 1.19;

            modeloTablaVenta.addRow(new Object[]{
                modeloTablaVenta.getRowCount() + 1,
                codigoBarraSeleccionado,
                descripcionSeleccionada,
                cantidad,
                String.format("$%,.0f", netoItem),
                "19%",
                String.format("$%,.0f", totalItem)
            });

            actualizarTotalGeneral();

            txtIngresoRapido.setText("");
            txtCantidad.setText("1");
            txtCantidad.setEnabled(false);
            txtIngresoRapido.requestFocusInWindow();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida.", "Error", JOptionPane.ERROR_MESSAGE);
            txtCantidad.requestFocusInWindow();
        }
    }

    private void filtrarYMostrarBuscador() {
        String texto = txtIngresoRapido.getText().trim();
        modeloBuscadorFlotante.setRowCount(0);

        ProductoVentaDAO dao = new ProductoVentaDAO();
        List<Object[]> listaProductos = dao.buscarProductosYVendedorDAO(texto);

        for (Object[] prod : listaProductos) {
            String barra = prod[0] != null ? prod[0].toString() : "";
            String desc = prod[1] != null ? prod[1].toString() : "";
            String color = prod[2] != null ? prod[2].toString() : "";

            if (!color.isEmpty() && !color.equalsIgnoreCase("NULL")) {
                desc = desc + " (" + color + ")";
            }

            double precio = 0.0;
            try {
                if (prod[3] != null) {
                    precio = Double.parseDouble(prod[3].toString());
                }
            } catch (NumberFormatException e) {
                precio = 0.0;
            }

            modeloBuscadorFlotante.addRow(new Object[]{
                barra,
                desc,
                String.format("$%,.2f", precio)
            });
        }

        if (modeloBuscadorFlotante.getRowCount() > 0) {
            tablaBuscadorFlotante.setRowSelectionInterval(0, 0);
            scrollBuscadorFlotante.setVisible(true);
            tablaBuscadorFlotante.requestFocusInWindow();
        } else {
            scrollBuscadorFlotante.setVisible(false);
            JOptionPane.showMessageDialog(this, "No se encontraron productos en la base de datos.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void seleccionarProductoFlotante() {
        int fila = tablaBuscadorFlotante.getSelectedRow();
        if (fila != -1) {
            codigoBarraSeleccionado = modeloBuscadorFlotante.getValueAt(fila, 0).toString();
            descripcionSeleccionada = modeloBuscadorFlotante.getValueAt(fila, 1).toString();

            String precioStr = modeloBuscadorFlotante.getValueAt(fila, 2).toString()
                    .replace("$", "").replace(".", "").replace(",", ".").trim();
            precioUnitarioSeleccionado = Double.parseDouble(precioStr);

            scrollBuscadorFlotante.setVisible(false);

            txtIngresoRapido.setText(codigoBarraSeleccionado + " - " + descripcionSeleccionada);
            txtCantidad.setEnabled(true);
            txtCantidad.requestFocusInWindow();
            txtCantidad.selectAll();
        }
    }

    private void actualizarTotalGeneral() {
        double sumaTotal = 0;
        for (int i = 0; i < modeloTablaVenta.getRowCount(); i++) {
            String valStr = modeloTablaVenta.getValueAt(i, 6).toString()
                    .replace("$", "").replace(".", "").replace(",", "").trim();
            try {
                sumaTotal += Double.parseDouble(valStr);
            } catch (NumberFormatException ignored) {
            }
        }
        lblTotalGeneral.setText(String.format("%,.0f", sumaTotal));
    }

    private void finalizarYGuardarVenta() {
        if (modeloTablaVenta.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay productos en la venta actual.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String codigoVendedorStr = txtVendedor.getText().trim();
        if (codigoVendedorStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un código de vendedor válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int codigoVendedor = Integer.parseInt(codigoVendedorStr);
            String rutClienteStr = txtRutCliente.getText().trim();
            int codigoCliente = 0;
            if (!rutClienteStr.isEmpty()) {
                try {
                    codigoCliente = Integer.parseInt(rutClienteStr.replaceAll("[^0-9]", ""));
                } catch (NumberFormatException ignored) {
                }
            }

            String fechaActual = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            int folioVenta = (int) (System.currentTimeMillis() % 100000);

            List<ItemVentaDTO> listaItems = new ArrayList<>();
            for (int i = 0; i < modeloTablaVenta.getRowCount(); i++) {
                String barra = modeloTablaVenta.getValueAt(i, 1).toString();
                int cant = Integer.parseInt(modeloTablaVenta.getValueAt(i, 3).toString());

                String totalStr = modeloTablaVenta.getValueAt(i, 6).toString()
                        .replace("$", "").replace(".", "").replace(",", "").trim();
                double totalItem = Double.parseDouble(totalStr);
                double precioUnit = totalItem / cant;

                listaItems.add(new ItemVentaDTO(barra, cant, precioUnit, totalItem));
            }

            VentaDAO ventaDAO = new VentaDAO();
            boolean exito = ventaDAO.guardarVenta(folioVenta, codigoCliente, fechaActual, codigoVendedor, listaItems);

            if (exito) {
                JOptionPane.showMessageDialog(this, "¡Venta registrada con éxito! Folio N°: " + folioVenta);

                modeloTablaVenta.setRowCount(0);
                lblTotalGeneral.setText("0");
                txtIngresoRapido.setText("");
                txtVendedor.setText("");
                lblNombreVendedor.setText("");
                txtVendedor.requestFocusInWindow();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo guardar la venta en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Verifique que el código del vendedor y las cantidades sean numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
