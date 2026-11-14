package vista.compras;

import dao.ProveedorDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DlgProveedores extends JDialog {

    private JTable tablaProveedores;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtBuscar;

    // Paleta de Colores Estilo Clipper Pro / Casa Victoria
    private final Color COLOR_AZUL_FONDO = new Color(0, 0, 128);       // Azul clásico DOS (#000080)
    private final Color COLOR_AMARILLO_TEXTO = new Color(255, 255, 0);   // Amarillo brillante (#FFFF00)
    private final Color COLOR_BLANCO_TEXTO = new Color(255, 255, 255);   // Blanco puro
    private final Color COLOR_SELECCION = new Color(0, 128, 192);        // Azul selección celestesoso
    private final Color COLOR_ROJO_ACCION = new Color(178, 34, 34);      // Rojo institucional del sistema
    private final Color COLOR_GRIS_STATUS = new Color(20, 20, 80);       // Azul oscuro para barra de estado

    public DlgProveedores(Window parent) {
        super(parent, "Maestro de Proveedores - Casa Victoria", ModalityType.APPLICATION_MODAL);
        setSize(1150, 680);
        setLocationRelativeTo(parent);
        initComponentsCustom();
        cargarDatosDesdeBD();
    }

    private void initComponentsCustom() {
        // Panel Principal con fondo azul MS-DOS y margen exterior
        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 0));
        panelPrincipal.setBackground(COLOR_AZUL_FONDO);
        panelPrincipal.setBorder(BorderFactory.createLineBorder(COLOR_AMARILLO_TEXTO, 2));

        // 1. ENCABEZADO Y TÍTULO DE LA SECCIÓN
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setBackground(COLOR_AZUL_FONDO);
        panelNorte.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        JLabel lblTitulo = new JLabel("=== MAESTRO DE PROVEEDORES ===", JLabel.CENTER);
        lblTitulo.setFont(new Font("Consolas", Font.BOLD, 18));
        lblTitulo.setForeground(COLOR_AMARILLO_TEXTO);
        panelNorte.add(lblTitulo, BorderLayout.NORTH);

        // Panel de búsqueda rápida integrada con estilo terminal
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBusqueda.setBackground(COLOR_AZUL_FONDO);

        JLabel lblBuscar = new JLabel("BUSCAR (R.U.T. o Nombre): ");
        lblBuscar.setFont(new Font("Consolas", Font.BOLD, 13));
        lblBuscar.setForeground(COLOR_AMARILLO_TEXTO);

        txtBuscar = new JTextField(30);
        txtBuscar.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtBuscar.setBackground(Color.BLACK);
        txtBuscar.setForeground(COLOR_AMARILLO_TEXTO);
        txtBuscar.setCaretColor(Color.YELLOW);
        txtBuscar.setBorder(BorderFactory.createLineBorder(COLOR_AMARILLO_TEXTO));

        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrarProveedores();
            }
        });

        panelBusqueda.add(lblBuscar);
        panelBusqueda.add(txtBuscar);
        panelNorte.add(panelBusqueda, BorderLayout.SOUTH);

        panelPrincipal.add(panelNorte, BorderLayout.NORTH);

        // 2. TABLA CENTRAL CON ESTILO CLÁSICO
        String[] columnas = {"R.U.T.", "NOMBRE DEL PROVEEDOR", "CIUDAD"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaProveedores = new JTable(modeloTabla);
        tablaProveedores.setFont(new Font("Consolas", Font.PLAIN, 13));
        tablaProveedores.setBackground(Color.BLACK);
        tablaProveedores.setForeground(COLOR_BLANCO_TEXTO);
        tablaProveedores.setSelectionBackground(COLOR_SELECCION);
        tablaProveedores.setSelectionForeground(Color.WHITE);
        tablaProveedores.setRowHeight(25);
        tablaProveedores.setShowGrid(true);
        tablaProveedores.setGridColor(new Color(0, 0, 80));

        // Cabecera de la tabla
        tablaProveedores.getTableHeader().setFont(new Font("Consolas", Font.BOLD, 13));
        tablaProveedores.getTableHeader().setBackground(COLOR_AZUL_FONDO);
        tablaProveedores.getTableHeader().setForeground(COLOR_AMARILLO_TEXTO);
        tablaProveedores.getTableHeader().setBorder(BorderFactory.createLineBorder(COLOR_AMARILLO_TEXTO));

        // Alineación de columnas
        DefaultTableCellRenderer tcrIzq = new DefaultTableCellRenderer();
        tcrIzq.setHorizontalAlignment(JLabel.LEFT);
        DefaultTableCellRenderer tcrCentro = new DefaultTableCellRenderer();
        tcrCentro.setHorizontalAlignment(JLabel.CENTER);

        tablaProveedores.getColumnModel().getColumn(0).setCellRenderer(tcrCentro); // RUT
        tablaProveedores.getColumnModel().getColumn(1).setCellRenderer(tcrIzq);    // Nombre
        tablaProveedores.getColumnModel().getColumn(2).setCellRenderer(tcrIzq);    // Ciudad

        // Anchos proporcionales
        tablaProveedores.getColumnModel().getColumn(0).setPreferredWidth(180);
        tablaProveedores.getColumnModel().getColumn(1).setPreferredWidth(600);
        tablaProveedores.getColumnModel().getColumn(2).setPreferredWidth(250);

        sorter = new TableRowSorter<>(modeloTabla);
        tablaProveedores.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(tablaProveedores);
        scrollPane.getViewport().setBackground(Color.BLACK);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 15, 0, 15),
                BorderFactory.createLineBorder(COLOR_AMARILLO_TEXTO)
        ));
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // 3. ZONA INFERIOR: ATAJOS Y BARRA DE ESTADO INSTITUCIONAL
        JPanel panelSurTotal = new JPanel(new BorderLayout());
        panelSurTotal.setBackground(COLOR_AZUL_FONDO);

        // A. Barra de Atajos con Corchetes (Rojo Institucional)
        JLabel lblAcciones = new JLabel("  [Ins]ertar    [Supr]imir    [M]odificar    [V]er    [B]uscar    [Esc] Salir  ", JLabel.CENTER);
        lblAcciones.setFont(new Font("Consolas", Font.BOLD, 13));
        lblAcciones.setForeground(COLOR_AMARILLO_TEXTO);

        JPanel panelAcciones = new JPanel(new BorderLayout());
        panelAcciones.setBackground(COLOR_ROJO_ACCION);
        panelAcciones.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        panelAcciones.add(lblAcciones, BorderLayout.CENTER);
        panelSurTotal.add(panelAcciones, BorderLayout.NORTH);

        // B. Barra de Estado Inferior (Estilo GORISOFT / CASA VICTORIA / Fecha)
        JPanel panelEstado = new JPanel(new BorderLayout());
        panelEstado.setBackground(COLOR_GRIS_STATUS);
        panelEstado.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        JLabel lblEmpresa = new JLabel("CASA VICTORIA");
        lblEmpresa.setFont(new Font("Consolas", Font.BOLD, 12));
        lblEmpresa.setForeground(COLOR_BLANCO_TEXTO);

        // Fecha actual formateada igual a la foto (dd/MM/yyyy)
        String fechaActual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        JLabel lblFecha = new JLabel(fechaActual + "  ");
        lblFecha.setFont(new Font("Consolas", Font.BOLD, 12));
        lblFecha.setForeground(COLOR_AMARILLO_TEXTO);

        panelEstado.add(lblEmpresa, BorderLayout.CENTER);
        panelEstado.add(lblFecha, BorderLayout.EAST);

        panelSurTotal.add(panelEstado, BorderLayout.SOUTH);
        panelPrincipal.add(panelSurTotal, BorderLayout.SOUTH);

        add(panelPrincipal);

        // Configurar atajos de teclado globales
        setupKeyBindings();

    }

    private void setupKeyBindings() {
        JRootPane root = getRootPane();

        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "salir");
        root.getActionMap().put("salir", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });

        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0), "insertar");
        root.getActionMap().put("insertar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                DlgInsertarProveedor dlg = new DlgInsertarProveedor(DlgProveedores.this);
                dlg.setVisible(true);
                // Refrescamos la tabla al volver
                cargarDatosDesdeBD();
            }
        });

        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "eliminar");
        root.getActionMap().put("eliminar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int fila = tablaProveedores.getSelectedRow();
                if (fila != -1) {
                    int filaModelo = tablaProveedores.convertRowIndexToModel(fila);

                    // Extraemos los datos para mostrar el mensaje de confirmación
                    String rutCompletoStr = (String) modeloTabla.getValueAt(filaModelo, 0); // Ej: "76474780-1"
                    String nombreProveedor = (String) modeloTabla.getValueAt(filaModelo, 1);

                    int opcion = JOptionPane.showConfirmDialog(
                            DlgProveedores.this,
                            "¿Desea eliminar al proveedor: " + nombreProveedor + "?",
                            "Confirmar Eliminación",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (opcion == JOptionPane.YES_OPTION) {
                        try {
                            // Obtenemos la parte numérica del RUT para buscarla y borrarla
                            int rutBuscado = Integer.parseInt(rutCompletoStr.split("-")[0]);

                            ProveedorDAO dao = new ProveedorDAO();
                            boolean exito = dao.eliminarProveedor(rutBuscado);

                            if (exito) {
                                JOptionPane.showMessageDialog(DlgProveedores.this, "Proveedor eliminado con éxito.", "Eliminado", JOptionPane.INFORMATION_MESSAGE);
                                // Refrescamos la tabla para que desaparezca el registro
                                cargarDatosDesdeBD();
                            } else {
                                JOptionPane.showMessageDialog(DlgProveedores.this, "No se pudo eliminar el proveedor de la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(DlgProveedores.this, "Error de sistema al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(DlgProveedores.this, "Por favor seleccione un proveedor para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_M, 0), "modificar");
        root.getActionMap().put("modificar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                int fila = tablaProveedores.getSelectedRow();
                if (fila != -1) {
                    int filaModelo = tablaProveedores.convertRowIndexToModel(fila);
                    String rutCompletoStr = (String) modeloTabla.getValueAt(filaModelo, 0); // Ej: "76474780-1"

                    try {
                        // Extraemos la parte numérica del RUT antes del guion para buscarlo en la BD
                        int rutBuscado = Integer.parseInt(rutCompletoStr.split("-")[0]);

                        DlgModificarProveedor dlg = new DlgModificarProveedor(DlgProveedores.this, rutBuscado);
                        dlg.setVisible(true);

                        // Refrescamos la tabla al cerrar la ventana de edición
                        cargarDatosDesdeBD();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(DlgProveedores.this, "Error al procesar el RUT del proveedor seleccionado.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(DlgProveedores.this, "Por favor seleccione un proveedor para modificar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_B, 0), "focalizarBuscar");
        root.getActionMap().put("focalizarBuscar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                txtBuscar.requestFocus();
                txtBuscar.selectAll();
            }
        });

        // ENTER dentro del buscador pasa el foco a la tabla para empezar a navegar con las flechas
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (tablaProveedores.getRowCount() > 0) {
                        tablaProveedores.requestFocus();
                        tablaProveedores.setRowSelectionInterval(0, 0);
                    }
                }
            }
        });
    }

    private void filtrarProveedores() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            // Filtra ignorando mayúsculas/minúsculas en la columna 0 (RUT) y columna 1 (Nombre)
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(texto), 0, 1));
        }
    }

    private void cargarDatosDesdeBD() {
        modeloTabla.setRowCount(0); // Limpiamos la tabla antes de cargar
        try {
            dao.ProveedorDAO dao = new dao.ProveedorDAO();
            java.util.List<Object[]> lista = dao.obtenerProveedoresParaTabla();
            for (Object[] fila : lista) {
                modeloTabla.addRow(fila);
            }
        } catch (Exception ex) {
            System.err.println("Error al cargar proveedores en la vista: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}
