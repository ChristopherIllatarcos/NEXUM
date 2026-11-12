package vista;

import dao.ClienteDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import modelo.ClienteDTO;

public class FrmArchivoClientes extends javax.swing.JInternalFrame {

    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private JLabel lblRegistros;
    private ClienteDAO clienteDAO;

    public FrmArchivoClientes() {
        clienteDAO = new ClienteDAO();
        configurarVentana();
        inicializarComponentes();
        cargarClientes();
    }

    private void configurarVentana() {
        setTitle("ARCHIVO DE CLIENTES");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(0, 0, 128)); // Azul clásico terminal
        setLayout(new BorderLayout());
    }

    private void inicializarComponentes() {
        // --- PANEL SUPERIOR (Título y contador de registros) ---
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(new Color(0, 0, 128));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        JLabel lblTitulo = new JLabel("ARCHIVO DE CLIENTES");
        lblTitulo.setFont(new Font("Monospaced", Font.BOLD, 18));
        lblTitulo.setForeground(Color.YELLOW);

        lblRegistros = new JLabel("Registros: 0");
        lblRegistros.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblRegistros.setForeground(Color.GREEN);

        panelSuperior.add(lblTitulo, BorderLayout.WEST);
        panelSuperior.add(lblRegistros, BorderLayout.EAST);
        add(panelSuperior, BorderLayout.NORTH);

        // --- TABLA CENTRAL (R.U.T. y NOMBRE DEL CLIENTE) ---
        String[] columnas = {"R.U.T.", "NOMBRE DEL CLIENTE"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Solo lectura tipo listado
            }
        };

        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setFont(new Font("Monospaced", Font.BOLD, 16));
        tablaClientes.setRowHeight(25);
        tablaClientes.setBackground(new Color(0, 0, 170)); // Azul brillante estilo consola
        tablaClientes.setForeground(Color.WHITE);
        tablaClientes.setSelectionBackground(Color.CYAN);
        tablaClientes.setSelectionForeground(Color.BLACK);

        // Cabecera de la tabla
        tablaClientes.getTableHeader().setFont(new Font("Monospaced", Font.BOLD, 16));
        tablaClientes.getTableHeader().setBackground(new Color(0, 0, 100));
        tablaClientes.getTableHeader().setForeground(Color.YELLOW);

        JScrollPane scrollPane = new JScrollPane(tablaClientes);
        scrollPane.getViewport().setBackground(new Color(0, 0, 170));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.CYAN, 1));

        JPanel panelCentro = new JPanel(new BorderLayout());
        panelCentro.setBackground(new Color(0, 0, 128));
        panelCentro.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        panelCentro.add(scrollPane, BorderLayout.CENTER);
        add(panelCentro, BorderLayout.CENTER);

        // --- PANEL INFERIOR (Barra de Acciones / Teclas rápidas) ---
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelInferior.setBackground(new Color(180, 0, 0)); // Barra roja clásica inferior
        panelInferior.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        JLabel lblAcciones = new JLabel("(Esc) Salir   |   (Ins) Insertar   |   (Supr) Eliminar   |   (M) Modificar   |   (B) Buscar   |   (V) Ver Ficha");
        lblAcciones.setFont(new Font("Monospaced", Font.BOLD, 13));
        lblAcciones.setForeground(Color.WHITE);
        panelInferior.add(lblAcciones);
        add(panelInferior, BorderLayout.SOUTH);

        // --- EVENTOS DE TECLADO (Atajos idénticos al sistema original) ---
        KeyAdapter listenerTeclado = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE ->
                        dispose();
                    case KeyEvent.VK_INSERT ->
                        insertarCliente();
                    case KeyEvent.VK_DELETE ->
                        eliminarCliente();
                    case KeyEvent.VK_B ->
                        buscarCliente();
                    case KeyEvent.VK_M ->
                        modificarCliente();
                    case KeyEvent.VK_V, KeyEvent.VK_ENTER ->
                        verEstadisticasCliente();
                    default -> {
                    }
                }
            }
        };

        this.addKeyListener(listenerTeclado);
        tablaClientes.addKeyListener(listenerTeclado);
        setFocusable(true);
    }

    private void cargarClientes() {
        modeloTabla.setRowCount(0);
        List<ClienteDTO> lista = clienteDAO.obtenerTodosLosClientes();

        // Ordena alfabéticamente al vuelo tomando en cuenta el apellido que pusimos al principio
        lista.sort((c1, c2) -> c1.getNombreCompletoFormato().compareToIgnoreCase(c2.getNombreCompletoFormato()));

        for (ClienteDTO c : lista) {
            modeloTabla.addRow(new Object[]{
                c.getRut().trim(),
                c.getNombreCompletoFormato()
            });
        }
        lblRegistros.setText("Registros: " + lista.size());
    }

    private void insertarCliente() {
        // 🎯 Usamos GridBagLayout en lugar de GridLayout para controlar el alto natural de los campos
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(0, 0, 128));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Espaciado entre elementos
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Solo se estiran a lo ancho, NUNCA a lo alto

        Font fuenteEtiqueta = new Font("Monospaced", Font.BOLD, 14);
        Font fuenteCampo = new Font("Monospaced", Font.PLAIN, 14);
        Color colorTexto = Color.YELLOW;

        JTextField txtRutIns = new JTextField(15); // Ancho estimado
        txtRutIns.setFont(fuenteCampo);

        JTextField txtNombresIns = new JTextField(15);
        txtNombresIns.setFont(fuenteCampo);

        JTextField txtSaldoIns = new JTextField("0", 15);
        txtSaldoIns.setFont(fuenteCampo);

        JTextField txtCupoIns = new JTextField("1000000", 15);
        txtCupoIns.setFont(fuenteCampo);

        JLabel lblR = new JLabel(" R.U.T. :");
        lblR.setForeground(colorTexto);
        lblR.setFont(fuenteEtiqueta);

        JLabel lblNom = new JLabel(" NOMBRE COMPLETO :");
        lblNom.setForeground(colorTexto);
        lblNom.setFont(fuenteEtiqueta);

        JLabel lblS = new JLabel(" SALDO :");
        lblS.setForeground(colorTexto);
        lblS.setFont(fuenteEtiqueta);

        JLabel lblC = new JLabel(" CUPO :");
        lblC.setForeground(colorTexto);
        lblC.setFont(fuenteEtiqueta);

        // --- FILA 0: RUT ---
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        panel.add(lblR, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0; // El campo de texto toma el ancho disponible
        panel.add(txtRutIns, gbc);

        // --- FILA 1: NOMBRE ---
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        panel.add(lblNom, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(txtNombresIns, gbc);

        // --- FILA 2: SALDO ---
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        panel.add(lblS, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(txtSaldoIns, gbc);

        // --- FILA 3: CUPO ---
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        panel.add(lblC, gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(txtCupoIns, gbc);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(550, 240));
        scrollPane.setBorder(null);
        int resultado = JOptionPane.showConfirmDialog(
                this, scrollPane, "INGRESAR NUEVO CLIENTE",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (resultado == JOptionPane.OK_OPTION) {
            String rutCompleto = txtRutIns.getText().trim();
            String nombresCompleto = txtNombresIns.getText().trim().toUpperCase();

            if (rutCompleto.isEmpty() || nombresCompleto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El RUT y el Nombre Completo son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Separamos el RUT y el dígito verificador limpiamente
            String rutParteNumero = rutCompleto;
            String digitoVerificador = "";

            if (rutCompleto.contains("-")) {
                String[] partes = rutCompleto.split("-");
                rutParteNumero = partes[0].trim();
                if (partes.length > 1) {
                    digitoVerificador = partes[1].trim();
                }
            }

            try {
                java.math.BigDecimal saldo = new java.math.BigDecimal(txtSaldoIns.getText().trim());
                java.math.BigDecimal cupo = new java.math.BigDecimal(txtCupoIns.getText().trim());

                // Creamos el DTO pasando el RUT limpio y el nombre completo unificado
                ClienteDTO nuevoCliente = new ClienteDTO();
                nuevoCliente.setRut(rutParteNumero);
                nuevoCliente.setDigitoVerificador(digitoVerificador);
                nuevoCliente.setNombres(nombresCompleto); // 👈 Todo el nombre y apellidos van aquí juntos
                nuevoCliente.setSaldoActual(saldo);
                nuevoCliente.setCupo(cupo);

                boolean exito = clienteDAO.insertarCliente(nuevoCliente);

                if (exito) {
                    JOptionPane.showMessageDialog(this, "¡Cliente registrado correctamente!");
                    cargarClientes();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo guardar el cliente en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Los campos de Saldo y Cupo deben ser numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void eliminarCliente() {
        int filaSeleccionada = tablaClientes.getSelectedRow();

        if (filaSeleccionada >= 0) {
            // Convertimos el índice de la vista al índice real del modelo (por si la tabla está ordenada o filtrada)
            int filaModelo = tablaClientes.convertRowIndexToModel(filaSeleccionada);

            // Obtenemos el RUT de la columna 0 usando el índice del modelo
            String rut = (String) modeloTabla.getValueAt(filaModelo, 0);

            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Desea eliminar al cliente RUT: " + rut + "?",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (opcion == JOptionPane.YES_OPTION) {
                // Llamamos al DAO que ya adaptamos para limpiar el RUT y borrar en pcliente_dbf
                boolean exito = clienteDAO.eliminarCliente(rut);

                if (exito) {
                    // Primero recargamos los datos y la tabla para reflejar el cambio en pantalla
                    cargarClientes();

                    // Luego mostramos la confirmación al usuario
                    JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente.");
                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "No se pudo eliminar el cliente de la base de datos.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Por favor, seleccione un cliente de la lista.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }

    private void modificarCliente() {
        int filaSeleccionada = tablaClientes.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un cliente de la lista para modificar, compadre.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String rutSeleccionado = tablaClientes.getValueAt(filaSeleccionada, 0).toString();
        ClienteDTO clienteActual = clienteDAO.obtenerClientePorRut(rutSeleccionado);

        if (clienteActual == null) {
            JOptionPane.showMessageDialog(this, "No se encontró la información del cliente en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(4, 2, 8, 12)); // 4 filas
        panel.setBackground(new Color(0, 0, 128));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        Font fuenteGrande = new Font("Monospaced", Font.BOLD, 16);
        Color colorTexto = Color.YELLOW;

        JTextField txtRutMod = new JTextField(clienteActual.getRut());
        txtRutMod.setFont(fuenteGrande);
        txtRutMod.setEditable(false);
        txtRutMod.setBackground(new Color(220, 220, 220));

        JTextField txtNombresMod = new JTextField(clienteActual.getNombres()); // Trae todo el CLINOMBRE completo
        txtNombresMod.setFont(fuenteGrande);

        JTextField txtSaldoMod = new JTextField(clienteActual.getSaldoActual() != null ? clienteActual.getSaldoActual().toString() : "0");
        txtSaldoMod.setFont(fuenteGrande);

        JTextField txtCupoMod = new JTextField(clienteActual.getCupo() != null ? clienteActual.getCupo().toString() : "1000000");
        txtCupoMod.setFont(fuenteGrande);

        JLabel lblR = new JLabel(" R.U.T. (Fijo) :");
        lblR.setForeground(colorTexto);
        lblR.setFont(fuenteGrande);

        JLabel lblNom = new JLabel(" NOMBRE COMPLETO :");
        lblNom.setForeground(colorTexto);
        lblNom.setFont(fuenteGrande);

        JLabel lblS = new JLabel(" SALDO :");
        lblS.setForeground(colorTexto);
        lblS.setFont(fuenteGrande);

        JLabel lblC = new JLabel(" CUPO :");
        lblC.setForeground(colorTexto);
        lblC.setFont(fuenteGrande);

        panel.add(lblR);
        panel.add(txtRutMod);
        panel.add(lblNom);
        panel.add(txtNombresMod);
        panel.add(lblS);
        panel.add(txtSaldoMod);
        panel.add(lblC);
        panel.add(txtCupoMod);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(600, 320));
        scrollPane.setBorder(null);

        int resultado = JOptionPane.showConfirmDialog(
                this, scrollPane, "MODIFICAR CLIENTE",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (resultado == JOptionPane.OK_OPTION) {
            String nombresCompleto = txtNombresMod.getText().trim().toUpperCase();

            if (nombresCompleto.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El Nombre Completo no puede quedar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                java.math.BigDecimal saldo = new java.math.BigDecimal(txtSaldoMod.getText().trim());
                java.math.BigDecimal cupo = new java.math.BigDecimal(txtCupoMod.getText().trim());

                ClienteDTO clienteModificado = new ClienteDTO();
                clienteModificado.setRut(rutSeleccionado);
                clienteModificado.setNombres(nombresCompleto); // 👈 Todo el bloque unificado directo a CLINOMBRE
                clienteModificado.setSaldoActual(saldo);
                clienteModificado.setCupo(cupo);

                boolean exito = clienteDAO.actualizarCliente(clienteModificado);

                if (exito) {
                    JOptionPane.showMessageDialog(this, "¡Cliente modificado correctamente!");
                    cargarClientes();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo actualizar el registro en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Los campos de Saldo y Cupo deben ser numéricos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buscarCliente() {
        String rutBuscado = JOptionPane.showInputDialog(this, "Ingrese R.U.T. a buscar (Ej: 12345678-9):");

        if (rutBuscado != null && !rutBuscado.trim().isEmpty()) {
            String busquedaLimpia = rutBuscado.trim().toLowerCase();
            boolean encontrado = false;

            // 1. Buscamos primero dentro de los registros cargados en la JTable
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                String rutTabla = (String) modeloTabla.getValueAt(i, 0); // Columna del RUT
                String nombreTabla = (String) modeloTabla.getValueAt(i, 1); // Columna del Nombre (por si acaso quieren buscar por nombre también)

                // Comparamos si coincide el RUT o si contiene parte del texto buscado
                if ((rutTabla != null && rutTabla.toLowerCase().contains(busquedaLimpia))
                        || (nombreTabla != null && nombreTabla.toLowerCase().contains(busquedaLimpia))) {

                    tablaClientes.setRowSelectionInterval(i, i);
                    tablaClientes.scrollRectToVisible(tablaClientes.getCellRect(i, 0, true));
                    encontrado = true;
                    break; // Encontramos la coincidencia y salimos del ciclo
                }
            }

            // 2. Si no se encontró en la tabla visual, podemos buscar directamente en el DAO o avisar
            if (!encontrado) {
                // Opcional: Podríamos hacer una consulta rápida al DAO si manejas una búsqueda general,
                // pero por lo pronto avisamos de forma limpia al usuario:
                JOptionPane.showMessageDialog(this,
                        "No se encontró ningún cliente con el criterio: " + rutBuscado,
                        "Cliente no encontrado",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    private void verEstadisticasCliente() {
        int fila = tablaClientes.getSelectedRow();
        System.out.println("DEBUG: Fila seleccionada -> " + fila);

        if (fila >= 0) {
            // Usamos el índice del modelo por si la tabla llega a estar filtrada u ordenada
            int filaModelo = tablaClientes.convertRowIndexToModel(fila);
            Object valorCelda = modeloTabla.getValueAt(filaModelo, 0);

            if (valorCelda == null) {
                JOptionPane.showMessageDialog(this, "La celda del RUT está vacía.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String rut = valorCelda.toString().trim();
            System.out.println("DEBUG: RUT capturado para la ficha -> [" + rut + "]");

            // 1. Instanciamos el frame interno de estadísticas pasándole el RUT
            FrmEstadisticasClientes frmEstadisticas = new FrmEstadisticasClientes(rut);

            // 2. OBTENEMOS EL ESCRITORIO REAL DE LA APLICACIÓN (el de FrmPrincipal)
            JDesktopPane escritorioReal = this.getDesktopPane();

            if (escritorioReal != null) {
                escritorioReal.add(frmEstadisticas);
                frmEstadisticas.setVisible(true);
                try {
                    frmEstadisticas.setSelected(true);
                } catch (java.beans.PropertyVetoException ex) {
                    ex.printStackTrace();
                }
            } else {
                // Respaldo por seguridad
                frmEstadisticas.setVisible(true);
            }

        } else {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para ver su ficha/estadísticas.");
        }
    }
}
