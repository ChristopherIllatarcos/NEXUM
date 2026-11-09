package conexion;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ProbarConexion {

    public static void main(String[] args) {
        System.out.println(">> Intentando conectar a PostgreSQL para NEXUM...");

        try (Connection con = Conexion.getConnection()) {
            if (con != null) {
                System.out.println(">> ¡CONEXIÓN EXITOSA! Base de datos vinculada correctamente.");

                try ( // Prueba rápida consultando un registro de la tabla migrada p003_dbf
                        Statement stmt = con.createStatement()) {
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM p003_dbf");
                    
                    if (rs.next()) {
                        int totalRegistros = rs.getInt("total");
                        System.out.println(">> Registros encontrados en p003_dbf: " + totalRegistros);
                    }
                    rs.close();
                }
            } else {
                System.err.println(">> ERROR: La conexión retornó nula.");
            }
        } catch (Exception e) {
            System.err.println(">> Ocurrió un error durante la prueba de conexión:");
            e.printStackTrace();
        }
    }
}
