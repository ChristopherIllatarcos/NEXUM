
package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoVentaDAO {

    // Método para buscar productos en la nueva tabla public.productos
    public List<Object[]> buscarProductosYVendedorDAO(String filtro) {
        List<Object[]> listaProductos = new ArrayList<>();
        
        String sql = "SELECT codigo_barra, descripcion, color, precio " +
                     "FROM public.productos " +
                     "WHERE codigo_barra ILIKE ? OR descripcion ILIKE ?";

        // Usamos estrictamente tu método getConnection() acordado
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String parametro = "%" + (filtro != null ? filtro : "") + "%";
            pstmt.setString(1, parametro);
            pstmt.setString(2, parametro);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[]{
                        rs.getString("codigo_barra"), // [0] Barra
                        rs.getString("descripcion"),  // [1] Descripción
                        rs.getString("color"),        // [2] Color
                        rs.getDouble("precio")        // [3] Precio
                    };
                    listaProductos.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar productos: " + e.getMessage());
        }

        return listaProductos;
    }
}