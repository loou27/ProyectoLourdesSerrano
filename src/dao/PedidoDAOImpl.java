package src.dao;

import src.db.ConexionDB;
import src.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Esta clase se encarga de gestionar los pedidos en la base de datos:
// crear, borrar, modificar, buscar y listar pedidos
public class PedidoDAOImpl implements PedidoDAO {

    @Override
    public int añadirPedido(Pedido pedido) {

        // Consulta para insertar un nuevo pedido
        String sql = "INSERT INTO pedido(cliente_id, estado, precio_total) VALUES (?,?,?)";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Se guarda el id del cliente que hace el pedido
            ps.setInt(1, pedido.getClienteId());

            // Se guarda el estado del pedido (por ejemplo: ABIERTO)
            ps.setString(2, pedido.getEstado().name());

            // El pedido empieza con precio 0
            ps.setDouble(3, 0);

            // Se ejecuta la inserción
            ps.executeUpdate();

            // Se obtiene el id generado automáticamente por la base de datos
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Si falla, se devuelve -1
        return -1;
    }

    @Override
    public void borrarPedido(int id) {

        // Consulta para eliminar un pedido por su id
        String sql = "DELETE FROM pedido WHERE id=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se indica qué pedido se quiere borrar
            ps.setInt(1, id);

            // Se ejecuta el borrado
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modificarPedido(Pedido pedido) {

        // Consulta para actualizar los datos de un pedido
        String sql = "UPDATE pedido SET cliente_id=?, metodo_pago=?, precio_total=?, estado=? WHERE id=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se actualiza el cliente del pedido
            ps.setInt(1, pedido.getClienteId());

            // Si tiene método de pago, se guarda
            if (pedido.getMetodoPago() != null) {
                ps.setString(2, pedido.getMetodoPago().name());
            } else {
                // Si no tiene, se guarda como vacío en la base de datos
                ps.setNull(2, Types.VARCHAR);
            }

            // Se actualiza el precio total del pedido
            ps.setDouble(3, pedido.getPrecio());

            // Se actualiza el estado del pedido
            ps.setString(4, pedido.getEstado().name());

            // Se indica qué pedido se está modificando
            ps.setInt(5, pedido.getId());

            // Se ejecuta la actualización
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Pedido buscarPedido(String nombre) {

        // Consulta para buscar un pedido por nombre
        String sql = "SELECT * FROM pedido WHERE nombre=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se asigna el nombre a buscar
            ps.setString(1, nombre);

            // Se ejecuta la consulta
            ResultSet rs = ps.executeQuery();

            // Si encuentra un resultado, se convierte en objeto Pedido
            if (rs.next()) {
                return leerPedidoDesdeResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Si no encuentra nada, devuelve null
        return null;
    }

    @Override
    public Pedido buscarPedidoPorId(int id) {

        // Consulta para buscar un pedido por id
        String sql = "SELECT * FROM pedido WHERE id=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se indica el id a buscar
            ps.setInt(1, id);

            // Se ejecuta la consulta
            ResultSet rs = ps.executeQuery();

            // Si existe el pedido, se convierte a objeto
            if (rs.next()) {
                return leerPedidoDesdeResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Método que convierte una fila de la base de datos en un objeto Pedido
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

        // Lista donde se guardan todos los pedidos
        List<Pedido> lista = new ArrayList<>();

        // Consulta para obtener todos los pedidos
        String sql = "SELECT * FROM pedido";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Se recorren todos los pedidos
            while (rs.next()) {

                // Se convierte cada fila en un objeto Pedido
                Pedido pedido = leerPedidoDesdeResultSet(rs);

                // Se añade a la lista
                lista.add(pedido);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Se devuelve la lista completa
        return lista;
    }

    @Override
    public List<Pedido> listarPedidosAbiertos() {

        // Lista de pedidos abiertos
        List<Pedido> lista = new ArrayList<>();

        // Consulta para obtener solo pedidos abiertos
        String sql = "SELECT * FROM pedido WHERE estado = 'ABIERTO' ORDER BY id";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Se recorren los pedidos abiertos
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

        // Se calcula el precio total sumando los productos del pedido
        double total = sumarPrecioProductos(pedidoId);

        // Consulta para actualizar el precio total
        String sql = "UPDATE pedido SET precio_total=? WHERE id=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se guarda el nuevo precio total
            ps.setDouble(1, total);

            // Se indica qué pedido se actualiza
            ps.setInt(2, pedidoId);

            // Se ejecuta la actualización
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void cerrarPedido(int pedidoId, MetodoPago metodoPago) {

        // Primero se actualiza el precio total antes de cerrar el pedido
        actualizarPrecioTotalPedido(pedidoId);

        // Consulta para cerrar el pedido y guardar el método de pago
        String sql = "UPDATE pedido SET estado=?, metodo_pago=? WHERE id=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // El pedido pasa a estado cerrado
            ps.setString(1, Estado.CERRADO.name());

            // Se guarda el método de pago elegido
            ps.setString(2, metodoPago.name());

            // Se indica el pedido a cerrar
            ps.setInt(3, pedidoId);

            // Se ejecuta la actualización
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Calcula el precio total sumando los productos de un pedido
    double sumarPrecioProductos(int pedidoId) {

        //Esta consulta SQL sirve para calcular información de los productos que pertenecen a un pedido concreto.
        String sql =
                "SELECT pp.*, " +
                "       p.precio * pp.cantidad AS precio " +
                "FROM pedido_producto pp " +
                "LEFT JOIN producto p ON pp.producto_id = p.id " +
                "WHERE pp.pedido_id = ?";

        double precioTotal = 0;

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, pedidoId);

            ResultSet rs = ps.executeQuery();

            // Se suman todos los precios de los productos del pedido
            while (rs.next()) {
                precioTotal += rs.getDouble("precio");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return precioTotal;
    }

    // Convierte datos simples en un objeto Pedido completo
    private Pedido cargarPedido(int id, double precio, String metodoPago, String estado, int clienteId) {

        Pedido p = new Pedido();

        // Se asigna el id del pedido
        p.setId(id);

        // Se asigna el precio total
        p.setPrecio(precio);

        // Se asigna el método de pago si existe
        if (metodoPago != null) {
            p.setMetodoPago(MetodoPago.valueOf(metodoPago.toUpperCase()));
        }

        // Se asigna el estado del pedido
        p.setEstado(Estado.valueOf(estado.toUpperCase()));

        // Se asigna el cliente dueño del pedido
        p.setClienteId(clienteId);

        return p;
    }
}