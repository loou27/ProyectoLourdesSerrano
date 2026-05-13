package src.model;

public class Pedido {

    private int id;
    private double precio;
    private MetodoPago metodoPago;
    private Estado estado;
    private int clienteId;

    public Pedido() {
    }

    public Pedido(int id, double precio, MetodoPago metodoPago, Estado estado, int clienteId) {
        this.id = id;
        this.precio = precio;
        this.metodoPago = metodoPago;
        this.estado = estado;
        this.clienteId = clienteId;
    }

    public Pedido(double precio, MetodoPago metodoPago, Estado estado, int clienteId) {
        this.precio = precio;
        this.metodoPago = metodoPago;
        this.estado = estado;
        this.clienteId = clienteId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // ESTE ES EL CAMBIO IMPORTANTE: antes DAO usaba getPrecioTotal()
    public double getPrecioTotal() {
        return precio;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precio = precioTotal;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }
}