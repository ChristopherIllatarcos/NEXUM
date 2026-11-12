package dao;

import conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoVentaDAO {

    // 1. Búsqueda para el buscador flotante de ventas
    public List<Object[]> buscarProductosYVendedorDAO(String textoBusqueda) {
        List<Object[]> lista = new ArrayList<>();

        String sql = "SELECT \"PXBARRA\", \"PXNOMBRE\", \"PXILA\", \"PXVENTA0\" "
                + "FROM pproduc_dbf "
                + "WHERE UPPER(\"PXBARRA\") LIKE UPPER(?) OR UPPER(\"PXNOMBRE\") LIKE UPPER(?)";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String parametro = "%" + textoBusqueda + "%";
            pstmt.setString(1, parametro);
            pstmt.setString(2, parametro);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[4];
                    fila[0] = rs.getString("PXBARRA");
                    fila[1] = rs.getString("PXNOMBRE");
                    fila[2] = rs.getString("PXILA");

                    double precioFinal = 0.0;
                    try {
                        String val = rs.getString("PXVENTA0");
                        if (val != null && !val.trim().isEmpty()) {
                            // Limpiamos y convertimos a double nativo
                            String limpio = val.trim().replace(".", "").replace(",", ".");
                            precioFinal = Double.parseDouble(limpio);
                        }
                    } catch (Exception e) {
                        System.err.println("Error convirtiendo precio: " + e.getMessage());
                    }

                    // Entregamos el valor como Double para que la interfaz lo pinte bien
                    fila[3] = precioFinal;
                    lista.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar productos: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    // 2. Obtener todos los productos para la tabla de registro
    public List<Object[]> obtenerTodosLosProductos() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT \"PXBARRA\", \"PXNOMBRE\", \"PXILA\", \"PXVENTA0\" FROM pproduc_dbf";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Object[] fila = new Object[4];
                fila[0] = rs.getString("PXBARRA");
                fila[1] = rs.getString("PXNOMBRE");
                fila[2] = rs.getString("PXILA");
                fila[3] = rs.getString("PXVENTA0");
                lista.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los productos: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

    // 3. Insertar un nuevo producto
    public boolean insertarProducto(String barra, String nombre, double precio) {
        String sql = "INSERT INTO pproduc_dbf (\"PXBARRA\", \"PXNOMBRE\", \"PXVENTA0\") VALUES (?, ?, ?)";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, barra);
            pstmt.setString(2, nombre);
            pstmt.setDouble(3, precio);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 4. Actualizar un producto existente (con 4 parámetros según la vista)
    public boolean actualizarProducto(String barra, String nombre, String ila, double precio) {
        String sql = "UPDATE pproduc_dbf SET \"PXNOMBRE\" = ?, \"PXILA\" = ?, \"PXVENTA0\" = ? WHERE \"PXBARRA\" = ?";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nombre);
            pstmt.setString(2, ila);
            pstmt.setDouble(3, precio);
            pstmt.setString(4, barra);

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
