
package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class VendedorDAO {

    public List<Object[]> cargarVendedoresDAO(String filtro) {
        List<Object[]> listaVendedores = new ArrayList<>();
        
        // Consultamos la tabla personal_dbf incorporando el codigo_vendedor
        String sql = "SELECT codigo_vendedor, \"RUT\", \"NOMBRE\", \"DOMICILIO\" " +
                     "FROM public.personal_dbf " +
                     "WHERE \"NOMBRE\" IS NOT NULL AND \"NOMBRE\" <> '' " +
                     "AND (CAST(codigo_vendedor AS TEXT) ILIKE ? OR \"NOMBRE\" ILIKE ? OR \"RUT\" ILIKE ?)";

        // Usamos estrictamente tu método getConnection() acordado
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String parametro = "%" + (filtro != null ? filtro : "") + "%";
            pstmt.setString(1, parametro);
            pstmt.setString(2, parametro);
            pstmt.setString(3, parametro);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[]{
                        rs.getObject("codigo_vendedor") != null ? rs.getInt("codigo_vendedor") : 0, // El número (ej. 5)
                        rs.getString("RUT"),
                        rs.getString("NOMBRE"),
                        rs.getString("DOMICILIO")
                    };
                    listaVendedores.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar los vendedores desde personal_dbf: " + e.getMessage());
        }

        return listaVendedores;
    }
}
