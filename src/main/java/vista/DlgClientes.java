package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DlgClientes extends JDialog {

    private JTextField txtBuscar;
    private JTable tablaClientes;
    private DefaultTableModel modeloTablaClientes;
    private TableRowSorter<DefaultTableModel> sorter;

    // Variable para retornar el cliente seleccionado a la venta
    private String rutSeleccionado = null;
    private String nombreSeleccionado = null;

    public DlgClientes(Window parent) {
        super(parent, "Búsqueda de Clientes", ModalityType.APPLICATION_MODAL);
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        setSize(1000, 600);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ==========================================
        // PALETA DE COLORES Y FUENTES ESTILO MS-DOS
        // ==========================================
        Color dosBlue = new Color(0, 0, 128);       // Azul Clipper clásico
        Color dosDarkPanel = new Color(16, 16, 16);  // Fondo negro terminal
        Color dosYellow = Color.YELLOW;             // Amarillo brillante
        Color dosWhite = Color.WHITE;

        Font fuenteConsola = new Font("Consolas", Font.BOLD, 14);
        if (fuenteConsola.getFamily().equals("Dialog")) {
            fuenteConsola = new Font("Monospaced", Font.BOLD, 14);
        }

        // ==========================================
        // 1. PANEL SUPERIOR (Buscador)
        // ==========================================
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        panelSuperior.setBackground(dosBlue);
        panelSuperior.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.WHITE));

        JLabel lblBuscar = new JLabel("Buscar (RUT o Nombre):");
        lblBuscar.setForeground(dosWhite);
        lblBuscar.setFont(fuenteConsola);

        txtBuscar = new JTextField(25);
        txtBuscar.setFont(fuenteConsola);
        txtBuscar.setBackground(Color.BLACK);
        txtBuscar.setForeground(dosYellow);
        txtBuscar.setCaretColor(dosYellow);
        txtBuscar.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        panelSuperior.add(lblBuscar);
        panelSuperior.add(txtBuscar);
        add(panelSuperior, BorderLayout.NORTH);

        // ==========================================
        // 2. CENTRO (Tabla de Clientes)
        // ==========================================
        String[] columnas = {"RUT", "Nombre", "Dirección", "Comuna", "Sector", "Celular", "Cond. Pago", "Días", "Plazo", "Ciudad"};
        modeloTablaClientes = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Solo lectura para evitar edición accidental
            }
        };

        tablaClientes = new JTable(modeloTablaClientes);
        tablaClientes.setFont(fuenteConsola);
        tablaClientes.setBackground(dosDarkPanel);
        tablaClientes.setForeground(dosWhite);
        tablaClientes.setGridColor(Color.DARK_GRAY);
        tablaClientes.setRowHeight(24);
        tablaClientes.setSelectionBackground(dosYellow);
        tablaClientes.setSelectionForeground(Color.BLACK);

        tablaClientes.getTableHeader().setFont(fuenteConsola);
        tablaClientes.getTableHeader().setBackground(dosBlue);
        tablaClientes.getTableHeader().setForeground(dosYellow);

        // Sorter para filtrado automático y veloz en tiempo real
        sorter = new TableRowSorter<>(modeloTablaClientes);
        tablaClientes.setRowSorter(sorter);

        JScrollPane scrollTabla = new JScrollPane(tablaClientes);
        scrollTabla.getViewport().setBackground(dosDarkPanel);
        scrollTabla.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        add(scrollTabla, BorderLayout.CENTER);

        // ==========================================
        // 3. PANEL INFERIOR (Atajos / Instrucciones)
        // ==========================================
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        panelInferior.setBackground(dosBlue);
        panelInferior.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.WHITE));

        JLabel lblAtajos = new JLabel("(ESC): Cancelar    (ENTER): Seleccionar Cliente");
        lblAtajos.setForeground(dosYellow);
        lblAtajos.setFont(fuenteConsola);
        panelInferior.add(lblAtajos);
        add(panelInferior, BorderLayout.SOUTH);

        // ==========================================
        // 4. EVENTOS DE BÚSQUEDA Y FILTRADO
        // ==========================================
        // Filtrado en tiempo real al escribir en el campo de texto
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filtrar();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filtrar();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filtrar();
            }
        });

        // Evento de teclado en el buscador (Permite saltar a la tabla con flecha abajo o Enter)
        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN && tablaClientes.getRowCount() > 0) {
                    tablaClientes.requestFocus();
                    tablaClientes.setRowSelectionInterval(0, 0);
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    seleccionarClienteYCerrar();
                }
            }
        });

        // Evento de teclado en la tabla (Enter selecciona, Escape cierra)
        tablaClientes.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    seleccionarClienteYCerrar();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });

        // Doble clic en la tabla también selecciona el cliente
        tablaClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    seleccionarClienteYCerrar();
                }
            }
        });

        // Escape global para cerrar
        getRootPane().registerKeyboardAction(ev -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

    

        // Foco inicial en el buscador
        txtBuscar.requestFocusInWindow();
    }

    private void filtrar() {
        String texto = txtBuscar.getText().trim();
        if (texto.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            // Filtra ignorando mayúsculas/minúsculas por RUT (columna 0) o Nombre (columna 1)
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(texto), 0, 1));
        }
    }

    private void seleccionarClienteYCerrar() {
        int rowVista = tablaClientes.getSelectedRow();
        if (rowVista != -1) {
            int rowModelo = tablaClientes.convertRowIndexToModel(rowVista);
            rutSeleccionado = String.valueOf(modeloTablaClientes.getValueAt(rowModelo, 0));
            nombreSeleccionado = String.valueOf(modeloTablaClientes.getValueAt(rowModelo, 1));
            dispose();
        } else if (tablaClientes.getRowCount() == 1) {
            // Si solo queda un resultado en pantalla tras filtrar, lo selecciona automático
            rutSeleccionado = String.valueOf(modeloTablaClientes.getValueAt(0, 0));
            nombreSeleccionado = String.valueOf(modeloTablaClientes.getValueAt(0, 1));
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Por favor seleccione un cliente de la lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Métodos públicos para recuperar el cliente seleccionado desde DlgVenta
    public String getRutSeleccionado() {
        return rutSeleccionado;
    }

    public String getNombreSeleccionado() {
        return nombreSeleccionado;
    }

    public String[] getClienteSeleccionado() {
        int rowVista = tablaClientes.getSelectedRow();
        if (rowVista != -1) {
            int rowModelo = tablaClientes.convertRowIndexToModel(rowVista);
            int numCols = modeloTablaClientes.getColumnCount();
            String[] datos = new String[numCols];
            for (int i = 0; i < numCols; i++) {
                Object val = modeloTablaClientes.getValueAt(rowModelo, i);
                datos[i] = val != null ? val.toString() : "";
            }
            return datos;
        }
        return null;
    }
    
     
    public void cargarClientesEnTabla(java.util.List<Object[]> listaClientes) {
        modeloTablaClientes.setRowCount(0);
        for (Object[] fila : listaClientes) {
            modeloTablaClientes.addRow(fila);
        }
    }
}
