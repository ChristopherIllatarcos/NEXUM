package vista.ventas;

import conexion.Conexion;
import dao.VentaDAO;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.util.List;
import modelo.VentaDTO;

public class FrmMantencionVentas extends JInternalFrame {

    private JTextField txtFiltro;
    private JTable tablaVentas;
    private DefaultTableModel modeloTabla;

    // Estética Clipper Clásica (Azul, Amarillo, Cian y Barra Roja)
    private final Color COLOR_AZUL_FONDO = new Color(0, 0, 139);      // Azul clásico Clipper
    private final Color COLOR_AMARILLO = new Color(255, 255, 0);       // Amarillo brillante textos/títulos
    private final Color COLOR_CIAN = new Color(0, 255, 255);           // Cian para bordes
    private final Color COLOR_ROJO_BARRA = new Color(178, 0, 0);       // Rojo sólido barra inferior
    private final Color COLOR_SELECCION = new Color(0, 0, 205);        // Azul selección grilla
    private final Font FUENTE_CONSOLA = new Font("Courier New", Font.BOLD, 12);

    public FrmMantencionVentas() {
        super(" 4.- MANTENCIÓN DE VENTAS ", false, true, false, true);
        setSize(950, 550);
        setLocation(35, 20);
        getContentPane().setBackground(COLOR_AZUL_FONDO);
        setLayout(new BorderLayout());

        // 1. PANEL DE BÚSQUEDA / FILTRO
        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelFiltro.setBackground(COLOR_AZUL_FONDO);
        panelFiltro.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(COLOR_CIAN, 2), " BUSCAR EN REGISTRO DE VENTAS ",
                0, 0, FUENTE_CONSOLA, COLOR_CIAN
        ));
        // Forzar color del título del borde
        ((javax.swing.border.TitledBorder) panelFiltro.getBorder()).setTitleColor(COLOR_AMARILLO);

        JLabel lblBuscar = new JLabel("Cliente / Boleta / Factura:");
        lblBuscar.setForeground(COLOR_AMARILLO);
        lblBuscar.setFont(FUENTE_CONSOLA);
        panelFiltro.add(lblBuscar);

        txtFiltro = new JTextField(20);
        estilizarCampo(txtFiltro);
        txtFiltro.addActionListener(e -> cargarDatos());
        panelFiltro.add(txtFiltro);

        JButton btnBuscar = new JButton("[ENTER] Buscar");
        estilizarBoton(btnBuscar);
        btnBuscar.addActionListener(e -> cargarDatos());
        panelFiltro.add(btnBuscar);

        add(panelFiltro, BorderLayout.NORTH);

        // 2. TABLA CON COLUMNAS REALES DE pventas_dbf
        String[] columnas = {"Código", "Cliente", "Fecha", "Boleta", "Factura", "Guía", "Total ($)"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaVentas = new JTable(modeloTabla);
        tablaVentas.setBackground(COLOR_AZUL_FONDO);
        tablaVentas.setForeground(COLOR_AMARILLO);
        tablaVentas.setFont(FUENTE_CONSOLA);
        tablaVentas.setSelectionBackground(COLOR_SELECCION);
        tablaVentas.setSelectionForeground(Color.WHITE);
        tablaVentas.getTableHeader().setBackground(COLOR_AZUL_FONDO);
        tablaVentas.getTableHeader().setForeground(COLOR_AMARILLO);
        tablaVentas.getTableHeader().setFont(FUENTE_CONSOLA);
        tablaVentas.setRowHeight(22);
        tablaVentas.setShowGrid(false);
        tablaVentas.setIntercellSpacing(new Dimension(0, 0));
        tablaVentas.setGridColor(COLOR_CIAN);

        JScrollPane scroll = new JScrollPane(tablaVentas);
        scroll.getViewport().setBackground(COLOR_AZUL_FONDO);
        scroll.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(COLOR_CIAN, 2), " REGISTROS DE VENTAS ",
                0, 0, FUENTE_CONSOLA, COLOR_CIAN
        ));
        ((javax.swing.border.TitledBorder) scroll.getBorder()).setTitleColor(COLOR_AMARILLO);
        add(scroll, BorderLayout.CENTER);

        // 3. BARRA INFERIOR ROJA CON ATAJOS (Estilo Clipper Auténtico)
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 6));
        panelInferior.setBackground(COLOR_ROJO_BARRA);
        panelInferior.setBorder(new LineBorder(COLOR_AMARILLO, 1));

        JLabel lblAtajos = new JLabel("(Esc) Salir   |   (F2) Modificar   |   (Supr) Eliminar");
        lblAtajos.setForeground(Color.WHITE);
        lblAtajos.setFont(FUENTE_CONSOLA);
        panelInferior.add(lblAtajos);

        add(panelInferior, BorderLayout.SOUTH);

        configurarAtajos();
        cargarDatos();
    }

    private void estilizarCampo(JTextField campo) {
        campo.setBackground(COLOR_AZUL_FONDO);
        campo.setForeground(COLOR_AMARILLO);
        campo.setCaretColor(COLOR_AMARILLO);
        campo.setFont(FUENTE_CONSOLA);
        campo.setBorder(new LineBorder(COLOR_CIAN, 1));
    }

    private void estilizarBoton(JButton boton) {
        boton.setBackground(COLOR_AZUL_FONDO);
        boton.setForeground(COLOR_AMARILLO);
        boton.setFont(FUENTE_CONSOLA);
        boton.setBorder(new LineBorder(COLOR_CIAN, 1));
        boton.setFocusPainted(false);
    }

    private void configurarAtajos() {
        JRootPane root = this.getRootPane();

        // [ESC] Cerrar ventana
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cerrar");
        root.getActionMap().put("cerrar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });

        // [SUPR] Eliminar registro
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "eliminar");
        root.getActionMap().put("eliminar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                eliminarSeleccionado();
            }
        });

        // [F2] Atajo global interceptado directamente mediante KeyboardFocusManager
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                // Verificamos que sea el evento de presionar la tecla F2 y que esta ventana sea la activa
                if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_F2) {
                    // Validamos que esta ventana (FrmMantencionVentas) sea la que tiene el foco actual
                    if (FrmMantencionVentas.this.isDisplayable() && FrmMantencionVentas.this.isSelected()) {
                        abrirModificacion();
                        e.consume(); // Consumimos el evento para que la tabla no haga nada más
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void cargarDatos() {
        modeloTabla.setRowCount(0);
        String texto = txtFiltro.getText().trim();

        try (Connection con = Conexion.getConnection()) {
            VentaDAO dao = new VentaDAO();
            List<VentaDTO> lista = dao.obtenerVentas(con, texto);

            for (VentaDTO v : lista) {
                modeloTabla.addRow(new Object[]{
                    v.getVcodigo(),
                    v.getVcliente(),
                    v.getVfecha(),
                    v.getVboleta(),
                    v.getVfactura(),
                    v.getVguia(),
                    String.format("$ %,.0f", v.getVpxtotal())
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar ventas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarSeleccionado() {
        int fila = tablaVentas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro de venta para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        long codigo = (long) modeloTabla.getValueAt(fila, 0);

        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de eliminar el registro ID: " + codigo + "?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = Conexion.getConnection()) {
                VentaDAO dao = new VentaDAO();
                if (dao.eliminarVenta(con, codigo)) {
                    JOptionPane.showMessageDialog(this, "Venta eliminada correctamente.");
                    cargarDatos();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar el registro.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void abrirModificacion() {
        int fila = tablaVentas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un registro de venta en la tabla para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        VentaDTO v = new VentaDTO();
        v.setVcodigo((long) modeloTabla.getValueAt(fila, 0));
        v.setVcliente((String) modeloTabla.getValueAt(fila, 1));
        v.setVfecha((String) modeloTabla.getValueAt(fila, 2));
        v.setVboleta((String) modeloTabla.getValueAt(fila, 3));
        v.setVfactura((String) modeloTabla.getValueAt(fila, 4));
        v.setVguia((String) modeloTabla.getValueAt(fila, 5));

        String totalStr = modeloTabla.getValueAt(fila, 6).toString().replaceAll("[^0-9]", "");
        v.setVpxtotal(totalStr.isEmpty() ? 0.0 : Double.parseDouble(totalStr));

        // Lanzamos el diálogo flotante modal pasando la ventana principal como referencia
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        FrmModificarVenta frmMod = new FrmModificarVenta(parentWindow, v);
        frmMod.setVisible(true);

        // Si guardó exitosamente, recargamos la tabla de inmediato
        if (frmMod.isGuardadoExitoso()) {
            cargarDatos();
        }
    }

    public void recargarDatosExternos() {
        cargarDatos();
    }
}
