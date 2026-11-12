package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VendedorDAO {

    public String obtenerNombreVendedor(int codigoVendedor) {
        String nombreVendedor = "";
        // Forzamos el nombre exacto de la columna con comillas dobles
        String sql = "SELECT \"VENDENOM\" FROM pvende_dbf WHERE \"VENDECOD\" = ?";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, codigoVendedor);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    nombreVendedor = rs.getString("VENDENOM").trim();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nombreVendedor;
    }
}
