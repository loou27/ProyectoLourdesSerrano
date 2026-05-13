package view;
import src.dao.*;
import src.model.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;


public class ProductosJPanel extends AbstractJPanelButtons {
    private static ProductoDAO productoDAO = new ProductoDAOImpl();

    
    public ProductosJPanel(JPanel panel) {
        super(panel);
    }

    @Override
    protected String getTitulo() {
        return "Productos";
    }

    @Override
    protected List<String> getBotones() {
        List<String> botones = new ArrayList<>();


        List<Producto> lista = productoDAO.listarProductos();

        for (Producto c : lista) {
            botones.add(c.getNombre());
        }

        return botones;
    }
}
