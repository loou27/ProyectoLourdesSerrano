package view.cliente;

import view.comun.AbstractPanelBotones;

import src.dao.ClienteDAO;
import src.dao.ClienteDAOImpl;
import src.model.Cliente;

import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * Esta clase representa la pantalla principal de "Clientes".
 *
 * No muestra una tabla, sino una lista de botones:
 * cada botón representa un cliente.
 *
 * Desde aquí se puede:
 * - Ver la lista de clientes (como botones)
 * - Ir a la pantalla de añadir cliente
 * - Ir a la pantalla de editar cliente
 */
public class ClientesJPanel extends AbstractPanelBotones {

    // Acceso a la base de datos para obtener clientes
    private static ClienteDAO clienteDAO = new ClienteDAOImpl();

    /**
     * Constructor:
     * recibe el panel principal donde se cambian las pantallas (CardLayout)
     */
    public ClientesJPanel(JPanel panel) {
        super(panel);
    }

    /**
     * Devuelve el título que se mostrará arriba en la pantalla
     */
    @Override
    protected String getTitulo() {
        return "Clientes";
    }

    /**
     * Devuelve la lista de botones que se van a mostrar en pantalla
     *
     * Cada botón representa un cliente de la base de datos
     */
    @Override
    protected List<String> getBotones() {

        // Lista donde se guardarán los nombres de los clientes
        List<String> botones = new ArrayList<>();

        // Se obtienen todos los clientes desde la base de datos
        List<Cliente> lista = clienteDAO.listarClientes();

        // Se convierte cada cliente en un botón usando su nombre
        for (Cliente c : lista) {
            botones.add(c.getNombre());
        }

        return botones;
    }

    /**
     * Acción cuando el usuario quiere añadir un nuevo cliente
     *
     * Cambia la pantalla al formulario de "Alta de cliente"
     */
    @Override
    protected void añadirRegistro() {

        // Cambia la vista usando CardLayout
        CardLayout cl = (CardLayout) jPanel.getLayout();

        // Muestra la pantalla de alta de cliente
        cl.show(jPanel, AltaClientePanel.NOMBRE_TARJETA);
    }

    /**
     * Acción cuando el usuario quiere editar un cliente
     *
     * Cambia a la pantalla de edición
     */
    @Override
    protected void actualizarRegistro() {

        // Cambia la vista usando CardLayout
        CardLayout cl = (CardLayout) jPanel.getLayout();

        // Muestra la pantalla de edición de cliente
        cl.show(jPanel, EditarClientePanel.NOMBRE_TARJETA);
    }
}