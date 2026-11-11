
package modelo;

public class EstadisticasClienteDTO {

    private String nombre;
    private double saldoActual;

    // Compras
    private int vecesCompro;
    private double totalCompro;
    private double promCompro;
    private String ultimaCompra;
    private double maxCompro;
    private String rut;
    private double saldo_actual;

    public double getSaldo_actual() {
        return saldo_actual;
    }

    public void setSaldo_actual(double saldo_actual) {
        this.saldo_actual = saldo_actual;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    // Pagos
    private int vecesPago;
    private double totalPago;
    private double promPago;
    private String ultimoPago;
    private double maxPago;

    // Constructores, Getters y Setters
    public EstadisticasClienteDTO() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getSaldoActual() {
        return saldoActual;
    }

    public void setSaldoActual(double saldoActual) {
        this.saldoActual = saldoActual;
    }

    public int getVecesCompro() {
        return vecesCompro;
    }

    public void setVecesCompro(int vecesCompro) {
        this.vecesCompro = vecesCompro;
    }

    public double getTotalCompro() {
        return totalCompro;
    }

    public void setTotalCompro(double totalCompro) {
        this.totalCompro = totalCompro;
    }

    public double getPromCompro() {
        return promCompro;
    }

    public void setPromCompro(double promCompro) {
        this.promCompro = promCompro;
    }

    public String getUltimaCompra() {
        return ultimaCompra;
    }

    public void setUltimaCompra(String ultimaCompra) {
        this.ultimaCompra = ultimaCompra;
    }

    public double getMaxCompro() {
        return maxCompro;
    }

    public void setMaxCompro(double maxCompro) {
        this.maxCompro = maxCompro;
    }

    public int getVecesPago() {
        return vecesPago;
    }

    public void setVecesPago(int vecesPago) {
        this.vecesPago = vecesPago;
    }

    public double getTotalPago() {
        return totalPago;
    }

    public void setTotalPago(double totalPago) {
        this.totalPago = totalPago;
    }

    public double getPromPago() {
        return promPago;
    }

    public void setPromPago(double promPago) {
        this.promPago = promPago;
    }

    public String getUltimoPago() {
        return ultimoPago;
    }

    public void setUltimoPago(String ultimoPago) {
        this.ultimoPago = ultimoPago;
    }

    public double getMaxPago() {
        return maxPago;
    }

    public void setMaxPago(double maxPago) {
        this.maxPago = maxPago;
    }
}
