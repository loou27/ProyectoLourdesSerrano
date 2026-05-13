package src.dao;

import java.util.List;
import src.model.Cliente;

public interface ClienteDAO {
    void añadirCliente(Cliente cliente);

    void borrarCliente(int id);

    Cliente buscarCliente(String nombre);

    List<Cliente> listarClientes();
}
