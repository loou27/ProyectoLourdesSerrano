package src.model;

public class Cliente {
    private int id;
    private String nombre;
    private TipoCliente tipoCliente;

    public Cliente(String nombre, TipoCliente tipoCliente) {
        this.nombre = nombre;
        this.tipoCliente = tipoCliente;
    }

    public Cliente(int id, String nombre, TipoCliente tipoCliente) {
        this.id = id;
        this.nombre = nombre;
        this.tipoCliente = tipoCliente;
    }

    public Cliente() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoCliente getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(TipoCliente tipoCliente) {
        this.tipoCliente = tipoCliente;
    }
}
