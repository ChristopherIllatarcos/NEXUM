package vista.ctacte;

import dao.ClienteDAO; // O el DAO que use tu sistema para buscar clientes
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class FrmBuscarCliente extends JDialog {

    private JTextField txtFiltro;
    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private String rutSeleccionado = null;
    
    // Paleta de colores terminal / Clipper Pro
    private final Color COLOR_AZUL_FONDO = new Color(0, 0, 132);
    private final Color COLOR_TEXTO_BLANCO = Color.WHITE;
    private final Color COLOR_TEXTO_VERDE = Color.GREEN;
    private final Color COLOR_CIAN = Color.CYAN;
    private final Font FUENTE_CONSOLA = new Font("Courier New", Font.BOLD, 13);

    public FrmBuscarCliente(Window parent) {
        super(parent, " BUSCAR CLIENTE ", ModalityType.APPLICATION_MODAL);
        setSize(850, 550);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(COLOR_AZUL_FONDO);
        panelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));

        // ---------------------------------------------------------
        // 1. PANEL SUPERIOR: Input de búsqueda rápida
        // ---------------------------------------------------------
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelBusqueda.setBackground(COLOR_AZUL_FONDO);
        panelBusqueda.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(COLOR_CIAN, 2), " CRITERIO (RUT / NOMBRE) ", 
            0, 0, FUENTE_CONSOLA, COLOR_CIAN
        ));

        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(FUENTE_CONSOLA);
        lblBuscar.setForeground(COLOR_TEXTO_BLANCO);
        panelBusqueda.add(lblBuscar);

        txtFiltro = new JTextField(35);
        txtFiltro.setFont(FUENTE_CONSOLA);
        txtFiltro.setBackground(Color.BLACK);
        txtFiltro.setForeground(COLOR_TEXTO_VERDE);
        txtFiltro.setCaretColor(COLOR_TEXTO_VERDE);
        txtFiltro.setBorder(new LineBorder(COLOR_CIAN, 1));
        panelBusqueda.add(txtFiltro);

        panelPrincipal.add(panelBusqueda, BorderLayout.NORTH);

        // ---------------------------------------------------------
        // 2. PANEL CENTRAL: Tabla de resultados
        // ---------------------------------------------------------
        String[] columnas = {"RUT", "Nombre / Razón Social"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setFont(FUENTE_CONSOLA);
        tablaClientes.setBackground(Color.BLACK);
        tablaClientes.setForeground(COLOR_TEXTO_VERDE);
        tablaClientes.setGridColor(COLOR_CIAN);
        tablaClientes.setRowHeight(22);
        tablaClientes.getTableHeader().setFont(FUENTE_CONSOLA);
        tablaClientes.getTableHeader().setBackground(new Color(0, 0, 80));
        tablaClientes.getTableHeader().setForeground(COLOR_TEXTO_BLANCO);

        JScrollPane scrollTabla = new JScrollPane(tablaClientes);
        scrollTabla.getViewport().setBackground(Color.BLACK);
        scrollTabla.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(COLOR_CIAN, 2), " SELECCIONE CLIENTE ", 
            0, 0, FUENTE_CONSOLA, COLOR_CIAN
        ));
        panelPrincipal.add(scrollTabla, BorderLayout.CENTER);

        // ---------------------------------------------------------
        // 3. PANEL INFERIOR: Barra de Ayuda
        // ---------------------------------------------------------
        JPanel panelAyuda = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 2));
        panelAyuda.setBackground(new Color(0, 0, 80));
        JLabel lblAyuda = new JLabel(" [ESC] Cancelar    [ENTER] Seleccionar ");
        lblAyuda.setFont(new Font("Courier New", Font.BOLD, 12));
        lblAyuda.setForeground(COLOR_CIAN);
        panelAyuda.add(lblAyuda);
        panelPrincipal.add(panelAyuda, BorderLayout.SOUTH);

        add(panelPrincipal);

        // ---------------------------------------------------------
        // EVENTOS Y NAVEGACIÓN
        // ---------------------------------------------------------
        // Cargar todos los clientes inicialmente
        cargarClientes("");

        // Filtrar en tiempo real mientras escribe
        txtFiltro.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (tablaClientes.getRowCount() > 0) {
                        tablaClientes.setRowSelectionInterval(0, 0);
                        tablaClientes.requestFocus();
                    }
                } else {
                    cargarClientes(txtFiltro.getText().trim());
                }
            }
        });

        // Seleccionar con Enter en la tabla o doble clic
        tablaClientes.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    seleccionarCliente();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });

        tablaClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2 && tablaClientes.getSelectedRow() != -1) {
                    seleccionarCliente();
                }
            }
        });

        // Salir con ESC desde el campo de texto
        txtFiltro.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
            }
        });
    }

    private void cargarClientes(String filtro) {
        modeloTabla.setRowCount(0);
        try {
            ClienteDAO dao = new ClienteDAO();
            List<Object[]> lista = dao.cargarClientesDAO(filtro); // O tu método DAO correspondiente
            for (Object[] cli : lista) {
                modeloTabla.addRow(new Object[]{
                    cli[0], // RUT
                    cli[1]  // Nombre
                });
            }
        } catch (Exception ex) {
            // Manejo silencioso o log de error
        }
    }

    private void seleccionarCliente() {
        int fila = tablaClientes.getSelectedRow();
        if (fila != -1) {
            rutSeleccionado = modeloTabla.getValueAt(fila, 0).toString();
            dispose();
        }
    }

    public String getRutSeleccionado() {
        return rutSeleccionado;
    }
}