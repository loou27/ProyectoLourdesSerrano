package src.dao;

import java.util.List;
import src.model.Pedido;

public interface PedidoDAO {
    void añadirPedido(Pedido pedido);

    void borrarPedido(int id);

    void modificarPedido(Pedido pedido);

    Pedido buscarPedido(String nombre);

    List<Pedido> listarPedidos();

    List<Pedido> listarPedidosAbiertos();
}
