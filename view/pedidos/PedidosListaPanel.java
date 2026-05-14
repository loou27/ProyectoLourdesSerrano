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

/** 
 * Lista de pedidos abiertos y acceso a nuevo pedido / detalle.
 * 
 * Este panel es la “pantalla principal” dentro del módulo de pedidos:
 * - Muestra todos los pedidos abiertos
 * - Permite crear uno nuevo
 * - Permite abrir un pedido existente para verlo/editarlo
 */
public class PedidosListaPanel extends JPanel {

    // Nombre de la tarjeta en el CardLayout principal
    // Se usa para cambiar entre pantallas (Clientes, Pedidos, etc.)
    public static final String NOMBRE_TARJETA = "Pedidos";

    // DAO para acceder a pedidos en base de datos
    private static final PedidoDAO pedidoDAO = new PedidoDAOImpl();

    // DAO para obtener información de clientes asociados a pedidos
    private static final ClienteDAO clienteDAO = new ClienteDAOImpl();

    // Referencia al contenedor principal con CardLayout
    // (la ventana “madre” que contiene todas las pantallas)
    private final JPanel tarjetas;

    // Referencia al panel de detalle de pedido
    // Se usa para abrir un pedido seleccionado desde la lista
    private PedidoDetallePanel pedidoDetallePanel;

    // Panel que contiene dinámicamente los botones de pedidos
    // Se rellena cada vez que se refresca la lista
    private final JPanel listaPedidosContenedor;

    /**
     * Constructor principal del panel de lista de pedidos.
     * @param tarjetas panel principal con CardLayout
     */
    public PedidosListaPanel(JPanel tarjetas) {
        this.tarjetas = tarjetas;

        // Layout general: cabecera arriba + lista scroll en el centro
        setLayout(new BorderLayout(0, 12));

        /*
         * ===================== CABECERA =====================
         * Contiene:
         * - Botón "Menu principal"
         * - Título "Pedidos"
         * - Botón "Nuevo pedido"
         */
        JPanel cabecera = new JPanel(new BorderLayout());

        // Botón que vuelve al menú principal del sistema
        JButton menuPrincipal = new JButton("Menu principal");
        menuPrincipal.addActionListener(e -> {
            CardLayout cl = (CardLayout) tarjetas.getLayout();
            cl.show(tarjetas, "Menu principal");
        });
        cabecera.add(menuPrincipal, BorderLayout.WEST);

        // Título centrado
        JLabel titulo = new JLabel("Pedidos");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
        JPanel centroTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centroTitulo.add(titulo);
        cabecera.add(centroTitulo, BorderLayout.CENTER);

        // Botón para ir a pantalla de crear nuevo pedido
        JButton nuevoPedido = new JButton("Nuevo pedido");
        nuevoPedido.addActionListener(e -> {
            CardLayout cl = (CardLayout) tarjetas.getLayout();
            cl.show(tarjetas, NuevoPedidoPanel.NOMBRE_TARJETA);
        });

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        derecha.add(nuevoPedido);
        cabecera.add(derecha, BorderLayout.EAST);

        add(cabecera, BorderLayout.NORTH);

        /*
         * ===================== LISTA DE PEDIDOS =====================
         * Este panel contiene botones dinámicos (uno por pedido abierto)
         * Se mete dentro de un JScrollPane para poder hacer scroll.
         */
        listaPedidosContenedor = new JPanel();
        listaPedidosContenedor.setLayout(new BoxLayout(listaPedidosContenedor, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(listaPedidosContenedor);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(24);

        add(scroll, BorderLayout.CENTER);

        /*
         * Cuando el panel se vuelve visible en pantalla:
         * se recarga la lista de pedidos automáticamente.
         */
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                refrescarLista();
            }
        });
    }

    /**
     * Se inyecta el panel de detalle desde fuera.
     * Esto evita dependencias circulares en el constructor.
     */
    public void setPedidoDetallePanel(PedidoDetallePanel pedidoDetallePanel) {
        this.pedidoDetallePanel = pedidoDetallePanel;
    }

    /**
     * Refresca la lista de pedidos abiertos.
     * - Borra botones antiguos
     * - Consulta DAO
     * - Crea un botón por pedido
     */
    public void refrescarLista() {
        listaPedidosContenedor.removeAll();

        // Pedidos con estado ABIERTO en base de datos
        List<Pedido> abiertos = pedidoDAO.listarPedidosAbiertos();

        // Si no hay pedidos, mostramos mensaje simple
        if (abiertos.isEmpty()) {
            JLabel vacio = new JLabel("No hay pedidos abiertos.");
            vacio.setAlignmentX(Component.LEFT_ALIGNMENT);
            listaPedidosContenedor.add(vacio);
        } else {

            // Por cada pedido, creamos un botón
            for (Pedido p : abiertos) {

                // Buscamos cliente asociado al pedido
                Cliente cli = clienteDAO.buscarClientePorId(p.getClienteId());

                // Texto del botón con info resumida
                String nombreCliente = cli != null ? cli.getNombre() : ("#" + p.getClienteId());

                String texto = String.format(
                    "Pedido #%d – %s – %.2f €",
                    p.getId(),
                    nombreCliente,
                    p.getPrecio()
                );

                JButton btn = new JButton(texto);

                // El botón ocupa todo el ancho disponible
                btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btn.getPreferredSize().height));
                btn.setAlignmentX(Component.LEFT_ALIGNMENT);

                // Capturamos id del pedido para el listener
                int pid = p.getId();

                // Al hacer click, abrimos el pedido en el panel detalle
                btn.addActionListener(ev -> abrirPedido(pid));

                listaPedidosContenedor.add(btn);

                // Espaciado entre botones
                listaPedidosContenedor.add(Box.createVerticalStrut(8));
            }
        }

        // Actualizamos UI Swing
        listaPedidosContenedor.revalidate();
        listaPedidosContenedor.repaint();
    }

    /**
     * Abre un pedido concreto en el panel de detalle.
     * Cambia la vista en el CardLayout principal.
     */
    private void abrirPedido(int pedidoId) {
        if (pedidoDetallePanel != null) {

            // Prepara el panel detalle con el pedido seleccionado
            pedidoDetallePanel.prepararParaPedido(pedidoId);

            // Cambia la tarjeta visible al detalle del pedido
            CardLayout cl = (CardLayout) tarjetas.getLayout();
            cl.show(tarjetas, PedidoDetallePanel.NOMBRE_TARJETA);
        }
    }
}