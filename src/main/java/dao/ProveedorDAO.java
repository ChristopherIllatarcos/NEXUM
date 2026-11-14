package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    public List<Object[]> obtenerProveedoresParaTabla() {
        List<Object[]> listaProveedores = new ArrayList<>();

        // Consulta SQL apuntando a los nombres exactos de las columnas de pproveed_dbf
        String sql = "SELECT \"PRVRUT\", \"PRVDIGIT\", \"PRVNOMBRE\", \"PRVCIUDAD\" FROM pproveed_dbf ORDER BY \"PRVNOMBRE\" ASC";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int rut = rs.getInt("PRVRUT");
                int digit = rs.getInt("PRVDIGIT");

                // Formateamos el RUT tipo "76474780-1" o vacío si no tiene
                String rutFormateado = (rut > 0) ? rut + "-" + digit : "";

                String nombre = rs.getString("PRVNOMBRE");
                String ciudad = rs.getString("PRVCIUDAD");

                Object[] fila = new Object[]{
                    rutFormateado,
                    nombre != null ? nombre.trim() : "",
                    ciudad != null ? ciudad.trim() : ""
                };
                listaProveedores.add(fila);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener proveedores para la tabla: " + e.getMessage());
            e.printStackTrace();
        }

        return listaProveedores;
    }

    public boolean insertarProveedor(String nombre, int rut, int digit, String ciudad) {
        String sql = "INSERT INTO pproveed_dbf (\"PRVNOMBRE\", \"PRVRUT\", \"PRVDIGIT\", \"PRVCIUDAD\") VALUES (?, ?, ?, ?)";

        try (java.sql.Connection conn = conexion.Conexion.getConnection(); java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setInt(2, rut);
            pstmt.setInt(3, digit);
            pstmt.setString(4, ciudad);

            pstmt.executeUpdate();
            return true;

        } catch (java.sql.SQLException e) {
            System.err.println("Error al insertar proveedor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 1. Obtener los datos de un proveedor específico para cargarlos en el formulario de edición
    public java.util.Map<String, Object> obtenerProveedorPorRut(int rut) {
        java.util.Map<String, Object> proveedor = new java.util.HashMap<>();
        String sql = "SELECT \"PRVNOMBRE\", \"PRVRUT\", \"PRVDIGIT\", \"PRVCIUDAD\" FROM pproveed_dbf WHERE \"PRVRUT\" = ?";

        try (java.sql.Connection conn = conexion.Conexion.getConnection(); java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, rut);
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    proveedor.put("nombre", rs.getString("PRVNOMBRE"));
                    proveedor.put("rut", rs.getInt("PRVRUT"));
                    proveedor.put("digit", rs.getInt("PRVDIGIT"));
                    proveedor.put("ciudad", rs.getString("PRVCIUDAD"));
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error al obtener proveedor para modificar: " + e.getMessage());
            e.printStackTrace();
        }
        return proveedor;
    }

    // 2. Actualizar el registro en la base de datos
    public boolean actualizarProveedor(String nombre, int rutOriginal, int nuevoRut, int digit, String ciudad) {
        String sql = "UPDATE pproveed_dbf SET \"PRVNOMBRE\" = ?, \"PRVRUT\" = ?, \"PRVDIGIT\" = ?, \"PRVCIUDAD\" = ? WHERE \"PRVRUT\" = ?";

        try (java.sql.Connection conn = conexion.Conexion.getConnection(); java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setInt(2, nuevoRut);
            pstmt.setInt(3, digit);
            pstmt.setString(4, ciudad);
            pstmt.setInt(5, rutOriginal); // Usamos el RUT original como llave por si se modificó

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (java.sql.SQLException e) {
            System.err.println("Error al actualizar proveedor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarProveedor(int rut) {
        String sql = "DELETE FROM pproveed_dbf WHERE \"PRVRUT\" = ?";

        try (java.sql.Connection conn = conexion.Conexion.getConnection(); java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, rut);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (java.sql.SQLException e) {
            System.err.println("Error al eliminar proveedor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
