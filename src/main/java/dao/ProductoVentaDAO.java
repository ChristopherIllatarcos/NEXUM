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

    // 1. Obtener un producto específico por su código de barra para rellenar el formulario de modificación
    public java.util.Map<String, Object> obtenerProductoPorBarra(String barra) {
        java.util.Map<String, Object> producto = new java.util.HashMap<>();
        String sql = "SELECT \"PXBARRA\", \"PXNOMBRE\", \"PXILA\", \"PXVENTA0\" FROM pproduc_dbf WHERE \"PXBARRA\" = ?";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, barra);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    producto.put("barra", rs.getString("PXBARRA"));
                    producto.put("nombre", rs.getString("PXNOMBRE"));
                    producto.put("ila", rs.getString("PXILA"));
                    producto.put("precio", rs.getString("PXVENTA0"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener producto por barra: " + e.getMessage());
            e.printStackTrace();
        }
        return producto;
    }

    // 3. Insertar un nuevo producto
    public boolean insertarProducto(String barra, String nombre, double precio) {
        String sql = "INSERT INTO pproduc_dbf (\"PXBARRA\", \"PXNOMBRE\", \"PXVENTA0\") VALUES (?, ?, ?)";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, barra);
            pstmt.setString(2, nombre);

            // Como PXVENTA0 es varchar(50) en tu BD, guardamos el número convertido a texto de forma segura
            pstmt.setString(3, String.valueOf(precio).replace(".", ",")); // O usa String.valueOf(precio) si prefieres con punto

            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al insertar producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 4. Actualizar un producto existente (con 4 parámetros según la vista)
// 1. Método para 4 parámetros (Usado por DlgNuevoProducto)
    public boolean actualizarProducto(String codigoOriginal, String barra, String nombre, double precio) {
        String sql = "UPDATE pproduc_dbf SET \"PXBARRA\" = ?, \"PXNOMBRE\" = ?, \"PXVENTA0\" = ? WHERE \"PXBARRA\" = ?";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, barra);
            pstmt.setString(2, nombre);
            pstmt.setDouble(3, precio);
            pstmt.setString(4, codigoOriginal);

            int filas = pstmt.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar producto (4 params): " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // 2. Método para 5 parámetros (Usado por DlgModificarProducto con ILA incluido)
    public boolean actualizarProducto(String nuevaBarra, String nombre, String precioStr, String barraOriginal) {
        String sql = "UPDATE pproduc_dbf SET \"PXBARRA\" = ?, \"PXNOMBRE\" = ?, \"PXVENTA0\" = ? WHERE \"PXBARRA\" = ?";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nuevaBarra);
            pstmt.setString(2, nombre);

            double precio = 0.0;
            if (precioStr != null && !precioStr.trim().isEmpty()) {
                try {
                    precio = Double.parseDouble(precioStr.replace(",", "."));
                } catch (NumberFormatException e) {
                    precio = 0.0;
                }
            }
            pstmt.setDouble(3, precio);
            pstmt.setString(4, barraOriginal);

            int filas = pstmt.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public java.util.List<Object[]> obtenerProductosParaTabla() {
        java.util.List<Object[]> listaProductos = new java.util.ArrayList<>();

        // Consulta usando los nombres reales de la estructura de tu tabla pproduc_dbf
        String sql = "SELECT \"PXNOMBRE\", \"PXBARRA\", \"PXVENTA0\" FROM pproduc_dbf ORDER BY \"PXNOMBRE\" ASC";

        try (java.sql.Connection conn = conexion.Conexion.getConnection(); java.sql.PreparedStatement pstmt = conn.prepareStatement(sql); java.sql.ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String desc = rs.getString("PXNOMBRE");
                String barra = rs.getString("PXBARRA");
                String venta0Str = rs.getString("PXVENTA0");

                // Parsear el precio de forma segura ya que PXVENTA0 es varchar y puede traer comas (ej: "0,00")
                java.math.BigDecimal precio = java.math.BigDecimal.ZERO;
                if (venta0Str != null && !venta0Str.trim().isEmpty()) {
                    try {
                        String limpio = venta0Str.trim().replace(",", ".");
                        precio = new java.math.BigDecimal(limpio);
                    } catch (NumberFormatException e) {
                        precio = java.math.BigDecimal.ZERO;
                    }
                }

                Object[] fila = new Object[]{
                    desc != null ? desc.trim() : "",
                    barra != null ? barra.trim() : "",
                    precio
                };
                listaProductos.add(fila);
            }

        } catch (java.sql.SQLException e) {
            System.err.println("Error al obtener productos para la tabla: " + e.getMessage());
            e.printStackTrace();
        }

        return listaProductos;
    }

    public boolean eliminarProducto(String barra) {
        String sql = "DELETE FROM pproduc_dbf WHERE \"PXBARRA\" = ?";

        try (Connection conn = Conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, barra);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar producto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
