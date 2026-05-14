package view.pedidos;

import src.dao.ClienteDAO;
import src.dao.ClienteDAOImpl;
import src.dao.PedidoDAO;
import src.dao.PedidoDAOImpl;
import src.model.Cliente;
import src.model.Estado;
import src.model.Pedido;

import java.awt.*;
import java.awt.event.HierarchyEvent;

import javax.swing.*;

/**
 * Pantalla de creación de un nuevo pedido.
 *
 * IDEA GENERAL:
 * Aquí no se añade contenido del pedido todavía.
 * Solo se selecciona el cliente que va a tener el pedido.
 *
 * Después:
 * - se crea el pedido en base de datos
 * - se abre la pantalla de detalle del pedido
 */
public class NuevoPedidoPanel extends JPanel {

    // Nombre de la tarjeta para el CardLayout
    public static final String NOMBRE_TARJETA = "Nuevo pedido";

    // DAO para crear pedidos en base de datos
    private static final PedidoDAO pedidoDAO = new PedidoDAOImpl();

    // DAO para obtener clientes
    private static final ClienteDAO clienteDAO = new ClienteDAOImpl();

    // Panel general de navegación (CardLayout)
    private final JPanel tarjetas;

    // Panel de lista de pedidos (para refrescar cuando se crea uno nuevo)
    private final PedidosListaPanel listaPedidosPanel;

    // Panel de detalle del pedido (donde se añaden productos después)
    private final PedidoDetallePanel pedidoDetallePanel;

    // Desplegable de clientes
    private final JComboBox<Cliente> comboCliente = new JComboBox<>();

    // Mensajes de error o validación
    private final JLabel mensajeValidacion = new JLabel(" ");

    /**
     * Constructor: construye toda la interfaz de creación de pedido
     */
    public NuevoPedidoPanel(
            JPanel tarjetas,
            PedidosListaPanel listaPedidosPanel,
            PedidoDetallePanel pedidoDetallePanel
    ) {
        this.tarjetas = tarjetas;
        this.listaPedidosPanel = listaPedidosPanel;
        this.pedidoDetallePanel = pedidoDetallePanel;

        setLayout(new BorderLayout(8, 8));

        /*
         * =========================
         * CABECERA
         * =========================
         */
        JPanel superior = new JPanel(new BorderLayout());

        JButton volver = new JButton("← Pedidos");
        volver.addActionListener(e -> volverLista());
        superior.add(volver, BorderLayout.WEST);

        JLabel titulo = new JLabel("Nuevo pedido");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));

        JPanel centroTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centroTitulo.add(titulo);
        superior.add(centroTitulo, BorderLayout.CENTER);

        /*
         * =========================
         * FORMULARIO (solo cliente)
         * =========================
         *
         * Aquí solo se elige el cliente porque:
         * el pedido aún no existe en base de datos
         */
        JPanel formulario = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formulario.add(new JLabel("Cliente:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(comboCliente, gbc);

        /*
         * =========================
         * CENTRADO VISUAL
         * =========================
         */
        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        centro.add(Box.createVerticalGlue());

        JPanel wrapForm = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapForm.add(formulario);

        centro.add(wrapForm);

        centro.add(Box.createVerticalGlue());

        /*
         * =========================
         * MENSAJES
         * =========================
         */
        mensajeValidacion.setForeground(Color.RED);
        mensajeValidacion.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel inferior = new JPanel();
        inferior.setLayout(new BoxLayout(inferior, BoxLayout.Y_AXIS));

        JPanel lineaError = new JPanel(new FlowLayout(FlowLayout.CENTER));
        lineaError.add(mensajeValidacion);

        inferior.add(lineaError);

        /*
         * =========================
         * BOTONES
         * =========================
         */
        JPanel botonera = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));

        JButton confirmar = new JButton("Confirmar");
        confirmar.addActionListener(e -> crearPedido());

        JButton cancelar = new JButton("Cancelar");
        cancelar.addActionListener(e -> volverLista());

        botonera.add(cancelar);
        botonera.add(confirmar);

        inferior.add(botonera);

        /*
         * Añadir todo a la pantalla
         */
        add(superior, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(inferior, BorderLayout.SOUTH);

        /*
         * =========================
         * CUANDO SE ABRE LA PANTALLA
         * =========================
         * Se cargan los clientes automáticamente
         */
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                recargarClientes();
            }
        });
    }

    /**
     * Carga todos los clientes desde la base de datos
     * y los mete en el desplegable
     */
    private void recargarClientes() {

        mensajeValidacion.setText(" ");

        comboCliente.removeAllItems();

        for (Cliente c : clienteDAO.listarClientes()) {
            comboCliente.addItem(c);
        }

        // Selecciona el primero si existe
        if (comboCliente.getItemCount() > 0) {
            comboCliente.setSelectedIndex(0);
        }
    }

    /**
     * Vuelve a la lista de pedidos
     */
    private void volverLista() {

        mensajeValidacion.setText(" ");

        CardLayout cl = (CardLayout) tarjetas.getLayout();

        cl.show(tarjetas, PedidosListaPanel.NOMBRE_TARJETA);

        // Refresca lista por si hay cambios
        listaPedidosPanel.refrescarLista();
    }

    /**
     * CREA EL PEDIDO EN BASE DE DATOS
     *
     * Este es el punto clave de toda la pantalla:
     * aquí el pedido pasa de "idea" a "registro real"
     */
    private void crearPedido() {

        mensajeValidacion.setText(" ");

        Cliente sel = (Cliente) comboCliente.getSelectedItem();

        if (sel == null) {
            mensajeValidacion.setText("Selecciona un cliente.");
            return;
        }

        // Se crea el objeto pedido (aún no tiene productos)
        Pedido nuevo = new Pedido();

        nuevo.setClienteId(sel.getId());

        // El pedido empieza siempre como ABIERTO
        nuevo.setEstado(Estado.ABIERTO);

        // Se guarda en base de datos y devuelve el ID generado
        int id = pedidoDAO.añadirPedido(nuevo);

        // Si falla la creación
        if (id <= 0) {
            mensajeValidacion.setText("No se pudo crear el pedido.");
            return;
        }

        // Se actualiza la lista de pedidos
        listaPedidosPanel.refrescarLista();

        // Se prepara el panel de detalle para este pedido recién creado
        pedidoDetallePanel.prepararParaPedido(id);

        // Se abre la pantalla del pedido
        CardLayout cl = (CardLayout) tarjetas.getLayout();

        cl.show(tarjetas, PedidoDetallePanel.NOMBRE_TARJETA);
    }
}