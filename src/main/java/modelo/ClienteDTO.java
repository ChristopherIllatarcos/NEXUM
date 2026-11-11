
package modelo;

import java.math.BigDecimal;

public class ClienteDTO {
    private String rut;
    private String paterno;
    private String materno;
    private String nombres;
    private BigDecimal saldoActual;
    private BigDecimal cupo;

    // Constructores
    public ClienteDTO() {
    }

    public ClienteDTO(String rut, String paterno, String materno, String nombres) {
        this.rut = rut;
        this.paterno = paterno;
        this.materno = materno;
        this.nombres = nombres;
    }

    // Getters y Setters
    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }

    public String getPaterno() { return paterno; }
    public void setPaterno(String paterno) { this.paterno = paterno; }

    public String getMaterno() { return materno; }
    public void setMaterno(String materno) { this.materno = materno; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public BigDecimal getSaldoActual() { return saldoActual; }
    public void setSaldoActual(BigDecimal saldoActual) { this.saldoActual = saldoActual; }

    public BigDecimal getCupo() { return cupo; }
    public void setCupo(BigDecimal cupo) { this.cupo = cupo; }

    // Método útil para mostrar el nombre completo ordenado por apellido en la tabla
    public String getNombreCompletoFormato() {
        String pat = (paterno != null) ? paterno : "";
        String mat = (materno != null) ? materno : "";
        String nom = (nombres != null) ? nombres : "";
        return (pat + " " + mat + ", " + nom).trim();
    }
}