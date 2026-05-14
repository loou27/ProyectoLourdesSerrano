package src.dao;

import java.util.List;
import src.model.MetodoPago;
import src.model.Pedido;

public interface PedidoDAO {
    /** @return id generado o -1 si falla */
    int añadirPedido(Pedido pedido);

    void borrarPedido(int id);https://github.com/loou27/ProyectoLourdesSerrano/pull/2/conflict?name=src%252Fdao%252FPedidoDAO.java&ancestor_oid=bbefa25d19c8151376f76c6043653e4d3d2dca82&base_oid=e6c5f3577985c677494b3aece545e28787947c54&head_oid=0ea89f4f781cc9c74076a950bd0e084663c388fd

    void modificarPedido(Pedido pedido);

    Pedido buscarPedido(String nombre);

    Pedido buscarPedidoPorId(int id);

    List<Pedido> listarPedidos();

    List<Pedido> listarPedidosAbiertos();

    void actualizarPrecioTotalPedido(int pedidoId);

    /** Marca el pedido como cerrado y registra el método de pago (tras actualizar el total desde líneas). */
    void cerrarPedido(int pedidoId, MetodoPago metodoPago);
}
