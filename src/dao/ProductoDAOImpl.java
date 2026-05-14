package src.dao;

import src.db.ConexionDB;
import src.model.Producto;
import src.model.TipoProducto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Esta clase gestiona los productos en la base de datos:
// permite añadir, borrar, modificar, buscar y listar productos
public class ProductoDAOImpl implements ProductoDAO {

    @Override
    public void añadirProducto(Producto producto) {

        // Consulta para insertar un nuevo producto en la base de datos
        String sql = "INSERT INTO producto(nombre, precio, cantidad, tipo) VALUES (?,?,?,?)";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se guarda el nombre del producto
            ps.setString(1, producto.getNombre());

            // Se guarda el precio del producto
            ps.setDouble(2, producto.getPrecio());

            // Se guarda la cantidad disponible
            ps.setInt(3, producto.getCantidad());

            // El tipo es un enum, se convierte a texto para guardarlo en la base de datos
            ps.setString(4, producto.getTipo().name());

            // Se ejecuta la inserción
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void borrarProducto(int id) {

        // Consulta para eliminar un producto por su id
        String sql = "DELETE FROM producto WHERE id=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se indica qué producto se quiere borrar
            ps.setInt(1, id);

            // Se ejecuta la eliminación
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modificarProducto(Producto producto) {

        // Consulta para actualizar los datos de un producto existente
        String sql = "UPDATE producto SET nombre=?, precio=?, cantidad=?, tipo=? WHERE id=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se actualiza el nombre
            ps.setString(1, producto.getNombre());

            // Se actualiza el precio
            ps.setDouble(2, producto.getPrecio());

            // Se actualiza la cantidad
            ps.setInt(3, producto.getCantidad());

            // Se convierte el enum a texto para guardarlo en la base de datos
            ps.setString(4, producto.getTipo().name());

            // Se indica qué producto se modifica
            ps.setInt(5, producto.getId());

            // Se ejecuta la actualización
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Producto buscarProducto(int id) throws Exception {

        // Consulta para buscar un producto por su id
        String sql = "SELECT * FROM producto WHERE id = ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se indica el id que queremos buscar
            ps.setInt(1, id);

            // Se ejecuta la consulta
            ResultSet rs = ps.executeQuery();

            // Si existe el producto, se construye el objeto Producto
            if (rs.next()) {

                return cargarProducto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precio"),
                        rs.getString("tipo")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Si no se encuentra, se lanza un error
        throw new Exception("Producto no encontrado.");
    }

    @Override
    public List<Producto> listarProductos() {

        // Lista donde se guardan todos los productos
        List<Producto> lista = new ArrayList<>();

        // Consulta para obtener todos los productos
        String sql = "SELECT * FROM producto";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Se recorren todos los productos de la base de datos
            while (rs.next()) {

                // Se convierte cada fila en un objeto Producto
                Producto producto = cargarProducto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getInt("cantidad"),
                        rs.getDouble("precio"),
                        rs.getString("tipo")
                );

                // Se añade a la lista final
                lista.add(producto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Se devuelve la lista completa de productos
        return lista;
    }

    // Método auxiliar que convierte datos de la base de datos en un objeto Producto
    private Producto cargarProducto(int id, String nombre, int cantidad, double precio, String tipoProducto) {

        Producto p = new Producto();

        // Se asigna el id
        p.setId(id);

        // Se asigna el nombre
        p.setNombre(nombre);

        // Se asigna la cantidad
        p.setCantidad(cantidad);

        // Se asigna el precio
        p.setPrecio(precio);

        // Se convierte el texto a enum TipoProducto
        p.setTipo(TipoProducto.valueOf(tipoProducto.toUpperCase()));

        return p;
    }
}