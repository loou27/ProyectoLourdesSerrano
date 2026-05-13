package src.dao;

import java.util.List;
import src.model.Producto;

public interface ProductoDAO {
    void añadirProducto(Producto producto);

    void borrarProducto(int id);

    void modificarProducto(Producto producto);

    List<Producto> listarProductos();
}
