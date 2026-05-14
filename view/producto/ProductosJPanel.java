package view.producto;

import view.comun.*;

import src.dao.ProductoDAO;
import src.dao.ProductoDAOImpl;
import src.model.Producto;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class ProductosJPanel extends AbstractPanelBotones {
    private static ProductoDAO productoDAO = new ProductoDAOImpl();

    private AbstractCatalogoProductosPanel catalogo;

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

    @Override
    protected void poblarAreaBotones() {
        if (catalogo == null) {
            catalogo = new AbstractCatalogoProductosPanel() {
                @Override
                protected void alSeleccionarProducto(Producto producto) {
                    CardLayout cl = (CardLayout) ProductosJPanel.this.jPanel.getLayout();
                    cl.show(ProductosJPanel.this.jPanel, producto.getNombre());
                }
            };
        }

        botonesGrid.removeAll();
        botonesGrid.setLayout(new BorderLayout(0, 0));
        catalogo.refrescarCatalogo(productoDAO.listarProductos());
        botonesGrid.add(catalogo, BorderLayout.CENTER);
    }

    @Override
    protected void añadirRegistro() {
        CardLayout cl = (CardLayout) jPanel.getLayout();
        cl.show(jPanel, AltaProductoPanel.NOMBRE_TARJETA);
    }

    @Override
    protected void actualizarRegistro() {
        CardLayout cl = (CardLayout) jPanel.getLayout();
        cl.show(jPanel, EditarProductoPanel.NOMBRE_TARJETA);
    }
}
