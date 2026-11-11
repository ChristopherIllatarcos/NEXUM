package vista;

import dao.ProductoVentaDAO;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class DlgRegistroProductos extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DlgRegistroProductos.class.getName());

    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JLabel lblBarraAcciones;

    /**
     * Creates new form DlgRegistroProductos
     *
     * @param parent
     * @param modal
     */
    public DlgRegistroProductos(Frame parent, boolean modal) {
        super(parent, "FICHERO PRODUCTOS (PRINCIPAL)", modal);
        setSize(900, 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // --- ENCABEZADO SUPERIOR AZUL/TURQUESA ---
        JPanel panelEncabezado = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelEncabezado.setBackground(new Color(0, 128, 128)); // Azul verdoso clásico
        JLabel lblTitulo = new JLabel("FICHERO PRODUCTOS (PRINCIPAL)");
        lblTitulo.setFont(new Font("Monospaced", Font.BOLD, 18));
        lblTitulo.setForeground(Color.YELLOW);
        panelEncabezado.add(lblTitulo);
        add(panelEncabezado, BorderLayout.NORTH);

        // --- TABLA DE PRODUCTOS (Estilo Retro) ---
        String[] columnas = {"DESCRIPCION", "#BARRA", "PRECIO"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Grilla de solo lectura
            }
        };

        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setFont(new Font("Monospaced", Font.BOLD, 14));
        tablaProductos.setRowHeight(22);
        tablaProductos.setBackground(new Color(0, 0, 170)); // Fondo azul retro
        tablaProductos.setForeground(Color.WHITE); // Texto blanco
        tablaProductos.setSelectionBackground(new Color(0, 170, 170));
        tablaProductos.setSelectionForeground(Color.YELLOW);
        tablaProductos.getTableHeader().setFont(new Font("Monospaced", Font.BOLD, 14));
        tablaProductos.getTableHeader().setBackground(new Color(0, 0, 120));
        tablaProductos.getTableHeader().setForeground(Color.CYAN);

        // Formato para alinear precios a la derecha
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaProductos.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);

        // Ajuste de anchos de columna
        tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(450); // Descripción
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(150); // #Barra
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(150); // Precio

        JScrollPane scrollTabla = new JScrollPane(tablaProductos);
        scrollTabla.getViewport().setBackground(new Color(0, 0, 170));
        add(scrollTabla, BorderLayout.CENTER);

        // --- BARRA INFERIOR DE ACCIONES (COMANDOS) ---
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(new Color(180, 0, 0)); // Rojo distintivo de menu comandos
        lblBarraAcciones = new JLabel("(Ins)erta  (Supr)ime  (M)odifica  (B)uscar  (K)dx  (T)iene  (C)ompras  (F)amilia", SwingConstants.CENTER);
        lblBarraAcciones.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblBarraAcciones.setForeground(Color.WHITE);
        panelInferior.add(lblBarraAcciones, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);

        // --- ATAJOS DE TECLADO Y ACCIONES ---
        configurarAtajosTeclado();

        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Cargar datos reales desde PostgreSQL
        cargarProductos();

    }

    private void cargarProductos() {
        modeloTabla.setRowCount(0);
        ProductoVentaDAO dao = new ProductoVentaDAO();
        List<Object[]> productos = dao.obtenerTodosLosProductos();

        for (Object[] p : productos) {
            String desc = p[0] != null ? p[0].toString() : "";
            String barra = p[1] != null ? p[1].toString() : "";
            double precio = p[2] != null ? Double.parseDouble(p[2].toString()) : 0.0;

            modeloTabla.addRow(new Object[]{
                desc,
                barra,
                String.format("%.2f", precio)
            });
        }

        if (modeloTabla.getRowCount() > 0) {
            tablaProductos.setRowSelectionInterval(0, 0);
        }
    }

    private void configurarAtajosTeclado() {
        // Tecla ESCAPE para salir
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Tecla INSERT (Inserta producto)
        getRootPane().registerKeyboardAction(e -> insertarProducto(),
                KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Tecla M (Modifica producto)
        getRootPane().registerKeyboardAction(e -> modificarProducto(),
                KeyStroke.getKeyStroke(KeyEvent.VK_M, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void insertarProducto() {
        Frame padre = (Frame) SwingUtilities.getWindowAncestor(this);
        DlgNuevoProducto dlgNuevo = new DlgNuevoProducto(padre, true);
        dlgNuevo.setVisible(true);

        if (dlgNuevo.isGuardado()) {
            cargarProductos();
        }
    }

    private void modificarProducto() {
        int fila = tablaProductos.getSelectedRow();
        if (fila != -1) {
         

            String nombre = modeloTabla.getValueAt(fila, 0).toString();
            String barra = modeloTabla.getValueAt(fila, 1).toString().trim();

            String precioStr = modeloTabla.getValueAt(fila, 2).toString().replace(",", ".");
            double precio = Double.parseDouble(precioStr);

            // Pasamos los datos ordenados al constructor de modificación
            DlgNuevoProducto dlgModificar = new DlgNuevoProducto(this, true, barra, nombre, precio);
            dlgModificar.setVisible(true);

            if (dlgModificar.isGuardado()) {
                cargarProductos();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un producto de la lista para modificar.", "Atención", JOptionPane.WARNING_MESSAGE);
        }
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
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
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
            DlgRegistroProductos dialog = new DlgRegistroProductos(new javax.swing.JFrame(), true);
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
