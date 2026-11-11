        package vista;

import dao.ProductoVentaDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class DlgBuscadorProductos extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DlgBuscadorProductos.class.getName());

    private JList<String> listaProductos;
    private DefaultListModel<String> modeloLista;
    private String productoSeleccionado = null;
    private String codigoBarraSeleccionado = "";
    

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

        // Cargamos los productos reales desde la BD de PostgreSQL
        modeloLista = new DefaultListModel<>();
        cargarProductosDesdeBD(filtroInicial);

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

    private void cargarProductosDesdeBD(String filtro) {
        modeloLista.clear();

        ProductoVentaDAO dao = new ProductoVentaDAO();
        java.util.List<Object[]> lista = dao.buscarProductosYVendedorDAO(filtro);

        for (Object[] prod : lista) {
            // [0] = codigo_barra, [1] = descripcion, [2] = color, [3] = precio
            String barra = prod[0] != null ? prod[0].toString() : "";
            String desc = prod[1] != null ? prod[1].toString() : "";
            String color = prod[2] != null ? prod[2].toString() : "";
            double precio = prod[3] != null ? Double.parseDouble(prod[3].toString()) : 0.0;

            // Formateamos una línea limpia para el JList (ej: [TEN] - TENDEREDO ($36.000))
            String itemTexto = String.format("[%-10s] %s %s - $%,.0f",
                    barra,
                    desc,
                    (!color.isEmpty() && !color.equalsIgnoreCase("NULL") ? "(" + color + ")" : ""),
                    precio
            );

            modeloLista.addElement(itemTexto);
        }
    }

    private void seleccionarYSalir() {
        String seleccionado = listaProductos.getSelectedValue();
        if (seleccionado != null && !seleccionado.startsWith("No se encontraron")) {
            // El formato que guardamos es: "[TEN]    TENDEREDO    $36,000.00"
            try {
                // Extraemos el código de barra que está entre los corchetes [...]
                int inicioCorchete = seleccionado.indexOf("[");
                int finCorchete = seleccionado.indexOf("]");
                if (inicioCorchete != -1 && finCorchete != -1) {
                    codigoBarraSeleccionado = seleccionado.substring(inicioCorchete + 1, finCorchete).trim();
                }

                // Extraemos la descripción (lo que está entre el ']' y el símbolo '$')
                int indiceDolar = seleccionado.lastIndexOf("$");
                if (finCorchete != -1 && indiceDolar != -1) {
                    productoSeleccionado = seleccionado.substring(finCorchete + 1, indiceDolar).trim();
                }
            } catch (Exception e) {
                System.err.println("Error al parsear el ítem seleccionado: " + e.getMessage());
            }
        }
        dispose();
    }

    public String getCodigoSeleccionado() {
        return codigoBarraSeleccionado;
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
        java.awt.EventQueue.invokeLater(() -> {
            DlgBuscadorProductos dialog = new DlgBuscadorProductos(new javax.swing.JFrame(), true);
            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
