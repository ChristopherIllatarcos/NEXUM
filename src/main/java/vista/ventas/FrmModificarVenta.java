
package vista.ventas;

import conexion.Conexion;
import dao.VentaDAO;
import modelo.VentaDTO;


import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.Connection;

public class FrmModificarVenta extends JDialog {

    private JTextField txtCliente, txtFecha, txtBoleta, txtFactura, txtGuia, txtTotal;
    private long codigoVenta;
    private boolean guardadoExitoso = false;

    // Estética Clipper Clásica (Azul profundo, textos amarillos, bordes cian, barra roja)
    private final Color COLOR_AZUL_FONDO = new Color(0, 0, 139);
    private final Color COLOR_AMARILLO = new Color(255, 255, 0);
    private final Color COLOR_CIAN = new Color(0, 255, 255);
    private final Color COLOR_ROJO_BARRA = new Color(178, 0, 0);
    private final Font FUENTE_CONSOLA = new Font("Courier New", Font.BOLD, 12);

    public FrmModificarVenta(Window parent, VentaDTO venta) {
        super(parent, " MODIFICACIÓN DE VENTA ", ModalityType.APPLICATION_MODAL);
        this.codigoVenta = venta.getVcodigo();

        setSize(480, 320);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(COLOR_AZUL_FONDO);
        setLayout(new BorderLayout());

        // Panel Principal con borde cian y título
        JPanel panelContenedor = new JPanel(new GridBagLayout());
        panelContenedor.setBackground(COLOR_AZUL_FONDO);
        panelContenedor.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(COLOR_CIAN, 2), " EDITAR VENTA (ID: " + codigoVenta + ") ",
                0, 0, FUENTE_CONSOLA, COLOR_CIAN
        ));
        ((javax.swing.border.TitledBorder) panelContenedor.getBorder()).setTitleColor(COLOR_AMARILLO);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Inicializamos campos con tamaños equilibrados
        txtCliente = crearCampo(venta.getVcliente(), 22);
        txtFecha = crearCampo(venta.getVfecha(), 10);
        txtBoleta = crearCampo(venta.getVboleta(), 10);
        txtFactura = crearCampo(venta.getVfactura(), 10);
        txtGuia = crearCampo(venta.getVguia(), 10);
        txtTotal = crearCampo(String.valueOf(venta.getVpxtotal()), 12);

        // Fila 0: Cliente
        gbc.gridx = 0; gbc.gridy = 0; panelContenedor.add(crearEtiqueta("Cliente:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 3; panelContenedor.add(txtCliente, gbc);
        gbc.gridwidth = 1;

        // Fila 1: Fecha y Boleta
        gbc.gridx = 0; gbc.gridy = 1; panelContenedor.add(crearEtiqueta("Fecha:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; panelContenedor.add(txtFecha, gbc);
        gbc.gridx = 2; gbc.gridy = 1; panelContenedor.add(crearEtiqueta("Boleta:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1; panelContenedor.add(txtBoleta, gbc);

        // Fila 2: Factura y Guía
        gbc.gridx = 0; gbc.gridy = 2; panelContenedor.add(crearEtiqueta("Factura:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; panelContenedor.add(txtFactura, gbc);
        gbc.gridx = 2; gbc.gridy = 2; panelContenedor.add(crearEtiqueta("Guía:"), gbc);
        gbc.gridx = 3; gbc.gridy = 2; panelContenedor.add(txtGuia, gbc);

        // Fila 3: Total ($)
        gbc.gridx = 0; gbc.gridy = 3; panelContenedor.add(crearEtiqueta("Total ($):"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; panelContenedor.add(txtTotal, gbc);

        // Panel central para mantenerlo compacto
        JPanel panelCentro = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        panelCentro.setBackground(COLOR_AZUL_FONDO);
        panelCentro.add(panelContenedor);
        add(panelCentro, BorderLayout.CENTER);

        // Barra inferior roja de atajos
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 6));
        panelInferior.setBackground(COLOR_ROJO_BARRA);
        panelInferior.setBorder(new LineBorder(COLOR_AMARILLO, 1));

        JLabel lblAtajos = new JLabel("(ESC) Cancelar   |   (ENTER) Guardar Cambios");
        lblAtajos.setForeground(Color.WHITE);
        lblAtajos.setFont(FUENTE_CONSOLA);
        panelInferior.add(lblAtajos);

        add(panelInferior, BorderLayout.SOUTH);

        configurarAtajos();
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(COLOR_AMARILLO);
        lbl.setFont(FUENTE_CONSOLA);
        return lbl;
    }

    private JTextField crearCampo(String valor, int columnas) {
        JTextField campo = new JTextField(valor != null ? valor : "", columnas);
        campo.setBackground(COLOR_AZUL_FONDO);
        campo.setForeground(COLOR_AMARILLO);
        campo.setCaretColor(COLOR_AMARILLO);
        campo.setFont(FUENTE_CONSOLA);
        campo.setBorder(new LineBorder(COLOR_CIAN, 1));
        return campo;
    }

    private void configurarAtajos() {
        JRootPane root = this.getRootPane();

        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancelar");
        root.getActionMap().put("cancelar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) { dispose(); }
        });

        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "guardar");
        root.getActionMap().put("guardar", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) { guardarCambios(); }
        });
    }

    private void guardarCambios() {
        try {
            VentaDTO dto = new VentaDTO();
            dto.setVcodigo(codigoVenta);
            dto.setVcliente(txtCliente.getText().trim());
            dto.setVfecha(txtFecha.getText().trim());
            dto.setVboleta(txtBoleta.getText().trim());
            dto.setVfactura(txtFactura.getText().trim());
            dto.setVguia(txtGuia.getText().trim());
            dto.setVpxtotal(Double.parseDouble(txtTotal.getText().trim().replace(",", ".")));

            try (Connection con = Conexion.getConnection()) {
                VentaDAO dao = new VentaDAO();
                if (dao.actualizarVenta(con, dto)) {
                    guardadoExitoso = true;
                    JOptionPane.showMessageDialog(this, "¡Registro modificado correctamente!");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo actualizar el registro.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El campo Total ($) debe ser un valor numérico válido.", "Error de Formato", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGuardadoExitoso() {
        return guardadoExitoso;
    }
}