package src.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import src.db.ConexionDB;
import src.model.LineaPedidoDetalle;

public class PedidoProductoDAOImpl implements PedidoProductoDAO {

    @Override
    public void añadirProducto(int pedidoId, int productoId, int cantidad) {
        String sql = "INSERT INTO pedido_producto(pedido_id, producto_id, cantidad) VALUES (?,?,?)";

        try (Connection con = ConexionDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, pedidoId);
            ps.setInt(2, productoId);
            ps.setInt(3, cantidad);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void añadirOIncrementarLinea(int pedidoId, int productoId, int delta) {
        if (delta <= 0) {
            return;
        }

        String selectSql = "SELECT id, cantidad FROM pedido_producto WHERE pedido_id=? AND producto_id=?";

        try (Connection con = ConexionDB.getConnection()) {

            try (PreparedStatement psSel = con.prepareStatement(selectSql)) {
                psSel.setInt(1, pedidoId);
                psSel.setInt(2, productoId);
                ResultSet rs = psSel.executeQuery();

                if (rs.next()) {
                    int lineaId = rs.getInt("id");
                    int cantidadActual = rs.getInt("cantidad");
                    String upd = "UPDATE pedido_producto SET cantidad=? WHERE id=?";
                    try (PreparedStatement psUpd = con.prepareStatement(upd)) {
                        psUpd.setInt(1, cantidadActual + delta);
                        psUpd.setInt(2, lineaId);
                        psUpd.executeUpdate();
                    }
                    return;
                }
            }

            añadirProducto(pedidoId, productoId, delta);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<LineaPedidoDetalle> listarLineasPedido(int pedidoId) {
        List<LineaPedidoDetalle> lista = new ArrayList<>();

        String sql = "SELECT p.nombre AS nombre, pp.cantidad AS cantidad, p.precio AS precio_unitario, "
                   + "(p.precio * pp.cantidad) AS subtotal "
                   + "FROM pedido_producto pp "
                   + "JOIN producto p ON pp.producto_id = p.id "
                   + "WHERE pp.pedido_id = ? "
                   + "ORDER BY p.nombre";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, pedidoId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
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

        return lista;
    }
}
