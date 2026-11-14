package vista.ctacte;

import dao.ClienteDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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
import vista.ventas.DlgClientes;

/**
 * Módulo CTACTE - Cartola Interna de Cuenta Corriente por Cliente Estilo MS-DOS
 * / Clipper adaptado a MDI JInternalFrame
 *
 * @author caill
 */
public class FrmCartolaCliente extends JInternalFrame {

    private JTextField txtRutCliente;
    private JLabel lblNombreCliente;
    private JLabel lblTotalDeuda;

    private JTable tablaCartola;
    private DefaultTableModel modeloTablaCartola;

    private javax.swing.table.DefaultTableModel modeloTabla;

    public FrmCartolaCliente() {
        super("Cartola Interna CtaCte Cliente", true, true, true, true);
        putClientProperty("JInternalFrame.isPalette", Boolean.FALSE);
        initComponentsManual();
        configurarTablaDetalle();
        modeloTablaCartola = (javax.swing.table.DefaultTableModel) tablaCartola.getModel();
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
        // 1. PANEL SUPERIOR (Cabecera y Buscador de Cliente)
        // ==========================================
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBounds(0, 0, 1100, 110);
        panelSuperior.setBackground(dosBlue);
        panelSuperior.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE));

        // Título de la sección estilo recuadro DOS
        JPanel panelTituloBox = new JPanel();
        panelTituloBox.setOpaque(false);
        panelTituloBox.setLayout(new BoxLayout(panelTituloBox, BoxLayout.Y_AXIS));
        panelTituloBox.setBorder(BorderFactory.createEmptyBorder(8, 15, 5, 15));

        JLabel lblSubTitulo = new JLabel(" CARTOLA INTERNA CTACTE CLIENTE ");
        lblSubTitulo.setForeground(dosYellow);
        lblSubTitulo.setFont(new Font(fuenteConsola.getFamily(), Font.BOLD, 16));

        panelTituloBox.add(lblSubTitulo);

        // Panel de inputs (RUT y Nombre)
        JPanel panelDatosFiltro = new JPanel(new GridLayout(2, 1, 0, 5));
        panelDatosFiltro.setOpaque(false);
        panelDatosFiltro.setBorder(BorderFactory.createEmptyBorder(0, 15, 8, 15));

        // Fila 1: RUT
        JPanel filaRut = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filaRut.setOpaque(false);

        JLabel lblRut = new JLabel("Ingrese R.U.T.:");
        lblRut.setForeground(dosWhite);
        lblRut.setFont(fuenteConsola);

        txtRutCliente = new JTextField(15);
        txtRutCliente.setFont(fuenteConsola);
        txtRutCliente.setBackground(Color.BLACK);
        txtRutCliente.setForeground(dosYellow);
        txtRutCliente.setCaretColor(dosYellow);
        txtRutCliente.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        txtRutCliente.addActionListener(e -> buscarClienteYCartola());

        JLabel lblAyudaF8 = new JLabel("(Presione F8 para buscar cliente)");
        lblAyudaF8.setForeground(dosYellow);
        lblAyudaF8.setFont(new Font(fuenteConsola.getFamily(), Font.ITALIC, 12));

        filaRut.add(lblRut);
        filaRut.add(txtRutCliente);
        filaRut.add(lblAyudaF8);

        // Fila 2: Nombre
        JPanel filaNombre = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filaNombre.setOpaque(false);

        JLabel lblNom = new JLabel("Nombre del Cliente:");
        lblNom.setForeground(dosWhite);
        lblNom.setFont(fuenteConsola);

        lblNombreCliente = new JLabel("[ Seleccione o ingrese un RUT válido ]");
        lblNombreCliente.setForeground(dosYellow);
        lblNombreCliente.setFont(fuenteConsola);

        filaNombre.add(lblNom);
        filaNombre.add(lblNombreCliente);

        panelDatosFiltro.add(filaRut);
        panelDatosFiltro.add(filaNombre);

        panelSuperior.add(panelTituloBox, BorderLayout.NORTH);
        panelSuperior.add(panelDatosFiltro, BorderLayout.CENTER);
        layeredPane.add(panelSuperior, JLayeredPane.DEFAULT_LAYER);

        // ==========================================
        // 2. CENTRO (Tabla de Detalle / Cartola de Movimientos)
        // ==========================================
        String[] columnasCartola = {"Tipo Doc", "N° Doc", "Fecha Emisión", "Vencimiento", "Cargo ($)", "Abono ($)", "Saldo ($)"};
        modeloTablaCartola = new DefaultTableModel(columnasCartola, 0);

        tablaCartola = new JTable(modeloTablaCartola);
        tablaCartola.setFont(fuenteConsola);
        tablaCartola.setBackground(dosDarkPanel);
        tablaCartola.setForeground(dosWhite);
        tablaCartola.setGridColor(Color.DARK_GRAY);
        tablaCartola.setRowHeight(26);

        tablaCartola.getTableHeader().setFont(fuenteConsola);
        tablaCartola.getTableHeader().setBackground(dosBlue);
        tablaCartola.getTableHeader().setForeground(dosYellow);

        JScrollPane scrollCartola = new JScrollPane(tablaCartola);
        scrollCartola.setBounds(0, 110, 1100, 485);
        scrollCartola.getViewport().setBackground(dosDarkPanel);
        scrollCartola.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        layeredPane.add(scrollCartola, JLayeredPane.DEFAULT_LAYER);

        // ==========================================
        // 3. PANEL INFERIOR (Totales y Barra de Comandos)
        // ==========================================
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBounds(0, 595, 1100, 105);
        panelInferior.setBackground(dosBlue);
        panelInferior.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.WHITE));

        // Caja de Total Deuda
        JPanel panelTotalBox = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        panelTotalBox.setOpaque(false);

        JLabel lblTextoDeuda = new JLabel("SALDO TOTAL CARTERA: $");
        lblTextoDeuda.setForeground(dosWhite);
        lblTextoDeuda.setFont(fuenteConsola);

        lblTotalDeuda = new JLabel("0");
        lblTotalDeuda.setForeground(dosYellow);
        lblTotalDeuda.setFont(new Font(fuenteConsola.getFamily(), Font.BOLD, 18));

        panelTotalBox.add(lblTextoDeuda);
        panelTotalBox.add(lblTotalDeuda);

        // Atajos inferiores
        JLabel lblAtajos = new JLabel("  (Esc)apar    (F8): Buscar Cliente    (F2): Detalle Documento    (F6): Imprimir Cartola");
        lblAtajos.setForeground(dosYellow);
        lblAtajos.setFont(new Font(fuenteConsola.getFamily(), Font.BOLD, 13));
        lblAtajos.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));

        panelInferior.add(panelTotalBox, BorderLayout.NORTH);
        panelInferior.add(lblAtajos, BorderLayout.SOUTH);
        layeredPane.add(panelInferior, JLayeredPane.DEFAULT_LAYER);

        // ==========================================
        // LISTENER RESPONSIVO
        // ==========================================
        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = layeredPane.getWidth();
                int h = layeredPane.getHeight();

                if (w > 0 && h > 0) {
                    panelSuperior.setBounds(0, 0, w, 110);
                    scrollCartola.setBounds(0, 110, w, h - 215);
                    panelInferior.setBounds(0, h - 105, w, 105);
                }
            }
        });

        // ==========================================
        // ATAJOS DE TECLADO GLOBALES
        // ==========================================
        // ESC para cerrar
        getRootPane().registerKeyboardAction(ev -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        // F8 para abrir el buscador de clientes (igual que en ventas)
        atajarF8Cliente();

        // F6 para Imprimir / Exportar Cartola 
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0), "imprimirCartolaF6");

        getRootPane().getActionMap().put("imprimirCartolaF6", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                imprimirCartolaPDF(); // <-- ¡Aquí es donde se llama al método!
            }
        });

        // F2 para Ver Detalle del Documento seleccionado en la tabla
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0), "verDetalleF2");

        getRootPane().getActionMap().put("verDetalleF2", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                verDetalleDocumentoF2();
            }
        });

        txtRutCliente.requestFocusInWindow();
        revalidate();
        repaint();
    }

   private void configurarTablaDetalle() {
        // 1. Usamos el modelo que la tabla ya trae creado por NetBeans (evita romper la vista)
        javax.swing.table.DefaultTableModel modeloTabla = (javax.swing.table.DefaultTableModel) tablaCartola.getModel();
     

        // 2. Interceptar la tecla F2 directamente dentro de la tabla para abrir el detalle
        tablaCartola.getInputMap(javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0), "verDetalleF2");

        tablaCartola.getActionMap().put("verDetalleF2", new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                verDetalleDocumentoF2();
            }
        });

        // 3. Abrir el detalle también con doble clic en la fila
        tablaCartola.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && tablaCartola.getSelectedRow() != -1) {
                    verDetalleDocumentoF2();
                }
            }
        });
    }
    
    private void atajarF8Cliente() {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0), "abrirBuscarClienteCartola");

        getRootPane().getActionMap().put("abrirBuscarClienteCartola", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                Window parentWindow = SwingUtilities.getWindowAncestor(FrmCartolaCliente.this);
                DlgClientes buscador = new DlgClientes(parentWindow);
                try {
                    ClienteDAO dao = new ClienteDAO();
                    List<Object[]> listaClientes = dao.cargarClientesDAO("");
                    buscador.cargarClientesEnTabla(listaClientes);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FrmCartolaCliente.this,
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
                        lblNombreCliente.setText(datosCliente[1]);
                    }

                    // Aquí puedes disparar la carga de la cartola para este cliente
                    cargarCartolaCliente(datosCliente[0]);
                }
            }
        });
    }

    private void buscarClienteYCartola() {
        String rut = txtRutCliente.getText().trim();
        if (rut.isEmpty()) {
            lblNombreCliente.setText("[ Ingrese un RUT válido ]");
            return;
        }

        // Simulación o llamada DAO para buscar el cliente por RUT y rellenar su cartola
        try {
            ClienteDAO dao = new ClienteDAO();
            List<Object[]> listaClientes = dao.cargarClientesDAO(rut);

            boolean encontrado = false;
            for (Object[] cli : listaClientes) {
                String rutCli = cli[0] != null ? cli[0].toString() : "";
                if (rutCli.equalsIgnoreCase(rut) || rutCli.contains(rut)) {
                    txtRutCliente.setText(rutCli);
                    lblNombreCliente.setText(cli[1] != null ? cli[1].toString() : "Sin Nombre");
                    encontrado = true;
                    break;
                }
            }

            if (encontrado) {
                cargarCartolaCliente(rut);
            } else {
                lblNombreCliente.setText("CLIENTE NO ENCONTRADO");
                modeloTablaCartola.setRowCount(0);
                lblTotalDeuda.setText("0");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al buscar el cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarCartolaCliente(String rutCliente) {
        // Limpiamos la tabla actual y reseteamos el total
        modeloTablaCartola.setRowCount(0);
        double saldoTotalCalculado = 0.0;

        try {
            dao.CuentaCorrienteDAO ctaCteDao = new dao.CuentaCorrienteDAO();
            List<Object[]> movimientos = ctaCteDao.obtenerCartolaCliente(rutCliente);

            if (movimientos.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "El cliente no registra movimientos en su cuenta corriente.",
                        "Aviso",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                for (Object[] mov : movimientos) {
                    // Conversión segura para evitar ClassCastException con BigDecimal/Double de JDBC
                    double cargo = 0.0;
                    double abono = 0.0;
                    double saldo = 0.0;

                    try {
                        if (mov[4] != null) {
                            cargo = Double.parseDouble(mov[4].toString());
                        }
                        if (mov[5] != null) {
                            abono = Double.parseDouble(mov[5].toString());
                        }
                        if (mov[6] != null) {
                            saldo = Double.parseDouble(mov[6].toString());
                        }
                    } catch (NumberFormatException e) {
                        // Si viene algún valor extraño, lo ignoramos o dejamos en 0
                    }

                    saldoTotalCalculado += saldo;

                    // Agregamos la fila a la tabla con formato visual chileno estilo moneda
                    modeloTablaCartola.addRow(new Object[]{
                        mov[0], // Tipo Doc
                        mov[1], // N° Doc
                        mov[2], // Fecha Emisión
                        mov[3] != null ? mov[3] : "-", // Vencimiento
                        String.format("$%,.0f", cargo),
                        String.format("$%,.0f", abono),
                        String.format("$%,.0f", saldo)
                    });
                }
            }

            // Actualizamos la etiqueta con el saldo total de la cartera
            lblTotalDeuda.setText(String.format("%,.0f", saldoTotalCalculado));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar la cartola desde la base de datos: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void imprimirCartolaPDF() {
        if (modeloTablaCartola.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay movimientos en la cartola para imprimir.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String rutCliente = txtRutCliente.getText().trim();
        String nombreCliente = lblNombreCliente.getText().trim();
        String totalDeuda = lblTotalDeuda.getText().trim();

        try {
            // ¡Llamada limpia a tu clase PDFUtils en el paquete utilidades!
            utilidades.PDFUtils.generarCartolaClientePDF(rutCliente, nombreCliente, totalDeuda, modeloTablaCartola);

            JOptionPane.showMessageDialog(this, "¡Cartola exportada a PDF con éxito!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al generar el PDF de la cartola: " + ex.getMessage(),
                    "Error de Impresión",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verDetalleDocumentoF2() {
        int filaSeleccionada = tablaCartola.getSelectedRow();

        if (filaSeleccionada == -1) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "Por favor, seleccione un movimiento de la tabla para ver su detalle.",
                    "Aviso",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Extraemos los datos de la fila seleccionada
        String tipoDoc = tablaCartola.getValueAt(filaSeleccionada, 0) != null ? tablaCartola.getValueAt(filaSeleccionada, 0).toString() : "";
        String nroDoc = tablaCartola.getValueAt(filaSeleccionada, 1) != null ? tablaCartola.getValueAt(filaSeleccionada, 1).toString() : "";
        String fechaEmision = tablaCartola.getValueAt(filaSeleccionada, 2) != null ? tablaCartola.getValueAt(filaSeleccionada, 2).toString() : "";
        String vencimiento = tablaCartola.getValueAt(filaSeleccionada, 3) != null ? tablaCartola.getValueAt(filaSeleccionada, 3).toString() : "";
        String cargo = tablaCartola.getValueAt(filaSeleccionada, 4) != null ? tablaCartola.getValueAt(filaSeleccionada, 4).toString() : "0";
        String abono = tablaCartola.getValueAt(filaSeleccionada, 5) != null ? tablaCartola.getValueAt(filaSeleccionada, 5).toString() : "0";
        String saldo = tablaCartola.getValueAt(filaSeleccionada, 6) != null ? tablaCartola.getValueAt(filaSeleccionada, 6).toString() : "0";

        // Creamos un JDialog modal con la estética MS-DOS
        javax.swing.JDialog dialogDetalle = new javax.swing.JDialog((java.awt.Frame) null, " Detalle de Documento [F2] ", true);
        dialogDetalle.setSize(450, 320);
        dialogDetalle.setLocationRelativeTo(this);
        dialogDetalle.setLayout(new java.awt.BorderLayout());

        // Panel principal con color azul Clipper clásico
        javax.swing.JPanel panelFondo = new javax.swing.JPanel();
        panelFondo.setBackground(new java.awt.Color(0, 0, 128)); // Azul marino MS-DOS
        panelFondo.setLayout(new java.awt.BorderLayout(10, 10));
        panelFondo.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.YELLOW, 2));

        // Texto estilo consola
        String contenido
                = """
                   \u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550
                              DETALLE DE MOVIMIENTO        
                   \u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550\u2550
                  
                   Tipo de Documento : """ + tipoDoc + "\n"
                + " N° de Documento   : " + nroDoc + "\n"
                + " Fecha de Emisión  : " + fechaEmision + "\n"
                + " Fecha Vencimiento : " + vencimiento + "\n\n"
                + " ─────────────────────────────────────────\n"
                + " Cargo ($)         : " + cargo + "\n"
                + " Abono ($)         : " + abono + "\n"
                + " Saldo Parcial ($) : " + saldo + "\n"
                + " ═════════════════════════════════════════";

        javax.swing.JTextArea areaTexto = new javax.swing.JTextArea(contenido);
        areaTexto.setFont(new java.awt.Font("Monospaced", java.awt.Font.BOLD, 13));
        areaTexto.setForeground(java.awt.Color.YELLOW); // Texto amarillo brillante clásico
        areaTexto.setBackground(new java.awt.Color(0, 0, 128));
        areaTexto.setEditable(false);
        areaTexto.setMargin(new java.awt.Insets(15, 15, 15, 15));

        panelFondo.add(areaTexto, java.awt.BorderLayout.CENTER);

//        // Botón inferior estilo retro
//        javax.swing.JButton btnCerrar = new javax.swing.JButton("[ CERRAR (ENTER / ESC) ]");
//        btnCerrar.setFont(new java.awt.Font("Monospaced", java.awt.Font.BOLD, 12));
//        btnCerrar.setBackground(java.awt.Color.LIGHT_GRAY);
//        btnCerrar.setForeground(java.awt.Color.BLACK);
//        btnCerrar.setFocusPainted(false);
//        btnCerrar.addActionListener(e -> dialogDetalle.dispose());
//
//        javax.swing.JPanel panelBoton = new javax.swing.JPanel();
//        panelBoton.setBackground(new java.awt.Color(0, 0, 128));
//        panelBoton.add(btnCerrar);
//        panelFondo.add(panelBoton, java.awt.BorderLayout.SOUTH);

        // Atajo ESC o ENTER para cerrar la ventana emergente
        dialogDetalle.getRootPane().registerKeyboardAction(e -> dialogDetalle.dispose(),
                javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0),
                javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);

        dialogDetalle.getRootPane().registerKeyboardAction(e -> dialogDetalle.dispose(),
                javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0),
                javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW);

        dialogDetalle.add(panelFondo);
        dialogDetalle.setVisible(true);
    }
}
