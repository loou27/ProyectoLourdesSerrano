package view.pedidos;

import view.comun.AbstractCatalogoProductosPanel;

import src.dao.ClienteDAO;
import src.dao.ClienteDAOImpl;
import src.dao.PedidoDAO;
import src.dao.PedidoDAOImpl;
import src.dao.PedidoProductoDAO;
import src.dao.PedidoProductoDAOImpl;
import src.dao.ProductoDAO;
import src.dao.ProductoDAOImpl;
import src.model.Cliente;
import src.model.LineaPedidoDetalle;
import src.model.MetodoPago;
import src.model.Pedido;
import src.model.Producto;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

/** Detalle de un pedido: catálogo, líneas y flujo de cobro en efectivo al cerrar. */
public class PedidoDetallePanel extends JPanel {

    public static final String NOMBRE_TARJETA = "Detalle pedido";

    private static final String CARD_EDITAR = "editar";
    private static final String CARD_COBRO = "cobro";

    private static final PedidoDAO pedidoDAO = new PedidoDAOImpl();
    private static final PedidoProductoDAO pedidoProductoDAO = new PedidoProductoDAOImpl();
    private static final ProductoDAO productoDAO = new ProductoDAOImpl();
    private static final ClienteDAO clienteDAO = new ClienteDAOImpl();

    private final JPanel tarjetas;
    private final PedidosListaPanel listaPedidosPanel;

    private int pedidoIdActivo = -1;

    private final AbstractCatalogoProductosPanel catalogo;
    private final DefaultTableModel tablaModel;
    private final JLabel totalLabel;
    private final JLabel cabeceraLabel;

    private final JPanel centroCards;
    private final CardLayout centroCardLayout;

    private final JLabel cobroTotalLabel;
    private final JTextField importeEntregadoField;
    private final JLabel cambioLabel;
    private final JLabel mensajeCobroLabel;

    public PedidoDetallePanel(JPanel tarjetas, PedidosListaPanel listaPedidosPanel) {
        this.tarjetas = tarjetas;
        this.listaPedidosPanel = listaPedidosPanel;

        cobroTotalLabel = new JLabel("Total a pagar: 0,00 €", SwingConstants.CENTER);
        cobroTotalLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));

        importeEntregadoField = new JTextField(12);
        importeEntregadoField.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 18));

        cambioLabel = new JLabel("Cambio: —", SwingConstants.CENTER);
        cambioLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));

        mensajeCobroLabel = new JLabel(" ");
        mensajeCobroLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        mensajeCobroLabel.setHorizontalAlignment(SwingConstants.CENTER);

        setLayout(new BorderLayout(8, 8));

        JPanel norte = new JPanel(new BorderLayout());
        JButton volver = new JButton("← Pedidos");
        volver.addActionListener(e -> volverLista());
        norte.add(volver, BorderLayout.WEST);

        cabeceraLabel = new JLabel(" ");
        cabeceraLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        JPanel centroTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centroTitulo.add(cabeceraLabel);
        norte.add(centroTitulo, BorderLayout.CENTER);

        add(norte, BorderLayout.NORTH);

        JButton cerrarPedidoBtn = new JButton("Cerrar pedido");
        cerrarPedidoBtn.addActionListener(e -> irAPanelCobro());

        catalogo = new AbstractCatalogoProductosPanel() {
            @Override
            protected boolean usarRejillaAdaptableAncho() {
                return true;
            }

            @Override
            protected void alSeleccionarProducto(Producto producto) {
                if (pedidoIdActivo <= 0) {
                    return;
                }
                pedidoProductoDAO.añadirOIncrementarLinea(pedidoIdActivo, producto.getId(), 1);
                pedidoDAO.actualizarPrecioTotalPedido(pedidoIdActivo);
                refrescarResumen();
            }
        };
        catalogo.setMinimumSize(new Dimension(180, 120));

        tablaModel = new DefaultTableModel(
                new String[]{"Producto", "Cantidad", "P. unitario (€)", "Subtotal (€)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabla = new JTable(tablaModel);
        tabla.setFillsViewportHeight(true);

        totalLabel = new JLabel("Total: 0,00 €");
        totalLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));

        JPanel panelDerecho = new JPanel(new BorderLayout(0, 8));
        panelDerecho.add(new JScrollPane(tabla), BorderLayout.CENTER);
        JPanel pieTotal = new JPanel(new BorderLayout(8, 0));
        pieTotal.add(cerrarPedidoBtn, BorderLayout.WEST);
        JPanel derechaTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        derechaTotal.add(totalLabel);
        pieTotal.add(derechaTotal, BorderLayout.EAST);
        panelDerecho.add(pieTotal, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, catalogo, panelDerecho);
        split.setResizeWeight(0.52);
        split.setBorder(null);

        JPanel panelEditar = new JPanel(new BorderLayout());
        panelEditar.add(split, BorderLayout.CENTER);

        centroCardLayout = new CardLayout();
        centroCards = new JPanel(centroCardLayout);
        centroCards.add(panelEditar, CARD_EDITAR);
        centroCards.add(construirPanelCobro(), CARD_COBRO);

        add(centroCards, BorderLayout.CENTER);

        importeEntregadoField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                limpiarMensajeCobro();
                actualizarCambio();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                limpiarMensajeCobro();
                actualizarCambio();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                limpiarMensajeCobro();
                actualizarCambio();
            }
        });
    }

    private JPanel construirPanelCobro() {
        JPanel root = new JPanel(new BorderLayout(0, 24));
        JPanel centro = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(12, 16, 12, 16);

        gbc.gridy = 0;
        centro.add(cobroTotalLabel, gbc);

        gbc.gridy = 1;
        JPanel filaImporte = new JPanel(new FlowLayout(FlowLayout.CENTER));
        filaImporte.add(new JLabel("Importe entregado (€):"));
        filaImporte.add(importeEntregadoField);
        centro.add(filaImporte, gbc);

        gbc.gridy = 2;
        centro.add(cambioLabel, gbc);

        gbc.gridy = 3;
        centro.add(mensajeCobroLabel, gbc);

        root.add(centro, BorderLayout.CENTER);

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        JButton volverEditar = new JButton("Volver al pedido");
        volverEditar.addActionListener(e -> volverAEditarPedido());
        JButton confirmar = new JButton("Confirmar y cerrar pedido");
        confirmar.addActionListener(e -> confirmarCierreEfectivo());
        sur.add(volverEditar);
        sur.add(confirmar);
        root.add(sur, BorderLayout.SOUTH);

        return root;
    }

    public void prepararParaPedido(int pedidoId) {
        pedidoIdActivo = pedidoId;
        centroCardLayout.show(centroCards, CARD_EDITAR);

        Pedido p = pedidoDAO.buscarPedidoPorId(pedidoId);
        if (p != null) {
            Cliente cli = clienteDAO.buscarClientePorId(p.getClienteId());
            String nombreCliente = cli != null ? cli.getNombre() : ("Cliente #" + p.getClienteId());
            cabeceraLabel.setText("Pedido #" + p.getId() + " – " + nombreCliente);
        } else {
            cabeceraLabel.setText("Pedido #" + pedidoId);
        }
        catalogo.refrescarCatalogo(productoDAO.listarProductos());
        refrescarResumen();
        importeEntregadoField.setText("");
        limpiarMensajeCobro();
        actualizarCambio();
    }

    private void limpiarMensajeCobro() {
        mensajeCobroLabel.setText(" ");
        mensajeCobroLabel.setForeground(null);
    }

    private void mostrarErrorCobro(String texto) {
        mensajeCobroLabel.setForeground(new Color(180, 0, 0));
        mensajeCobroLabel.setText(texto);
    }

    private void irAPanelCobro() {
        if (pedidoIdActivo <= 0) {
            return;
        }
        pedidoDAO.actualizarPrecioTotalPedido(pedidoIdActivo);
        Pedido p = pedidoDAO.buscarPedidoPorId(pedidoIdActivo);
        double total = p != null ? p.getPrecio() : 0;
        cobroTotalLabel.setText(String.format("Total a pagar: %.2f €", total));
        importeEntregadoField.setText("");
        limpiarMensajeCobro();
        actualizarCambio();
        centroCardLayout.show(centroCards, CARD_COBRO);
    }

    private void volverAEditarPedido() {
        centroCardLayout.show(centroCards, CARD_EDITAR);
    }

    private void actualizarCambio() {
        if (pedidoIdActivo <= 0) {
            cambioLabel.setText("Cambio: —");
            return;
        }
        Pedido p = pedidoDAO.buscarPedidoPorId(pedidoIdActivo);
        double total = p != null ? p.getPrecio() : 0;
        double entregado = parseImporteEuros(importeEntregadoField.getText());
        if (Double.isNaN(entregado) || importeEntregadoField.getText().isBlank()) {
            cambioLabel.setText("Cambio: —");
            return;
        }
        double cambio = entregado - total;
        cambioLabel.setText(String.format("Cambio: %.2f €", cambio));
    }

    private static double parseImporteEuros(String texto) {
        if (texto == null) {
            return Double.NaN;
        }
        String t = texto.trim().replace(',', '.');
        if (t.isEmpty()) {
            return Double.NaN;
        }
        try {
            return Double.parseDouble(t);
        } catch (NumberFormatException ex) {
            return Double.NaN;
        }
    }

    private void confirmarCierreEfectivo() {
        if (pedidoIdActivo <= 0) {
            return;
        }
        pedidoDAO.actualizarPrecioTotalPedido(pedidoIdActivo);
        Pedido p = pedidoDAO.buscarPedidoPorId(pedidoIdActivo);
        double total = p != null ? p.getPrecio() : 0;

        double entregado = parseImporteEuros(importeEntregadoField.getText());
        if (Double.isNaN(entregado)) {
            mostrarErrorCobro("Introduce un importe válido.");
            return;
        }
        if (entregado + 1e-6 < total) {
            mostrarErrorCobro(String.format(
                "Importe insuficiente: entregado %.2f €, total %.2f €.", entregado, total));
            return;
        }

        pedidoDAO.cerrarPedido(pedidoIdActivo, MetodoPago.EFECTIVO);

        limpiarMensajeCobro();

        pedidoIdActivo = -1;
        listaPedidosPanel.refrescarLista();
        CardLayout cl = (CardLayout) tarjetas.getLayout();
        cl.show(tarjetas, PedidosListaPanel.NOMBRE_TARJETA);
        centroCardLayout.show(centroCards, CARD_EDITAR);
    }

    private void refrescarResumen() {
        tablaModel.setRowCount(0);
        if (pedidoIdActivo <= 0) {
            totalLabel.setText("Total: 0,00 €");
            return;
        }

        for (LineaPedidoDetalle linea : pedidoProductoDAO.listarLineasPedido(pedidoIdActivo)) {
            tablaModel.addRow(new Object[]{
                linea.getNombreProducto(),
                linea.getCantidad(),
                String.format("%.2f", linea.getPrecioUnitario()),
                String.format("%.2f", linea.getSubtotal())
            });
        }

        Pedido ped = pedidoDAO.buscarPedidoPorId(pedidoIdActivo);
        if (ped != null) {
            totalLabel.setText(String.format("Total: %.2f €", ped.getPrecio()));
        }
    }

    private void volverLista() {
        centroCardLayout.show(centroCards, CARD_EDITAR);
        listaPedidosPanel.refrescarLista();
        CardLayout cl = (CardLayout) tarjetas.getLayout();
        cl.show(tarjetas, PedidosListaPanel.NOMBRE_TARJETA);
    }
}
