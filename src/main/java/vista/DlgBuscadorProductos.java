package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class DlgBuscadorProductos extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DlgBuscadorProductos.class.getName());

    private JList<String> listaProductos;
    private DefaultListModel<String> modeloLista;
    private String productoSeleccionado = null;

    /**
     * Creates new form DlgBuscadorProductos
     *
     * @param parent
     * @param modal
     */
    public DlgBuscadorProductos(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    public DlgBuscadorProductos(JDialog owner, String filtroInicial) {
        super(owner, "Elija Código de Producto", true);
        setSize(700, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Panel superior rojo distintivo de la opción de búsqueda
        JPanel panelEncabezado = new JPanel();
        panelEncabezado.setBackground(new Color(180, 0, 0));
        JLabel lblTitulo = new JLabel("ELIJA CODIGO DE PRODUCTO");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panelEncabezado.add(lblTitulo);
        add(panelEncabezado, BorderLayout.NORTH);

        // Lista de productos simulando la tercera imagen
        modeloLista = new DefaultListModel<>();
        cargarDatosPrueba(filtroInicial);

        listaProductos = new JList<>(modeloLista);
        listaProductos.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        listaProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        if (modeloLista.getSize() > 0) {
            listaProductos.setSelectedIndex(0);
        }

        JScrollPane scroll = new JScrollPane(listaProductos);
        add(scroll, BorderLayout.CENTER);

        // Eventos de selección con ENTER
        listaProductos.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    seleccionarYSalir();
                }
            }
        });

        listaProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    seleccionarYSalir();
                }
            }
        });

        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void cargarDatosPrueba(String filtro) {
        // Datos simulados idénticos a tu sistema clásico
        String[][] productos = {
            {"TEN", "TENDEREDO", "$36,000.00"},
            {"TETER", "TETERA 2,5 LT.", "$28,000.00"},
            {"TETE", "TETERA 4 LT ACERO", "$29,000.00"},
            {"TA", "TETERA ACERO 3LT", "$24,000.00"},
            {"TT", "TETERA ALUM 3LT", "$19,000.00"},
            {"TET", "TETERA ALUM 4LT", "$22,000.00"},
            {"TE", "TETERA ALUM 5LT", "$25,000.00"},
            {"TH", "THERMO", "$36,000.00"},
            {"TERH", "THERMO-HERVIDOR", "$68,000.00"},
            {"TOALLA", "TOALLA", "$19,000.00"},
            {"TDP", "TOALLA PLAYA", "$12,000.00"},
            {"TOALLA INF", "TOALLA PLAYA INANTIL", "$14,000.00"}
        };

        for (String[] p : productos) {
            String linea = String.format("%-15s   %-35s   %s", p[0], p[1], p[2]);
            if (filtro.isEmpty() || p[0].toLowerCase().startsWith(filtro.toLowerCase()) || p[1].toLowerCase().contains(filtro.toLowerCase())) {
                modeloLista.addElement(linea);
            }
        }
        if (modeloLista.isEmpty()) {
            modeloLista.addElement("TEN        TENDEREDO                             $36,000.00");
        }

    }

    private void seleccionarYSalir() {
        String seleccionado = listaProductos.getSelectedValue();
        if (seleccionado != null) {
            // Extraer descripción del producto seleccionado
            productoSeleccionado = seleccionado.substring(15, 50).trim();
        }
        dispose();
    }

    public String getProductoSeleccionado() {
        return productoSeleccionado;
    }
    
    public double getPrecioSeleccionado() {
        String seleccionado = listaProductos.getSelectedValue();
        if (seleccionado != null) {
            try {
                // Buscamos la parte del texto que tiene el signo '$'
                int indiceDolar = seleccionado.lastIndexOf("$");
                if (indiceDolar != -1) {
                    String strPrecio = seleccionado.substring(indiceDolar + 1)
                            .replace(".", "")
                            .replace(",", ".")
                            .trim();
                    return Double.parseDouble(strPrecio);
                }
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
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
            .addGap(0, 613, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 477, Short.MAX_VALUE)
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
                DlgBuscadorProductos dialog = new DlgBuscadorProductos(new javax.swing.JFrame(), true);
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
