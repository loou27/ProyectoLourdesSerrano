package src.dao;

import src.db.ConexionDB;
import src.model.Cliente;
import src.model.TipoCliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Esta clase se encarga de hacer operaciones con la base de datos de clientes:
// guardar, borrar, buscar, modificar y listar.
public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public void añadirCliente(Cliente cliente) {

        // Consulta SQL para insertar un cliente en la base de datos
        String sql = "INSERT INTO cliente(nombre, tipo) VALUES (?,?)";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se asigna el nombre del cliente al primer "?"
            ps.setString(1, cliente.getNombre());

            // Se asigna el tipo de cliente al segundo "?"
            // (por ejemplo: alumno o profesor)
            ps.setString(2, cliente.getTipoCliente().name());

            // Se ejecuta la inserción en la base de datos
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void borrarCliente(int id) {

        // Consulta SQL para eliminar un cliente según su id
        String sql = "DELETE FROM cliente WHERE id=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se indica qué cliente se debe borrar
            ps.setInt(1, id);

            // Se ejecuta el borrado
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Cliente buscarCliente(String nombre) {

        // Consulta para buscar un cliente por nombre
        String sql = "SELECT * FROM cliente WHERE nombre=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se coloca el nombre que queremos buscar
            ps.setString(1, nombre);

            // Se ejecuta la consulta y se obtienen resultados
            ResultSet rs = ps.executeQuery();

            // Si existe al menos un resultado
            if (rs.next()) {

                // Se crea un objeto Cliente con los datos obtenidos
                Cliente cliente = cargarCliente(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("tipo")
                );

                return cliente;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Si no se encuentra nada, se devuelve null
        return null;
    }

    @Override
    public Cliente buscarClientePorId(int id) {

        // Consulta para buscar un cliente por su id
        String sql = "SELECT * FROM cliente WHERE id=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    // Se construye el objeto Cliente con los datos encontrados
                    return cargarCliente(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("tipo")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void modificarCliente(Cliente cliente) {

        // Consulta para actualizar un cliente existente
        String sql = "UPDATE cliente SET nombre=?, tipo=? WHERE id=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Nuevo nombre del cliente
            ps.setString(1, cliente.getNombre());

            // Nuevo tipo de cliente
            ps.setString(2, cliente.getTipoCliente().name());

            // Id del cliente que se va a modificar
            ps.setInt(3, cliente.getId());

            // Se ejecuta la actualización
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Cliente> listarClientes() {

        // Lista donde se guardarán todos los clientes
        List<Cliente> lista = new ArrayList<>();

        // Consulta para obtener todos los clientes
        String sql = "SELECT * FROM cliente";

        System.out.println("Debug: listarClientes");

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Se recorren todos los registros de la base de datos
            while (rs.next()) {

                // Se convierte cada fila en un objeto Cliente
                Cliente cliente = cargarCliente(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("tipo")
                );

                // Se añade el cliente a la lista
                lista.add(cliente);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Se devuelve la lista completa de clientes
        return lista;
    }

    // Método auxiliar: convierte datos de la base de datos en un objeto Cliente
    private Cliente cargarCliente(int id, String nombre, String tipoCliente) {

        Cliente c = new Cliente();

        // Se asigna el id
        c.setId(id);

        // Se asigna el nombre
        c.setNombre(nombre);

        // Se convierte el texto (tipo) a un valor del enum TipoCliente
        c.setTipoCliente(TipoCliente.valueOf(tipoCliente.toUpperCase()));

        return c;
    }
}