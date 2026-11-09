package vista;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author caill
 */
public class DlgParametros extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(DlgParametros.class.getName());

    // Componentes del formulario
    private JTextField txtRazonSocial, txtRut;
    //private JLabel lblSucursal;
    private JTextField txtFolioBoleta, txtFolioFactura, txtFolioGuia, txtIva, txtFecha;
    private JButton btnGuardar, btnCancelar;

    /**
     * Creates new form DlgParametros
     *
     * @param parent
     * @param modal
     */
    public DlgParametros(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        initComponentsCustom();
    }

    private void initComponentsCustom() {
        setTitle("NEXUM - Parámetros y Correlativos");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(550, 420); // Ajustamos la altura al quitar los 2 campos sobrantes
        setLocationRelativeTo(getParent());
        setResizable(false);
        setLayout(new BorderLayout(15, 15));

        // 1. Cargar datos de la BD
        String[] valoresActuales = dao.ParametroDAO.cargarParametros();

        String boleta = (valoresActuales[0] != null) ? valoresActuales[0] : "0";
        String factura = (valoresActuales[1] != null) ? valoresActuales[1] : "0";
        String guia = (valoresActuales[2] != null) ? valoresActuales[2] : "0";
        String iva = (valoresActuales[3] != null) ? valoresActuales[3] : "19";

        String fechaActual = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String fechaDb = (valoresActuales[4] != null && !valoresActuales[4].isEmpty()) ? valoresActuales[4] : fechaActual;

        // 2. Instanciar solo los 5 campos activos
        txtFolioBoleta = new JTextField(boleta);
        txtFolioFactura = new JTextField(factura);
        txtFolioGuia = new JTextField(guia);
        txtIva = new JTextField(iva);
        txtFecha = new JTextField(fechaDb);

        // 3. Panel central (5 filas en lugar de 7)
        JPanel panelFormulario = new JPanel(new GridLayout(5, 2, 15, 12));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        agregarCampo(panelFormulario, "Boleta:", txtFolioBoleta);
        agregarCampo(panelFormulario, "Factura:", txtFolioFactura);
        agregarCampo(panelFormulario, "G. Despacho:", txtFolioGuia);
        agregarCampo(panelFormulario, "IVA (%):", txtIva);
        agregarCampo(panelFormulario, "Fecha Sistema:", txtFecha);

        add(panelFormulario, BorderLayout.CENTER);

        // 4. Panel inferior de botones
        add(crearPanelBotones(), BorderLayout.SOUTH);

        // 5. Configurar navegación
        configurarNavegacionTeclado();
    }

    private void guardarParametros() {
        // Recoger los textos limpios de la vista
        String boleta = txtFolioBoleta.getText().trim();
        String factura = txtFolioFactura.getText().trim();
        String guia = txtFolioGuia.getText().trim();
        String iva = txtIva.getText().trim();
        String fecha = txtFecha.getText().trim();

        // Llamar al controlador para que procese y guarde
        boolean guardado = controlador.ParametroController.procesarGuardado(boleta, factura, guia, iva, fecha);

        if (guardado) {
            dispose(); // Cierra la ventana sólo si se guardó con éxito
        }
    }

    private void agregarCampo(JPanel panel, String etiquetaTexto, JComponent campo) {
        JLabel etiqueta = new JLabel(etiquetaTexto);
        
   
        etiqueta.setFont(new Font("Segoe UI", Font.BOLD, 18)); // subirlo a 20 si lo quieres más grande
        
        //  JTextField también necesitan fuente grande, se puede configurar aquí mismo o al crearlos:
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        panel.add(etiqueta);
        panel.add(campo);
    }

    private JPanel crearPanelBotones() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));

        Font fuenteBoton = new Font("Segoe UI", Font.BOLD, 15);
        Dimension dimensionBoton = new Dimension(160, 40);

        btnGuardar = new JButton("Guardar Cambios");
        btnGuardar.setFont(fuenteBoton);
        btnGuardar.setPreferredSize(dimensionBoton);
        btnGuardar.setMnemonic(KeyEvent.VK_G);
        btnGuardar.addActionListener(e -> guardarParametros());

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(fuenteBoton);
        btnCancelar.setPreferredSize(dimensionBoton);
        btnCancelar.setMnemonic(KeyEvent.VK_C);
        btnCancelar.addActionListener(e -> dispose());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        return panelBotones;
    }

    private void configurarNavegacionTeclado() {
        // Arreglo estricto con los 5 campos y los 2 botones activos
        Component[] componentes = {
            txtFolioBoleta,
            txtFolioFactura,
            txtFolioGuia,
            txtIva,
            txtFecha,
            btnGuardar,
            btnCancelar
        };

        KeyAdapter navegacionTeclado = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                Component fuente = (Component) e.getSource();

                int index = -1;
                for (int i = 0; i < componentes.length; i++) {
                    if (componentes[i] == fuente) {
                        index = i;
                        break;
                    }
                }

                if (index != -1) {
                    if (key == KeyEvent.VK_DOWN || key == KeyEvent.VK_ENTER) {
                        int siguiente = (index + 1) % componentes.length;
                        componentes[siguiente].requestFocusInWindow();
                        e.consume();
                    } else if (key == KeyEvent.VK_UP) {
                        int anterior = (index - 1 + componentes.length) % componentes.length;
                        componentes[anterior].requestFocusInWindow();
                        e.consume();
                    }
                }
            }
        };

        for (Component c : componentes) {
            if (c != null) {
                c.addKeyListener(navegacionTeclado);
            }
        }

        getRootPane().registerKeyboardAction(e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
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
            .addGap(0, 600, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 486, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            DlgParametros dialog = new DlgParametros(new javax.swing.JFrame(), true);
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
