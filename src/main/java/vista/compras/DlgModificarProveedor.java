package vista.compras;

import dao.ProveedorDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Map;

public class DlgModificarProveedor extends JDialog {

    private JTextField txtNombre;
    private JTextField txtRut;
    private JTextField txtDigito;
    private JTextField txtCiudad;
    
    private int rutOriginal;

    // Colores Estilo Clipper / MS-DOS Pro
    private final Color COLOR_AZUL_FONDO = new Color(0, 0, 128);
    private final Color COLOR_AMARILLO_TEXTO = new Color(255, 255, 0);
    private final Color COLOR_ROJO_ACCION = new Color(178, 34, 34);

    public DlgModificarProveedor(Window parent, int rutProveedor) {
        super(parent, "Modificar Proveedor", ModalityType.APPLICATION_MODAL);
        this.rutOriginal = rutProveedor;
        setSize(580, 380);
        setLocationRelativeTo(parent);
        initComponentsCustom();
        cargarDatosProveedor();
    }

    private void initComponentsCustom() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(COLOR_AZUL_FONDO);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Título Superior
        JLabel lblTitulo = new JLabel("=== MODIFICAR PROVEEDOR ===", JLabel.CENTER);
        lblTitulo.setFont(new Font("Consolas", Font.BOLD, 16));
        lblTitulo.setForeground(COLOR_AMARILLO_TEXTO);
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // Panel central con GridBagLayout
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBackground(COLOR_AZUL_FONDO);
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        Dimension dimGrande = new Dimension(280, 28);
        Dimension dimCorto = new Dimension(80, 28);
        Dimension dimDv = new Dimension(40, 28);

        // Fila 0: Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblNombre = new JLabel("NOMBRE:");
        lblNombre.setFont(new Font("Consolas", Font.BOLD, 13));
        lblNombre.setForeground(COLOR_AMARILLO_TEXTO);
        panelFormulario.add(lblNombre, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 3;
        txtNombre = new JTextField();
        estilizarCampoTexto(txtNombre, dimGrande);
        panelFormulario.add(txtNombre, gbc);

        // Fila 1: RUT y DV
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblRut = new JLabel("R.U.T. / D.V.:");
        lblRut.setFont(new Font("Consolas", Font.BOLD, 13));
        lblRut.setForeground(COLOR_AMARILLO_TEXTO);
        panelFormulario.add(lblRut, gbc);

        JPanel panelRutDv = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelRutDv.setBackground(COLOR_AZUL_FONDO);
        
        txtRut = new JTextField();
        estilizarCampoTexto(txtRut, dimCorto);
        
        JLabel lblGuion = new JLabel("-");
        lblGuion.setForeground(COLOR_AMARILLO_TEXTO);
        lblGuion.setFont(new Font("Consolas", Font.BOLD, 14));
        
        txtDigito = new JTextField();
        estilizarCampoTexto(txtDigito, dimDv);
        
        panelRutDv.add(txtRut);
        panelRutDv.add(lblGuion);
        panelRutDv.add(txtDigito);

        gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 3;
        panelFormulario.add(panelRutDv, gbc);

        // Fila 2: Ciudad
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblCiudad = new JLabel("CIUDAD:");
        lblCiudad.setFont(new Font("Consolas", Font.BOLD, 13));
        lblCiudad.setForeground(COLOR_AMARILLO_TEXTO);
        panelFormulario.add(lblCiudad, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 3;
        txtCiudad = new JTextField();
        estilizarCampoTexto(txtCiudad, dimGrande);
        panelFormulario.add(txtCiudad, gbc);

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

    private void cargarDatosProveedor() {
        try {
            ProveedorDAO dao = new ProveedorDAO();
            Map<String, Object> datos = dao.obtenerProveedorPorRut(rutOriginal);
            if (!datos.isEmpty()) {
                txtNombre.setText((String) datos.get("nombre"));
                txtRut.setText(String.valueOf(datos.get("rut")));
                txtDigito.setText(String.valueOf(datos.get("digit")));
                txtCiudad.setText((String) datos.get("ciudad"));
            }
        } catch (Exception e) {
            System.err.println("Error al cargar datos en formulario de modificación: " + e.getMessage());
        }
    }

    private void setupKeyBindings() {
        JRootPane root = getRootPane();

        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancelar");
        root.getActionMap().put("cancelar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });

        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "guardar");
        root.getActionMap().put("guardar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                actualizarProveedor();
            }
        });
    }

    private void actualizarProveedor() {
        String nombre = txtNombre.getText().trim();
        String rutStr = txtRut.getText().trim();
        String dvStr = txtDigito.getText().trim();
        String ciudad = txtCiudad.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre no puede estar vacío.", "Aviso", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        int nuevoRut, dv;
        try {
            nuevoRut = Integer.parseInt(rutStr);
            dv = Integer.parseInt(dvStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El RUT y el dígito deben ser numéricos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            ProveedorDAO dao = new ProveedorDAO();
            boolean exito = dao.actualizarProveedor(nombre, rutOriginal, nuevoRut, dv, ciudad);

            if (exito) {
                JOptionPane.showMessageDialog(this, "¡Proveedor modificado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar el registro.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error de sistema: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}