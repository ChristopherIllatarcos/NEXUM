package vista;

import dao.ProductoVentaDAO;
import dao.VendedorDAO;
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
                DlgBuscarCliente buscador = new DlgBuscarCliente(DlgVenta.this, true);
                buscador.setVisible(true);

                // Al cerrarse el diálogo, recuperamos el cliente seleccionado y autocompletamos
                String[] datosCliente = buscador.getClienteSeleccionado();
                if (datosCliente != null) {
                    // Suponiendo que tienes tus JTextField en DlgVenta:
                    txtRutCliente.setText(datosCliente[0]);      // RUT
                    txtNombreCliente.setText(datosCliente[1]);   // Nombre
                    txtDireccion.setText(datosCliente[2]);       // Dirección
                    txtComuna.setText(datosCliente[3]);          // Comuna
                    txtSector.setText(datosCliente[4]);          // Sector
                    txtCelular.setText(datosCliente[5]);         // Celular
                    cmbCondicionPago.setSelectedItem(datosCliente[6]); // Condición de pago
                    txtDias.setText(datosCliente[7]);            // Días
                    txtPlazo.setText(datosCliente[8]);           // Plazo
                    txtCiudad.setText(datosCliente[9]);          // Ciudad
                    //txtCodigoProducto.requestFocusInWindow();
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

        Color azulFondo = new Color(0, 0, 160);
        Color azulOscuroPanel = new Color(0, 0, 120);

        // ==========================================
        // 1. PANEL SUPERIOR (Cabecera)
        // ==========================================
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBounds(0, 0, 1100, 75);
        panelSuperior.setBackground(azulFondo);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Vendedor (Dinámico desde la BD)
        JPanel panelVendedor = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelVendedor.setOpaque(false);
        JLabel lblVende = new JLabel("Vende:");
        lblVende.setForeground(Color.WHITE);
        lblVende.setFont(new Font("Segoe UI", Font.BOLD, 16));

        txtVendedor = new JTextField(4);
        txtVendedor.setFont(new Font("Segoe UI", Font.BOLD, 15));
        txtVendedor.setText(""); // Valor por defecto
        txtVendedor.addActionListener(e -> {
            buscarVendedor();
            txtIngresoRapido.requestFocusInWindow();
            txtIngresoRapido.selectAll();
        });

        lblNombreVendedor = new JLabel(""); // Se llenará dinámicamente desde la BD
        lblNombreVendedor.setForeground(Color.YELLOW);
        lblNombreVendedor.setFont(new Font("Segoe UI", Font.BOLD, 15));

        panelVendedor.add(lblVende);
        panelVendedor.add(txtVendedor);
        panelVendedor.add(lblNombreVendedor);

        // Título central
        JPanel panelTitulo = new JPanel();
        panelTitulo.setOpaque(false);
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        JLabel lblTituloPrincipal = new JLabel("INGRESO DE VENTA");
        lblTituloPrincipal.setForeground(Color.WHITE);
        lblTituloPrincipal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloPrincipal.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblEmpresa = new JLabel("— CASA VICTORIA —");
        lblEmpresa.setForeground(new Color(255, 180, 180));
        lblEmpresa.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblEmpresa.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelTitulo.add(lblTituloPrincipal);
        panelTitulo.add(lblEmpresa);

        // Derecha (Fecha y Total)
        JPanel panelDerecha = new JPanel(new GridLayout(2, 1, 0, 5));
        panelDerecha.setOpaque(false);
        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        JLabel lblFecha = new JLabel("Fecha: " + fechaHoy, JLabel.RIGHT);
        lblFecha.setForeground(Color.WHITE);
        lblFecha.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel panelTotalBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        panelTotalBox.setOpaque(false);
        JLabel lblTextoTotal = new JLabel("TOTAL: $");
        lblTextoTotal.setForeground(Color.WHITE);
        lblTextoTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));

        lblTotalGeneral = new JLabel("0");
        lblTotalGeneral.setForeground(Color.YELLOW);
        lblTotalGeneral.setFont(new Font("Segoe UI", Font.BOLD, 18));
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
        tablaVenta.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tablaVenta.setRowHeight(25);
        tablaVenta.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));

        JScrollPane scrollTabla = new JScrollPane(tablaVenta);
        scrollTabla.setBounds(0, 75, 1100, 505);
        layeredPane.add(scrollTabla, JLayeredPane.DEFAULT_LAYER);

        // ==========================================
        // 3. TABLA FLOTANTE DE BÚSQUEDA (Superpuesta)
        // ==========================================
        String[] columnasBuscador = {"Barra", "Descripción del Producto", "Precio Público"};
        modeloBuscadorFlotante = new DefaultTableModel(columnasBuscador, 0);
        tablaBuscadorFlotante = new JTable(modeloBuscadorFlotante);
        tablaBuscadorFlotante.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        tablaBuscadorFlotante.setRowHeight(24);
        tablaBuscadorFlotante.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        scrollBuscadorFlotante = new JScrollPane(tablaBuscadorFlotante);
        scrollBuscadorFlotante.setBounds(50, 180, 850, 250);
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
        // 4. PANEL INFERIOR (Barra de comandos y entradas)
        // ==========================================
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBounds(0, 580, 1100, 85);
        panelInferior.setBackground(azulOscuroPanel);
        panelInferior.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JPanel panelInputsLinea = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panelInputsLinea.setOpaque(false);

        JLabel lblIngreso = new JLabel("Barra/Prod:");
        lblIngreso.setForeground(Color.WHITE);
        lblIngreso.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtIngresoRapido = new JTextField(20);
        txtIngresoRapido.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtIngresoRapido.addActionListener(e -> filtrarYMostrarBuscador());

        JLabel lblCant = new JLabel("uni:");
        lblCant.setForeground(Color.WHITE);
        lblCant.setFont(new Font("Segoe UI", Font.BOLD, 14));

        txtCantidad = new JTextField(5);
        txtCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        txtCantidad.setText("1");
        txtCantidad.setEnabled(false);
        txtCantidad.addActionListener(e -> confirmarYAgregarItemTabla());

        panelInputsLinea.add(lblIngreso);
        panelInputsLinea.add(txtIngresoRapido);
        panelInputsLinea.add(lblCant);
        panelInputsLinea.add(txtCantidad);

        JLabel lblAtajos = new JLabel("(Esc)apar    (F4): Eliminar    (F8): Cliente    (F10): Descto");
        lblAtajos.setForeground(new Color(220, 220, 220));
        lblAtajos.setFont(new Font("Segoe UI", Font.BOLD, 13));

        panelInferior.add(panelInputsLinea, BorderLayout.NORTH);
        panelInferior.add(lblAtajos, BorderLayout.SOUTH);
        layeredPane.add(panelInferior, JLayeredPane.DEFAULT_LAYER);

        // ==========================================
        // ATAJOS GLOBALES DE TECLADO
        // ==========================================
        getRootPane().registerKeyboardAction(ev -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Llamamos a tu método de atajo F8 para clientes
        atajof8();

        // ==========================================
        // CARGA INICIAL DESDE LA BASE DE DATOS
        // ==========================================
        // Consulta al iniciar la ventana el nombre del vendedor por defecto (ej. código 5)
        buscarVendedor();

        txtIngresoRapido.requestFocusInWindow();
    }

    private void buscarVendedor() {
        try {
            String textoCodigo = txtVendedor.getText().trim();
            if (textoCodigo.isEmpty()) {
                lblNombreVendedor.setText("INGRESE CÓDIGO");
                return;
            }

            VendedorDAO dao = new VendedorDAO();
            java.util.List<Object[]> lista = dao.cargarVendedoresDAO(textoCodigo);

            boolean encontrado = false;
            for (Object[] fila : lista) {
                if (fila[0] != null && fila[0].toString().equals(textoCodigo)) {
                    String nombreVendedor = (String) fila[2];
                    lblNombreVendedor.setText(nombreVendedor);
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                lblNombreVendedor.setText("VENDEDOR NO ENCONTRADO");
            }

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
