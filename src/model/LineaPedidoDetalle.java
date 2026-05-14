package src.model;

/** Línea de pedido para resumen en UI (JOIN producto). */
public class LineaPedidoDetalle {

    private final String nombreProducto;
    private final int cantidad;
    private final double precioUnitario;
    private final double subtotal;

    public LineaPedidoDetalle(String nombreProducto, int cantidad, double precioUnitario, double subtotal) {
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public double getSubtotal() {
        return subtotal;
    }
}
