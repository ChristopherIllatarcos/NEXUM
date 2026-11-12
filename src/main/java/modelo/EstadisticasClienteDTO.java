package modelo;

public class EstadisticasClienteDTO {

    private String rut;
    private String nombre;
    private double saldoActual; 

    // Compras
    private int vecesCompro;
    private double totalCompro;
    private double promCompro;
    private double maxCompro;
    private String ultimaCompra;

    // Pagos
    private int vecesPago;
    private double totalPago;
    private double promPago;
    private double maxPago;
    private String ultimoPago;

    // Constructores
    public EstadisticasClienteDTO() {
    }

    // Getters y Setters
    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
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

    public double getMaxCompro() {
        return maxCompro;
    }

    public void setMaxCompro(double maxCompro) {
        this.maxCompro = maxCompro;
    }

    public String getUltimaCompra() {
        return ultimaCompra;
    }

    public void setUltimaCompra(String ultimaCompra) {
        this.ultimaCompra = ultimaCompra;
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

    public double getMaxPago() {
        return maxPago;
    }

    public void setMaxPago(double maxPago) {
        this.maxPago = maxPago;
    }

    public String getUltimoPago() {
        return ultimoPago;
    }

    public void setUltimoPago(String ultimoPago) {
        this.ultimoPago = ultimoPago;
    }
}
