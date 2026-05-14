package view.principal;

import view.comun.AbstractPanelBotones;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class MainJPanel extends AbstractPanelBotones {
    public MainJPanel(JPanel panel) {
        super(panel);
    }

    @Override
    protected boolean mostrarBotonAñadir() {
        return false;
    }

    @Override
    protected String getTitulo() {
        return "Cafetería Ayala";
    }

    @Override
    protected List<String> getBotones() {
        List<String> botones = new ArrayList<>();

        botones.add("Clientes");
        botones.add("Productos");
        botones.add("Pedidos");

        return botones;
    }
}
