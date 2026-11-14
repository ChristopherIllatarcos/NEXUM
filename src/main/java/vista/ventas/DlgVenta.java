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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;
import modelo.ItemVentaDTO;

/**
 *
 * @author caill
 */
public class DlgVenta extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DlgVenta.class.getName());

    private JTextField txtVendedor;
    private JLabel lblNombreVendedor;

    private JTextField txtIngresoRapido;
    private JTextField txtCantidad;

    private JTable tablaVenta;
    private DefaultTableModel modeloTablaVenta;

    // Tabla flotante superpuesta para elegir el producto (como en tu sistema clásico)
    private JTable tablaBuscadorFlotante;
    private DefaultTableModel modeloBuscadorFlotante;
    private JScrollPane scrollBuscadorFlotante;
    private JPanel panelFlotanteContenedor;

    private JLabel lblTotalGeneral;

    // Datos temporales del producto seleccionado
    private String codigoBarraSeleccionado = "";
    private String descripcionSeleccionada = "";
    private double precioUnitarioSeleccionado = 0.0;

    // Campos de texto para los datos del cliente en DlgVenta
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

    public DlgVenta(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initComponentsCustom();

    }

    private void atajof8() {
        // ==========================================
        // CONFIGURACIÓN DE ATAJO F8 PARA CLIENTES
        // ==========================================
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0), "abrirBuscarCliente");

        getRootPane().getActionMap().put("abrirBuscarCliente", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                // 1. Instanciamos la ventana de clientes con estilo MS-DOS
                DlgClientes buscador = new DlgClientes(DlgVenta.this);

                // 2. Usamos tu ClienteDAO real llamando a cargarClientesDAO("") fuera de la vista
                try {
                    ClienteDAO dao = new ClienteDAO();
                    java.util.List<Object[]> listaClientes = dao.cargarClientesDAO("");
                    buscador.cargarClientesEnTabla(listaClientes);
                } catch (Exception ex) {
                    javax.swing.JOptionPane.showMessageDialog(DlgVenta.this,
                            "Error al cargar los clientes: " + ex.getMessage(),
                            "Error de Base de Datos",
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                }

                // 3. Mostramos la ventana de búsqueda respetando la arquitectura limpia
                buscador.setVisible(true);

                // 4. Al cerrarse, recuperamos el cliente seleccionado y autocompletamos
                String[] datosCliente = buscador.getClienteSeleccionado();
                if (datosCliente != null) {
                    // Mapeo ordenado según las 7 columnas que retorna tu cargarClientesDAO:
                    if (datosCliente.length > 0) {
                        txtRutCliente.setText(datosCliente[0]);      // RUT (rut_completo)
                    }
                    if (datosCliente.length > 1) {
                        txtNombreCliente.setText(datosCliente[1]);   // Nombre (CLINOMBRE)
                    }
                    if (datosCliente.length > 2) {
                        txtDireccion.setText(datosCliente[2]);       // Dirección (CLIDIREC)
                    }
                    if (datosCliente.length > 3) {
                        txtComuna.setText(datosCliente[3]);          // Comuna (CLICOMUNA)
                    }
                    if (datosCliente.length > 4) {
                        txtSector.setText(datosCliente[4]);          // Sector / Zona (CLIZONA)
                    }
                    if (datosCliente.length > 5) {
                        txtCelular.setText(datosCliente[5]);         // Celular (CLIFONO1)
                    }
                    if (datosCliente.length > 6) {
                        txtCiudad.setText(datosCliente[6]);          // Ciudad (CLICIUDAD)
                    }
                    txtIngresoRapido.requestFocusInWindow();
                }
            }
        });
    }

    private void initComponentsCustom() {
        setTitle("Ingreso de Venta");
        setSize(1100, 700);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        setContentPane(layeredPane);

        // ==========================================
        // PALETA DE COLORES Y FUENTES ESTILO MS-DOS
        // ==========================================
        Color dosBlue = new Color(0, 0, 128);        // Azul Clipper clásico
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

        // Vendedor (Dinámico desde la BD)
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

        // Derecha (Fecha y Total - Margen derecho ampliado para que no se pegue al borde)
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
        scrollTabla.setBounds(0, 80, 1100, 480); // Ajustado para dar espacio al panel inferior
        scrollTabla.getViewport().setBackground(dosDarkPanel);
        scrollTabla.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        layeredPane.add(scrollTabla, JLayeredPane.DEFAULT_LAYER);

        // ==========================================
        // 3. TABLA FLOTANTE DE BÚSQUEDA (Superpuesta)
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
        scrollBuscadorFlotante.setBounds(50, 160, 900, 260);
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
        // 4. PANEL INFERIOR (Barra de comandos y atajos reubicados)
        // ==========================================
        JPanel panelInferior = new JPanel(null); // Layout absoluto para control total de posiciones
        panelInferior.setBounds(0, 560, 1100, 105);
        panelInferior.setBackground(dosBlue);
        panelInferior.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.WHITE));

        // Inputs de línea (Barra/Prod y uni)
        JPanel panelInputsLinea = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        panelInputsLinea.setBounds(5, 5, 1085, 50);
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

        JLabel lblAtajos = new JLabel("(Esc)apar    (F4): Eliminar    (F8): Cliente    (F10): Descto    (F11): PDF    (F12): Guardar");
        lblAtajos.setForeground(dosYellow);
        lblAtajos.setFont(new Font(fuenteConsola.getFamily(), Font.BOLD, 13));
        lblAtajos.setBounds(15, 62, 1070, 25);

        panelInferior.add(panelInputsLinea);
        panelInferior.add(lblAtajos);
        layeredPane.add(panelInferior, JLayeredPane.DEFAULT_LAYER);

        // ==========================================
        // ATAJOS GLOBALES DE TECLADO
        // ==========================================
        getRootPane().registerKeyboardAction(ev -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        atajof8();
        atajof4();

        // Atajo F12 para guardar venta
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0), "guardarVentaF12");

        getRootPane().getActionMap().put("guardarVentaF12", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                finalizarYGuardarVenta();
            }
        });

        // NUEVO: Atajo F11 para exportar a PDF
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0), "exportarPDFF11");

        getRootPane().getActionMap().put("exportarPDFF11", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                exportarVentaPDF();
            }
        });

        // ==========================================
        // CARGA INICIAL DESDE LA BASE DE DATOS
        // ==========================================
        buscarVendedor();
        txtIngresoRapido.requestFocusInWindow();
    }

    private void exportarVentaPDF() {
        if (modeloTablaVenta.getRowCount() == 0) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "No hay productos en la venta actual para exportar.",
                    "Aviso",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        String vendedor = lblNombreVendedor.getText() != null ? lblNombreVendedor.getText() : "Desconocido";
        String total = lblTotalGeneral.getText();

        // Llamamos directamente al método correcto que creamos en PDFUtils
        utilidades.PDFUtils.generarComprobanteVenta(vendedor, total, modeloTablaVenta);
    }

    // ==========================================
    // ATAJO F4: ELIMINAR ÍTEM SELECCIONADO DE LA TABLA
    // ==========================================
    private void atajof4() {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), "eliminarItemF4");

        getRootPane().getActionMap().put("eliminarItemF4", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int filaSeleccionada = tablaVenta.getSelectedRow();
                if (filaSeleccionada != -1) {
                    modeloTablaVenta.removeRow(filaSeleccionada);
                    // Reordenar los números (N°) de las filas restantes
                    for (int i = 0; i < modeloTablaVenta.getRowCount(); i++) {
                        modeloTablaVenta.setValueAt(i + 1, i, 0);
                    }
                    actualizarTotalGeneral();
                    JOptionPane.showMessageDialog(DlgVenta.this, "Ítem eliminado correctamente.");
                } else {
                    JOptionPane.showMessageDialog(DlgVenta.this, "Seleccione un ítem de la tabla para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }

    private void buscarVendedor() {
        try {
            String textoCodigo = txtVendedor.getText().trim();
            if (textoCodigo.isEmpty()) {
                lblNombreVendedor.setText("INGRESE CÓDIGO");
                return;
            }

            int codigoVendedor = Integer.parseInt(textoCodigo);

            // Llamamos directamente a tu método limpio del DAO
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

    // Confirma la cantidad ('uni'), calcula y mete la fila a la tabla principal
    private void confirmarYAgregarItemTabla() {
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) {
                cantidad = 1;
            }

            double totalItem = precioUnitarioSeleccionado * cantidad;
            double netoItem = totalItem / 1.19; // IVA 19%

            // Agregar a la grilla principal de venta
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

            // Limpiar para el siguiente producto
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

        // Llamamos a tu DAO para consultar directamente la tabla public.productos en PostgreSQL
        ProductoVentaDAO dao = new ProductoVentaDAO();
        java.util.List<Object[]> listaProductos = dao.buscarProductosYVendedorDAO(texto);

        for (Object[] prod : listaProductos) {
            // Orden real devuelto por el DAO:
            // [0] = codigo_barra, [1] = descripcion, [2] = color, [3] = precio
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

            // Agregamos a la tabla flotante con formato de dinero limpio
            modeloBuscadorFlotante.addRow(new Object[]{
                barra,
                desc,
                String.format("$%,.2f", precio)
            });
        }

        // Mostramos u ocultamos la tablita flotante según los resultados
        if (modeloBuscadorFlotante.getRowCount() > 0) {
            tablaBuscadorFlotante.setRowSelectionInterval(0, 0);
            scrollBuscadorFlotante.setVisible(true);
            tablaBuscadorFlotante.requestFocusInWindow();
        } else {
            scrollBuscadorFlotante.setVisible(false);
            JOptionPane.showMessageDialog(this, "No se encontraron productos en la base de datos.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Selecciona el producto desde la tablita flotante con ENTER
    private void seleccionarProductoFlotante() {
        int fila = tablaBuscadorFlotante.getSelectedRow();
        if (fila != -1) {
            codigoBarraSeleccionado = modeloBuscadorFlotante.getValueAt(fila, 0).toString();
            descripcionSeleccionada = modeloBuscadorFlotante.getValueAt(fila, 1).toString();

            String precioStr = modeloBuscadorFlotante.getValueAt(fila, 2).toString()
                    .replace("$", "").replace(".", "").replace(",", ".").trim();
            precioUnitarioSeleccionado = Double.parseDouble(precioStr);

            // Ocultamos la tabla flotante
            scrollBuscadorFlotante.setVisible(false);

            // Pasamos la sigla/barra al campo de texto inferior y habilitamos la cantidad 'uni'
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

            // Recopilamos los ítems de la tabla visual en una lista limpia
            java.util.List<ItemVentaDTO> listaItems = new java.util.ArrayList<>();
            for (int i = 0; i < modeloTablaVenta.getRowCount(); i++) {
                String barra = modeloTablaVenta.getValueAt(i, 1).toString();
                int cant = Integer.parseInt(modeloTablaVenta.getValueAt(i, 3).toString());

                String totalStr = modeloTablaVenta.getValueAt(i, 6).toString()
                        .replace("$", "").replace(".", "").replace(",", "").trim();
                double totalItem = Double.parseDouble(totalStr);
                double precioUnit = totalItem / cant;

                listaItems.add(new ItemVentaDTO(barra, cant, precioUnit, totalItem));
            }

            // Llamamos al DAO (separación de lógica lograda con éxito)
            VentaDAO ventaDAO = new VentaDAO();
            boolean exito = ventaDAO.guardarVenta(folioVenta, codigoCliente, fechaActual, codigoVendedor, listaItems);

            if (exito) {
                JOptionPane.showMessageDialog(this, "¡Venta registrada con éxito! Folio N°: " + folioVenta);

                // Limpiamos la pantalla para una nueva venta
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
   

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                DlgVenta dialog = new DlgVenta(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
