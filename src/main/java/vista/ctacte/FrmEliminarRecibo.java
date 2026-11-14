package vista.ctacte;

import conexion.Conexion;
import dao.CuentaCorrienteDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.util.List;
import java.util.Vector;
import modelo.DocumentoCarteraDTO;

public class FrmEliminarRecibo extends JInternalFrame {

    private JTextField txtFiltroCliente;
    private JTable tablaRecibos;
    private DefaultTableModel modeloTabla;

    private JLabel lblValorFolio;
    private JLabel lblValorFecha;
    private JLabel lblValorRut;
    private JLabel lblValorMonto;

    private boolean eliminadoConExito = false;

    // Paleta de colores terminal / Clipper Pro
    private final Color COLOR_AZUL_FONDO = new Color(0, 0, 132);
    private final Color COLOR_TEXTO_BLANCO = Color.WHITE;
    private final Color COLOR_TEXTO_VERDE = Color.GREEN;
    private final Color COLOR_CIAN = Color.CYAN;
    private final Font FUENTE_CONSOLA = new Font("Courier New", Font.BOLD, 13);

    public FrmEliminarRecibo() {
        super(" ADMINISTRADOR Y ELIMINACION DE RECIBOS ", false, true, false, true);
        setSize(850, 580);
        setFrameIcon(null);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(COLOR_AZUL_FONDO);
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));

        // 1. Panel superior: Buscador
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelBusqueda.setBackground(COLOR_AZUL_FONDO);
        panelBusqueda.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(COLOR_CIAN, 2), " 1. BUSCAR CLIENTE (RUT / FOLIO) ",
                0, 0, FUENTE_CONSOLA, COLOR_CIAN
        ));

        JLabel lblFiltro = new JLabel("Filtro:");
        lblFiltro.setFont(FUENTE_CONSOLA);
        lblFiltro.setForeground(COLOR_TEXTO_BLANCO);
        panelBusqueda.add(lblFiltro);

        txtFiltroCliente = new JTextField(35);
        estilarCampo(txtFiltroCliente);
        panelBusqueda.add(txtFiltroCliente);

        panelPrincipal.add(panelBusqueda, BorderLayout.NORTH);

        // 2. Panel Central: Tabla y Detalles
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setBackground(COLOR_AZUL_FONDO);
        splitPane.setDividerLocation(240);
        splitPane.setResizeWeight(0.5);

        String[] columnas = {"N° Folio", "Fecha", "RUT Cliente", "Abono ($)"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaRecibos = new JTable(modeloTabla);
        estilarTabla(tablaRecibos);
        JScrollPane scrollTabla = new JScrollPane(tablaRecibos);
        scrollTabla.getViewport().setBackground(Color.BLACK);
        scrollTabla.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(COLOR_CIAN, 2), " 2. SELECCIONE EL RECIBO DE LA LISTA ",
                0, 0, FUENTE_CONSOLA, COLOR_CIAN
        ));
        splitPane.setTopComponent(scrollTabla);

        // Panel de detalles inferior
        JPanel panelDetalle = new JPanel(new GridBagLayout());
        panelDetalle.setBackground(COLOR_AZUL_FONDO);
        panelDetalle.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(COLOR_CIAN, 2), " 3. DETALLE DEL FOLIO Y ACCION DE ELIMINACION ",
                0, 0, FUENTE_CONSOLA, COLOR_CIAN
        ));

        // 1. Margenes más amplios para que la cuadrícula se expanda
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 25, 12, 25); // Más espacio vertical y horizontal
        gbc.anchor = GridBagConstraints.WEST;

        // Fuente con mayor protagonismo visual (puedes subirla a 15 o 16 si lo prefieres)
        Font fuenteDetalleGrande = new Font("Courier New", Font.BOLD, 15);

        // Fila 0: Folio
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblF = new JLabel("Folio Seleccionado :");
        lblF.setFont(fuenteDetalleGrande);
        lblF.setForeground(COLOR_CIAN);
        panelDetalle.add(lblF, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        lblValorFolio = new JLabel("---");
        lblValorFolio.setFont(fuenteDetalleGrande);
        lblValorFolio.setForeground(COLOR_TEXTO_VERDE);
        panelDetalle.add(lblValorFolio, gbc);

        // Fila 1: Fecha
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblFe = new JLabel("Fecha del Pago     :");
        lblFe.setFont(fuenteDetalleGrande);
        lblFe.setForeground(COLOR_CIAN);
        panelDetalle.add(lblFe, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        lblValorFecha = new JLabel("---");
        lblValorFecha.setFont(fuenteDetalleGrande);
        lblValorFecha.setForeground(COLOR_TEXTO_VERDE);
        panelDetalle.add(lblValorFecha, gbc);

        // Fila 2: RUT
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblR = new JLabel("RUT del Cliente    :");
        lblR.setFont(fuenteDetalleGrande);
        lblR.setForeground(COLOR_CIAN);
        panelDetalle.add(lblR, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        lblValorRut = new JLabel("---");
        lblValorRut.setFont(fuenteDetalleGrande);
        lblValorRut.setForeground(COLOR_TEXTO_VERDE);
        panelDetalle.add(lblValorRut, gbc);

        // Fila 3: Monto
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lblM = new JLabel("Monto Abono        :");
        lblM.setFont(fuenteDetalleGrande);
        lblM.setForeground(COLOR_CIAN);
        panelDetalle.add(lblM, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        lblValorMonto = new JLabel("---");
        lblValorMonto.setFont(fuenteDetalleGrande);
        lblValorMonto.setForeground(COLOR_TEXTO_VERDE);
        panelDetalle.add(lblValorMonto, gbc);

        splitPane.setBottomComponent(panelDetalle);
        panelPrincipal.add(splitPane, BorderLayout.CENTER);

        // 3. Barra de atajos inferior
        JPanel panelAyuda = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panelAyuda.setBackground(new Color(0, 0, 80));
        JLabel lblAyuda = new JLabel(" [ESC] Cancelar / Salir    [ENTER] Seleccionar Tabla    [SUPR / DEL] Eliminar Recibo ");
        lblAyuda.setFont(new Font("Courier New", Font.BOLD, 12));
        lblAyuda.setForeground(COLOR_CIAN);
        panelAyuda.add(lblAyuda);
        panelPrincipal.add(panelAyuda, BorderLayout.SOUTH);

        add(panelPrincipal);

        cargarDatosTabla("");

        // Eventos
        txtFiltroCliente.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (tablaRecibos.getRowCount() > 0) {
                        tablaRecibos.setRowSelectionInterval(0, 0);
                        tablaRecibos.requestFocus();
                    }
                } else {
                    cargarDatosTabla(txtFiltroCliente.getText().trim());
                }
            }
        });

        tablaRecibos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarDetallesSeleccionados();
            }
        });

        tablaRecibos.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_F3) {
                    confirmarYEliminarSeleccionado();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });

        panelPrincipal.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ESCAPE");
        panelPrincipal.getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
    }

    private void estilarCampo(JTextField txt) {
        txt.setFont(FUENTE_CONSOLA);
        txt.setBackground(Color.BLACK);
        txt.setForeground(COLOR_TEXTO_VERDE);
        txt.setCaretColor(COLOR_TEXTO_VERDE);
        txt.setBorder(new LineBorder(COLOR_CIAN, 1));
    }

    private void estilarTabla(JTable tabla) {
        tabla.setFont(FUENTE_CONSOLA);
        tabla.setBackground(Color.BLACK);
        tabla.setForeground(COLOR_TEXTO_VERDE);
        tabla.setGridColor(COLOR_CIAN);
        tabla.setRowHeight(22);
        tabla.getTableHeader().setFont(FUENTE_CONSOLA);
        tabla.getTableHeader().setBackground(new Color(0, 0, 80));
        tabla.getTableHeader().setForeground(COLOR_TEXTO_BLANCO);
    }

    private void cargarDatosTabla(String filtro) {
        modeloTabla.setRowCount(0);
        try (Connection con = Conexion.getConnection()) {
            CuentaCorrienteDAO dao = new CuentaCorrienteDAO();
            List<DocumentoCarteraDTO> lista = dao.buscarRecibosPorFiltro(con, filtro);

            for (DocumentoCarteraDTO doc : lista) {
                Vector<Object> fila = new Vector<>();
                fila.add(doc.getFolio());
                fila.add(doc.getFecha());
                fila.add(doc.getCliente());
                fila.add(String.format("$ %,.0f", doc.getMonto()));
                modeloTabla.addRow(fila);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void actualizarDetallesSeleccionados() {
        int fila = tablaRecibos.getSelectedRow();
        if (fila != -1) {
            lblValorFolio.setText(modeloTabla.getValueAt(fila, 0).toString());
            lblValorFecha.setText(modeloTabla.getValueAt(fila, 1).toString());
            lblValorRut.setText(modeloTabla.getValueAt(fila, 2).toString());
            lblValorMonto.setText(modeloTabla.getValueAt(fila, 3).toString());
        } else {
            lblValorFolio.setText("---");
            lblValorFecha.setText("---");
            lblValorRut.setText("---");
            lblValorMonto.setText("---");
        }
    }

    private void confirmarYEliminarSeleccionado() {
        int fila = tablaRecibos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un recibo de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String folio = modeloTabla.getValueAt(fila, 0).toString();
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está ABSOLUTAMENTE seguro de eliminar el recibo N° " + folio + "?",
                "Confirmación de Seguridad",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try (Connection con = Conexion.getConnection()) {
                CuentaCorrienteDAO dao = new CuentaCorrienteDAO();
                if (dao.eliminarPagoPorFolio(con, folio)) {
                    JOptionPane.showMessageDialog(this, "¡Recibo eliminado con éxito!", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                    eliminadoConExito = true;
                    cargarDatosTabla(txtFiltroCliente.getText().trim());
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo completar la eliminación.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public boolean isEliminadoConExito() {
        return eliminadoConExito;
    }
}
