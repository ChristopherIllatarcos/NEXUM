package dao;

import conexion.Conexion;
import java.util.List;
import modelo.ItemVentaDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import modelo.VentaDTO;

public class VentaDAO {

    // Método exclusivo para guardar la venta en la base de datos
    public boolean guardarVenta(int folioVenta, int codigoCliente, String fechaActual, int codigoVendedor, List<ItemVentaDTO> items) {
        String sql = "INSERT INTO pventas_dbf (VCODIGO, VCLIENTE, VFECHA, VPXBARRA, VPXCANT, VPXPRECIO, VPXTOTAL, VVENDE) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Conexion.getConnection(); // Usando tu método getConnection()
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (ItemVentaDTO item : items) {
                pstmt.setInt(1, folioVenta);
                pstmt.setInt(2, codigoCliente);
                pstmt.setString(3, fechaActual);
                pstmt.setString(4, item.getBarra());
                pstmt.setInt(5, item.getCantidad());
                pstmt.setDouble(6, item.getPrecioUnitario());
                pstmt.setInt(7, (int) item.getTotalItem());
                pstmt.setInt(8, codigoVendedor);

                pstmt.addBatch();
            }

            pstmt.executeBatch();
            return true;

        } catch (SQLException e) {
            System.err.println("Error en VentaDAO al guardar venta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Método en tu DAO para eliminar el pago por número de recibo
    public boolean eliminarPagoPorRecibo(String numeroRecibo) {
        // Usamos CTEFOLIO o CTENUMERO dependiendo de cuál campo use la oficina para el "N° Recibo"
        String sql = "DELETE FROM pctacte_dbf WHERE TRIM(CAST(\"CTEFOLIO\" AS TEXT)) = ?";

        try (PreparedStatement pstmt = Conexion.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, numeroRecibo);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarPagoPorFolio(String nroFolio) {
        String sql = "DELETE FROM pctacte_dbf WHERE TRIM(CAST(\"CTEFOLIO\" AS TEXT)) = ?";

        try (PreparedStatement pstmt = Conexion.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, nroFolio);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<VentaDTO> obtenerVentas(Connection con, String filtroTexto) {
        List<VentaDTO> lista = new ArrayList<>();
        String filtro = "%" + (filtroTexto != null ? filtroTexto.trim() : "") + "%";

        // Ordenamos convirtiendo la fecha de texto (DD/MM/YYYY o YYYY-MM-DD) de forma segura
        String sql = "SELECT \"VCODIGO\", \"VCLIENTE\", \"VFECHA\", \"VPXBARRA\", \"VPXCANT\", "
                + "\"VPXPRECIO\", \"VPXDESCTO\", \"VPXTOTAL\", \"VBOLETA\", \"VFACTURA\", "
                + "\"VGUIA\", \"VVALE\", \"VNOTCRE\", \"VCAJA\" "
                + "FROM pventas_dbf "
                + "WHERE UPPER(COALESCE(\"VCLIENTE\", '')) LIKE UPPER(?) "
                + "   OR COALESCE(CAST(\"VBOLETA\" AS TEXT), '') LIKE ? "
                + "   OR COALESCE(CAST(\"VFACTURA\" AS TEXT), '') LIKE ? "
                + "   OR COALESCE(CAST(\"VCODIGO\" AS TEXT), '') LIKE ? "
                + "ORDER BY CASE "
                + "    WHEN \"VFECHA\" ~ '^\\d{2}/\\d{2}/\\d{4}' THEN TO_DATE(\"VFECHA\", 'DD/MM/YYYY') "
                + "    WHEN \"VFECHA\" ~ '^\\d{4}-\\d{2}-\\d{2}' THEN TO_DATE(\"VFECHA\", 'YYYY-MM-DD') "
                + "    ELSE NULL "
                + "END DESC NULLS LAST, \"VCODIGO\" DESC LIMIT 200";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, filtro);
            ps.setString(2, filtro);
            ps.setString(3, filtro);
            ps.setString(4, filtro);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    VentaDTO v = new VentaDTO();
                    v.setVcodigo(rs.getLong("VCODIGO"));
                    v.setVcliente(rs.getString("VCLIENTE") != null ? rs.getString("VCLIENTE") : "");
                    v.setVfecha(rs.getString("VFECHA") != null ? rs.getString("VFECHA") : "");
                    v.setVpxbarra(rs.getString("VPXBARRA") != null ? rs.getString("VPXBARRA") : "");
                    v.setVpxcant(rs.getString("VPXCANT") != null ? rs.getString("VPXCANT") : "");
                    v.setVpxprecio(rs.getString("VPXPRECIO") != null ? rs.getString("VPXPRECIO") : "");
                    v.setVpxdescto(rs.getString("VPXDESCTO") != null ? rs.getString("VPXDESCTO") : "");
                    v.setVpxtotal(rs.getDouble("VPXTOTAL"));
                    v.setVboleta(rs.getString("VBOLETA") != null ? rs.getString("VBOLETA") : "");
                    v.setVfactura(rs.getString("VFACTURA") != null ? rs.getString("VFACTURA") : "");
                    v.setVguia(rs.getString("VGUIA") != null ? rs.getString("VGUIA") : "");
                    v.setVvale(rs.getDouble("VVALE"));
                    v.setVnotcre(rs.getString("VNOTCRE") != null ? rs.getString("VNOTCRE") : "");
                    v.setVcaja(rs.getDouble("VCAJA"));
                    lista.add(v);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lista;
    }

    public boolean eliminarVenta(Connection con, long vcodigo) {
        String sql = "DELETE FROM pventas_dbf WHERE \"VCODIGO\" = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, vcodigo);
            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean actualizarVenta(Connection con, VentaDTO v) {
        String sql = "UPDATE pventas_dbf SET "
                + "\"VCLIENTE\" = ?, \"VFECHA\" = ?, \"VBOLETA\" = ?, "
                + "\"VFACTURA\" = ?, \"VGUIA\" = ?, \"VPXTOTAL\" = ? "
                + "WHERE \"VCODIGO\" = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, v.getVcliente());
            ps.setString(2, v.getVfecha());
            ps.setString(3, v.getVboleta());
            ps.setString(4, v.getVfactura());
            ps.setString(5, v.getVguia());
            ps.setDouble(6, v.getVpxtotal());
            ps.setLong(7, v.getVcodigo());
            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
