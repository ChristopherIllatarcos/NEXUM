package modelo;

import java.math.BigDecimal;

public class ClienteDTO {

    private String rut;
    private String digitoVerificador;
    private String nombres; // Almacena todo el contenido de CLINOMBRE (nombres y apellidos juntos)
    private BigDecimal saldoActual;
    private BigDecimal cupo;

    // Constructores
    public ClienteDTO() {
    }

    public ClienteDTO(String rut, String nombres) {
        this.rut = rut;
        this.nombres = nombres;
    }

    // Constructor de compatibilidad (por si alguna otra clase aún pasa paterno/materno)
    public ClienteDTO(String rut, String paterno, String materno, String nombres) {
        this.rut = rut;
        String pat = (paterno != null) ? paterno.trim() : "";
        String mat = (materno != null) ? materno.trim() : "";
        String nom = (nombres != null) ? nombres.trim() : "";

        // Unimos todo en un solo string si se pasa por separado
        this.nombres = (pat + " " + mat + " " + nom).replaceAll("\\s+", " ").trim();
    }

    // Getters y Setters principales
    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getDigitoVerificador() {
        return digitoVerificador;
    }

    public void setDigitoVerificador(String digitoVerificador) {
        this.digitoVerificador = digitoVerificador;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public BigDecimal getSaldoActual() {
        return saldoActual;
    }

    public void setSaldoActual(BigDecimal saldoActual) {
        this.saldoActual = saldoActual;
    }

    public BigDecimal getCupo() {
        return cupo;
    }

    public void setCupo(BigDecimal cupo) {
        this.cupo = cupo;
    }

    // Métodos de compatibilidad (devuelven vacío para evitar errores en legacy o vistas antiguas)
    public String getPaterno() {
        return "";
    }

    public void setPaterno(String paterno) {
    }

    public String getMaterno() {
        return "";
    }

    public void setMaterno(String materno) {
    }

    // Método para mostrar en la tabla con los APELLIDOS PRIMERO y luego los NOMBRES
    public String getNombreCompletoFormato() {
        if (nombres == null || nombres.trim().isEmpty()) {
            return "";
        }

        String textoLimpio = nombres.trim().replaceAll("\\s+", " ");
        String[] partes = textoLimpio.split(" ");

        // Si tiene 2 o menos palabras (ej: "JUAN PEREZ"), lo dejamos o invertimos si se puede
        if (partes.length <= 2) {
            return textoLimpio;
        }

        // Asumiendo un formato común de nombres y apellidos (ej: JUAN CARLOS PEREZ SOTO)
        // Por lo general, los últimos 2 tokens son los apellidos (Paterno y Materno)
        // y los anteriores son los nombres.
        int total = partes.length;
        String apellidoMaterno = partes[total - 1];
        String apellidoPaterno = partes[total - 2];

        // Juntamos los nombres del medio/inicio
        StringBuilder nombresRestantes = new StringBuilder();
        for (int i = 0; i < total - 2; i++) {
            nombresRestantes.append(partes[i]).append(" ");
        }

        // Retornamos ordenado: APELLIDOS + NOMBRES
        return (apellidoPaterno + " " + apellidoMaterno + " " + nombresRestantes.toString()).trim();
    }
}
