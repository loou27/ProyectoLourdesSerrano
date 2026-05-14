
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

public class MainJSwing {
        private static void GUI() {
        //Creamos la ventana de Windows
        JFrame frame = new JFrame("Cafetería Ayala");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension (720, 480));


        JPanel contentPane = new JPanel();

        contentPane.setBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        );
        //Creamos un cardLayout 
        contentPane.setLayout(new CardLayout());

        //Creamos el mainJPanel
        MainJPanel mainJPanel = new MainJPanel(contentPane);
        ClientesJPanel clientesJPanel = new ClientesJPanel(contentPane);
        ProductosJPanel productosJPanel = new ProductosJPanel(contentPane);
        AltaClientePanel altaClientePanel = new AltaClientePanel(contentPane, clientesJPanel);
        AltaProductoPanel altaProductoPanel = new AltaProductoPanel(contentPane, productosJPanel);
        EditarClientePanel editarClientePanel = new EditarClientePanel(contentPane, clientesJPanel);
        EditarProductoPanel editarProductoPanel = new EditarProductoPanel(contentPane, productosJPanel);

        PedidosListaPanel pedidosListaPanel = new PedidosListaPanel(contentPane);
        PedidoDetallePanel pedidoDetallePanel = new PedidoDetallePanel(contentPane, pedidosListaPanel);
        pedidosListaPanel.setPedidoDetallePanel(pedidoDetallePanel);
        NuevoPedidoPanel nuevoPedidoPanel = new NuevoPedidoPanel(contentPane, pedidosListaPanel, pedidoDetallePanel);

        contentPane.add(mainJPanel, "Menu principal");
        contentPane.add(clientesJPanel, "Clientes");
        contentPane.add(productosJPanel, "Productos");
        contentPane.add(altaClientePanel, AltaClientePanel.NOMBRE_TARJETA);
        contentPane.add(altaProductoPanel, AltaProductoPanel.NOMBRE_TARJETA);
        contentPane.add(editarClientePanel, EditarClientePanel.NOMBRE_TARJETA);
        contentPane.add(editarProductoPanel, EditarProductoPanel.NOMBRE_TARJETA);
        contentPane.add(pedidosListaPanel, PedidosListaPanel.NOMBRE_TARJETA);
        contentPane.add(nuevoPedidoPanel, NuevoPedidoPanel.NOMBRE_TARJETA);
        contentPane.add(pedidoDetallePanel, PedidoDetallePanel.NOMBRE_TARJETA);

        //Asignamos el JPanel a la ventana
        contentPane.setOpaque(true);
        frame.setContentPane(contentPane);

        //Mostramos la ventana
        frame.pack();
        frame.setVisible(true);
    }


    public static void main(String[] args) {
        //Creamos nuevo sub-proceso para poder escuchar eventos
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Mostramos la interfaz de usuario
                GUI(); 
            }
        });
    }
}
