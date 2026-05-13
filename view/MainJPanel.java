package view;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class MainJPanel extends AbstractJPanelButtons {
    public MainJPanel(JPanel panel) {
        super(panel);
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

        return botones;
    }
}
