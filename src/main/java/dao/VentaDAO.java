
package dao;
import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import modelo.ItemVentaDTO;

public class VentaDAO {

    // Método exclusivo para guardar la venta en la base de datos
    public boolean guardarVenta(int folioVenta, int codigoCliente, String fechaActual, int codigoVendedor, List<ItemVentaDTO> items) {
        String sql = "INSERT INTO pventas_dbf (VCODIGO, VCLIENTE, VFECHA, VPXBARRA, VPXCANT, VPXPRECIO, VPXTOTAL, VVENDE) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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
}