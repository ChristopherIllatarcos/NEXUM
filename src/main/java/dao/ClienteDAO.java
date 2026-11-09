
package dao;
import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class ClienteDAO {
 public List<Object[]> cargarClientesDAO(String filtro) {
        List<Object[]> listaClientes = new ArrayList<>();
        
        String sql = "SELECT rut, nombre, direccion, comuna, sector, celular, condicion_pago, dias, plazo, ciudad " +
                     "FROM public.clientes " +
                     "WHERE rut ILIKE ? OR nombre ILIKE ?";

        // Usamos estrictamente tu método getConnection() acordado
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
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
}
