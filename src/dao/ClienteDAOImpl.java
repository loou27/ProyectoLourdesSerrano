package src.dao;

import src.db.ConexionDB;
import src.model.Cliente;
import src.model.TipoCliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public void añadirCliente(Cliente cliente) {

        String sql = "INSERT INTO cliente(nombre, tipo) VALUES (?,?)";

        try (Connection con = ConexionDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            // Se asigna el nombre del cliente
            ps.setString(1, cliente.getNombre());

            // Se guarda el tipo de cliente como texto (alumno o profesor)
            ps.setString(2, cliente.getTipoCliente().name());

            // Actualizamos la base de datos
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void borrarCliente(int id) {

        String sql = "DELETE FROM cliente WHERE id=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            // Se indica qué cliente eliminar
            ps.setInt(1, id);

            // Actualizamos
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Cliente buscarCliente(String nombre) {

        String sql = "SELECT * FROM cliente WHERE nombre=?";

        try (Connection con = ConexionDB.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)) {

            // Se asigna el nombre al SELECT
            ps.setString(1, nombre);

            ResultSet rs = ps.executeQuery();

            // Si encuentra cliente se crea el objeto
            if (rs.next()) {
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

        return null;
    }

    @Override
    public Cliente buscarClientePorId(int id) {
        String sql = "SELECT * FROM cliente WHERE id=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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

        String sql = "UPDATE cliente SET nombre=?, tipo=? WHERE id=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getTipoCliente().name());
            ps.setInt(3, cliente.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Cliente> listarClientes() {

        List<Cliente> lista = new ArrayList<>();

        String sql = "SELECT * FROM cliente";

        System.out.println("Debug: listarClientes");

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // Se recorren todos los clientes de la base de datos
            while (rs.next()) {
                Cliente cliente = cargarCliente(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("tipo")
                );
                lista.add(cliente);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    private Cliente cargarCliente(int id, String nombre, String tipoCliente) {
        Cliente c = new Cliente();

        c.setId(id);
        c.setNombre(nombre);
        c.setTipoCliente(TipoCliente.valueOf(tipoCliente.toUpperCase()));

        return c;
    }
}