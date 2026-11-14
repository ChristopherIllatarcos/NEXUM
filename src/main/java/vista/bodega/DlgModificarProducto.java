package vista.bodega;

import dao.ProductoVentaDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Map;

public class DlgModificarProducto extends JDialog {

    private JTextField txtBarra;
    private JTextField txtNombre;
    private JTextField txtPrecio;
    
    private String barraOriginal;

    // Colores Estilo Clipper / MS-DOS Pro
    private final Color COLOR_AZUL_FONDO = new Color(0, 0, 128);
    private final Color COLOR_AMARILLO_TEXTO = new Color(255, 255, 0);
    private final Color COLOR_ROJO_ACCION = new Color(178, 34, 34);

    public DlgModificarProducto(Window parent, String barraProducto) {
        super(parent, "Modificar Producto", ModalityType.APPLICATION_MODAL);
        this.barraOriginal = barraProducto;
        setSize(580, 360);
        setLocationRelativeTo(parent);
        initComponentsCustom();
        cargarDatosProducto();
    }

    private void initComponentsCustom() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(COLOR_AZUL_FONDO);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Título Superior
        JLabel lblTitulo = new JLabel("=== MODIFICAR PRODUCTO ===", JLabel.CENTER);
        lblTitulo.setFont(new Font("Consolas", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_AMARILLO_TEXTO);
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // Panel central con GridBagLayout para control total de dimensiones
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(COLOR_AZUL_FONDO);
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        Dimension dimensionCampo = new Dimension(280, 28);

        // Fila 0: Código de Barra
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblBarra = new JLabel("# BARRA:");
        lblBarra.setFont(new Font("Consolas", Font.BOLD, 13));
        lblBarra.setForeground(COLOR_AMARILLO_TEXTO);
        panelFormulario.add(lblBarra, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        txtBarra = new JTextField();
        estilizarCampoTexto(txtBarra, dimensionCampo);
        panelFormulario.add(txtBarra, gbc);

        // Fila 1: Nombre / Descripción
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblNombre = new JLabel("DESCRIPCIÓN:");
        lblNombre.setFont(new Font("Consolas", Font.BOLD, 13));
        lblNombre.setForeground(COLOR_AMARILLO_TEXTO);
        panelFormulario.add(lblNombre, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtNombre = new JTextField();
        estilizarCampoTexto(txtNombre, dimensionCampo);
        panelFormulario.add(txtNombre, gbc);

        // Fila 2: Precio (Venta 0)
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblPrecio = new JLabel("PRECIO (VENTA 0):");
        lblPrecio.setFont(new Font("Consolas", Font.BOLD, 13));
        lblPrecio.setForeground(COLOR_AMARILLO_TEXTO);
        panelFormulario.add(lblPrecio, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        txtPrecio = new JTextField();
        estilizarCampoTexto(txtPrecio, dimensionCampo);
        panelFormulario.add(txtPrecio, gbc);

        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);

        // Franja inferior con atajos (Estilo Rojo Institucional)
        JLabel lblAcciones = new JLabel(" (ENTER) Guardar Cambios    (ESC) Cancelar ", JLabel.CENTER);
        lblAcciones.setFont(new Font("Consolas", Font.BOLD, 12));
        lblAcciones.setForeground(COLOR_AMARILLO_TEXTO);
        
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBackground(COLOR_ROJO_ACCION);
        panelSur.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        panelSur.add(lblAcciones, BorderLayout.CENTER);
        
        panelPrincipal.add(panelSur, BorderLayout.SOUTH);
        add(panelPrincipal);

        setupKeyBindings();
    }

    private void estilizarCampoTexto(JTextField campo, Dimension dim) {
        campo.setFont(new Font("Consolas", Font.PLAIN, 13));
        campo.setBackground(Color.BLACK);
        campo.setForeground(COLOR_AMARILLO_TEXTO);
        campo.setCaretColor(Color.YELLOW);
        campo.setBorder(BorderFactory.createLineBorder(COLOR_AMARILLO_TEXTO));
        campo.setPreferredSize(dim);
        campo.setMinimumSize(dim);
    }

    private void cargarDatosProducto() {
        try {
            ProductoVentaDAO dao = new ProductoVentaDAO();
            Map<String, Object> datos = dao.obtenerProductoPorBarra(barraOriginal);
            if (!datos.isEmpty()) {
                txtBarra.setText((String) datos.get("barra"));
                txtNombre.setText((String) datos.get("nombre"));
                txtPrecio.setText(String.valueOf(datos.get("precio")));
            }
        } catch (Exception e) {
            System.err.println("Error al cargar datos en formulario de modificación: " + e.getMessage());
        }
    }

    private void setupKeyBindings() {
        JRootPane root = getRootPane();

        // ESC para salir
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancelar");
        root.getActionMap().put("cancelar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });

        // ENTER para guardar cambios
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "guardar");
        root.getActionMap().put("guardar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                actualizarProducto();
            }
        });
    }

    private void actualizarProducto() {
        String nuevaBarra = txtBarra.getText().trim();
        String nombre = txtNombre.getText().trim();
        String precio = txtPrecio.getText().trim();

        if (nuevaBarra.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El código de barra no puede estar vacío.", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtBarra.requestFocus();
            return;
        }

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La descripción no puede estar vacía.", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        try {
            ProductoVentaDAO dao = new ProductoVentaDAO();
            boolean exito = dao.actualizarProducto(nuevaBarra, nombre, precio, barraOriginal);

            if (exito) {
                JOptionPane.showMessageDialog(this, "¡Producto modificado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar el producto en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error de sistema al actualizar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}