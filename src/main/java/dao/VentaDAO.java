
package dao;
import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class VentaDAO {

    public boolean guardarVenta(Object[] cabecera, List<Object[]> detalles) {
        String sqlVenta = "INSERT INTO public.ventas (nro_docto, rut_cliente, nombre_cliente, codigo_vendedor, condicion_pago, dias, total) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?)";
                          
        String sqlDetalle = "INSERT INTO public.detalle_ventas (id_venta, articulo, color, cantidad, precio_unitario, subtotal) " +
                            "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            // Usamos estrictamente getConnection() acordado
            conn = Conexion.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            int idVentaGenerado = -1;

            // 1. Guardar la cabecera (Panel Superior)
            try (PreparedStatement pstmtVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                pstmtVenta.setString(1, (String) cabecera[0]); // nro_docto
                pstmtVenta.setString(2, (String) cabecera[1]); // rut_cliente
                pstmtVenta.setString(3, (String) cabecera[2]); // nombre_cliente
                pstmtVenta.setInt(4, (Integer) cabecera[3]);    // codigo_vendedor (ej. 5)
                pstmtVenta.setString(5, (String) cabecera[4]); // condicion_pago
                pstmtVenta.setInt(6, (Integer) cabecera[5]);    // dias
                pstmtVenta.setInt(7, (Integer) cabecera[6]);    // total

                pstmtVenta.executeUpdate();

                try (ResultSet rs = pstmtVenta.getGeneratedKeys()) {
                    if (rs.next()) {
                        idVentaGenerado = rs.getInt(1);
                    }
                }
            }

            // 2. Guardar los detalles (Tabla de productos)
            if (idVentaGenerado != -1) {
                try (PreparedStatement pstmtDetalle = conn.prepareStatement(sqlDetalle)) {
                    for (Object[] det : detalles) {
                        pstmtDetalle.setInt(1, idVentaGenerado);
                        pstmtDetalle.setString(2, (String) det[0]); // articulo
                        pstmtDetalle.setString(3, (String) det[1]); // color
                        pstmtDetalle.setInt(4, (Integer) det[2]);   // cantidad
                        pstmtDetalle.setInt(5, (Integer) det[3]);   // precio_unitario
                        pstmtDetalle.setInt(6, (Integer) det[4]);   // subtotal
                        pstmtDetalle.addBatch();
                    }
                    pstmtDetalle.executeBatch();
                }
            }

            conn.commit(); // Confirmar cambios en la BD
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Deshacer cambios si hay error
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("Error al registrar la venta: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}