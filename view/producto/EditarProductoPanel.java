package view.producto;

import src.dao.ProductoDAO;
import src.dao.ProductoDAOImpl;
import src.model.Producto;
import src.model.TipoProducto;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.HierarchyEvent;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** Modificación de producto: desplegable + campos editables + guardar (sin diálogos). */
public class EditarProductoPanel extends JPanel {

    public static final String NOMBRE_TARJETA = "Editar producto";

    private static final ProductoDAO productoDAO = new ProductoDAOImpl();

    private final JPanel tarjetas;
    private final ProductosJPanel productosPanel;

    private final JComboBox<Producto> comboProducto = new JComboBox<>();
    private final JTextField campoNombre = new JTextField(24);
    private final JTextField campoCantidad = new JTextField(8);
    private final JTextField campoPrecio = new JTextField(8);
    private final JComboBox<TipoProducto> comboTipo = new JComboBox<>(TipoProducto.values());
    private final JLabel mensajeValidacion = new JLabel(" ");

    public EditarProductoPanel(JPanel tarjetas, ProductosJPanel productosPanel) {
        this.tarjetas = tarjetas;
        this.productosPanel = productosPanel;

        comboProducto.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Producto p) {
                    setText(p.getId() + " – " + p.getNombre());
                }
                return this;
            }
        });

        setLayout(new BorderLayout(8, 8));

        JPanel superior = new JPanel(new BorderLayout());
        JButton volver = new JButton("← Productos");
        volver.addActionListener(e -> volverLista());
        superior.add(volver, BorderLayout.WEST);

        JLabel titulo = new JLabel("Actualizar producto");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        JPanel centroTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centroTitulo.add(titulo);
        superior.add(centroTitulo, BorderLayout.CENTER);

        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;
        gbc.gridx = 0;
        gbc.gridy = row;
        formulario.add(new JLabel("Producto:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(comboProducto, gbc);

        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        formulario.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(campoNombre, gbc);

        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        formulario.add(new JLabel("Cantidad:"), gbc);
        gbc.gridx = 1;
        formulario.add(campoCantidad, gbc);

        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        formulario.add(new JLabel("Precio:"), gbc);
        gbc.gridx = 1;
        formulario.add(campoPrecio, gbc);

        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        formulario.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        formulario.add(comboTipo, gbc);

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
        JButton guardar = new JButton("Guardar");
        guardar.addActionListener(e -> guardar());
        JButton cancelar = new JButton("Cancelar");
        cancelar.addActionListener(e -> volverLista());
        botonera.add(cancelar);
        botonera.add(guardar);
        inferior.add(botonera);

        add(superior, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(inferior, BorderLayout.SOUTH);

        comboProducto.addActionListener(e -> {
            if (comboProducto.getSelectedItem() != null) {
                mensajeValidacion.setText(" ");
                rellenarCamposDesdeSeleccion();
            }
        });

        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                recargarComboYCampos();
            }
        });

        campoNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, campoNombre.getPreferredSize().height));
        campoCantidad.setMaximumSize(new Dimension(Integer.MAX_VALUE, campoCantidad.getPreferredSize().height));
        campoPrecio.setMaximumSize(new Dimension(Integer.MAX_VALUE, campoPrecio.getPreferredSize().height));
    }

    private void recargarComboYCampos() {
        mensajeValidacion.setText(" ");
        comboProducto.removeAllItems();
        List<Producto> lista = productoDAO.listarProductos();
        for (Producto p : lista) {
            comboProducto.addItem(p);
        }
        if (lista.isEmpty()) {
            campoNombre.setText("");
            campoCantidad.setText("");
            campoPrecio.setText("");
            comboTipo.setSelectedIndex(0);
            mensajeValidacion.setText("No hay productos registrados.");
            return;
        }
        comboProducto.setSelectedIndex(0);
        rellenarCamposDesdeSeleccion();
    }

    private void rellenarCamposDesdeSeleccion() {
        Producto p = (Producto) comboProducto.getSelectedItem();
        if (p == null) {
            return;
        }
        campoNombre.setText(p.getNombre());
        campoCantidad.setText(String.valueOf(p.getCantidad()));
        campoPrecio.setText(String.valueOf(p.getPrecio()));
        comboTipo.setSelectedItem(p.getTipo());
    }

    private void volverLista() {
        mensajeValidacion.setText(" ");
        CardLayout cl = (CardLayout) tarjetas.getLayout();
        cl.show(tarjetas, "Productos");
    }

    private void guardar() {
        mensajeValidacion.setText(" ");
        Producto sel = (Producto) comboProducto.getSelectedItem();
        if (sel == null) {
            mensajeValidacion.setText("Selecciona un producto.");
            return;
        }

        String nombre = campoNombre.getText() != null ? campoNombre.getText().trim() : "";
        if (nombre.isEmpty()) {
            mensajeValidacion.setText("El nombre no puede estar vacío.");
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(campoCantidad.getText().trim());
            if (cantidad < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            mensajeValidacion.setText("La cantidad debe ser un número entero ≥ 0.");
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(campoPrecio.getText().trim().replace(',', '.'));
            if (precio < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            mensajeValidacion.setText("El precio debe ser un número ≥ 0.");
            return;
        }

        TipoProducto tipo = (TipoProducto) comboTipo.getSelectedItem();
        int idGuardado = sel.getId();
        productoDAO.modificarProducto(new Producto(idGuardado, nombre, cantidad, precio, tipo));
        productosPanel.refreshGrid();
        volverLista();
    }
}
