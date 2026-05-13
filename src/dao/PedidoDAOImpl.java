package src.dao;

import src.db.ConexionDB;
import src.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAOImpl implements PedidoDAO {

    @Override
    public void añadirPedido(Pedido pedido) {

        String sql = "INSERT INTO pedido(cliente_id, estado) VALUES (?,?)";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se asigna el id del cliente asociado al pedido
            ps.setInt(1, pedido.getClienteId());

            // Se convierte el enum MetodoPago a String para guardarlo en la base de datos
           // ps.setString(2, pedido.getMetodoPago().name());

            // Se convierte el enum Estado a String
            ps.setString(2, pedido.getEstado().name());

            // Se ejecuta la inserción del pedido en la base de datos
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void borrarPedido(int id) {

        String sql = "DELETE FROM pedido WHERE id=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se indica el id del pedido a eliminar
            ps.setInt(1, id);

            // Se ejecuta el borrado
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modificarPedido(Pedido pedido) {

        String sql = "UPDATE pedido SET cliente_id=?, metodo_pago=?, precio_total=?, estado=? WHERE id=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se actualiza el cliente asociado al pedido
            ps.setInt(1, pedido.getClienteId());

            // Se actualiza el método de pago
            ps.setString(2, pedido.getMetodoPago().name());

            // Se actualiza el precio del pedido
            ps.setDouble(3, pedido.getPrecio());

            // Se actualiza el estado del pedido
            ps.setString(4, pedido.getEstado().name());

            // Se indica qué pedido se modifica
            ps.setInt(5, pedido.getId());

            // Se ejecuta la actualización
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Pedido buscarPedido(String nombre) {

        String sql = "SELECT * FROM pedido WHERE nombre=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se le asigna el nombre al SELECT
            ps.setString(1, nombre);

            ResultSet rs = ps.executeQuery();

            // Si existe el pedido, se construye el objeto
            if (rs.next()) {

                Pedido p = new Pedido();

                p.setId(rs.getInt("id"));
                p.setClienteId(rs.getInt("cliente_id"));

                // Se convierte el String de la base de datos a enum
                p.setMetodoPago(MetodoPago.valueOf(rs.getString("metodo_pago")));
                p.setEstado(Estado.valueOf(rs.getString("estado")));

                p.setPrecio(rs.getDouble("precio_total"));

                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Pedido> listarPedidos() {

        List<Pedido> lista = new ArrayList<>();

        String sql = "SELECT * FROM pedido";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Se recorren todos los pedidos de la base de datos
            while (rs.next()) {

                Pedido pedido = cargarPedido(
                    rs.getInt("id"),
                    sumarPrecioProductos(rs.getInt("id")),
                    rs.getString("metodo_pago"),
                    rs.getString("estado"),
                    rs.getInt("cliente_id")
                );

                lista.add(pedido);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private double sumarPrecioProductos(int pedidoId) {
        String sql = "SELECT pp.*, " + 
                    "        p.precio * pp.cantidad AS precio " +
                    "FROM pedido_producto pp " +
                    "LEFT JOIN producto p ON pp.producto_id = p.id " + 
                    "WHERE pp.pedido_id = ?";
        double precioTotal = 0;
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
             ps.setInt(1, pedidoId);
             ResultSet rs = ps.executeQuery();

             while (rs.next()) {
                precioTotal += rs.getDouble("precio");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return precioTotal;
    }

    private Pedido cargarPedido(int id, double precio, String metodoPago, String estado, int clienteId) {
        Pedido p = new Pedido();

        p.setId(id);
        p.setPrecio(precio);

        if(metodoPago != null){
            p.setMetodoPago(MetodoPago.valueOf(metodoPago.toUpperCase()));
        }

        p.setEstado(Estado.valueOf(estado.toUpperCase()));
        p.setClienteId(clienteId);

        return p;
    }
}

