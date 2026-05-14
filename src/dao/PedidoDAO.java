package src.dao;

import java.util.List;
import src.model.MetodoPago;
import src.model.Pedido;

public interface PedidoDAO {
    /** @return id generado o -1 si falla */
    int añadirPedido(Pedido pedido);

    void borrarPedido(int id);

    void modificarPedido(Pedido pedido);

    Pedido buscarPedido(String nombre);

    Pedido buscarPedidoPorId(int id);

    List<Pedido> listarPedidos();

    List<Pedido> listarPedidosAbiertos();

    void actualizarPrecioTotalPedido(int pedidoId);

    /** Marca el pedido como cerrado y registra el método de pago (tras actualizar el total desde líneas). */
    void cerrarPedido(int pedidoId, MetodoPago metodoPago);
}
