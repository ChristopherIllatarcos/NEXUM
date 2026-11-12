package modelo;

public class ItemVentaDTO {

    private String barra;
    private int cantidad;
    private double precioUnitario;
    private double totalItem;

    public ItemVentaDTO() {
    }

    public ItemVentaDTO(String barra, int cantidad, double precioUnitario, double totalItem) {
        this.barra = barra;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.totalItem = totalItem;
    }

    public String getBarra() {
        return barra;
    }

    public void setBarra(String barra) {
        this.barra = barra;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public double getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(double totalItem) {
        this.totalItem = totalItem;
    }
}
