package vista.ctacte;

import conexion.Conexion;
import dao.CuentaCorrienteDAO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import modelo.DocumentoCarteraDTO;
import utilidades.PDFUtils;

public class FrmCartolaPorFecha extends JInternalFrame {

    private JTextField txtRut;
    private JTextField txtFechaDesde;
    private JTextField txtFechaHasta;
    private JTable tablaCartola;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotal;

    // Colores y Fuentes estilo consola / Clipper
    private final Color COLOR_FONDO = new Color(0, 0, 0);          // Negro clásico
    private final Color COLOR_TEXTO = new Color(0, 255, 0);          // Verde terminal
    private final Color COLOR_CIAN = new Color(0, 255, 255);         // Cian bordes
    private final Color COLOR_SELECCION = new Color(0, 0, 128);      // Azul oscuro selección
    private final Font FUENTE_CONSOLA = new Font("Courier New", Font.BOLD, 12);

    public FrmCartolaPorFecha() {
        super(" CARTOLA DE MOVIMIENTOS POR FECHA ", false, true, false, true);
        setSize(850, 520);
        setLocation(50, 30);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout());

        // 1. PANEL SUPERIOR DE FILTROS
        JPanel panelFiltros = new JPanel(new GridBagLayout());
        panelFiltros.setBackground(COLOR_FONDO);
        panelFiltros.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(COLOR_CIAN, 2), " PARAMETROS DE CONSULTA ",
                0, 0, FUENTE_CONSOLA, COLOR_CIAN
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // RUT Cliente
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblRut = new JLabel("RUT / Cliente:");
        lblRut.setForeground(COLOR_TEXTO);
        lblRut.setFont(FUENTE_CONSOLA);
        panelFiltros.add(lblRut, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        txtRut = new JTextField(12);
        estilizarCampo(txtRut);
        panelFiltros.add(txtRut, gbc);

        // Fecha Desde
        gbc.gridx = 2;
        gbc.gridy = 0;
        JLabel lblDesde = new JLabel("Desde (DD-MM-YYYY):");
        lblDesde.setForeground(COLOR_TEXTO);
        lblDesde.setFont(FUENTE_CONSOLA);
        panelFiltros.add(lblDesde, gbc);

        gbc.gridx = 3;
        gbc.gridy = 0;
        txtFechaDesde = new JTextField(10);
        estilizarCampo(txtFechaDesde);
        // Autocompletar con la fecha actual o inicio de mes por defecto
        txtFechaDesde.setText("01-01-2026");
        panelFiltros.add(txtFechaDesde, gbc);

        // Fecha Hasta
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblHasta = new JLabel("Hasta (DD-MM-YYYY):");
        lblHasta.setForeground(COLOR_TEXTO);
        lblHasta.setFont(FUENTE_CONSOLA);
        panelFiltros.add(lblHasta, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        txtFechaHasta = new JTextField(10);
        estilizarCampo(txtFechaHasta);
        // Autocompletar con la fecha actual del sistema
        txtFechaHasta.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        panelFiltros.add(txtFechaHasta, gbc);

        // Botón Consultar
        gbc.gridx = 3;
        gbc.gridy = 1;
        JButton btnConsultar = new JButton("[ENTER] Consultar");
        estilizarBoton(btnConsultar);
        btnConsultar.addActionListener(e -> realizarConsulta());
        panelFiltros.add(btnConsultar, gbc);

        add(panelFiltros, BorderLayout.NORTH);

        // 2. TABLA / GRILLA CENTRAL
        String[] columnas = {"Cliente / RUT", "Folio Doc.", "Fecha", "Monto Abono ($)"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Solo lectura
            }
        };

        tablaCartola = new JTable(modeloTabla);
        tablaCartola.setBackground(COLOR_FONDO);
        tablaCartola.setForeground(COLOR_TEXTO);
        tablaCartola.setFont(FUENTE_CONSOLA);
        tablaCartola.setSelectionBackground(COLOR_SELECCION);
        tablaCartola.setSelectionForeground(Color.WHITE);
        tablaCartola.getTableHeader().setBackground(COLOR_FONDO);
        tablaCartola.getTableHeader().setForeground(COLOR_CIAN);
        tablaCartola.getTableHeader().setFont(FUENTE_CONSOLA);
        tablaCartola.setRowHeight(22);

        JScrollPane scrollTabla = new JScrollPane(tablaCartola);
        scrollTabla.getViewport().setBackground(COLOR_FONDO);
        scrollTabla.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(COLOR_CIAN, 2), " LISTADO DE MOVIMIENTOS EN CARTOLA ",
                0, 0, FUENTE_CONSOLA, COLOR_CIAN
        ));
        add(scrollTabla, BorderLayout.CENTER);

        // 3. PANEL INFERIOR (TOTALES Y ATAJOS)
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(COLOR_FONDO);
        panelInferior.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        lblTotal = new JLabel("TOTAL ABONOS PERIODO: $ 0");
        lblTotal.setForeground(COLOR_CIAN);
        lblTotal.setFont(new Font("Courier New", Font.BOLD, 13));
        panelInferior.add(lblTotal, BorderLayout.WEST);

        JLabel lblAtajos = new JLabel("[P] Imprimir PDF   |   [ESC] Salir");
        lblAtajos.setForeground(COLOR_TEXTO);
        lblAtajos.setFont(FUENTE_CONSOLA);
        panelInferior.add(lblAtajos, BorderLayout.EAST);

        add(panelInferior, BorderLayout.SOUTH);

        // 4. CONFIGURACIÓN DE ATAJOS DE TECLADO GLOBALES EN LA VENTANA
        configurarAtajosTeclado();
    }

    private void estilizarCampo(JTextField campo) {
        campo.setBackground(COLOR_FONDO);
        campo.setForeground(COLOR_TEXTO);
        campo.setCaretColor(COLOR_TEXTO);
        campo.setFont(FUENTE_CONSOLA);
        campo.setBorder(new LineBorder(COLOR_CIAN, 1));
        // Al presionar Enter en cualquier campo, ejecuta la consulta
        campo.addActionListener(e -> realizarConsulta());
    }

    private void estilizarBoton(JButton boton) {
        boton.setBackground(COLOR_FONDO);
        boton.setForeground(COLOR_CIAN);
        boton.setFont(FUENTE_CONSOLA);
        boton.setBorder(new LineBorder(COLOR_CIAN, 1));
        boton.setFocusPainted(false);
    }

    private void configurarAtajosTeclado() {
        JRootPane rootPane = this.getRootPane();

        // Atajo [ESC] para cerrar la ventana
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cerrarVentana");
        rootPane.getActionMap().put("cerrarVentana", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });

        // Atajo [P] o [p] para imprimir / exportar a PDF
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), "imprimirPdf");
        rootPane.getActionMap().put("imprimirPdf", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                exportarCartolaPDF();
            }
        });
    }

    private void realizarConsulta() {
        String rut = txtRut.getText().trim();
        String fechaDesde = txtFechaDesde.getText().trim();
        String fechaHasta = txtFechaHasta.getText().trim();

        if (rut.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un RUT de cliente válido.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            txtRut.requestFocus();
            return;
        }

        modeloTabla.setRowCount(0);
        double sumaTotal = 0.0;

        try (Connection con = Conexion.getConnection()) { // Reemplaza por tu método de conexión
            CuentaCorrienteDAO dao = new CuentaCorrienteDAO();
            List<DocumentoCarteraDTO> lista = dao.obtenerCartolaPorFecha(con, rut, fechaDesde, fechaHasta);

            for (DocumentoCarteraDTO doc : lista) {
                modeloTabla.addRow(new Object[]{
                    doc.getCliente(),
                    doc.getFolio(),
                    doc.getFecha(),
                    String.format("$ %,.0f", doc.getMonto())
                });
                sumaTotal += doc.getMonto();
            }

            lblTotal.setText(String.format("TOTAL ABONOS PERIODO: $ %,.0f", sumaTotal));

            if (lista.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron movimientos para el RUT y rango de fechas indicado.", "Información", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al consultar la base de datos: " + ex.getMessage(), "Error Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportarCartolaPDF() {
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos en la cartola para exportar a PDF.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Obtenemos los filtros actuales para la cabecera del reporte
            String rutCliente = txtRut.getText().trim();
            String fechaDesde = txtFechaDesde.getText().trim();
            String fechaHasta = txtFechaHasta.getText().trim();
            String fechaActual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            // Llamamos a tu clase PDFUtils del paquete utilidades
            // (Ajusta el método estático o la instancia según cómo lo tengas construido en PDFUtils)
            PDFUtils.generarCartolaPorFechaPDF(
                    modeloTabla,
                    rutCliente,
                    fechaDesde,
                    fechaHasta,
                    fechaActual,
                    lblTotal.getText(),
                    this
            );

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al generar el archivo PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
