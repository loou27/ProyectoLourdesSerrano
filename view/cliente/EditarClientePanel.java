package view.cliente;

import src.dao.ClienteDAO;
import src.dao.ClienteDAOImpl;
import src.model.Cliente;
import src.model.TipoCliente;

import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.util.List;

import javax.swing.*;

/**
 * Esta pantalla sirve para editar clientes existentes.
 *
 * Flujo general:
 * 1. Se carga una lista de clientes en un desplegable
 * 2. El usuario selecciona uno
 * 3. Se rellenan automáticamente sus datos
 * 4. El usuario modifica nombre o tipo
 * 5. Se guarda en la base de datos
 */
public class EditarClientePanel extends JPanel {

    // Nombre que identifica esta pantalla dentro del CardLayout
    public static final String NOMBRE_TARJETA = "Editar cliente";

    // Acceso a la base de datos para modificar clientes
    private static final ClienteDAO clienteDAO = new ClienteDAOImpl();

    // Panel principal que contiene todas las pantallas
    private final JPanel tarjetas;

    // Panel de lista de clientes (para refrescar después de editar)
    private final ClientesJPanel clientesPanel;

    // Desplegable con todos los clientes existentes
    private final JComboBox<Cliente> comboCliente = new JComboBox<>();

    // Campo donde se edita el nombre del cliente
    private final JTextField campoNombre = new JTextField(24);

    // Desplegable para cambiar el tipo de cliente
    private final JComboBox<TipoCliente> comboTipo =
            new JComboBox<>(TipoCliente.values());

    // Mensajes de error o validación
    private final JLabel mensajeValidacion = new JLabel(" ");

    /**
     * Constructor: aquí se construye toda la interfaz de edición
     */
    public EditarClientePanel(JPanel tarjetas, ClientesJPanel clientesPanel) {
        this.tarjetas = tarjetas;
        this.clientesPanel = clientesPanel;

        // Layout general de la pantalla
        setLayout(new BorderLayout(8, 8));

        /*
         * =========================
         * PARTE SUPERIOR (CABECERA)
         * =========================
         */
        JPanel superior = new JPanel(new BorderLayout());

        JButton volver = new JButton("← Clientes");
        volver.addActionListener(e -> volverLista());
        superior.add(volver, BorderLayout.WEST);

        JLabel titulo = new JLabel("Actualizar cliente");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));

        JPanel centroTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centroTitulo.add(titulo);
        superior.add(centroTitulo, BorderLayout.CENTER);

        /*
         * =========================
         * FORMULARIO DE EDICIÓN
         * =========================
         */
        JPanel formulario = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        /*
         * Fila 1: selector de cliente
         * Aquí eliges qué cliente quieres editar
         */
        gbc.gridx = 0;
        gbc.gridy = row;
        formulario.add(new JLabel("Cliente:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(comboCliente, gbc);

        /*
         * Fila 2: nombre editable
         */
        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        formulario.add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(campoNombre, gbc);

        /*
         * Fila 3: tipo de cliente
         */
        row++;
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        formulario.add(new JLabel("Tipo:"), gbc);

        gbc.gridx = 1;
        formulario.add(comboTipo, gbc);

        /*
         * Centrado visual del formulario
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
         * MENSAJES DE ERROR
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

        JButton guardar = new JButton("Guardar");
        guardar.addActionListener(e -> guardar());

        JButton cancelar = new JButton("Cancelar");
        cancelar.addActionListener(e -> volverLista());

        botonera.add(cancelar);
        botonera.add(guardar);

        inferior.add(botonera);

        /*
         * Añadir todo al panel principal
         */
        add(superior, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(inferior, BorderLayout.SOUTH);

        /*
         * =========================
         * CUANDO SE ABRE LA PANTALLA
         * =========================
         * Se recarga la lista de clientes automáticamente
         */
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                recargarComboYCampos();
            }
        });

        /*
         * Cuando cambias de cliente en el combo,
         * se actualizan los campos automáticamente
         */
        comboCliente.addActionListener(e -> {
            if (comboCliente.getSelectedItem() != null) {
                mensajeValidacion.setText(" ");
                rellenarCamposDesdeSeleccion();
            }
        });

        /*
         * Ajuste visual del campo de texto
         */
        campoNombre.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, campoNombre.getPreferredSize().height)
        );
    }

    /**
     * Recarga la lista de clientes desde la base de datos
     * y actualiza el desplegable
     */
    private void recargarComboYCampos() {

        mensajeValidacion.setText(" ");

        comboCliente.removeAllItems();

        List<Cliente> lista = clienteDAO.listarClientes();

        // Se añaden todos los clientes al desplegable
        for (Cliente c : lista) {
            comboCliente.addItem(c);
        }

        // Si no hay clientes, se muestra mensaje
        if (lista.isEmpty()) {
            campoNombre.setText("");
            comboTipo.setSelectedIndex(0);
            mensajeValidacion.setText("No hay clientes registrados.");
            return;
        }

        // Selecciona el primero automáticamente
        comboCliente.setSelectedIndex(0);

        // Rellena los campos con el cliente seleccionado
        rellenarCamposDesdeSeleccion();
    }

    /**
     * Rellena los campos del formulario con el cliente seleccionado
     */
    private void rellenarCamposDesdeSeleccion() {

        Cliente c = (Cliente) comboCliente.getSelectedItem();

        if (c == null) {
            return;
        }

        campoNombre.setText(c.getNombre());
        comboTipo.setSelectedItem(c.getTipoCliente());
    }

    /**
     * Vuelve a la pantalla principal de clientes
     */
    private void volverLista() {
        mensajeValidacion.setText(" ");
        CardLayout cl = (CardLayout) tarjetas.getLayout();
        cl.show(tarjetas, "Clientes");
    }

    /**
     * Guarda los cambios del cliente en la base de datos
     */
    private void guardar() {

        mensajeValidacion.setText(" ");

        // Cliente seleccionado en el combo
        Cliente sel = (Cliente) comboCliente.getSelectedItem();

        if (sel == null) {
            mensajeValidacion.setText("Selecciona un cliente.");
            return;
        }

        // Validación del nombre
        String nombre = campoNombre.getText() != null
                ? campoNombre.getText().trim()
                : "";

        if (nombre.isEmpty()) {
            mensajeValidacion.setText("El nombre no puede estar vacío.");
            return;
        }

        // Tipo seleccionado
        TipoCliente tipo = (TipoCliente) comboTipo.getSelectedItem();

        // Se conserva el ID original del cliente
        int idGuardado = sel.getId();

        // Se actualiza en la base de datos
        clienteDAO.modificarCliente(
                new Cliente(idGuardado, nombre, tipo)
        );

        // Se refresca la lista de clientes en la pantalla principal
        clientesPanel.refreshGrid();

        // Se vuelve a la lista
        volverLista();
    }
}