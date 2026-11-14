package vista.bodega;

import dao.ProductoVentaDAO; // Ajusta el import según tu paquete DAO
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;

public class DlgInsertarProducto extends JDialog {

    private JTextField txtDescripcion;
    private JTextField txtBarra;
    private JTextField txtPrecio;

    // Colores Estilo Clipper / MS-DOS Pro
    private final Color COLOR_AZUL_FONDO = new Color(0, 0, 128);
    private final Color COLOR_AMARILLO_TEXTO = new Color(255, 255, 0);
    private final Color COLOR_ROJO_ACCION = new Color(178, 34, 34);

    public DlgInsertarProducto(Window parent) {
        super(parent, "Insertar Nuevo Producto", ModalityType.APPLICATION_MODAL);
        setSize(550, 350);
        setLocationRelativeTo(parent);
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(COLOR_AZUL_FONDO);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Título Superior
        JLabel lblTitulo = new JLabel("=== ALTA DE NUEVO PRODUCTO ===", JLabel.CENTER);
        lblTitulo.setFont(new Font("Consolas", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_AMARILLO_TEXTO);
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // Panel central con GridBagLayout para control total de dimensiones
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(COLOR_AZUL_FONDO);
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Dimensión fija y decente para los inputs (Ancho x Alto)
        Dimension dimensionCampo = new Dimension(280, 28);

        // Fila 0: Descripción
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblDesc = new JLabel("DESCRIPCIÓN:");
        lblDesc.setFont(new Font("Consolas", Font.BOLD, 13));
        lblDesc.setForeground(COLOR_AMARILLO_TEXTO);
        panelFormulario.add(lblDesc, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        txtDescripcion = new JTextField();
        estilizarCampoTexto(txtDescripcion, dimensionCampo);
        panelFormulario.add(txtDescripcion, gbc);

        // Fila 1: Código de Barra
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblBarra = new JLabel("# BARRA:");
        lblBarra.setFont(new Font("Consolas", Font.BOLD, 13));
        lblBarra.setForeground(COLOR_AMARILLO_TEXTO);
        panelFormulario.add(lblBarra, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        txtBarra = new JTextField();
        estilizarCampoTexto(txtBarra, dimensionCampo);
        panelFormulario.add(txtBarra, gbc);

        // Fila 2: Precio
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblPrecio = new JLabel("PRECIO (VENTA 0):");
        lblPrecio.setFont(new Font("Consolas", Font.BOLD, 13));
        lblPrecio.setForeground(COLOR_AMARILLO_TEXTO);
        panelFormulario.add(lblPrecio, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        txtPrecio = new JTextField();
        estilizarCampoTexto(txtPrecio, dimensionCampo);
        panelFormulario.add(txtPrecio, gbc);

        panelPrincipal.add(panelFormulario, BorderLayout.CENTER);

        // Franja inferior con atajos (Estilo Rojo Institucional)
        JLabel lblAcciones = new JLabel(" (ENTER) Guardar    (ESC) Cancelar ", JLabel.CENTER);
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

    private void setupKeyBindings() {
        JRootPane root = getRootPane();

        // ESC para cerrar sin guardar
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancelar");
        root.getActionMap().put("cancelar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });

        // ENTER para guardar el registro
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "guardar");
        root.getActionMap().put("guardar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                guardarProducto();
            }
        });
    }

    private void guardarProducto() {
        String descripcion = txtDescripcion.getText().trim();
        String barra = txtBarra.getText().trim();
        String precioStr = txtPrecio.getText().trim().replace(",", ".");

        if (descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La descripción no puede estar vacía.", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtDescripcion.requestFocus();
            return;
        }

        BigDecimal precio;
        try {
            precio = precioStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(precioStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El formato del precio no es válido.", "Error", JOptionPane.ERROR_MESSAGE);
            txtPrecio.requestFocus();
            return;
        }

        try {
            // Llamamos a un método DAO para insertar en pproduc_dbf
            ProductoVentaDAO dao = new ProductoVentaDAO();
            boolean exito = dao.insertarProducto(barra, descripcion, precio.doubleValue());

            if (exito) {
                JOptionPane.showMessageDialog(this, "¡Producto guardado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo guardar el producto en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error de sistema al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
