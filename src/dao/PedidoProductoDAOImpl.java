package src.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import src.db.ConexionDB;
import src.model.LineaPedidoDetalle;

// Esta clase gestiona las líneas de un pedido (los productos dentro de un pedido)
// Se encarga de añadir productos y listar lo que hay dentro de un pedido
public class PedidoProductoDAOImpl implements PedidoProductoDAO {

    @Override
    public void añadirProducto(int pedidoId, int productoId, int cantidad) {

        // Inserta una nueva línea en la tabla pedido_producto
        String sql = "INSERT INTO pedido_producto(pedido_id, producto_id, cantidad) VALUES (?,?,?)";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se indica a qué pedido pertenece el producto
            ps.setInt(1, pedidoId);

            // Se indica qué producto se añade
            ps.setInt(2, productoId);

            // Se indica cuántas unidades se añaden
            ps.setInt(3, cantidad);

            // Se guarda en la base de datos
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void añadirOIncrementarLinea(int pedidoId, int productoId, int delta) {

        // Si la cantidad es 0 o negativa, no se hace nada
        if (delta <= 0) {
            return;
        }

        // Consulta para comprobar si el producto ya existe en el pedido
        String selectSql =
                "SELECT id, cantidad FROM pedido_producto WHERE pedido_id=? AND producto_id=?";

        try (Connection con = ConexionDB.getConnection()) {

            // Primero se busca si ya existe esa línea en el pedido
            try (PreparedStatement psSel = con.prepareStatement(selectSql)) {

                psSel.setInt(1, pedidoId);
                psSel.setInt(2, productoId);

                ResultSet rs = psSel.executeQuery();

                // Si ya existe el producto en el pedido
                if (rs.next()) {

                    int lineaId = rs.getInt("id");
                    int cantidadActual = rs.getInt("cantidad");

                    // Se actualiza sumando la nueva cantidad
                    String upd =
                            "UPDATE pedido_producto SET cantidad=? WHERE id=?";

                    try (PreparedStatement psUpd = con.prepareStatement(upd)) {

                        psUpd.setInt(1, cantidadActual + delta);
                        psUpd.setInt(2, lineaId);

                        psUpd.executeUpdate();
                    }

                    return;
                }
            }

            // Si no existía, se crea una nueva línea
            añadirProducto(pedidoId, productoId, delta);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<LineaPedidoDetalle> listarLineasPedido(int pedidoId) {

        // Lista donde se guardan las líneas del pedido
        List<LineaPedidoDetalle> lista = new ArrayList<>();

        // Consulta que obtiene los productos de un pedido con detalles
        String sql =
                "SELECT p.nombre AS nombre, " +
                "       pp.cantidad AS cantidad, " +
                "       p.precio AS precio_unitario, " +
                "       (p.precio * pp.cantidad) AS subtotal " +
                "FROM pedido_producto pp " +
                "JOIN producto p ON pp.producto_id = p.id " +
                "WHERE pp.pedido_id = ? " +
                "ORDER BY p.nombre";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se indica el pedido que queremos consultar
            ps.setInt(1, pedidoId);

            ResultSet rs = ps.executeQuery();

            // Se recorren todas las líneas del pedido
            while (rs.next()) {

                // Se crea un objeto con la información de cada línea
                lista.add(new LineaPedidoDetalle(
                        rs.getString("nombre"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precio_unitario"),
                        rs.getDouble("subtotal")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Se devuelve la lista completa de productos del pedido
        return lista;
    }
}