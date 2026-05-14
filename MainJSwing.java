import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JFrame;

import java.awt.CardLayout;
import java.awt.Dimension;

import view.cliente.AltaClientePanel;
import view.cliente.ClientesJPanel;
import view.cliente.EditarClientePanel;
import view.pedidos.NuevoPedidoPanel;
import view.pedidos.PedidoDetallePanel;
import view.pedidos.PedidosListaPanel;
import view.principal.MainJPanel;
import view.producto.AltaProductoPanel;
import view.producto.EditarProductoPanel;
import view.producto.ProductosJPanel;

/** Clase principal encargada de lanzar la interfaz Swing de la aplicación. */
public class MainJSwing {

    /** Construye y muestra toda la interfaz gráfica principal. */
    private static void GUI() {

        // Creamos la ventana principal de la aplicación.
        JFrame frame = new JFrame("Cafetería Ayala");

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Tamaño mínimo permitido para evitar deformaciones.
        frame.setMinimumSize(new Dimension(720, 480));

        // Panel raíz que contendrá todas las tarjetas.
        JPanel contentPane = new JPanel();

        // Margen exterior del contenido.
        contentPane.setBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        );

        // Layout de tarjetas para navegar entre vistas.
        contentPane.setLayout(new CardLayout());

        // Panel principal / menú.
        MainJPanel mainJPanel = new MainJPanel(contentPane);

        // Paneles de clientes.
        ClientesJPanel clientesJPanel = new ClientesJPanel(contentPane);

        AltaClientePanel altaClientePanel =
            new AltaClientePanel(contentPane, clientesJPanel);

        EditarClientePanel editarClientePanel =
            new EditarClientePanel(contentPane, clientesJPanel);

        // Paneles de productos.
        ProductosJPanel productosJPanel =
            new ProductosJPanel(contentPane);

        AltaProductoPanel altaProductoPanel =
            new AltaProductoPanel(contentPane, productosJPanel);

        EditarProductoPanel editarProductoPanel =
            new EditarProductoPanel(contentPane, productosJPanel);

        // Paneles de pedidos.
        PedidosListaPanel pedidosListaPanel =
            new PedidosListaPanel(contentPane);

        PedidoDetallePanel pedidoDetallePanel =
            new PedidoDetallePanel(contentPane, pedidosListaPanel);

        // Vincula la lista con el panel detalle.
        pedidosListaPanel.setPedidoDetallePanel(pedidoDetallePanel);

        NuevoPedidoPanel nuevoPedidoPanel =
            new NuevoPedidoPanel(
                contentPane,
                pedidosListaPanel,
                pedidoDetallePanel
            );

        // Registro de todas las tarjetas del CardLayout.
        contentPane.add(mainJPanel, "Menu principal");

        contentPane.add(clientesJPanel, "Clientes");
        contentPane.add(productosJPanel, "Productos");

        contentPane.add(
            altaClientePanel,
            AltaClientePanel.NOMBRE_TARJETA
        );

        contentPane.add(
            altaProductoPanel,
            AltaProductoPanel.NOMBRE_TARJETA
        );

        contentPane.add(
            editarClientePanel,
            EditarClientePanel.NOMBRE_TARJETA
        );

        contentPane.add(
            editarProductoPanel,
            EditarProductoPanel.NOMBRE_TARJETA
        );

        contentPane.add(
            pedidosListaPanel,
            PedidosListaPanel.NOMBRE_TARJETA
        );

        contentPane.add(
            nuevoPedidoPanel,
            NuevoPedidoPanel.NOMBRE_TARJETA
        );

        contentPane.add(
            pedidoDetallePanel,
            PedidoDetallePanel.NOMBRE_TARJETA
        );

        // Asignamos el panel raíz a la ventana.
        contentPane.setOpaque(true);
        frame.setContentPane(contentPane);

        // Ajusta tamaños automáticamente.
        frame.pack();

        // Muestra la ventana en pantalla.
        frame.setVisible(true);
    }

    /**
     * Punto de entrada de la aplicación.
     *
     * @param args argumentos de ejecución
     */
    public static void main(String[] args) {

        // Ejecuta la interfaz en el hilo de eventos de Swing.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                // Lanza la interfaz gráfica.
                GUI();
            }
        });
    }
}