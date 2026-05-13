package src.dao;

import src.db.ConexionDB;
import src.model.Producto;
import src.model.TipoProducto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAOImpl implements ProductoDAO {

    @Override
    public void añadirProducto(Producto producto) {

        String sql = "INSERT INTO producto(nombre, precio, cantidad, tipo) VALUES (?,?,?,?)";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se asignan los valores del objeto Producto a la consulta SQL
            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());
            ps.setInt(3, producto.getCantidad());

            // Se convierte el enum a String para guardarlo en la base de datos
            ps.setString(4, producto.getTipo().name());

            // Se ejecuta la inserción en la base de datos
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void borrarProducto(int id) {

        String sql = "DELETE FROM producto WHERE id=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se asigna el id del producto a eliminar
            ps.setInt(1, id);

            // Se ejecuta la eliminación
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modificarProducto(Producto producto) {

        String sql = "UPDATE producto SET nombre=?, precio=?, cantidad=?, tipo=? WHERE id=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se asignan los nuevos valores del producto
            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());
            ps.setInt(3, producto.getCantidad());

            // Conversión del enum a texto para la base de datos
            ps.setString(4, producto.getTipo().name());

            // Identificador del producto a modificar
            ps.setInt(5, producto.getId());

            // Ejecución de la actualización
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Producto> listarProductos() {

        List<Producto> lista = new ArrayList<>();

        String sql = "SELECT * FROM producto";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Se recorren los resultados obtenidos de la base de datos
            while (rs.next()) {

                Producto p = new Producto();

                // Se asignan los valores de cada columna al objeto Producto
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setPrecio(rs.getDouble("precio"));
                p.setCantidad(rs.getInt("cantidad"));

                // Conversión de String a enum
                p.setTipo(TipoProducto.valueOf(rs.getString("tipo")));

                // Se añade el producto a la lista final
                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Se devuelve la lista completa de productos
        return lista;
    }
}