package vista;

import vista.ctacte.FrmEstadisticasClientes;
import vista.ventas.FrmArchivoClientes;
import vista.ventas.DlgVenta;
import com.formdev.flatlaf.FlatDarkLaf;
import conexion.Conexion;
import dao.CuentaCorrienteDAO;
import dao.ProductoVentaDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.sql.Connection;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import vista.bodega.DlgProductos;
import vista.compras.DlgProveedores;
import vista.ctacte.FrmCarteraCheques;
import vista.ctacte.FrmCartolaCliente;
import vista.ctacte.FrmCartolaPorFecha;
import vista.ctacte.FrmCuentasCobrarAlfabetico;
import vista.ctacte.FrmEliminarRecibo;
import vista.ventas.FrmMantencionVentas;
import vista.ventas.FrmVenta;

/**
 *
 * @author caill
 */
public class FrmPrincipal extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmPrincipal.class.getName());

    private JDesktopPane escritorio = new JDesktopPane();
    private final CuentaCorrienteDAO dao = new CuentaCorrienteDAO();

    public FrmPrincipal() {
        initComponents();

        // 1. Configuración de la ventana principal
        setMinimumSize(new Dimension(1024, 768));
        setSize(1024, 768);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 2. Establecer el JDesktopPane como el contenedor principal (ContentPane)
        setContentPane(escritorio);
        escritorio.setLayout(new BorderLayout());

        // 3. Cargar la barra de menú superior
        configurarMenu();

        // 4. Colocar el Panel Dashboard de bienvenida al centro del escritorio
        PanelDashboard dashboard = new PanelDashboard();
        escritorio.add(dashboard, BorderLayout.CENTER);

        // 5. Listeners para restaurar y minimizar
        minimizarPrincipal();

        setVisible(true);
    }

    private void minimizarPrincipal() {
        // Asegurar el redibujado de la barra de menú al restaurar o activar la ventana
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowActivated(java.awt.event.WindowEvent e) {
                if (getJMenuBar() != null) {
                    getJMenuBar().revalidate();
                    getJMenuBar().repaint();
                }
                repaint();
            }
        });

        this.addWindowStateListener(e -> {
            if ((Frame.NORMAL) != 0 || (e.getNewState() & Frame.MAXIMIZED_BOTH) != 0) {
                if (getJMenuBar() != null) {
                    getJMenuBar().revalidate();
                    getJMenuBar().repaint();
                }
                repaint();
            }
        });
    }

    private void configurarMenu() {
        JMenuBar menuBar = new JMenuBar();

        // Fuentes grandes y limpias
        Font fuenteMenuPrincipal = new Font("Segoe UI", Font.BOLD, 22);
        Font fuenteSubMenu = new Font("Segoe UI", Font.PLAIN, 20);
        Font fuenteTitulo = new Font("Segoe UI", Font.BOLD, 18);

        // ==========================================
        // 1. VENTAS (Atajo: Alt + V)
        // ==========================================
        JMenu menuVentas = new JMenu("VENTAS");
        menuVentas.setFont(fuenteMenuPrincipal);
        menuVentas.setMnemonic(java.awt.event.KeyEvent.VK_V);

        // Desactivar popups ligeros para que se dibuje perfectamente al frente
        menuVentas.getPopupMenu().setLightWeightPopupEnabled(false);

        String[] itemsVentas = {
            "1. Ventas",
            "2. Consulta de documentos",
            "3. Hoja de ruta",
            "4. Mantención de ventas",
            "5. Informe ventas por cliente",
            "6. Ficha clientes",
            "7. Comisión por ventas",
            "8. Comisión por cobranza",
            "9. Ficha de vendedores"
        };

        for (int i = 0; i < itemsVentas.length; i++) {
            JMenuItem item = new JMenuItem(itemsVentas[i]);
            item.setFont(fuenteSubMenu);
            item.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

            // Asignar mnemónicos del 1 al 9 para acceso rápido por teclado
            switch (i) {
                case 0 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_1);
                case 1 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_2);
                case 2 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_3);
                case 3 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_4);
                case 4 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_5);
                case 5 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_6);
                case 6 ->

                    item.setMnemonic(java.awt.event.KeyEvent.VK_7);
                case 7 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_8);
                case 8 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_9);
            }

            // Aquí puedes enlazar la acción de cada opción más adelante (por ejemplo, abrir sus respectivos Diálogos)
            final int indexAccion = i + 1;

            // Acción individual para cada opción del menú
            item.addActionListener(e -> {
                switch (indexAccion) {
                    case 1 -> {
                        // Abrimos el módulo de ventas actualizado como JInternalFrame
                        FrmVenta ventanaVenta = new FrmVenta();

                        // Lo agregamos al panel de escritorio principal (desktopPane)
                        escritorio.add(ventanaVenta);

                        ventanaVenta.setVisible(true);

                        try {
                            ventanaVenta.setSelected(true); // Lo pasamos al frente si ya estaba abierto
                        } catch (java.beans.PropertyVetoException ex) {
                            ex.printStackTrace();
                        }

                        System.out.println("Seleccionado módulo Ventas op: " + indexAccion);
                    }
                    case 2 -> {
                        // TODO: Aquí irá la lógica para "2. Listado de Documentos"
                        System.out.println("Abriendo módulo: Listado de Documentos");
                    }
                    case 3 -> {

                    }
                    case 4 -> {
                        FrmMantencionVentas frmVentas = new FrmMantencionVentas();
                        escritorio.add(frmVentas);
                        frmVentas.setVisible(true);

                        try {
                            frmVentas.setSelected(true); 
                        } catch (java.beans.PropertyVetoException ex) {
                            ex.printStackTrace();
                        }

                    }
                    case 5 -> {
                        // TODO: Cartola por Fecha
                        System.out.println("Abriendo módulo: Cartola por Fecha");
                    }
                    case 6 -> {
                        FrmArchivoClientes frmClientes = new FrmArchivoClientes();
                        escritorio.add(frmClientes);
                        frmClientes.setVisible(true);
                        try {
                            frmClientes.setSelected(true);
                        } catch (java.beans.PropertyVetoException ex) { // <--- Cambiado de 'e' a 'ex'
                            ex.printStackTrace();
                        }
                        break;
                    }
                }
            });

            menuVentas.add(item);
        }

        // ==========================================
        // 2. COMPRAS (Atajo: Alt + C)
        // ==========================================
        JMenu menuCompras = new JMenu("COMPRAS");
        menuCompras.setFont(fuenteMenuPrincipal);
        menuCompras.setMnemonic(java.awt.event.KeyEvent.VK_C);

        // Desactivar popups ligeros para que se dibuje perfectamente al frente
        menuCompras.getPopupMenu().setLightWeightPopupEnabled(false);

        String[] itemsCompras = {
            "1. Proveedores",
            "2. Ingreso de Compras",
            "3. Libro de Compras",};

        for (int i = 0; i < itemsCompras.length; i++) {
            JMenuItem item = new JMenuItem(itemsCompras[i]);
            item.setFont(fuenteSubMenu);
            item.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

            switch (i) {
                case 0 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_1);
                case 1 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_2);
                case 2 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_3);

            }

            final int indexAccion = i + 1;
            item.addActionListener(e -> {
                switch (indexAccion) {
                    case 1 -> {
                        // Pasamos solo 'this' porque el constructor ya maneja la modalidad por dentro
                        DlgProveedores ventanaPro = new DlgProveedores(this);
                        ventanaPro.setVisible(true);
                        System.out.println("Abriendo Maestro de Proveedores, op: " + indexAccion);
                    }
                    case 2 -> {

                    }
                    case 3 -> {
                        // Siguiente opción de tu menú...
                        System.out.println("Abriendo opción: " + indexAccion);
                    }
                }
            });

            menuCompras.add(item);
        }

        // ==========================================
        // 3. BODEGAS (Atajo: Alt + B)
        // ==========================================
        JMenu menuBodegas = new JMenu("BODEGAS");
        menuBodegas.setFont(fuenteMenuPrincipal);
        menuBodegas.setMnemonic(java.awt.event.KeyEvent.VK_B);

        // Desactivar popups ligeros para que se dibuje perfectamente al frente
        menuBodegas.getPopupMenu().setLightWeightPopupEnabled(false);

        String[] itemsBodegas = {
            "1. Registros de Productos",
            "2. Registros de Familias",
            "3. Ingreso de Mercadería",
            "4. Kardex de Mercadería",
            "5. Inventario de Mercadería",
            "6. Inventario Crítico",
            "7. Rotación de Ventas",
            "8. Ventas por Familias",};

        for (int i = 0; i < itemsBodegas.length; i++) {
            JMenuItem item = new JMenuItem(itemsBodegas[i]);
            item.setFont(fuenteSubMenu);
            item.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

            // Asignar mnemónicos del 1 al 8 para acceso rápido por teclado
            switch (i) {
                case 0 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_1);
                case 1 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_2);
                case 2 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_3);
                case 3 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_4);
                case 4 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_5);
                case 5 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_6);
                case 6 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_7);
                case 7 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_8);
            }

            // Acción para cada opción del menú Bodegas
            final int index = i;
            item.addActionListener(e -> {
                if (index == 0) {
                    // Opción 0: Fichero Productos (Principal) como JInternalFrame
                    DlgProductos dlg = new DlgProductos();

                    // Lo agregamos al panel de escritorio principal de tu aplicación MDI
                    escritorio.add(dlg); // (Asegúrate de cambiar 'desktopPane' por el nombre de tu variable JDesktopPane si se llama diferente, ej: escritorio.add(dlg);)

                    dlg.setVisible(true);
                    try {
                        dlg.setSelected(true); // Lo pasamos al frente si ya estaba abierto
                    } catch (java.beans.PropertyVetoException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    // Espacio para los demás ítems del menú de bodegas
                    javax.swing.JOptionPane.showMessageDialog(this,
                            "Módulo en desarrollo: " + itemsBodegas[index],
                            "Información",
                            javax.swing.JOptionPane.INFORMATION_MESSAGE);
                }
            });

            menuBodegas.add(item);
        }

        // ==========================================
        // 4. CTACTE (Atajo: Alt + T)
        // ==========================================
        JMenu menuCtacte = new JMenu("CTACTE");
        menuCtacte.setFont(fuenteMenuPrincipal);
        menuCtacte.setMnemonic(java.awt.event.KeyEvent.VK_T);

        // Desactivar popups ligeros para que se dibuje perfectamente al frente
        menuCtacte.getPopupMenu().setLightWeightPopupEnabled(false);

        String[] itemsCtaCte = {
            "1. Eliminar Pago",
            "2. Listado de Documentos",
            "3. Estadístico de Clientes",
            "4. Cartola por Ventas",
            "5. Cartola por Fecha",
            "6. Cobros de la A a la Z",};

        for (int i = 0; i < itemsCtaCte.length; i++) {
            JMenuItem item = new JMenuItem(itemsCtaCte[i]);
            item.setFont(fuenteSubMenu);
            item.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

            // Asignar mnemónicos del 1 al 6 para acceso rápido por teclado
            switch (i) {
                case 0 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_1);
                case 1 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_2);
                case 2 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_3);
                case 3 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_4);
                case 4 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_5);
                case 5 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_6);
            }

            final int indexAccion = i + 1;

            // Acción individual para cada opción del menú
            item.addActionListener(e -> {
                switch (indexAccion) {
                    case 1 -> {
                        // Como 'this' es tu ventana principal (que contiene el JDesktopPane o puedes acceder a él)
                        // Reemplaza 'this.desktopPane' por el nombre de tu variable JDesktopPane real si se llama distinto.
                        FrmEliminarRecibo frmEliminar = new FrmEliminarRecibo();
                        this.escritorio.add(frmEliminar);
                        frmEliminar.setVisible(true);
                        try {
                            frmEliminar.setSelected(true);
                        } catch (java.beans.PropertyVetoException ex) {
                            ex.printStackTrace();
                        }
                    }
                    case 2 -> {
                        FrmCarteraCheques frmCartera = new FrmCarteraCheques();
                        escritorio.add(frmCartera);
                        frmCartera.setVisible(true);
                        try {
                            frmCartera.setSelected(true);
                        } catch (java.beans.PropertyVetoException ex) {
                            ex.printStackTrace();
                        }
                    }
                    case 3 -> {

                        FrmEstadisticasClientes frmCte = new FrmEstadisticasClientes();
                        escritorio.add(frmCte);
                        frmCte.setVisible(true);
                        try {
                            frmCte.setSelected(true);
                        } catch (java.beans.PropertyVetoException ex) { // <--- Cambiado de 'e' a 'ex'
                            ex.printStackTrace();
                        }
                        break;
                    }
                    case 4 -> {
                        FrmCartolaCliente frmCartola = new FrmCartolaCliente();
                        escritorio.add(frmCartola);
                        frmCartola.setVisible(true);
                        try {
                            frmCartola.setSelected(true);
                        } catch (java.beans.PropertyVetoException ex) { // <--- Cambiado de 'e' a 'ex'
                            ex.printStackTrace();
                        }
                        break;
                    }
                    case 5 -> {
                        FrmCartolaPorFecha frmFecha = new FrmCartolaPorFecha();
                        escritorio.add(frmFecha);
                        frmFecha.setVisible(true);
                        try {
                            frmFecha.setSelected(true);
                        } catch (java.beans.PropertyVetoException ex) {
                            ex.printStackTrace();
                        }
                    }
                    case 6 -> {
                        FrmCuentasCobrarAlfabetico frmCuentas = new FrmCuentasCobrarAlfabetico();
                        this.escritorio.add(frmCuentas);
                        frmCuentas.setVisible(true);
                        try {
                            frmCuentas.setSelected(true);
                        } catch (java.beans.PropertyVetoException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });

            menuCtacte.add(item);
        }

        // ==========================================
        // 5. ARCHIVOS (Atajo: Alt + A)
        // ==========================================
        JMenu menuArchivos = new JMenu("ARCHIVOS");
        menuArchivos.setFont(fuenteMenuPrincipal);
        menuArchivos.setMnemonic(java.awt.event.KeyEvent.VK_A); // Letra 'A' subrayada

        // Clave vital para que el submenú se dibuje al frente
        menuArchivos.getPopupMenu().setLightWeightPopupEnabled(false);
        String[] itemsArchivos = {
            "1. Finalizar la sesión",
            "2. Usuario del sistema",
            "3. Accesos a funciones",
            "4. Cámara key",
            "5. Foliador de hojas",
            "6. Parámetros y correlativos",
            "7. Indexación de la base de datos",
            "8. Licencia de uso"
        };

        for (int i = 0; i < itemsArchivos.length; i++) {
            JMenuItem item = new JMenuItem(itemsArchivos[i]);
            item.setFont(fuenteSubMenu);
            item.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

            // ==========================================
            // ASIGNAR TECLAS RÁPIDAS (1 al 8) COMO MNEMÓNICOS
            // ==========================================
            switch (i) {
                case 0 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_1);
                case 1 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_2);
                case 2 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_3);
                case 3 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_4);
                case 4 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_5);
                case 5 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_6);
                case 6 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_7);
                case 7 ->
                    item.setMnemonic(java.awt.event.KeyEvent.VK_8);
            }

            // Opción 6: Abrir ventana de Parámetros
            if (i == 5) {
                item.addActionListener(e -> {
                    DlgParametros dlg = new DlgParametros(this, true);
                    dlg.setVisible(true);
                });
            }

            menuArchivos.add(item);
        }

        // ==========================================
        // DISTRIBUCIÓN AUTOMÁTICA EN LA BARRA
        // ==========================================
        menuBar.add(Box.createHorizontalStrut(15)); // Margen inicial izquierdo
        menuBar.add(menuVentas);
        menuBar.add(Box.createHorizontalGlue());    // Espacio flexible automático
        menuBar.add(menuCompras);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(menuBodegas);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(menuCtacte);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(menuArchivos);

        menuBar.add(Box.createHorizontalStrut(30)); // Separación antes del título

        JLabel lblTitulo = new JLabel("NEXUM");
        lblTitulo.setFont(fuenteTitulo);
        lblTitulo.setForeground(new Color(100, 180, 255)); // aqui se cambia el color del t´tulo

        // ==========================================
        // CARGAR EL ICONO AL LADO IZQUIERDO
        // ==========================================
        try {
            ImageIcon iconoNexum = new ImageIcon(getClass().getResource("/iconos/nexum-16.png"));
            lblTitulo.setIcon(iconoNexum);
            // Espacio entre el icono y la letra "N" de NEXUM (en píxeles)
            lblTitulo.setIconTextGap(8);
            // Posicionamos el texto a la derecha del icono
            lblTitulo.setHorizontalTextPosition(JLabel.RIGHT);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el icono de la barra: " + e.getMessage());
        }

        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 15));
        menuBar.add(lblTitulo);

        setJMenuBar(menuBar);
        revalidate();
        repaint();

//        // ==========================================
//        // DISTRIBUCIÓN EN LA BARRA
//        // ==========================================
//        menuBar.add(Box.createHorizontalStrut(95));
//        menuBar.add(menuVentas);
//        menuBar.add(Box.createHorizontalStrut(105));
//        menuBar.add(menuCompras);
//        menuBar.add(Box.createHorizontalStrut(105));
//        menuBar.add(menuBodegas);
//        menuBar.add(Box.createHorizontalStrut(105));
//        menuBar.add(menuCtacte);
//        menuBar.add(Box.createHorizontalStrut(105));
//        menuBar.add(menuArchivos);
//
//        menuBar.add(Box.createHorizontalGlue());
//
//        JLabel lblTitulo = new JLabel("NEXUM");
//        lblTitulo.setFont(fuenteTitulo);
//        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 15));
//        menuBar.add(lblTitulo);
//
//        setJMenuBar(menuBar);
//        revalidate();
//        repaint();
    }

//    private void inicializarVentanaPrincipal() {
//        setTitle("");
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa opcional
//        setLayout(new BorderLayout());
//
//        // 1. Cargar tu barra de menú arriba
//        configurarMenu();
//
//        // 2. Colocar el Panel Dashboard de bienvenida al centro
//        PanelDashboard dashboard = new PanelDashboard();
//        add(dashboard, BorderLayout.CENTER);
//
//        revalidate();
//        repaint();
//    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        menuVentas = new javax.swing.JMenu();
        menuCompras = new javax.swing.JMenu();
        menuBodegas = new javax.swing.JMenu();
        menuCtacte = new javax.swing.JMenu();
        menuArchivos = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        menuVentas.setText("VENTAS");
        menuVentas.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jMenuBar1.add(menuVentas);

        menuCompras.setText("COMPRAS");
        menuCompras.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jMenuBar1.add(menuCompras);

        menuBodegas.setText("BODEGAS");
        menuBodegas.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jMenuBar1.add(menuBodegas);

        menuCtacte.setText("CTACTE CLIENTES");
        menuCtacte.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jMenuBar1.add(menuCtacte);

        menuArchivos.setText("ARCHIVOS");
        menuArchivos.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jMenuBar1.add(menuArchivos);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Aplicar FlatLaf Dark para el diseño moderno y oscuro
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException ex) {
        }

        SwingUtilities.invokeLater(FrmPrincipal::new);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu menuArchivos;
    private javax.swing.JMenu menuBodegas;
    private javax.swing.JMenu menuCompras;
    private javax.swing.JMenu menuCtacte;
    private javax.swing.JMenu menuVentas;
    // End of variables declaration//GEN-END:variables
}
