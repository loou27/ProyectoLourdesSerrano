package view.cliente;

import view.comun.AbstractPanelBotones;

import src.dao.ClienteDAO;
import src.dao.ClienteDAOImpl;
import src.model.Cliente;

import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class ClientesJPanel extends AbstractPanelBotones {
    private static ClienteDAO clienteDAO = new ClienteDAOImpl();

    public ClientesJPanel(JPanel panel) {
        super(panel);
    }

    @Override
    protected String getTitulo() {
        return "Clientes";
    }

    @Override
    protected List<String> getBotones() {
        List<String> botones = new ArrayList<>();

        List<Cliente> lista = clienteDAO.listarClientes();

        for (Cliente c : lista) {
            botones.add(c.getNombre());
        }

        return botones;
    }

    @Override
    protected void añadirRegistro() {
        CardLayout cl = (CardLayout) jPanel.getLayout();
        cl.show(jPanel, AltaClientePanel.NOMBRE_TARJETA);
    }

    @Override
    protected void actualizarRegistro() {
        CardLayout cl = (CardLayout) jPanel.getLayout();
        cl.show(jPanel, EditarClientePanel.NOMBRE_TARJETA);
    }
}
