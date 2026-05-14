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

/** Panel principal de productos con catálogo agrupado y accesos a alta/edición. */
public class ProductosJPanel extends AbstractPanelBotones {

    /** DAO de acceso a productos. */
    private static ProductoDAO productoDAO = new ProductoDAOImpl();

    /** Catálogo visual reutilizable de productos agrupados por tipo. */
    private AbstractCatalogoProductosPanel catalogo;

    /**
     * Construye el panel principal de productos.
     *
     * @param panel contenedor principal con CardLayout
     */
    public ProductosJPanel(JPanel panel) {
        super(panel);
    }

    /**
     * Devuelve el título mostrado en la cabecera.
     *
     * @return título del panel
     */
    @Override
    protected String getTitulo() {
        return "Productos";
    }

    /**
     * Devuelve la lista de botones de productos.
     * Aunque la vista usa catálogo personalizado, se mantiene
     * para respetar el contrato de AbstractPanelBotones.
     *
     * @return nombres de productos
     */
    @Override
    protected List<String> getBotones() {
        List<String> botones = new ArrayList<>();

        List<Producto> lista = productoDAO.listarProductos();

        for (Producto c : lista) {
            botones.add(c.getNombre());
        }

        return botones;
    }

    /**
     * Sustituye la rejilla genérica por un catálogo agrupado
     * por tipo de producto (comida/bebida).
     */
    @Override
    protected void poblarAreaBotones() {

        // Crea el catálogo una sola vez.
        if (catalogo == null) {

            catalogo = new AbstractCatalogoProductosPanel() {

                /**
                 * Abre la tarjeta asociada al producto seleccionado.
                 *
                 * @param producto producto pulsado
                 */
                @Override
                protected void alSeleccionarProducto(Producto producto) {
                    CardLayout cl =
                        (CardLayout) ProductosJPanel.this.jPanel.getLayout();

                    cl.show(
                        ProductosJPanel.this.jPanel,
                        producto.getNombre()
                    );
                }
            };
        }

        botonesGrid.removeAll();
        botonesGrid.setLayout(new BorderLayout(0, 0));

        // Refresca el catálogo con los productos actuales.
        catalogo.refrescarCatalogo(productoDAO.listarProductos());

        botonesGrid.add(catalogo, BorderLayout.CENTER);
    }

    /** Navega hacia el panel de alta de producto. */
    @Override
    protected void añadirRegistro() {
        CardLayout cl = (CardLayout) jPanel.getLayout();
        cl.show(jPanel, AltaProductoPanel.NOMBRE_TARJETA);
    }

    /** Navega hacia el panel de edición de producto. */
    @Override
    protected void actualizarRegistro() {
        CardLayout cl = (CardLayout) jPanel.getLayout();
        cl.show(jPanel, EditarProductoPanel.NOMBRE_TARJETA);
    }
}