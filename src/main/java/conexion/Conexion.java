package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    // Configura aquí los datos de tu servidor PostgreSQL local
    private static final String URL = "jdbc:postgresql://localhost:5432/DBASE"; 
    private static final String USUARIO = "postgres";
    private static final String PASSWORD = "nA4pYRSr2ziagy"; 

    /**
     * Establece y retorna la conexión con la base de datos PostgreSQL.
     * Método nombrado explícitamente getConnection() para evitar conflictos.
     * @return 
     */
    public static Connection getConnection() {
        Connection conexion = null;
        try {
            // Cargar el driver de PostgreSQL (opcional en versiones modernas de JDBC, pero buena práctica)
            Class.forName("org.postgresql.Driver");
            
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            // System.out.println(">> ¡Conexión exitosa a PostgreSQL para NEXUM!");
            
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el Driver JDBC de PostgreSQL.");
        } catch (SQLException e) {
            System.err.println("Error de SQL al intentar conectar con la base de datos.");
        }
        return conexion;
    }
}