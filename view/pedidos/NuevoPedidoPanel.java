package view.pedidos;

import src.dao.ClienteDAO;
import src.dao.ClienteDAOImpl;
import src.dao.PedidoDAO;
import src.dao.PedidoDAOImpl;
import src.model.Cliente;
import src.model.Estado;
import src.model.Pedido;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.HierarchyEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** Selección de cliente antes de crear un pedido abierto. */
public class NuevoPedidoPanel extends JPanel {

    public static final String NOMBRE_TARJETA = "Nuevo pedido";

    private static final PedidoDAO pedidoDAO = new PedidoDAOImpl();
    private static final ClienteDAO clienteDAO = new ClienteDAOImpl();

    private final JPanel tarjetas;
    private final PedidosListaPanel listaPedidosPanel;
    private final PedidoDetallePanel pedidoDetallePanel;

    private final JComboBox<Cliente> comboCliente = new JComboBox<>();
    private final JLabel mensajeValidacion = new JLabel(" ");

    public NuevoPedidoPanel(JPanel tarjetas, PedidosListaPanel listaPedidosPanel,
            PedidoDetallePanel pedidoDetallePanel) {
        this.tarjetas = tarjetas;
        this.listaPedidosPanel = listaPedidosPanel;
        this.pedidoDetallePanel = pedidoDetallePanel;

        setLayout(new BorderLayout(8, 8));

        JPanel superior = new JPanel(new BorderLayout());
        JButton volver = new JButton("← Pedidos");
        volver.addActionListener(e -> volverLista());
        superior.add(volver, BorderLayout.WEST);

        JLabel titulo = new JLabel("Nuevo pedido");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        JPanel centroTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centroTitulo.add(titulo);
        superior.add(centroTitulo, BorderLayout.CENTER);

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

        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.add(Box.createVerticalGlue());
        JPanel wrapForm = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapForm.add(formulario);
        centro.add(wrapForm);
        centro.add(Box.createVerticalGlue());

        mensajeValidacion.setForeground(Color.RED);
        mensajeValidacion.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel inferior = new JPanel();
        inferior.setLayout(new BoxLayout(inferior, BoxLayout.Y_AXIS));
        JPanel lineaError = new JPanel(new FlowLayout(FlowLayout.CENTER));
        lineaError.add(mensajeValidacion);
        inferior.add(lineaError);

        JPanel botonera = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        JButton confirmar = new JButton("Confirmar");
        confirmar.addActionListener(e -> crearPedido());
        JButton cancelar = new JButton("Cancelar");
        cancelar.addActionListener(e -> volverLista());
        botonera.add(cancelar);
        botonera.add(confirmar);
        inferior.add(botonera);

        add(superior, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(inferior, BorderLayout.SOUTH);

        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                recargarClientes();
            }
        });
    }

    private void recargarClientes() {
        mensajeValidacion.setText(" ");
        comboCliente.removeAllItems();
        for (Cliente c : clienteDAO.listarClientes()) {
            comboCliente.addItem(c);
        }
        if (comboCliente.getItemCount() > 0) {
            comboCliente.setSelectedIndex(0);
        }
    }

    private void volverLista() {
        mensajeValidacion.setText(" ");
        CardLayout cl = (CardLayout) tarjetas.getLayout();
        cl.show(tarjetas, PedidosListaPanel.NOMBRE_TARJETA);
        listaPedidosPanel.refrescarLista();
    }

    private void crearPedido() {
        mensajeValidacion.setText(" ");
        Cliente sel = (Cliente) comboCliente.getSelectedItem();
        if (sel == null) {
            mensajeValidacion.setText("Selecciona un cliente.");
            return;
        }

        Pedido nuevo = new Pedido();
        nuevo.setClienteId(sel.getId());
        nuevo.setEstado(Estado.ABIERTO);

        int id = pedidoDAO.añadirPedido(nuevo);
        if (id <= 0) {
            mensajeValidacion.setText("No se pudo crear el pedido.");
            return;
        }

        listaPedidosPanel.refrescarLista();
        pedidoDetallePanel.prepararParaPedido(id);
        CardLayout cl = (CardLayout) tarjetas.getLayout();
        cl.show(tarjetas, PedidoDetallePanel.NOMBRE_TARJETA);
    }
}
