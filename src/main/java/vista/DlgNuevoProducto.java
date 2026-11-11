package vista;

import dao.ProductoVentaDAO;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class DlgNuevoProducto extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DlgNuevoProducto.class.getName());

    private JTextField txtBarra;
    private JTextField txtNombre;
    private JTextField txtFamilia;
    private JTextField txtImptoIla;
    private JTextField txtUnidad;

    private JTextField txtPrecio1, txtDesde1, txtUnidades1;
    private JTextField txtPrecio2, txtDesde2, txtUnidades2;
    private JTextField txtPrecio3, txtDesde3, txtUnidades3;
    private JTextField txtCosto;
    private JTextField txtCritico;
    private JTextField txtPuntoPedido;

    private JTextArea txtContenido;

    private boolean guardado = false;

    private boolean esEdicion = false;
    private String codigoOriginal = "";

    // Constructor 1: 
    public DlgNuevoProducto(java.awt.Frame parent, boolean modal) {
        super(parent, "REGISTRO DE PRODUCTO", modal);
        this.esEdicion = false;
        inicializarVentanaVisual(parent);
    }

    // Constructor 2
    public DlgNuevoProducto(java.awt.Dialog parent, boolean modal) {
        super(parent, "REGISTRO DE PRODUCTO", modal);
        this.esEdicion = false;
        inicializarVentanaVisual(parent);
    }

    // Constructor 3: Para MODIFICAR (Recibe Dialog, datos de la tabla y activa el modo edición)
    public DlgNuevoProducto(java.awt.Dialog parent, boolean modal, String codigoBarra, String nombreActual, double precioActual) {
        this(parent, modal); // Llama al constructor 2
        this.esEdicion = true;
        this.codigoOriginal = codigoBarra;

        txtBarra.setText(codigoBarra);
        txtNombre.setText(nombreActual);
        txtPrecio1.setText(String.valueOf(precioActual));
    }

    // Método auxiliar interno para no repetir todo el diseño visual
    private void inicializarVentanaVisual(java.awt.Window parent) {
        setSize(780, 580);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // Color de fondo retro
        Color fondoAzul = new Color(0, 0, 170);
        Color azulEncabezado = new Color(0, 128, 128);
        Color textoVerde = Color.GREEN;

        // --- ENCABEZADO SUPERIOR ---
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelTitulo.setBackground(azulEncabezado);
        JLabel lblTitulo = new JLabel("REGISTRO DE PRODUCTO");
        lblTitulo.setFont(new Font("Monospaced", Font.BOLD, 18));
        lblTitulo.setForeground(Color.YELLOW);
        panelTitulo.add(lblTitulo);
        add(panelTitulo, BorderLayout.NORTH);

        // --- PANEL CENTRAL CONTENEDOR ---
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBackground(fondoAzul);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        // 1. CAMPO #BARRA
        JPanel panelBarra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBarra.setBackground(fondoAzul);
        JLabel lblBarra = new JLabel("#BARRA : ");
        lblBarra.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblBarra.setForeground(Color.CYAN);

        txtBarra = crearTextField(12);
        panelBarra.add(lblBarra);
        panelBarra.add(txtBarra);
        panelCentral.add(panelBarra);

        // 2. SECCIÓN DESCRIPCION (Borde recuadro)
        JPanel panelDescripcion = new JPanel(new GridBagLayout());
        panelDescripcion.setBackground(fondoAzul);
        panelDescripcion.setBorder(crearBordeTitulo("DESCRIPCION"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nombre
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelDescripcion.add(crearLabel("NOMBRE :"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        txtNombre = crearTextField(35);
        panelDescripcion.add(txtNombre, gbc);

        // Familia y Unidad
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panelDescripcion.add(crearLabel("FAMILIA:"), gbc);
        gbc.gridx = 1;
        txtFamilia = crearTextField(10);
        panelDescripcion.add(txtFamilia, gbc);

        gbc.gridx = 2;
        panelDescripcion.add(crearLabel("UNIDAD:"), gbc);
        gbc.gridx = 3;
        txtUnidad = crearTextField(10);
        panelDescripcion.add(txtUnidad, gbc);

        // Impto ILA
        gbc.gridx = 0;
        gbc.gridy = 2;
        panelDescripcion.add(crearLabel("IMPTO ILA:"), gbc);
        gbc.gridx = 1;
        txtImptoIla = crearTextField(10);
        panelDescripcion.add(txtImptoIla, gbc);

        panelCentral.add(panelDescripcion);
        panelCentral.add(Box.createVerticalStrut(5));

        // 3. SECCIÓN VALORES
        JPanel panelValores = new JPanel(new GridBagLayout());
        panelValores.setBackground(fondoAzul);
        panelValores.setBorder(crearBordeTitulo("VALORES"));

        GridBagConstraints gbcV = new GridBagConstraints();
        gbcV.insets = new Insets(2, 4, 2, 4);
        gbcV.fill = GridBagConstraints.HORIZONTAL;

        // Precios 1, 2, 3 con Desde y Unidades
        txtPrecio1 = crearTextField(8);
        txtDesde1 = crearTextField(6);
        txtUnidades1 = crearTextField(6);
        txtPrecio2 = crearTextField(8);
        txtDesde2 = crearTextField(6);
        txtUnidades2 = crearTextField(6);
        txtPrecio3 = crearTextField(8);
        txtDesde3 = crearTextField(6);
        txtUnidades3 = crearTextField(6);

        agregarFilaPrecio(panelValores, gbcV, 0, "PRECIO 1:", txtPrecio1, txtDesde1, txtUnidades1);
        agregarFilaPrecio(panelValores, gbcV, 1, "PRECIO 2:", txtPrecio2, txtDesde2, txtUnidades2);
        agregarFilaPrecio(panelValores, gbcV, 2, "PRECIO 3:", txtPrecio3, txtDesde3, txtUnidades3);

        // Costo
        gbcV.gridx = 0;
        gbcV.gridy = 3;
        panelValores.add(crearLabel("COSTO:"), gbcV);
        gbcV.gridx = 1;
        txtCosto = crearTextField(8);
        panelValores.add(txtCosto, gbcV);

        // Crítico y Punto Pedido
        gbcV.gridx = 0;
        gbcV.gridy = 4;
        panelValores.add(crearLabel("CRITICO:"), gbcV);
        gbcV.gridx = 1;
        txtCritico = crearTextField(8);
        panelValores.add(txtCritico, gbcV);

        gbcV.gridx = 2;
        panelValores.add(crearLabel("PUNTO PEDIDO:"), gbcV);
        gbcV.gridx = 3;
        txtPuntoPedido = crearTextField(8);
        panelValores.add(txtPuntoPedido, gbcV);

        panelCentral.add(panelValores);
        panelCentral.add(Box.createVerticalStrut(5));

        // 4. SECCIÓN CONTENIDO
        JPanel panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(fondoAzul);
        panelContenido.setBorder(crearBordeTitulo("CONTENIDO"));

        txtContenido = new JTextArea(3, 40);
        txtContenido.setFont(new Font("Monospaced", Font.BOLD, 14));
        txtContenido.setBackground(Color.BLACK);
        txtContenido.setForeground(Color.GREEN);
        txtContenido.setCaretColor(Color.WHITE);
        JScrollPane scrollContenido = new JScrollPane(txtContenido);
        panelContenido.add(scrollContenido, BorderLayout.CENTER);

        panelCentral.add(panelContenido);

        add(panelCentral, BorderLayout.CENTER);

        // --- BARRA INFERIOR DE ACCIONES ---
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        panelInferior.setBackground(new Color(180, 0, 0)); // Rojo retro

        JLabel lblInfo = new JLabel("[ F10 / Enter ] Guardar     [ ESC ] Cancelar");
        lblInfo.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblInfo.setForeground(Color.WHITE);
        panelInferior.add(lblInfo);

        add(panelInferior, BorderLayout.SOUTH);

        // Atajos de teclado
        configurarTeclado();
    }

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Monospaced", Font.BOLD, 13));
        lbl.setForeground(Color.CYAN);
        return lbl;
    }

    private JTextField crearTextField(int columnas) {
        JTextField tf = new JTextField(columnas);
        tf.setFont(new Font("Monospaced", Font.BOLD, 14));
        tf.setBackground(Color.BLACK);
        tf.setForeground(Color.YELLOW);
        tf.setCaretColor(Color.WHITE);
        return tf;
    }

    private TitledBorder crearBordeTitulo(String titulo) {
        Border line = BorderFactory.createLineBorder(Color.WHITE, 1);
        TitledBorder border = BorderFactory.createTitledBorder(line, " " + titulo + " ", TitledBorder.CENTER, TitledBorder.TOP);
        border.setTitleFont(new Font("Monospaced", Font.BOLD, 13));
        border.setTitleColor(Color.YELLOW);
        return border;
    }

    private void agregarFilaPrecio(JPanel panel, GridBagConstraints gbc, int fila, String labelPrecio, JTextField txtP, JTextField txtD, JTextField txtU) {
        gbc.gridy = fila;

        gbc.gridx = 0;
        panel.add(crearLabel(labelPrecio), gbc);
        gbc.gridx = 1;
        panel.add(txtP, gbc);

        gbc.gridx = 2;
        panel.add(crearLabel("Desde:"), gbc);
        gbc.gridx = 3;
        panel.add(txtD, gbc);

        gbc.gridx = 4;
        panel.add(crearLabel("Unidades"), gbc);
        gbc.gridx = 5;
        panel.add(txtU, gbc);
    }

    private void configurarTeclado() {
        // ESCAPE para salir
        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        // F10 para guardar
        getRootPane().registerKeyboardAction(e -> guardarProducto(),
                KeyStroke.getKeyStroke(KeyEvent.VK_F10, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void guardarProducto() {
        String barra = txtBarra.getText().trim();
        String nombre = txtNombre.getText().trim();
        String precioStr = txtPrecio1.getText().trim();

        if (barra.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar el código #BARRA y el NOMBRE.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double precio = 0.0;
        try {
            if (!precioStr.isEmpty()) {
                precio = Double.parseDouble(precioStr);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El PRECIO 1 debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ProductoVentaDAO dao = new ProductoVentaDAO();
        boolean exito = false;

        if (esEdicion) {
            // Le pasamos el código original y el nuevo código que tipeaste en la caja de texto
            exito = dao.actualizarProducto(codigoOriginal, barra, nombre, precio);
        } else {
            exito = dao.insertarProducto(barra, nombre, precio);
        }
        if (exito) {
            guardado = true;
            String mensaje = esEdicion ? "Producto modificado correctamente." : "Producto registrado correctamente.";
            JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo guardar el producto en la base de datos.", "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGuardado() {
        return guardado;
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

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {
            DlgNuevoProducto dialog = new DlgNuevoProducto(new javax.swing.JFrame(), true);
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
