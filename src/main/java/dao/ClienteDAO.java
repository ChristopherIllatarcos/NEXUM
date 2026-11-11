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

public class ClienteDAO {

    public List<Object[]> cargarClientesDAO(String filtro) {
        List<Object[]> listaClientes = new ArrayList<>();

        String sql = "SELECT rut, nombre, direccion, comuna, sector, celular, condicion_pago, dias, plazo, ciudad "
                + "FROM public.clientes "
                + "WHERE rut ILIKE ? OR nombre ILIKE ?";

        // Usamos estrictamente tu método getConnection() acordado
        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String parametro = "%" + (filtro != null ? filtro : "") + "%";
            pstmt.setString(1, parametro);
            pstmt.setString(2, parametro);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[]{
                        rs.getString("rut"),
                        rs.getString("nombre"),
                        rs.getString("direccion"),
                        rs.getString("comuna"),
                        rs.getString("sector"),
                        rs.getString("celular"),
                        rs.getString("condicion_pago"),
                        rs.getInt("dias"),
                        rs.getString("plazo"),
                        rs.getString("ciudad")
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

    public EstadisticasClienteDTO obtenerEstadisticasPorRut(String rut) {
        EstadisticasClienteDTO stats = null;

        // 1. Consulta para datos principales del cliente y su saldo
        String sqlCliente = "SELECT rut, nombre, saldo_actual FROM clientes WHERE rut = ?";

        // 2. Consulta para calcular estadísticas de compras en tiempo real
        String sqlCompras = "SELECT COUNT(*), COALESCE(SUM(monto), 0), COALESCE(AVG(monto), 0), "
                + "COALESCE(MAX(monto), 0), MAX(fecha) FROM ctacte_compras WHERE rut_cliente = ?";

        // 3. Consulta para calcular estadísticas de pagos en tiempo real
        String sqlPagos = "SELECT COUNT(*), COALESCE(SUM(monto), 0), COALESCE(AVG(monto), 0), "
                + "COALESCE(MAX(monto), 0), MAX(fecha) FROM ctacte_pagos WHERE rut_cliente = ?";

        try (Connection conn = Conexion.getConnection()) {

            // --- OBTENER CLIENTE ---
            try (PreparedStatement pstmt = conn.prepareStatement(sqlCliente)) {
                pstmt.setString(1, rut);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        stats = new EstadisticasClienteDTO();
                        stats.setRut(rs.getString("rut")); // (Si ya agregaste setRut en el DTO)
                        stats.setNombre(rs.getString("nombre"));
                        stats.setSaldoActual(rs.getDouble("saldo_actual"));
                    }
                }
            }

            // Si el cliente existe, consultamos sus compras y pagos reales
            if (stats != null) {

                // --- OBTENER ESTADÍSTICAS DE COMPRAS ---
                try (PreparedStatement pstmtC = conn.prepareStatement(sqlCompras)) {
                    pstmtC.setString(1, rut);
                    try (ResultSet rsC = pstmtC.executeQuery()) {
                        if (rsC.next()) {
                            // --- EN COMPRAS ---
                            stats.setVecesCompro(rsC.getInt(1));
                            stats.setTotalCompro(rsC.getDouble(2));
                            stats.setPromCompro(rsC.getDouble(3));
                            stats.setMaxCompro(rsC.getDouble(4));

                            // Formatear la fecha si viene de la BD
                            java.sql.Date fechaC = rsC.getDate(5);
                            stats.setUltimaCompra(fechaC != null ? fechaC.toString() : "N/A");
                        }
                    }
                }

                // --- OBTENER ESTADÍSTICAS DE PAGOS ---
                try (PreparedStatement pstmtP = conn.prepareStatement(sqlPagos)) {
                    pstmtP.setString(1, rut);
                    try (ResultSet rsP = pstmtP.executeQuery()) {
                        if (rsP.next()) {
                            // --- EN PAGOS ---
                            stats.setVecesPago(rsP.getInt(1));
                            stats.setTotalPago(rsP.getDouble(2));
                            stats.setPromPago(rsP.getDouble(3));
                            stats.setMaxPago(rsP.getDouble(4));

                            java.sql.Date fechaP = rsP.getDate(5);
                            stats.setUltimoPago(fechaP != null ? fechaP.toString() : "N/A");
                        }
                    }
                }
            }

        } catch (SQLException e) {
        }

        return stats;
    }

    public List<ClienteDTO> obtenerTodosLosClientes() {
        List<ClienteDTO> lista = new ArrayList<>();

        // Consulta SQL ordenada estrictamente por Apellido Paterno, Materno y Nombres (A-Z)
        String sql = "SELECT rut, paterno, materno, nombres, saldo_actual, cupo FROM clientes ORDER BY paterno ASC, materno ASC, nombres ASC";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ClienteDTO cliente = new ClienteDTO();
                cliente.setRut(rs.getString("rut"));
                cliente.setPaterno(rs.getString("paterno"));
                cliente.setMaterno(rs.getString("materno"));
                cliente.setNombres(rs.getString("nombres"));
                cliente.setSaldoActual(rs.getBigDecimal("saldo_actual"));
                cliente.setCupo(rs.getBigDecimal("cupo"));

                lista.add(cliente);
            }
        } catch (SQLException e) {
        }
        return lista;
    }

    public boolean eliminarCliente(String rut) {
        String sql = "DELETE FROM clientes WHERE rut = ?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rut);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean insertarCliente(ClienteDTO cliente) {
        // Si el RUT ya existe, actualiza los datos en lugar de arrojar error de llave duplicada
        String sql = "INSERT INTO clientes (rut, paterno, materno, nombres, saldo_actual, cupo) "
                + "VALUES (?, ?, ?, ?, ?, ?) "
                + "ON CONFLICT (rut) DO UPDATE SET "
                + "paterno = EXCLUDED.paterno, "
                + "materno = EXCLUDED.materno, "
                + "nombres = EXCLUDED.nombres, "
                + "saldo_actual = EXCLUDED.saldo_actual, "
                + "cupo = EXCLUDED.cupo";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cliente.getRut());
            pstmt.setString(2, cliente.getPaterno() != null ? cliente.getPaterno().toUpperCase() : "");
            pstmt.setString(3, cliente.getMaterno() != null ? cliente.getMaterno().toUpperCase() : "");
            pstmt.setString(4, cliente.getNombres() != null ? cliente.getNombres().toUpperCase() : "");
            pstmt.setBigDecimal(5, cliente.getSaldoActual() != null ? cliente.getSaldoActual() : java.math.BigDecimal.ZERO);
            pstmt.setBigDecimal(6, cliente.getCupo() != null ? cliente.getCupo() : java.math.BigDecimal.ZERO);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizarCliente(ClienteDTO cliente) {
        // Quitamos 'nombre = ?' porque la columna ya no existe en la tabla
        String sql = "UPDATE clientes SET paterno = ?, materno = ?, nombres = ?, saldo_actual = ?, cupo = ? WHERE rut = ?";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cliente.getPaterno() != null ? cliente.getPaterno().toUpperCase() : "");
            pstmt.setString(2, cliente.getMaterno() != null ? cliente.getMaterno().toUpperCase() : "");
            pstmt.setString(3, cliente.getNombres() != null ? cliente.getNombres().toUpperCase() : "");
            pstmt.setBigDecimal(4, cliente.getSaldoActual() != null ? cliente.getSaldoActual() : java.math.BigDecimal.ZERO);
            pstmt.setBigDecimal(5, cliente.getCupo() != null ? cliente.getCupo() : java.math.BigDecimal.ZERO);
            pstmt.setString(6, cliente.getRut()); // La condición WHERE al final

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("--- ERROR SQL AL ACTUALIZAR CLIENTE ---");
            e.printStackTrace();
            return false;
        }
    }

    public ClienteDTO obtenerClientePorRut(String rut) {
        String sql = "SELECT rut, paterno, materno, nombres, saldo_actual, cupo FROM clientes WHERE rut = ?";
        ClienteDTO cliente = null;

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rut);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    cliente = new ClienteDTO();
                    cliente.setRut(rs.getString("rut"));
                    cliente.setPaterno(rs.getString("paterno"));
                    cliente.setMaterno(rs.getString("materno"));
                    cliente.setNombres(rs.getString("nombres"));
                    cliente.setSaldoActual(rs.getBigDecimal("saldo_actual"));
                    cliente.setCupo(rs.getBigDecimal("cupo"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cliente;
    }

}
