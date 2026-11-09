package vista;

import dao.ClienteDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DlgBuscarCliente extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DlgBuscarCliente.class.getName());
    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;

    // Objeto o arreglo para retornar el cliente seleccionado (ajustalo a tu modelo de datos)
    private String[] clienteSeleccionado = null;

    /**
     * Creates new form DlgBuscarCliente
     *
     * @param parent
     * @param modal
     */
    // Cambia 'java.awt.Frame' por 'java.awt.Window'
    public DlgBuscarCliente(java.awt.Window parent, boolean modal) {
        super(parent, "Búsqueda de Clientes", ModalityType.APPLICATION_MODAL);
        setSize(900, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        initComponents();
        initComponents1();
        cargarClientesDesdeBD("");
    }

    private void initComponents1() {
        setLayout(new BorderLayout(10, 10));

        // Panel superior para búsqueda
        JPanel panelNorte = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        panelNorte.add(new JLabel("Buscar (RUT o Nombre):"));

        txtBuscar = new JTextField(25);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelNorte.add(txtBuscar);

        add(panelNorte, BorderLayout.NORTH);

        // Definición de las columnas solicitadas
        String[] columnas = {
            "RUT", "Nombre", "Dirección", "Comuna", "Sector",
            "Celular", "Cond. Pago", "Días", "Plazo", "Ciudad"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla de sólo lectura
            }
        };

        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setRowHeight(25);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(tablaClientes);
        add(scrollPane, BorderLayout.CENTER);

        // Panel inferior con instrucciones
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JLabel lblAyuda = new JLabel("Presiona [ENTER] o haz doble clic para seleccionar un cliente | [ESC] para salir");
        lblAyuda.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        panelSur.add(lblAyuda);
        add(panelSur, BorderLayout.SOUTH);

        // Evento de selección por doble clic
        tablaClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    seleccionarCliente();
                }
            }
        });

        // Evento de teclado (Enter para seleccionar, Esc para cerrar)
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

        txtBuscar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dispose();
                }
                // Aquí puedes filtrar el modeloTabla según lo que escribas en txtBuscar
            }
        });
    }

    private void cargarClientesDesdeBD(String filtro) {
        modeloTabla.setRowCount(0); // Limpiar tabla

        ClienteDAO dao = new ClienteDAO();
        java.util.List<Object[]> clientes = dao.cargarClientesDAO(filtro);
        for (Object[] fila : clientes) {
            modeloTabla.addRow(fila);
        }
    }

    private void seleccionarCliente() {
        int filaFila = tablaClientes.getSelectedRow();
        if (filaFila != -1) {
            int numColumnas = modeloTabla.getColumnCount();
            clienteSeleccionado = new String[numColumnas];
            for (int i = 0; i < numColumnas; i++) {
                clienteSeleccionado[i] = String.valueOf(modeloTabla.getValueAt(filaFila, i));
            }
            dispose(); // Cierra el diálogo y deja disponible el clienteSeleccionado
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un cliente de la lista.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    public String[] getClienteSeleccionado() {
        return clienteSeleccionado;
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
            .addGap(0, 796, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 630, Short.MAX_VALUE)
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
                DlgBuscarCliente dialog = new DlgBuscarCliente(new javax.swing.JFrame(), true);
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
