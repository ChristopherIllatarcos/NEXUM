package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.ClienteDTO;
import modelo.EstadisticasClienteDTO;
import java.util.Comparator;

public class ClienteDAO {

    public List<Object[]> cargarClientesDAO(String filtro) {
        List<Object[]> listaClientes = new ArrayList<>();

        // Apuntamos a pcliente_dbf, uniendo CLIRUT y CLIDIGIT, y buscando en CLINOMBRE y CLIRUT
        String sql = "SELECT (\"CLIRUT\" || '-' || \"CLIDIGIT\") AS rut_completo, "
                + "\"CLINOMBRE\", \"CLIDIREC\", \"CLICOMUNA\", \"CLIZONA\", \"CLIFONO1\", \"CLICIUDAD\" "
                + "FROM pcliente_dbf "
                + "WHERE (\"CLIRUT\" || '-' || \"CLIDIGIT\") ILIKE ? OR \"CLINOMBRE\" ILIKE ?";

        // Usamos estrictamente tu método getConnection() acordado
        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String parametro = "%" + (filtro != null ? filtro : "") + "%";
            pstmt.setString(1, parametro);
            pstmt.setString(2, parametro);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[]{
                        rs.getString("rut_completo"),
                        rs.getString("CLINOMBRE"),
                        rs.getString("CLIDIREC"),
                        rs.getString("CLICOMUNA"),
                        rs.getString("CLIZONA"),
                        rs.getString("CLIFONO1"),
                        rs.getString("CLICIUDAD")
                    // Agrega o ajusta las columnas adicionales de acuerdo al orden de tu JTable
                    };
                    listaClientes.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar los clientes: " + e.getMessage());
            e.printStackTrace();
        }

        return listaClientes;
    }

    public EstadisticasClienteDTO obtenerEstadisticasPorRut(String rutParam) {
        EstadisticasClienteDTO stats = null;

        String rutLimpio = rutParam;
        if (rutParam != null && rutParam.contains("-")) {
            rutLimpio = rutParam.split("-")[0];
        }

        int rutInt = 0;
        try {
            rutInt = Integer.parseInt(rutLimpio != null ? rutLimpio.replaceAll("[^0-9]", "").trim() : "0");
        } catch (NumberFormatException e) {
            rutInt = 0;
        }

        String sqlCliente = "SELECT \"CLIRUT\", \"CLIDIGIT\", \"CLINOMBRE\", \"CLISALDO\" "
                + "FROM pcliente_dbf "
                + "WHERE TRIM(\"CLIRUT\")::text = ?";

        // --- CONSULTA DE COMPRAS ORDENADA CRONOLÓGICAMENTE ---
        String sqlCompras = "SELECT COUNT(DISTINCT \"VCODIGO\"), "
                + "COALESCE(SUM(CAST(NULLIF(REPLACE(CAST(\"VPXPRECIO\" AS TEXT), ',', '.'), '') AS NUMERIC)), 0), "
                + "COALESCE(AVG(CAST(NULLIF(REPLACE(CAST(\"VPXPRECIO\" AS TEXT), ',', '.'), '') AS NUMERIC)), 0), "
                + "COALESCE(MAX(CAST(NULLIF(REPLACE(CAST(\"VPXPRECIO\" AS TEXT), ',', '.'), '') AS NUMERIC)), 0), "
                + "TO_CHAR(MAX(TO_DATE(NULLIF(TRIM(CAST(\"VFECHA\" AS TEXT)), ''), 'DD-MM-YYYY')), 'DD-MM-YYYY') "
                + "FROM pventas_dbf WHERE TRIM(CAST(\"VCLIENTE\" AS text)) = ?";

        // --- CONSULTA DE PAGOS ORDENADA CRONOLÓGICAMENTE ---
        String sqlPagos = "SELECT COUNT(*), "
                + "COALESCE(SUM(CAST(NULLIF(REPLACE(CAST(\"CTEABONO\" AS TEXT), ',', '.'), '') AS NUMERIC)), 0), "
                + "COALESCE(AVG(CAST(NULLIF(REPLACE(CAST(\"CTEABONO\" AS TEXT), ',', '.'), '') AS NUMERIC)), 0), "
                + "COALESCE(MAX(CAST(NULLIF(REPLACE(CAST(\"CTEABONO\" AS TEXT), ',', '.'), '') AS NUMERIC)), 0), "
                + "TO_CHAR(MAX(TO_DATE(NULLIF(TRIM(CAST(\"CTEFECHA\" AS TEXT)), ''), 'DD-MM-YYYY')), 'DD-MM-YYYY') "
                + "FROM pctacte_dbf "
                + "WHERE TRIM(CAST(\"CTERUT\" AS text)) = ? "
                + "AND COALESCE(CAST(NULLIF(REPLACE(CAST(\"CTEABONO\" AS TEXT), ',', '.'), '') AS NUMERIC), 0) > 0";

        try (Connection conn = Conexion.getConnection()) {

            // --- OBTENER CLIENTE ---
            try (PreparedStatement pstmt = conn.prepareStatement(sqlCliente)) {
                pstmt.setString(1, String.valueOf(rutInt));
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        stats = new EstadisticasClienteDTO();
                        String rutNumStr = rs.getString("CLIRUT") != null ? rs.getString("CLIRUT").trim() : "";
                        String digito = rs.getString("CLIDIGIT") != null ? rs.getString("CLIDIGIT").trim() : "";

                        stats.setRut(rutNumStr + "-" + digito);
                        stats.setNombre(rs.getString("CLINOMBRE") != null ? rs.getString("CLINOMBRE").trim() : "");
                        stats.setSaldoActual(rs.getBigDecimal("CLISALDO") != null ? rs.getBigDecimal("CLISALDO").doubleValue() : 0.0);
                    }
                }
            }

            // Si el cliente existe, consultamos sus compras y pagos
            if (stats != null) {

                // --- OBTENER ESTADÍSTICAS DE COMPRAS (PVENTAS) ---
                try (PreparedStatement pstmtC = conn.prepareStatement(sqlCompras)) {
                    pstmtC.setString(1, String.valueOf(rutInt));
                    try (ResultSet rsC = pstmtC.executeQuery()) {
                        if (rsC.next()) {
                            stats.setVecesCompro(rsC.getInt(1));
                            stats.setTotalCompro(rsC.getDouble(2));
                            stats.setPromCompro(rsC.getDouble(3));
                            stats.setMaxCompro(rsC.getDouble(4));
                            stats.setUltimaCompra(rsC.getString(5) != null ? rsC.getString(5).trim() : "N/A");
                        }
                    }
                } catch (SQLException ex) {
                    System.err.println("Aviso en compras: " + ex.getMessage());
                }

                // --- OBTENER ESTADÍSTICAS DE PAGOS/ABONOS ---
                try (PreparedStatement pstmtP = conn.prepareStatement(sqlPagos)) {
                    pstmtP.setString(1, String.valueOf(rutInt));
                    try (ResultSet rsP = pstmtP.executeQuery()) {
                        if (rsP.next()) {
                            stats.setVecesPago(rsP.getInt(1));
                            stats.setTotalPago(rsP.getDouble(2));
                            stats.setPromPago(rsP.getDouble(3));
                            stats.setMaxPago(rsP.getDouble(4));
                            stats.setUltimoPago(rsP.getString(5) != null ? rsP.getString(5).trim() : "N/A");
                        }
                    }
                } catch (SQLException ex) {
                    System.err.println("Aviso en pagos: " + ex.getMessage());
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener estadísticas del cliente desde DBF: " + e.getMessage());
            e.printStackTrace();
        }

        return stats;
    }

    public List<ClienteDTO> obtenerTodosLosClientes() {
        List<ClienteDTO> lista = new ArrayList<>();

        String sql = "SELECT \"CLIRUT\", \"CLIDIGIT\", \"CLINOMBRE\", \"CLISALDO\", \"CLICREDITO\" FROM pcliente_dbf";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ClienteDTO cliente = new ClienteDTO();
                // Construimos el RUT limpio con su dígito verificador
                String rut = rs.getString("CLIRUT") != null ? rs.getString("CLIRUT").trim() : "";
                String digit = rs.getString("CLIDIGIT") != null ? rs.getString("CLIDIGIT").trim() : "";
                cliente.setRut(rut + (!digit.isEmpty() ? "-" + digit : ""));

                // Leemos directamente todo el bloque de CLINOMBRE sin trozarlo
                String nombreCompleto = rs.getString("CLINOMBRE");
                cliente.setNombres(nombreCompleto != null ? nombreCompleto.trim() : "");

                // Mapeamos saldo y cupo de forma segura
                cliente.setSaldoActual(rs.getBigDecimal("CLISALDO"));
                cliente.setCupo(rs.getBigDecimal("CLICREDITO"));

                lista.add(cliente);
            }

            // Ordenado de la A a la Z por el nombre completo unificado
            lista.sort(Comparator.comparing(ClienteDTO::getNombres, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)));

        } catch (SQLException e) {
            System.err.println("Error al obtener los clientes: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    public boolean eliminarCliente(String rutParam) {
        if (rutParam == null || rutParam.trim().isEmpty()) {
            System.err.println("Error: El RUT llegó nulo o vacío al DAO.");
            return false;
        }

        // 1. Separamos el RUT y el dígito verificador limpiamente
        String rutParteNumero = rutParam;
        String digitoVerificador = "";

        if (rutParam.contains("-")) {
            String[] partes = rutParam.split("-");
            rutParteNumero = partes[0].trim();
            if (partes.length > 1) {
                digitoVerificador = partes[1].trim();
            }
        }

        // Limpiamos la parte numérica para asegurarnos de que solo sean dígitos
        rutParteNumero = rutParteNumero.replaceAll("[^0-9]", "").trim();

        System.out.println(">>> Buscando en BD -> CLIRUT: [" + rutParteNumero + "] y CLIDIGIT: [" + digitoVerificador + "]");

        // 2. SQL que borra haciendo match exacto con ambas columnas de la DBF
        String sql = "DELETE FROM pcliente_dbf WHERE TRIM(\"CLIRUT\")::text = ? AND TRIM(\"CLIDIGIT\") = ?";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rutParteNumero);
            pstmt.setString(2, digitoVerificador);

            int filasAfectadas = pstmt.executeUpdate();
            System.out.println(">>> Filas afectadas en la BD: " + filasAfectadas);

            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar el cliente de la base de datos: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertarCliente(ClienteDTO cliente) {
        // Como ya unificamos todo, tomamos el nombre completo directo de cliente.getNombres()
        String cliNombreFinal = cliente.getNombres() != null ? cliente.getNombres().trim().toUpperCase() : "";

        // Limpiamos la parte numérica del RUT
        String rutParam = cliente.getRut();
        String rutLimpio = rutParam != null ? rutParam.split("-")[0].replaceAll("[^0-9]", "").trim() : "";

        String sql = "INSERT INTO pcliente_dbf (\"CLIRUT\", \"CLIDIGIT\", \"CLINOMBRE\", \"CLISALDO\", \"CLICREDITO\") "
                + "VALUES (?, ?, ?, ?, ?) "
                + "ON CONFLICT (\"CLIRUT\") DO UPDATE SET "
                + "\"CLIDIGIT\" = EXCLUDED.\"CLIDIGIT\", "
                + "\"CLINOMBRE\" = EXCLUDED.\"CLINOMBRE\", "
                + "\"CLISALDO\" = EXCLUDED.\"CLISALDO\", "
                + "\"CLICREDITO\" = EXCLUDED.\"CLICREDITO\"";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rutLimpio);
            pstmt.setString(2, cliente.getDigitoVerificador());
            pstmt.setString(3, cliNombreFinal);
            pstmt.setBigDecimal(4, cliente.getSaldoActual() != null ? cliente.getSaldoActual() : java.math.BigDecimal.ZERO);
            pstmt.setBigDecimal(5, cliente.getCupo() != null ? cliente.getCupo() : java.math.BigDecimal.ZERO);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarCliente(ClienteDTO cliente) {
        // Tomamos el nombre completo directamente de la única caja de texto
        String cliNombreFinal = cliente.getNombres() != null ? cliente.getNombres().trim().toUpperCase() : "";

        // Limpiamos el RUT como String (evitamos pasarlo como int para prevenir el error de tipos)
        String rutParam = cliente.getRut();
        String rutLimpio = rutParam != null ? rutParam.split("-")[0].replaceAll("[^0-9]", "").trim() : "";

        // Consulta SQL usando TRIM y ::text para calzar perfectamente con el varchar de PostgreSQL
        String sql = "UPDATE pcliente_dbf SET "
                + "\"CLINOMBRE\" = ?, "
                + "\"CLISALDO\" = ?, "
                + "\"CLICREDITO\" = ? "
                + "WHERE TRIM(\"CLIRUT\")::text = ?";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cliNombreFinal);
            pstmt.setBigDecimal(2, cliente.getSaldoActual() != null ? cliente.getSaldoActual() : java.math.BigDecimal.ZERO);
            pstmt.setBigDecimal(3, cliente.getCupo() != null ? cliente.getCupo() : java.math.BigDecimal.ZERO);
            pstmt.setString(4, rutLimpio); // Pasado como String limpio

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("--- ERROR SQL AL ACTUALIZAR CLIENTE ---");
            e.printStackTrace();
            return false;
        }
    }

    public ClienteDTO obtenerClientePorRut(String rutParam) {
        if (rutParam == null || rutParam.trim().isEmpty()) {
            return null;
        }

        // 1. Limpiamos el RUT
        String rutLimpio = rutParam;
        if (rutParam.contains("-")) {
            rutLimpio = rutParam.split("-")[0];
        }
        rutLimpio = rutLimpio.replaceAll("[^0-9]", "").trim();

        // 2. Consulta SQL con casteo seguro para evitar conflictos de tipos
        String sql = "SELECT * FROM pcliente_dbf WHERE TRIM(\"CLIRUT\")::text = ?";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rutLimpio);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    ClienteDTO cliente = new ClienteDTO();
                    cliente.setRut(rs.getString("CLIRUT").trim());
                    cliente.setDigitoVerificador(rs.getString("CLIDIGIT"));

                    // Nombre completo unificado
                    String nombreCompleto = rs.getString("CLINOMBRE");
                    cliente.setNombres(nombreCompleto != null ? nombreCompleto.trim() : "");

                    // Mapeo exacto con las columnas que vimos en tu DBeaver
                    cliente.setSaldoActual(rs.getBigDecimal("CLISALDO")); // Columna nummric real[cite: 5]
                    cliente.setCupo(rs.getBigDecimal("CLICREDITO"));      // Columna nummric real de crédito/cupo[cite: 5]

                    return cliente;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener cliente por RUT: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

}
