package vista.bodega;

import dao.ProductoVentaDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class DlgProductos extends JInternalFrame {

    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtBuscar;

    // Colores Estilo Clipper / MS-DOS Pro
    private final Color COLOR_AZUL_FONDO = new Color(0, 0, 128);       // Azul clásico DOS
    private final Color COLOR_AMARILLO_TEXTO = new Color(255, 255, 0);   // Amarillo brillante
    private final Color COLOR_BLANCO_TEXTO = new Color(255, 255, 255);   // Blanco
    private final Color COLOR_SELECCION = new Color(0, 128, 192);        // Celeste selección
    private final Color COLOR_ROJO_ACCION = new Color(178, 34, 34);      // Rojo institucional del sistema para la barra inferior

    public DlgProductos() {
        // Configuración como JInternalFrame: título, resizabel, closable, maximizable, iconifiable
        super("Fichero Productos (Principal)", true, true, true, true);

        // Ocultar botones modernos y mantener estética limpia de consola si se prefiere
        putClientProperty("JInternalFrame.isPalette", Boolean.FALSE);

        // Tamaño amplio para aprovechar el escritorio MDI
        setSize(1150, 650);
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        // Panel Principal con fondo azul MS-DOS
        JPanel panelPrincipal = new JPanel(new BorderLayout(5, 5));
        panelPrincipal.setBackground(COLOR_AZUL_FONDO);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Evento global para cerrar con la tecla ESC
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        panelPrincipal.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "cerrar");
        panelPrincipal.getActionMap().put("cerrar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });

        // 1. TÍTULO SUPERIOR Y BUSCADOR
        JLabel lblTitulo = new JLabel("=== FICHERO PRODUCTOS (PRINCIPAL) ===", JLabel.CENTER);
        lblTitulo.setFont(new Font("Consolas", Font.BOLD, 18));
        lblTitulo.setForeground(COLOR_AMARILLO_TEXTO);

        JPanel panelNorte = new JPanel(new BorderLayout(5, 5));
        panelNorte.setBackground(COLOR_AZUL_FONDO);
        panelNorte.add(lblTitulo, BorderLayout.NORTH);

        // Barra de búsqueda rápida integrada
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panelBusqueda.setBackground(COLOR_AZUL_FONDO);

        JLabel lblBuscar = new JLabel("BUSCAR (Descripción o Barra): ");
        lblBuscar.setFont(new Font("Consolas", Font.BOLD, 16));
        lblBuscar.setForeground(COLOR_AMARILLO_TEXTO);

        txtBuscar = new JTextField(35);
        txtBuscar.setFont(new Font("Consolas", Font.PLAIN, 14));
        txtBuscar.setBackground(Color.BLACK);
        txtBuscar.setForeground(COLOR_AMARILLO_TEXTO);
        txtBuscar.setCaretColor(Color.YELLOW);
        txtBuscar.setBorder(BorderFactory.createLineBorder(COLOR_AMARILLO_TEXTO));

        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrarProductos();
            }
        });

        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscar);
        panelNorte.add(panelBusqueda, BorderLayout.SOUTH);

        panelPrincipal.add(panelNorte, BorderLayout.NORTH);

        // 2. TABLA DE PRODUCTOS
        String[] columnas = {"DESCRIPCIÓN", "#BARRA", "PRECIO"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Solo lectura en tabla principal
            }
        };

        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setFont(new Font("Consolas", Font.PLAIN, 14));
        tablaProductos.setBackground(Color.BLACK);
        tablaProductos.setForeground(COLOR_BLANCO_TEXTO);
        tablaProductos.setSelectionBackground(COLOR_SELECCION);
        tablaProductos.setSelectionForeground(Color.WHITE);
        tablaProductos.setRowHeight(24);
        tablaProductos.setShowGrid(false);

        // Cabecera de la tabla estilo Clipper
        tablaProductos.getTableHeader().setFont(new Font("Consolas", Font.BOLD, 16));
        tablaProductos.getTableHeader().setBackground(COLOR_AZUL_FONDO);
        tablaProductos.getTableHeader().setForeground(COLOR_AMARILLO_TEXTO);

        // Renderizadores para alinear columnas
        DefaultTableCellRenderer tcrIzq = new DefaultTableCellRenderer();
        tcrIzq.setHorizontalAlignment(JLabel.LEFT);
        DefaultTableCellRenderer tcrCentro = new DefaultTableCellRenderer();
        tcrCentro.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer tcrDer = new DefaultTableCellRenderer();
        tcrDer.setHorizontalAlignment(JLabel.RIGHT);

        tablaProductos.getColumnModel().getColumn(0).setCellRenderer(tcrIzq);
        tablaProductos.getColumnModel().getColumn(1).setCellRenderer(tcrCentro);
        tablaProductos.getColumnModel().getColumn(2).setCellRenderer(tcrDer);

        // Anchos de columna proporcionales
        tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(500);
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(250);
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(150);

        sorter = new TableRowSorter<>(modeloTabla);
        tablaProductos.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        scrollPane.getViewport().setBackground(Color.BLACK);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_AMARILLO_TEXTO));
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // 3. BARRA DE ACCIONES INFERIOR (CON EL ROJO INSTITUCIONAL)
        JLabel lblAcciones = new JLabel(" (INS) Insertar     (M) Modificar     (SUPR) Eliminar     (ESC) Salir ", JLabel.CENTER);
        lblAcciones.setFont(new Font("Consolas", Font.BOLD, 16));
        lblAcciones.setForeground(COLOR_AMARILLO_TEXTO);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBackground(COLOR_ROJO_ACCION);
        panelSur.setBorder(BorderFactory.createEmptyBorder(10, 4, 10, 4));
        panelSur.add(lblAcciones, BorderLayout.CENTER);
        panelPrincipal.add(panelSur, BorderLayout.SOUTH);

        add(panelPrincipal);

        // 4. CONFIGURACIÓN DE TECLAS RÁPIDAS
        setupKeyBindings();

        // Carga inicial de datos al abrir la ventana
        cargarDatosIniciales();
    }

    private void setupKeyBindings() {
        JRootPane root = getRootPane();

        // INS para Insertar
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0), "insertar");
        root.getActionMap().put("insertar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                accionInsertar();
            }
        });

        // M para Modificar
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_M, 0), "modificar");
        root.getActionMap().put("modificar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                accionModificar();
            }
        });

        // SUPR para Eliminar
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "eliminar");
        root.getActionMap().put("eliminar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                accionEliminar();
            }
        });
    }

    private void filtrarProductos() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(texto), 0, 1));
        }
    }

    public void cargarProductosEnTabla(List<Object[]> listaProductos) {
        modeloTabla.setRowCount(0);
        if (listaProductos != null) {
            for (Object[] fila : listaProductos) {
                modeloTabla.addRow(fila);
            }
        }
    }

    private void cargarDatosIniciales() {
        try {
            ProductoVentaDAO dao = new ProductoVentaDAO();
            List<Object[]> lista = dao.obtenerProductosParaTabla();
            cargarProductosEnTabla(lista);
        } catch (Exception ex) {
            System.err.println("Error al cargar productos iniciales: " + ex.getMessage());
        }
    }

    private void accionInsertar() {
        // Nota: Si DlgInsertarProducto es un JDialog modal, se le pasa SwingUtilities.getWindowAncestor(this) como padre
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        DlgInsertarProducto dlgInsertar = new DlgInsertarProducto(parentWindow);
        dlgInsertar.setVisible(true);

        // Al cerrarse, refrescamos la tabla principal
        try {
            ProductoVentaDAO dao = new ProductoVentaDAO();
            List<Object[]> lista = dao.obtenerProductosParaTabla();
            cargarProductosEnTabla(lista);
        } catch (Exception ex) {
            System.err.println("Error al refrescar tabla: " + ex.getMessage());
        }
    }

    private void accionModificar() {
        int fila = tablaProductos.getSelectedRow();
        if (fila != -1) {
            int filaModelo = tablaProductos.convertRowIndexToModel(fila);
            String barra = (String) modeloTabla.getValueAt(filaModelo, 1); // Asumiendo columna 1 es la barra

            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            DlgModificarProducto dlg = new DlgModificarProducto(parentWindow, barra);
            dlg.setVisible(true);

            try {
                ProductoVentaDAO daoProd = new ProductoVentaDAO();
                cargarProductosEnTabla(daoProd.obtenerProductosParaTabla());
            } catch (Exception ex) {
                System.err.println("Error al recargar tabla tras modificar: " + ex.getMessage());
            }

        } else {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un producto para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void accionEliminar() {
        int fila = tablaProductos.getSelectedRow();
        if (fila != -1) {
            int filaModelo = tablaProductos.convertRowIndexToModel(fila);

            String desc = (String) modeloTabla.getValueAt(filaModelo, 0);
            String barra = (String) modeloTabla.getValueAt(filaModelo, 1);

            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Desea eliminar el producto: " + desc + "?",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION
            );

            if (opcion == JOptionPane.YES_OPTION) {
                try {
                    ProductoVentaDAO dao = new ProductoVentaDAO();
                    boolean exito = dao.eliminarProducto(barra);

                    if (exito) {
                        JOptionPane.showMessageDialog(this, "Producto eliminado con éxito.", "Eliminado", JOptionPane.INFORMATION_MESSAGE);
                        List<Object[]> lista = dao.obtenerProductosParaTabla();
                        cargarProductosEnTabla(lista);
                    } else {
                        JOptionPane.showMessageDialog(this, "No se pudo eliminar el producto de la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error de sistema al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un producto para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }
}
