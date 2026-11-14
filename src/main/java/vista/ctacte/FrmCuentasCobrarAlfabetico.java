package vista.ctacte;

import conexion.Conexion;
import dao.CuentaCorrienteDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.util.List;
import java.util.Vector;
import modelo.DocumentoCarteraDTO;

public class FrmCuentasCobrarAlfabetico extends JInternalFrame {

    private JTextField txtBuscarAlfabetico;
    private JTable tablaCuentas;
    private DefaultTableModel modeloTabla;
    private JLabel lblModoVal;

    private boolean modoResumen = true; // Inicia en Resumen por defecto

    private final Color COLOR_AZUL_FONDO = new Color(0, 0, 132);
    private final Color COLOR_TEXTO_BLANCO = Color.WHITE;
    private final Color COLOR_TEXTO_VERDE = Color.GREEN;
    private final Color COLOR_CIAN = Color.CYAN;
    private final Font FUENTE_CONSOLA = new Font("Courier New", Font.BOLD, 13);

    public FrmCuentasCobrarAlfabetico() {
        super(" 6.- CUENTAS x COBRAR x ALFABETICO ", false, true, false, true);
        setSize(850, 560);
        setFrameIcon(null);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(COLOR_AZUL_FONDO);
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Panel superior de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelBusqueda.setBackground(COLOR_AZUL_FONDO);
        panelBusqueda.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(COLOR_CIAN, 2), " BUSQUEDA ALFABETICA CLIENTE ",
                0, 0, FUENTE_CONSOLA, COLOR_CIAN
        ));

        JLabel lblFiltro = new JLabel("Cliente / RUT :");
        lblFiltro.setFont(FUENTE_CONSOLA);
        lblFiltro.setForeground(COLOR_TEXTO_BLANCO);
        panelBusqueda.add(lblFiltro);

        txtBuscarAlfabetico = new JTextField(30);
        estilarCampo(txtBuscarAlfabetico);
        panelBusqueda.add(txtBuscarAlfabetico);

        // Ventanita mini de estado (Resumen/Detalle)
        JPanel panelModoMini = new JPanel(new GridLayout(2, 1));
        panelModoMini.setBackground(new Color(0, 0, 90));
        panelModoMini.setBorder(new LineBorder(COLOR_CIAN, 1));

        JLabel lblTituloMini = new JLabel(" Ctas x Cobrar ", JLabel.CENTER);
        lblTituloMini.setFont(new Font("Courier New", Font.BOLD, 11));
        lblTituloMini.setForeground(Color.RED);
        panelModoMini.add(lblTituloMini);

        lblModoVal = new JLabel(" (R)esumen  SECTOR: 0 ", JLabel.CENTER);
        lblModoVal.setFont(new Font("Courier New", Font.BOLD, 11));
        lblModoVal.setForeground(COLOR_CIAN);
        panelModoMini.add(lblModoVal);

        panelBusqueda.add(Box.createHorizontalStrut(30));
        panelBusqueda.add(panelModoMini);
        panelPrincipal.add(panelBusqueda, BorderLayout.NORTH);

        // Tabla central (Se inicializa con columnas de Resumen)
        modeloTabla = new DefaultTableModel(obtenerColumnasActuales(), 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCuentas = new JTable(modeloTabla);
        estilarTabla(tablaCuentas);
        JScrollPane scrollTabla = new JScrollPane(tablaCuentas);
        scrollTabla.getViewport().setBackground(Color.BLACK);
        scrollTabla.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(COLOR_CIAN, 2), " LISTADO GENERAL ORDENADO ALFABETICAMENTE ",
                0, 0, FUENTE_CONSOLA, COLOR_CIAN
        ));
        panelPrincipal.add(scrollTabla, BorderLayout.CENTER);

        // Barra inferior de atajos
        JPanel panelAyuda = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        panelAyuda.setBackground(new Color(0, 0, 80));
        JLabel lblAyuda = new JLabel(" [ESC] Salir    [R] Resumen    [D] Detalle    [P] Exportar PDF ");
        lblAyuda.setFont(new Font("Courier New", Font.BOLD, 12));
        lblAyuda.setForeground(COLOR_CIAN);
        panelAyuda.add(lblAyuda);
        panelPrincipal.add(panelAyuda, BorderLayout.SOUTH);

        add(panelPrincipal);

        // Carga inicial
        recargarDatos();

        // Eventos
        txtBuscarAlfabetico.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (tablaCuentas.getRowCount() > 0) {
                        tablaCuentas.requestFocus();
                        tablaCuentas.setRowSelectionInterval(0, 0);
                    }
                } else {
                    recargarDatos();
                }
            }
        });

        // Atajos globales
        panelPrincipal.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "ESCAPE");
        panelPrincipal.getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });

        panelPrincipal.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0), "MODO_RESUMEN");
        panelPrincipal.getActionMap().put("MODO_RESUMEN", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                modoResumen = true;
                lblModoVal.setText(" (R)esumen  SECTOR: 0 ");
                actualizarEstructuraTabla();
                recargarDatos();
            }
        });

        panelPrincipal.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "MODO_DETALLE");
        panelPrincipal.getActionMap().put("MODO_DETALLE", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                modoResumen = false;
                lblModoVal.setText(" (D)etalle  SECTOR: 0 ");
                actualizarEstructuraTabla();
                recargarDatos();
            }
        });

        panelPrincipal.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0), "EXPORTAR_PDF");
        panelPrincipal.getActionMap().put("EXPORTAR_PDF", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                exportarPDFActual();
            }
        });
    }

    private String[] obtenerColumnasActuales() {
        if (modoResumen) {
            return new String[]{"RUT / Cliente", "Cant. Documentos", "Fecha Ref.", "Total Acumulado ($)"};
        } else {
            return new String[]{"RUT / Cliente", "N° Folio", "Fecha Documento", "Monto Abono ($)"};
        }
    }

    private void actualizarEstructuraTabla() {
        modeloTabla.setColumnIdentifiers(obtenerColumnasActuales());
    }

    private void estilarCampo(JTextField txt) {
        txt.setFont(FUENTE_CONSOLA);
        txt.setBackground(Color.BLACK);
        txt.setForeground(COLOR_TEXTO_VERDE);
        txt.setCaretColor(COLOR_TEXTO_VERDE);
        txt.setBorder(new LineBorder(COLOR_CIAN, 1));
    }

    private void estilarTabla(JTable tabla) {
        tabla.setFont(FUENTE_CONSOLA);
        tabla.setBackground(Color.BLACK);
        tabla.setForeground(COLOR_TEXTO_VERDE);
        tabla.setGridColor(COLOR_CIAN);
        tabla.setRowHeight(22);
        tabla.getTableHeader().setFont(FUENTE_CONSOLA);
        tabla.getTableHeader().setBackground(new Color(0, 0, 80));
        tabla.getTableHeader().setForeground(COLOR_TEXTO_BLANCO);
    }

    private void recargarDatos() {
        modeloTabla.setRowCount(0);
        String filtro = txtBuscarAlfabetico.getText().trim();

        // Llamada puramente delegada al DAO (Cero SQL en la interfaz)
        try (Connection con = Conexion.getConnection()) {
            CuentaCorrienteDAO dao = new CuentaCorrienteDAO();
            List<DocumentoCarteraDTO> lista;

            if (modoResumen) {
                lista = dao.obtenerCuentasResumen(con, filtro);
            } else {
                lista = dao.obtenerCuentasDetalle(con, filtro);
            }

            for (DocumentoCarteraDTO doc : lista) {
                Vector<Object> fila = new Vector<>();
                fila.add(doc.getCliente());
                fila.add(doc.getFolio());
                fila.add(doc.getFecha());
                fila.add(String.format("$ %,.0f", doc.getMonto()));
                modeloTabla.addRow(fila);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void exportarPDFActual() {
        String filtro = txtBuscarAlfabetico.getText().trim();
        try (Connection con = Conexion.getConnection()) {
            CuentaCorrienteDAO dao = new CuentaCorrienteDAO();
            List<DocumentoCarteraDTO> lista;

            if (modoResumen) {
                lista = dao.obtenerCuentasResumen(con, filtro);
            } else {
                lista = dao.obtenerCuentasDetalle(con, filtro);
            }

            boolean exito = utilidades.PDFUtils.exportarCuentasCobrarPDF(lista, modoResumen);
            if (exito) {
                JOptionPane.showMessageDialog(this, "¡PDF exportado y generado con éxito!", "Exportación Completa", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al generar el archivo PDF.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Excepción al conectar para exportar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
