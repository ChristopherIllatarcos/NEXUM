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

        String sql = "SELECT codigo_barra, descripcion, color, precio "
                + "FROM public.productos "
                + "WHERE codigo_barra ILIKE ? OR descripcion ILIKE ?";

        // Usamos estrictamente tu método getConnection() acordado
        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String parametro = "%" + (filtro != null ? filtro : "") + "%";
            pstmt.setString(1, parametro);
            pstmt.setString(2, parametro);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Object[] fila = new Object[]{
                        rs.getString("codigo_barra"), // [0] Barra
                        rs.getString("descripcion"), // [1] Descripción
                        rs.getString("color"), // [2] Color
                        rs.getDouble("precio") // [3] Precio
                    };
                    listaProductos.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar productos: " + e.getMessage());
        }

        return listaProductos;
    }

    public java.util.List<Object[]> obtenerTodosLosProductos() {
        java.util.List<Object[]> lista = new java.util.ArrayList<>();
        String sql = "SELECT descripcion, codigo_barra, precio FROM public.productos ORDER BY descripcion ASC";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("descripcion"),
                    rs.getString("codigo_barra"),
                    rs.getDouble("precio")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener lista completa de productos: " + e.getMessage());
        }
        return lista;
    }

    public boolean insertarProducto(String codigoBarra, String descripcion, double precio) {
        String sql = "INSERT INTO public.productos (codigo_barra, descripcion, precio) VALUES (?, ?, ?)";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, codigoBarra);
            pstmt.setString(2, descripcion.toUpperCase());
            pstmt.setDouble(3, precio);

            int filas = pstmt.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar producto en DAO: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarProducto(String codigoOriginal, String nuevoCodigoBarra, String descripcion, double precio) {
        // Actualizamos el codigo_barra (la sigla), la descripción y el precio, 
        // buscando el registro viejo por el codigoOriginal
        String sql = "UPDATE public.productos SET codigo_barra = ?, descripcion = ?, precio = ? WHERE TRIM(codigo_barra) = ?";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nuevoCodigoBarra.toUpperCase().trim());
            pstmt.setString(2, descripcion.toUpperCase().trim());
            pstmt.setDouble(3, precio);
            pstmt.setString(4, codigoOriginal.trim()); // El código antiguo para ubicarlo en la BD

            int filas = pstmt.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar producto en DAO: " + e.getMessage());
            return false;
        }
    }
}
