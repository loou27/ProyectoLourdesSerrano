package src.dao;

import src.db.ConexionDB;
import src.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAOImpl implements PedidoDAO {

    @Override
    public int añadirPedido(Pedido pedido) {

        String sql = "INSERT INTO pedido(cliente_id, estado, precio_total) VALUES (?,?,?)";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, pedido.getClienteId());
            ps.setString(2, pedido.getEstado().name());
            ps.setDouble(3, 0);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void borrarPedido(int id) {

        String sql = "DELETE FROM pedido WHERE id=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

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

            ps.setInt(1, pedido.getClienteId());

            if (pedido.getMetodoPago() != null) {
                ps.setString(2, pedido.getMetodoPago().name());
            } else {
                ps.setNull(2, Types.VARCHAR);
            }

            ps.setDouble(3, pedido.getPrecio());
            ps.setString(4, pedido.getEstado().name());
            ps.setInt(5, pedido.getId());

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

            ps.setString(1, nombre);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                return leerPedidoDesdeResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Pedido buscarPedidoPorId(int id) {
        String sql = "SELECT * FROM pedido WHERE id=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return leerPedidoDesdeResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Pedido leerPedidoDesdeResultSet(ResultSet rs) throws SQLException {
        int pid = rs.getInt("id");
        return cargarPedido(
            pid,
            sumarPrecioProductos(pid),
            rs.getString("metodo_pago"),
            rs.getString("estado"),
            rs.getInt("cliente_id")
        );
    }

    @Override
    public List<Pedido> listarPedidos() {

        List<Pedido> lista = new ArrayList<>();

        String sql = "SELECT * FROM pedido";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Pedido pedido = leerPedidoDesdeResultSet(rs);

                lista.add(pedido);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Pedido> listarPedidosAbiertos() {
        List<Pedido> lista = new ArrayList<>();

        String sql = "SELECT * FROM pedido WHERE estado = 'ABIERTO' ORDER BY id";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(leerPedidoDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public void actualizarPrecioTotalPedido(int pedidoId) {
        double total = sumarPrecioProductos(pedidoId);
        String sql = "UPDATE pedido SET precio_total=? WHERE id=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, total);
            ps.setInt(2, pedidoId);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cerrarPedido(int pedidoId, MetodoPago metodoPago) {
        actualizarPrecioTotalPedido(pedidoId);
        String sql = "UPDATE pedido SET estado=?, metodo_pago=? WHERE id=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, Estado.CERRADO.name());
            ps.setString(2, metodoPago.name());
            ps.setInt(3, pedidoId);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    double sumarPrecioProductos(int pedidoId) {
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

        if (metodoPago != null) {
            p.setMetodoPago(MetodoPago.valueOf(metodoPago.toUpperCase()));
        }

        p.setEstado(Estado.valueOf(estado.toUpperCase()));
        p.setClienteId(clienteId);

        return p;
    }
}
