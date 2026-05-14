package view.pedidos;

import src.dao.ClienteDAO;
import src.dao.ClienteDAOImpl;
import src.dao.PedidoDAO;
import src.dao.PedidoDAOImpl;
import src.model.Cliente;
import src.model.Pedido;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.HierarchyEvent;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/** Lista de pedidos abiertos y acceso a nuevo pedido / detalle. */
public class PedidosListaPanel extends JPanel {

    public static final String NOMBRE_TARJETA = "Pedidos";

    private static final PedidoDAO pedidoDAO = new PedidoDAOImpl();
    private static final ClienteDAO clienteDAO = new ClienteDAOImpl();

    private final JPanel tarjetas;
    private PedidoDetallePanel pedidoDetallePanel;

    private final JPanel listaPedidosContenedor;

    public PedidosListaPanel(JPanel tarjetas) {
        this.tarjetas = tarjetas;
        setLayout(new BorderLayout(0, 12));

        JPanel cabecera = new JPanel(new BorderLayout());

        JButton menuPrincipal = new JButton("Menu principal");
        menuPrincipal.addActionListener(e -> {
            CardLayout cl = (CardLayout) tarjetas.getLayout();
            cl.show(tarjetas, "Menu principal");
        });
        cabecera.add(menuPrincipal, BorderLayout.WEST);

        JLabel titulo = new JLabel("Pedidos");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        JPanel centroTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centroTitulo.add(titulo);
        cabecera.add(centroTitulo, BorderLayout.CENTER);

        JButton nuevoPedido = new JButton("Nuevo pedido");
        nuevoPedido.addActionListener(e -> {
            CardLayout cl = (CardLayout) tarjetas.getLayout();
            cl.show(tarjetas, NuevoPedidoPanel.NOMBRE_TARJETA);
        });
        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        derecha.add(nuevoPedido);
        cabecera.add(derecha, BorderLayout.EAST);

        add(cabecera, BorderLayout.NORTH);

        listaPedidosContenedor = new JPanel();
        listaPedidosContenedor.setLayout(new BoxLayout(listaPedidosContenedor, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(listaPedidosContenedor);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(24);
        add(scroll, BorderLayout.CENTER);

        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                refrescarLista();
            }
        });
    }

    public void setPedidoDetallePanel(PedidoDetallePanel pedidoDetallePanel) {
        this.pedidoDetallePanel = pedidoDetallePanel;
    }

    public void refrescarLista() {
        listaPedidosContenedor.removeAll();

        List<Pedido> abiertos = pedidoDAO.listarPedidosAbiertos();

        if (abiertos.isEmpty()) {
            JLabel vacio = new JLabel("No hay pedidos abiertos.");
            vacio.setAlignmentX(Component.LEFT_ALIGNMENT);
            listaPedidosContenedor.add(vacio);
        } else {
            for (Pedido p : abiertos) {
                Cliente cli = clienteDAO.buscarClientePorId(p.getClienteId());
                String nombreCliente = cli != null ? cli.getNombre() : ("#" + p.getClienteId());
                String texto = String.format(
                    "Pedido #%d – %s – %.2f €",
                    p.getId(),
                    nombreCliente,
                    p.getPrecio()
                );
                JButton btn = new JButton(texto);
                btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btn.getPreferredSize().height));
                btn.setAlignmentX(Component.LEFT_ALIGNMENT);
                int pid = p.getId();
                btn.addActionListener(ev -> abrirPedido(pid));
                listaPedidosContenedor.add(btn);
                listaPedidosContenedor.add(Box.createVerticalStrut(8));
            }
        }

        listaPedidosContenedor.revalidate();
        listaPedidosContenedor.repaint();
    }

    private void abrirPedido(int pedidoId) {
        if (pedidoDetallePanel != null) {
            pedidoDetallePanel.prepararParaPedido(pedidoId);
            CardLayout cl = (CardLayout) tarjetas.getLayout();
            cl.show(tarjetas, PedidoDetallePanel.NOMBRE_TARJETA);
        }
    }
}
