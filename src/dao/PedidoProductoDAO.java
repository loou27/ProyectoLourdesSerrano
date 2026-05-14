package src.dao;

import java.util.List;
import src.model.LineaPedidoDetalle;

public interface PedidoProductoDAO {

    void añadirProducto(int pedidoId, int productoId, int cantidad);

    void añadirOIncrementarLinea(int pedidoId, int productoId, int delta);

    List<LineaPedidoDetalle> listarLineasPedido(int pedidoId);
}
