package view;
import src.dao.*;
import src.model.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;


public class ClientesJPanel extends AbstractJPanelButtons {
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
}
