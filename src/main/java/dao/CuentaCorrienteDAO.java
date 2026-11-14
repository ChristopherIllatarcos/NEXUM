package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.DocumentoCarteraDTO;
import modelo.EstadisticasClienteDTO;

/**
 *
 * @author caill
 */
public class CuentaCorrienteDAO {

    // --- CONSULTAR ESTADÍSTICAS DEL CLIENTE ---
    public EstadisticasClienteDTO obtenerEstadisticas(Connection con, String rut) {
        EstadisticasClienteDTO stats = new EstadisticasClienteDTO();
        if (con == null || rut == null || rut.trim().isEmpty()) {
            return stats;
        }

        stats.setRut(rut.trim());

        try {
            // 1. Compras ordenadas cronológicamente
            String sqlCompras = "SELECT COUNT(DISTINCT \"VCODIGO\"), "
                    + "COALESCE(SUM(CAST(NULLIF(REPLACE(CAST(\"VPXPRECIO\" AS TEXT), ',', '.'), '') AS NUMERIC)), 0), "
                    + "COALESCE(AVG(CAST(NULLIF(REPLACE(CAST(\"VPXPRECIO\" AS TEXT), ',', '.'), '') AS NUMERIC)), 0), "
                    + "COALESCE(MAX(CAST(NULLIF(REPLACE(CAST(\"VPXPRECIO\" AS TEXT), ',', '.'), '') AS NUMERIC)), 0), "
                    + "TO_CHAR(MAX(TO_DATE(NULLIF(TRIM(CAST(\"VFECHA\" AS TEXT)), ''), 'DD-MM-YYYY')), 'DD-MM-YYYY') "
                    + "FROM pventas_dbf WHERE TRIM(CAST(\"VCLIENTE\" AS text)) LIKE ?";

            try (PreparedStatement ps = con.prepareStatement(sqlCompras)) {
                ps.setString(1, "%" + rut.trim() + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        stats.setVecesCompro(rs.getInt(1));
                        stats.setTotalCompro(rs.getDouble(2));
                        stats.setPromCompro(rs.getDouble(3));
                        stats.setMaxCompro(rs.getDouble(4));
                        String fec = rs.getString(5);
                        if (fec != null) {
                            stats.setUltimaCompra(fec);
                        }
                    }
                }
            }

            // 2. Pagos / Abonos ordenados cronológicamente
            String sqlPagos = "SELECT COUNT(*), "
                    + "COALESCE(SUM(CAST(NULLIF(REPLACE(CAST(\"CTEABONO\" AS TEXT), ',', '.'), '') AS NUMERIC)), 0), "
                    + "COALESCE(AVG(CAST(NULLIF(REPLACE(CAST(\"CTEABONO\" AS TEXT), ',', '.'), '') AS NUMERIC)), 0), "
                    + "COALESCE(MAX(CAST(NULLIF(REPLACE(CAST(\"CTEABONO\" AS TEXT), ',', '.'), '') AS NUMERIC)), 0), "
                    + "TO_CHAR(MAX(TO_DATE(NULLIF(TRIM(CAST(\"CTEFECHA\" AS TEXT)), ''), 'DD-MM-YYYY')), 'DD-MM-YYYY') "
                    + "FROM pctacte_dbf "
                    + "WHERE TRIM(CAST(\"CTERUT\" AS text)) LIKE ? "
                    + "AND COALESCE(CAST(NULLIF(REPLACE(CAST(\"CTEABONO\" AS TEXT), ',', '.'), '') AS NUMERIC), 0) > 0";

            try (PreparedStatement ps = con.prepareStatement(sqlPagos)) {
                ps.setString(1, "%" + rut.trim() + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        stats.setVecesPago(rs.getInt(1));
                        stats.setTotalPago(rs.getDouble(2));
                        stats.setPromPago(rs.getDouble(3));
                        stats.setMaxPago(rs.getDouble(4));
                        String fec = rs.getString(5);
                        if (fec != null) {
                            stats.setUltimoPago(fec);
                        }
                    }
                }
            }

            // 3. Calcular saldo actual basado en Totales
            stats.setSaldoActual(stats.getTotalCompro() - stats.getTotalPago());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    // --- ELIMINAR PAGO / RECIBO POR FOLIO ---
    public boolean eliminarPagoPorFolio(Connection con, String folio) {
        String sql = "DELETE FROM pctacte_dbf WHERE CAST(\"CTEFOLIO\" AS TEXT) = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, folio.trim());
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // (Dentro de tu clase CuentaCorrienteDAO existente)
    public List<DocumentoCarteraDTO> listarDocumentosCartera(Connection con, String nombreCliente, String fechaInicio, String fechaFin) {
        List<DocumentoCarteraDTO> lista = new ArrayList<>();

        // Limpiamos y separamos el RUT por si el usuario ingresó el dígito verificador con guion (ej: 10542125-7)
        String clienteTrim = nombreCliente != null ? nombreCliente.trim() : "";
        if (clienteTrim.contains("-")) {
            clienteTrim = clienteTrim.split("-")[0]; // Nos quedamos solo con la parte antes del guion (10542125)
        }
        clienteTrim = clienteTrim.replace(".", "").replace(" ", "");

        String filtroCliente = "%" + clienteTrim + "%";

        // SQL limpio enfocado en buscar por el cuerpo base del RUT y el rango de fechas
        String sql = "SELECT \"CTEFOLIO\", \"CTEFECHA\", \"CTERUT\", \"CTEABONO\" "
                + "FROM pctacte_dbf "
                + "WHERE ( "
                + "    UPPER(CAST(\"CTERUT\" AS TEXT)) LIKE UPPER(?) "
                + "    OR ? = '' "
                + ") "
                + "AND (CASE WHEN TRIM(CAST(\"CTEFECHA\" AS TEXT)) ~ '^\\d{2}-\\d{2}-\\d{4}$' "
                + "      THEN TO_DATE(TRIM(CAST(\"CTEFECHA\" AS TEXT)), 'DD-MM-YYYY') "
                + "      ELSE NULL END >= CASE WHEN ? <> '' THEN TO_DATE(?, 'DD-MM-YYYY') ELSE NULL END OR ? = '') "
                + "AND (CASE WHEN TRIM(CAST(\"CTEFECHA\" AS TEXT)) ~ '^\\d{2}-\\d{2}-\\d{4}$' "
                + "      THEN TO_DATE(TRIM(CAST(\"CTEFECHA\" AS TEXT)), 'DD-MM-YYYY') "
                + "      ELSE NULL END <= CASE WHEN ? <> '' THEN TO_DATE(?, 'DD-MM-YYYY') ELSE NULL END OR ? = '') "
                + "ORDER BY TO_DATE(NULLIF(TRIM(CAST(\"CTEFECHA\" AS TEXT)), ''), 'DD-MM-YYYY') ASC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            String iniVal = fechaInicio != null ? fechaInicio.trim() : "";
            String finVal = fechaFin != null ? fechaFin.trim() : "";

            // 1 y 2: Filtros de Cliente (RUT limpio sin DV y condición de vacío)
            ps.setString(1, filtroCliente);
            ps.setString(2, clienteTrim);

            // 3, 4 y 5: Fecha Inicio
            ps.setString(3, iniVal);
            ps.setString(4, iniVal);
            ps.setString(5, iniVal);

            // 6, 7 y 8: Fecha Fin
            ps.setString(6, finVal);
            ps.setString(7, finVal);
            ps.setString(8, finVal);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DocumentoCarteraDTO doc = new DocumentoCarteraDTO();
                    doc.setFolio(rs.getString("CTEFOLIO"));
                    doc.setFecha(rs.getString("CTEFECHA"));
                    doc.setCliente(rs.getString("CTERUT"));
                    doc.setMonto(rs.getDouble("CTEABONO"));
                    doc.setEstado("ACTIVO");
                    lista.add(doc);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Object[]> obtenerCartolaCliente(String rutCliente) {
        List<Object[]> listaMovimientos = new ArrayList<>();

        if (rutCliente == null || rutCliente.trim().isEmpty()) {
            return listaMovimientos;
        }

        // 1. Limpiamos el RUT completo (ej: 105421257)
        String rutLimpio = rutCliente.replaceAll("[^0-9kK]", "").trim();

        // 2. Extraemos el cuerpo sin el dígito verificador (ej: 10542125)
        String cuerpoRut = rutLimpio;
        if (rutLimpio.length() > 1) {
            cuerpoRut = rutLimpio.substring(0, rutLimpio.length() - 1);
        }

        // Consulta SQL ordenada de la fecha más reciente a la más antigua (DESC)
        String sql = "SELECT \"CTECPTDOC\", \"CTEDOCUMEN\", \"CTEFECHA\", \"CTEVENCE\", "
                + "\"CTECREDITO\", \"CTEABONO\", "
                + "(COALESCE(\"CTECREDITO\", 0) - COALESCE(\"CTEABONO\", 0)) AS saldo "
                + "FROM pctacte_dbf WHERE REPLACE(REPLACE(REPLACE(\"CTERUT\", '.', ''), '-', ''), ' ', '') = ? "
                + "OR REPLACE(REPLACE(REPLACE(\"CTERUT\", '.', ''), '-', ''), ' ', '') = ? "
                + "ORDER BY TO_DATE(\"CTEFECHA\", 'DD-MM-YYYY') DESC, \"CTECOMPROB\" DESC";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rutLimpio);
            pstmt.setString(2, cuerpoRut);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String tipoDoc = rs.getString("CTECPTDOC");
                    String nroDoc = rs.getString("CTEDOCUMEN");
                    String fecEmision = rs.getString("CTEFECHA");
                    String fecVenc = rs.getString("CTEVENCE");
                    double cargo = rs.getDouble("CTECREDITO");
                    double abono = rs.getDouble("CTEABONO");
                    double saldo = rs.getDouble("saldo");

                    listaMovimientos.add(new Object[]{
                        tipoDoc != null && !tipoDoc.trim().isEmpty() ? tipoDoc.trim() : "DOC",
                        nroDoc != null && !nroDoc.trim().isEmpty() ? nroDoc.trim() : "-",
                        fecEmision != null ? fecEmision.trim() : "",
                        fecVenc != null && !fecVenc.trim().isEmpty() ? fecVenc.trim() : "-",
                        cargo,
                        abono,
                        saldo
                    });
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error en CtaCteDAO.obtenerCartolaCliente: " + ex.getMessage());
        }

        return listaMovimientos;
    }

    // Método para consultar los detalles de un pago por su folio
    public DocumentoCarteraDTO buscarDetallePorFolio(Connection con, String folio) {
        DocumentoCarteraDTO doc = null;
        // Casteamos la columna numérica CTEFOLIO a TEXT para que coincida perfectamente con el parámetro String
        String sql = "SELECT \"CTEFECHA\", \"CTERUT\", \"CTEABONO\" FROM pctacte_dbf WHERE CAST(\"CTEFOLIO\" AS TEXT) = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, folio.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    doc = new DocumentoCarteraDTO();
                    doc.setFolio(folio);
                    doc.setFecha(rs.getString("CTEFECHA"));
                    doc.setCliente(rs.getString("CTERUT"));
                    doc.setMonto(rs.getDouble("CTEABONO"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return doc;
    }

    public java.util.List<DocumentoCarteraDTO> buscarRecibosPorFiltro(Connection con, String filtro) {
        java.util.List<DocumentoCarteraDTO> lista = new java.util.ArrayList<>();

        String filtroBusqueda = filtro.trim();

        // Si el usuario escribe un RUT con guión (ej: 10542125-7), nos quedamos solo con la parte antes del guión
        if (filtroBusqueda.contains("-")) {
            filtroBusqueda = filtroBusqueda.split("-")[0].replaceAll("[^0-9]", "");
        } else {
            // Si no tiene guión, limpiamos puntos o espacios comunes por si acaso
            filtroBusqueda = filtroBusqueda.replaceAll("[^0-9]", "");
            if (filtroBusqueda.isEmpty()) {
                filtroBusqueda = filtro.trim(); // Si es texto normal
            }
        }

        // CORREGIDO: Apuntando a 'pctacte_dbf_respaldo' para que coincida con el borrado de pruebas
        String sql = "SELECT \"CTEFOLIO\", \"CTEFECHA\", \"CTERUT\", \"CTEABONO\" FROM pctacte_dbf "
                + "WHERE CAST(\"CTERUT\" AS TEXT) LIKE ? OR CAST(\"CTEFOLIO\" AS TEXT) LIKE ? "
                + "ORDER BY "
                + "   CASE "
                + "       WHEN TRIM(CAST(\"CTEFECHA\" AS TEXT)) ~ '^\\d{2}-\\d{2}-\\d{4}$' "
                + "       THEN TO_DATE(TRIM(CAST(\"CTEFECHA\" AS TEXT)), 'DD-MM-YYYY') "
                + "       ELSE '1900-01-01'::date "
                + "   END DESC LIMIT 100";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            // Si el campo quedó vacío (por ejemplo, si borró todo), buscamos todo ("%%")
            String parametroBusqueda = filtroBusqueda.isEmpty() ? "%" : "%" + filtroBusqueda + "%";

            ps.setString(1, parametroBusqueda);
            ps.setString(2, parametroBusqueda);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DocumentoCarteraDTO doc = new DocumentoCarteraDTO();
                    doc.setFolio(rs.getString("CTEFOLIO"));
                    doc.setFecha(rs.getString("CTEFECHA"));
                    doc.setCliente(rs.getString("CTERUT"));
                    doc.setMonto(rs.getDouble("CTEABONO"));
                    lista.add(doc);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    // --- LISTADO DE CUENTAS POR COBRAR ALFABETICO CON JOIN A CLIENTES ---
    public java.util.List<DocumentoCarteraDTO> obtenerCuentasPorCobrarAlfabetico(Connection con, String filtroTexto) {
        java.util.List<DocumentoCarteraDTO> lista = new java.util.ArrayList<>();

        String filtro = filtroTexto.trim();

        // Consulta SQL con JOIN a pcliente_dbf para buscar por Nombre (CLINOMBRE), RUT o Folio
        String sql = "SELECT p.\"CTERUT\", p.\"CTEFOLIO\", p.\"CTEFECHA\", p.\"CTEABONO\", c.\"CLINOMBRE\" "
                + "FROM pctacte_dbf p "
                + "LEFT JOIN pcliente_dbf c ON CAST(p.\"CTERUT\" AS TEXT) = CAST(c.\"CLIRUT\" AS TEXT) "
                + "WHERE UPPER(COALESCE(c.\"CLINOMBRE\", '')) LIKE UPPER(?) "
                + "   OR CAST(p.\"CTERUT\" AS TEXT) LIKE ? "
                + "   OR CAST(p.\"CTEFOLIO\" AS TEXT) LIKE ? "
                + "ORDER BY c.\"CLINOMBRE\" ASC, p.\"CTEFECHA\" DESC LIMIT 200";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            String parametro = "%" + filtro + "%";

            ps.setString(1, parametro); // Para CLINOMBRE
            ps.setString(2, parametro); // Para CTERUT
            ps.setString(3, parametro); // Para CTEFOLIO

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DocumentoCarteraDTO doc = new DocumentoCarteraDTO();
                    // Si el DTO tiene campo para el nombre, lo guardamos o lo unimos en el cliente
                    String nombreCliente = rs.getString("CLINOMBRE");
                    String rutCliente = rs.getString("CTERUT");

                    // Mostramos por ejemplo "Nombre (RUT)" o guardamos en el DTO
                    doc.setCliente(nombreCliente != null ? nombreCliente + " (" + rutCliente + ")" : rutCliente);
                    doc.setFolio(rs.getString("CTEFOLIO"));
                    doc.setFecha(rs.getString("CTEFECHA"));
                    doc.setMonto(rs.getDouble("CTEABONO"));

                    lista.add(doc);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lista;
    }

// --- 1. MODO RESUMEN: Ordenado puramente por fecha más reciente ---
    public java.util.List<DocumentoCarteraDTO> obtenerCuentasResumen(Connection con, String filtroTexto) {
        java.util.List<DocumentoCarteraDTO> lista = new java.util.ArrayList<>();
        String filtro = filtroTexto.trim();

        String sql = "SELECT c.\"CLINOMBRE\", p.\"CTERUT\", "
                + "       COUNT(p.\"CTEFOLIO\") AS total_docs, "
                + "       SUM(p.\"CTEABONO\") AS suma_abonos, "
                + "       MAX(p.\"CTEFECHA\") AS max_fecha "
                + "FROM pctacte_dbf p "
                + "LEFT JOIN pcliente_dbf c ON CAST(p.\"CTERUT\" AS TEXT) = CAST(c.\"CLIRUT\" AS TEXT) "
                + "WHERE UPPER(COALESCE(c.\"CLINOMBRE\", '')) LIKE UPPER(?) "
                + "   OR CAST(p.\"CTERUT\" AS TEXT) LIKE ? "
                + "GROUP BY c.\"CLINOMBRE\", p.\"CTERUT\" "
                + "ORDER BY "
                + "   CASE "
                + "       WHEN TRIM(CAST(MAX(p.\"CTEFECHA\") AS TEXT)) ~ '^\\d{2}-\\d{2}-\\d{4}$' "
                + "       THEN TO_DATE(TRIM(CAST(MAX(p.\"CTEFECHA\") AS TEXT)), 'DD-MM-YYYY') "
                + "       ELSE '1900-01-01'::date "
                + "   END DESC LIMIT 200";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            String parametro = "%" + filtro + "%";
            ps.setString(1, parametro);
            ps.setString(2, parametro);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DocumentoCarteraDTO doc = new DocumentoCarteraDTO();
                    String nombreCrudo = rs.getString("CLINOMBRE");
                    String rut = rs.getString("CTERUT");

                    // --- AQUÍ ESTÁ EL LLAMADO QUE FALTABA ---
                    String nombreFormateado = formatearNombreApellidosPrimero(nombreCrudo);

                    doc.setCliente(nombreFormateado != null && !nombreFormateado.isEmpty()
                            ? nombreFormateado + " (" + rut + ")"
                            : rut);
                    doc.setFolio(rs.getInt("total_docs") + " docs");
                    doc.setFecha(rs.getString("max_fecha") != null ? rs.getString("max_fecha") : "---");
                    doc.setMonto(rs.getDouble("suma_abonos"));
                    lista.add(doc);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    // --- 2. MODO DETALLE: Ordenado puramente por fecha de documento descendente ---
    public java.util.List<DocumentoCarteraDTO> obtenerCuentasDetalle(Connection con, String filtroTexto) {
        java.util.List<DocumentoCarteraDTO> lista = new java.util.ArrayList<>();
        String filtro = filtroTexto.trim();

        String sql = "SELECT p.\"CTERUT\", p.\"CTEFOLIO\", p.\"CTEFECHA\", p.\"CTEABONO\", c.\"CLINOMBRE\" "
                + "FROM pctacte_dbf p "
                + "LEFT JOIN pcliente_dbf c ON CAST(p.\"CTERUT\" AS TEXT) = CAST(c.\"CLIRUT\" AS TEXT) "
                + "WHERE UPPER(COALESCE(c.\"CLINOMBRE\", '')) LIKE UPPER(?) "
                + "   OR CAST(p.\"CTERUT\" AS TEXT) LIKE ? "
                + "   OR CAST(p.\"CTEFOLIO\" AS TEXT) LIKE ? "
                + "ORDER BY "
                + "   CASE "
                + "       WHEN TRIM(CAST(p.\"CTEFECHA\" AS TEXT)) ~ '^\\d{2}-\\d{2}-\\d{4}$' "
                + "       THEN TO_DATE(TRIM(CAST(p.\"CTEFECHA\" AS TEXT)), 'DD-MM-YYYY') "
                + "       ELSE '1900-01-01'::date "
                + "   END DESC LIMIT 200";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            String parametro = "%" + filtro + "%";
            ps.setString(1, parametro);
            ps.setString(2, parametro);
            ps.setString(3, parametro);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DocumentoCarteraDTO doc = new DocumentoCarteraDTO();
                    String nombreCrudo = rs.getString("CLINOMBRE");
                    String rut = rs.getString("CTERUT");

                    // --- AQUÍ ESTÁ EL LLAMADO QUE FALTABA ---
                    String nombreFormateado = formatearNombreApellidosPrimero(nombreCrudo);

                    doc.setCliente(nombreFormateado != null && !nombreFormateado.isEmpty()
                            ? nombreFormateado + " (" + rut + ")"
                            : rut);
                    doc.setFolio(rs.getString("CTEFOLIO"));
                    doc.setFecha(rs.getString("CTEFECHA"));
                    doc.setMonto(rs.getDouble("CTEABONO"));
                    lista.add(doc);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lista;
    }

// Método auxiliar unificado para poner los apellidos siempre primero
    private String formatearNombreApellidosPrimero(String nombreOriginal) {
        if (nombreOriginal == null || nombreOriginal.trim().isEmpty()) {
            return "";
        }

        String limpio = nombreOriginal.trim().replaceAll("\\s+", " ");
        String[] partes = limpio.split(" ");

        if (partes.length == 2) {

            return partes[1] + " " + partes[0];
        } else if (partes.length == 3) {

            return partes[1] + " " + partes[2] + " " + partes[0];
        } else if (partes.length >= 4) {
            // Caso de 4 o más palabras (Asume los últimos 2 como apellidos)
            String apellido2 = partes[partes.length - 1];
            String apellido1 = partes[partes.length - 2];

            StringBuilder nombresRestantes = new StringBuilder();
            for (int i = 0; i < partes.length - 2; i++) {
                nombresRestantes.append(partes[i]).append(" ");
            }
            return apellido1 + " " + apellido2 + " " + nombresRestantes.toString().trim();
        }

        return nombreOriginal;
    }

    // --- 3. CARTOLA POR FECHA: Ordenada alfabéticamente por apellido y rango de fechas ---
    public java.util.List<DocumentoCarteraDTO> obtenerCartolaPorFecha(Connection con, String rutCliente, String fechaDesde, String fechaHasta) {
        java.util.List<DocumentoCarteraDTO> lista = new java.util.ArrayList<>();

        String sql = "SELECT p.\"CTERUT\", p.\"CTEFOLIO\", p.\"CTEFECHA\", p.\"CTEABONO\", c.\"CLINOMBRE\" "
                + "FROM pctacte_dbf p "
                + "LEFT JOIN pcliente_dbf c ON CAST(p.\"CTERUT\" AS TEXT) = CAST(c.\"CLIRUT\" AS TEXT) "
                + "WHERE CAST(p.\"CTERUT\" AS TEXT) = ? "
                + "  AND TO_DATE(TRIM(CAST(p.\"CTEFECHA\" AS TEXT)), 'DD-MM-YYYY') "
                + "      BETWEEN TO_DATE(?, 'DD-MM-YYYY') AND TO_DATE(?, 'DD-MM-YYYY') "
                + "ORDER BY c.\"CLINOMBRE\" ASC, "
                + "   CASE "
                + "       WHEN TRIM(CAST(p.\"CTEFECHA\" AS TEXT)) ~ '^\\d{2}-\\d{2}-\\d{4}$' "
                + "       THEN TO_DATE(TRIM(CAST(p.\"CTEFECHA\" AS TEXT)), 'DD-MM-YYYY') "
                + "       ELSE '1900-01-01'::date "
                + "   END DESC";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, rutCliente.trim());
            ps.setString(2, fechaDesde.trim());
            ps.setString(3, fechaHasta.trim());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DocumentoCarteraDTO doc = new DocumentoCarteraDTO();
                    String nombreCrudo = rs.getString("CLINOMBRE");
                    String rut = rs.getString("CTERUT");

                    // Aplicamos el formato estricto con apellidos primero
                    String nombreFormateado = formatearNombreApellidosPrimero(nombreCrudo);

                    doc.setCliente(nombreFormateado != null && !nombreFormateado.isEmpty()
                            ? nombreFormateado + " (" + rut + ")"
                            : rut);
                    doc.setFolio(rs.getString("CTEFOLIO"));
                    doc.setFecha(rs.getString("CTEFECHA"));
                    doc.setMonto(rs.getDouble("CTEABONO"));
                    
                    lista.add(doc);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lista;
    }
}
