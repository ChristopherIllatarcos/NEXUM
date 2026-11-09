package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import conexion.Conexion;

public class ParametroDAO {

    // Método para obtener los valores actuales desde PostgreSQL y mostrarlos en el diálogo
    public static String[] cargarParametros() {
        String[] valores = new String[5]; // [boleta, factura, guia, iva, fecha]
        String sql = "SELECT folio_boleta, folio_factura, folio_guia, iva, fecha_sistema FROM parametros WHERE id = 1";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                valores[0] = String.valueOf(rs.getInt("folio_boleta"));
                valores[1] = String.valueOf(rs.getInt("folio_factura"));
                valores[2] = String.valueOf(rs.getInt("folio_guia"));
                valores[3] = String.valueOf(rs.getDouble("iva"));
                valores[4] = rs.getString("fecha_sistema");
            }
        } catch (SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(null, 
                "Error al cargar los parámetros desde la base de datos: " + e.getMessage(), 
                "Error SQL", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
        return valores;
    }

    // Método para actualizar los valores en PostgreSQL
    public static boolean actualizar(int boleta, int factura, int guia, double iva, String fecha) {
        String sql = "UPDATE parametros SET folio_boleta = ?, folio_factura = ?, folio_guia = ?, iva = ?, fecha_sistema = ? WHERE id = 1";
        
        try (Connection conn = Conexion.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, boleta);
            pstmt.setInt(2, factura);
            pstmt.setInt(3, guia);
            pstmt.setDouble(4, iva);
            pstmt.setString(5, fecha);
            
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, 
                "Error al actualizar los parámetros en la base de datos: " + e.getMessage(), 
                "Error SQL", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
