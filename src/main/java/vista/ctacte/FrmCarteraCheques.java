package vista.ctacte;

import modelo.DocumentoCarteraDTO;
import conexion.Conexion;
import dao.CuentaCorrienteDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.sql.Connection;
import java.util.List;

public class FrmCarteraCheques extends JInternalFrame {

    private JTextField txtCliente;
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JTable tablaResultados;
    private DefaultTableModel modeloTabla;

    private final CuentaCorrienteDAO dao = new CuentaCorrienteDAO();

    // Paleta de colores terminal / Clipper Pro
    private final Color COLOR_AZUL_FONDO = new Color(0, 0, 132);
    private final Color COLOR_TEXTO_BLANCO = Color.WHITE;
    private final Color COLOR_TEXTO_VERDE = Color.GREEN;
    private final Color COLOR_CIAN = Color.CYAN;
    private final Font FUENTE_CONSOLA = new Font("Courier New", Font.BOLD, 13);

    public FrmCarteraCheques() {
        setTitle("MODULO CARTERA DE CHEQUES Y DOCUMENTOS");
        setSize(1000, 600);

        // Ocultar botones de control moderno y permitir cierre con ESC
        putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
        setClosable(true);
        setResizable(true);
        setIconifiable(false);
        setMaximizable(true);

        // Panel principal con diseño de bordes y márgenes limpios
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(COLOR_AZUL_FONDO);
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Evento para cerrar la ventana con la tecla ESC
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0, false);
        panelPrincipal.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        panelPrincipal.getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });

        // ---------------------------------------------------------
        // 1. PANEL SUPERIOR: Filtros y Criterios de Búsqueda
        // ---------------------------------------------------------
        JPanel panelFiltros = new JPanel(new GridBagLayout());
        panelFiltros.setBackground(COLOR_AZUL_FONDO);
        panelFiltros.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(COLOR_CIAN, 2),
                " CRITERIOS DE BUSQUEDA ",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.TOP,
                FUENTE_CONSOLA,
                COLOR_CIAN
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Fila 0: Cliente
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblCliente = new JLabel("Nombre Cliente :");
        lblCliente.setFont(FUENTE_CONSOLA);
        lblCliente.setForeground(COLOR_TEXTO_BLANCO);
        panelFiltros.add(lblCliente, gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtCliente = new JTextField();
        estilarCampoTexto(txtCliente, 380);
        panelFiltros.add(txtCliente, gbc);

        // Obtenemos el año actual del sistema de forma dinámica
        int anioActual = LocalDate.now().getYear();
        String fechaInicioPorDefecto = "01-01-" + anioActual;
        String fechaFinPorDefecto = "31-12-" + anioActual;

        // Fila 1: Fechas con valores dinámicos del año en curso
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblFecIni = new JLabel("Fecha Inicial   :");
        lblFecIni.setFont(FUENTE_CONSOLA);
        lblFecIni.setForeground(COLOR_TEXTO_BLANCO);
        panelFiltros.add(lblFecIni, gbc);

        gbc.gridx = 1;
        txtFechaInicio = new JTextField(fechaInicioPorDefecto);
        estilarCampoTexto(txtFechaInicio, 160);
        panelFiltros.add(txtFechaInicio, gbc);

        gbc.gridx = 2;
        JLabel lblFecFin = new JLabel(" Fecha Final   :");
        lblFecFin.setFont(FUENTE_CONSOLA);
        lblFecFin.setForeground(COLOR_TEXTO_BLANCO);
        panelFiltros.add(lblFecFin, gbc);

        gbc.gridx = 3;
        txtFechaFin = new JTextField(fechaFinPorDefecto);
        estilarCampoTexto(txtFechaFin, 160);
        panelFiltros.add(txtFechaFin, gbc);

        // Botón Consultar con Cursor de Manito
        JButton btnBuscar = new JButton("[ CONSULTAR ]");
        btnBuscar.setFont(FUENTE_CONSOLA);
        btnBuscar.setBackground(new Color(192, 192, 192));
        btnBuscar.setForeground(Color.BLACK);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBuscar.addActionListener(e -> buscarDocumentos());

        gbc.gridx = 4;
        gbc.insets = new Insets(6, 20, 6, 10);
        panelFiltros.add(btnBuscar, gbc);

        panelPrincipal.add(panelFiltros, BorderLayout.NORTH);

        // ---------------------------------------------------------
        // 2. PANEL CENTRAL: Tabla de Resultados Fluida
        // ---------------------------------------------------------
        String[] columnas = {"Folio", "Fecha", "Cliente/RUT", "Monto"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Solo lectura tipo reporte
            }
        };

        tablaResultados = new JTable(modeloTabla);
        tablaResultados.setFont(FUENTE_CONSOLA);
        tablaResultados.setBackground(Color.BLACK);
        tablaResultados.setForeground(COLOR_TEXTO_VERDE);
        tablaResultados.setGridColor(COLOR_CIAN);
        tablaResultados.setRowHeight(22);
        tablaResultados.getTableHeader().setFont(FUENTE_CONSOLA);
        tablaResultados.getTableHeader().setBackground(new Color(0, 0, 80));
        tablaResultados.getTableHeader().setForeground(COLOR_TEXTO_BLANCO);

        JScrollPane scrollTabla = new JScrollPane(tablaResultados);
        scrollTabla.getViewport().setBackground(Color.BLACK);
        scrollTabla.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(COLOR_CIAN, 2),
                " LISTADO DE DOCUMENTOS EN CARTERA ",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.TOP,
                FUENTE_CONSOLA,
                COLOR_CIAN
        ));

        panelPrincipal.add(scrollTabla, BorderLayout.CENTER);

        // ---------------------------------------------------------
        // 3. PANEL INFERIOR: Barra de Ayuda Teclado (Estilo Clipper)
        // ---------------------------------------------------------
        JPanel panelAyuda = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 2));
        panelAyuda.setBackground(new Color(0, 0, 80));

        JLabel lblAyuda = new JLabel(" [ESC] Salir    [ENTER] Seleccionar    [F2] Foco RUT    [F8] Buscar Cliente ");
        lblAyuda.setFont(new Font("Courier New", Font.BOLD, 12));
        lblAyuda.setForeground(COLOR_CIAN);
        panelAyuda.add(lblAyuda);

        panelPrincipal.add(panelAyuda, BorderLayout.SOUTH);

        add(panelPrincipal);
        atajoTeclado();
    }

    private void estilarCampoTexto(JTextField txt, int ancho) {
        txt.setFont(FUENTE_CONSOLA);
        txt.setBackground(Color.BLACK);
        txt.setForeground(COLOR_TEXTO_VERDE);
        txt.setCaretColor(COLOR_TEXTO_VERDE);
        txt.setBorder(new LineBorder(COLOR_CIAN, 1));
        txt.setPreferredSize(new Dimension(ancho, 26));
    }

    private void buscarDocumentos() {
        String cliente = txtCliente.getText().trim();
        String fecIni = txtFechaInicio.getText().trim();
        String fecFin = txtFechaFin.getText().trim();

        modeloTabla.setRowCount(0);

        try (Connection con = Conexion.getConnection()) {
            List<DocumentoCarteraDTO> resultados = dao.listarDocumentosCartera(con, cliente, fecIni, fecFin);

            for (DocumentoCarteraDTO d : resultados) {
                modeloTabla.addRow(new Object[]{
                    d.getFolio(),
                    d.getFecha(),
                    d.getCliente(),
                    String.format("$ %,.0f", d.getMonto())
                });
            }

            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No se encontraron documentos en el rango indicado.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al consultar la base de datos:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void atajoTeclado() {
        // ---------------------------------------------------------
        // ATAJOS DE TECLADO CLÁSICOS (CLIPPER MS-DOS)
        // ---------------------------------------------------------

        // F2: Lleva el foco al campo de búsqueda de cliente desde cualquier parte de la ventana
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0), "F2_BUSCAR");
        getRootPane().getActionMap().put("F2_BUSCAR", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                txtCliente.requestFocus();
                txtCliente.selectAll();
            }
        });

        // ENTER en los campos: Ejecuta la consulta automáticamente
        txtCliente.addActionListener(e -> buscarDocumentos());
        txtFechaInicio.addActionListener(e -> buscarDocumentos());
        txtFechaFin.addActionListener(e -> buscarDocumentos());

        // F8: Abre el buscador flotante estilo Clipper de Clientes
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F8, 0), "F8_BUSCAR_CLIENTE");
        getRootPane().getActionMap().put("F8_BUSCAR_CLIENTE", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                abrirBuscadorClientesF8();
            }
        });
    }

    private void abrirBuscadorClientesF8() {
        // Obtenemos la ventana contenedora actual
        Window ventanaPadre = SwingUtilities.getWindowAncestor(this);
        FrmBuscarCliente buscador = new FrmBuscarCliente(ventanaPadre);
        buscador.setVisible(true);

        // Al cerrarse, si seleccionó un RUT, lo cargamos en el campo y disparamos la consulta
        String rutElegido = buscador.getRutSeleccionado();
        if (rutElegido != null && !rutElegido.isEmpty()) {
            txtCliente.setText(rutElegido);
            buscarDocumentos(); // Ejecuta la consulta de inmediato con el RUT elegido
        }
    }
}
