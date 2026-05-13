package src.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import src.db.ConexionDB;

public class PedidoProductoDAOImpl implements PedidoProductoDAO {

    @Override
    public void añadirProducto(int pedidoId, int productoId, int cantidad) {
        String sql = "INSERT INTO pedido(pedido_id, producto_id, cantidad) VALUES (?,?,?)";

        try (Connection con = ConexionDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            // Se asignan los parámetros de la query
            ps.setInt(1, pedidoId);
            ps.setInt(2, productoId);
            ps.setInt(3, cantidad);

            // Se ejecuta la inserción del pedido en la base de datos
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
