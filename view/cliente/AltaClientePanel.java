package view.cliente;

import src.dao.ClienteDAO;
import src.dao.ClienteDAOImpl;
import src.model.Cliente;
import src.model.TipoCliente;

import java.awt.*;
import java.awt.event.HierarchyEvent;

import javax.swing.*;

/**
 * Esta clase representa la pantalla de "Alta de cliente".
 * Es decir, el formulario donde el usuario crea un cliente nuevo.
 *
 * No abre una ventana nueva: se muestra dentro de un sistema de "pestañas" (CardLayout).
 */
public class AltaClientePanel extends JPanel {

    // Nombre que identifica esta pantalla dentro del CardLayout
    public static final String NOMBRE_TARJETA = "Alta cliente";

    // Acceso a la base de datos para guardar clientes
    private static final ClienteDAO clienteDAO = new ClienteDAOImpl();

    // Panel principal que contiene todas las pantallas (clientes, alta, etc.)
    private final JPanel tarjetas;

    // Panel de la lista de clientes (para actualizarlo después de guardar)
    private final ClientesJPanel clientesPanel;

    // Campo donde el usuario escribe el nombre del cliente
    private final JTextField campoNombre = new JTextField(24);

    // Desplegable donde se elige el tipo de cliente (alumno, profesor, etc.)
    private final JComboBox<TipoCliente> comboTipo = new JComboBox<>(TipoCliente.values());

    // Etiqueta donde se muestran mensajes de error o validación
    private final JLabel mensajeValidacion = new JLabel(" ");

    /**
     * Constructor: aquí se construye toda la pantalla visual
     *
     * @param tarjetas panel principal con todas las pantallas (CardLayout)
     * @param clientesPanel pantalla de lista de clientes (para refrescarla)
     */
    public AltaClientePanel(JPanel tarjetas, ClientesJPanel clientesPanel) {
        this.tarjetas = tarjetas;
        this.clientesPanel = clientesPanel;

        // Layout general de la pantalla: arriba, centro y abajo
        setLayout(new BorderLayout(8, 8));

        /*
         * =========================
         * PARTE SUPERIOR (HEADER)
         * =========================
         * Botón de volver + título
         */
        JPanel superior = new JPanel(new BorderLayout());

        // Botón para volver a la lista de clientes
        JButton volver = new JButton("← Clientes");
        volver.addActionListener(e -> volverLista());

        superior.add(volver, BorderLayout.WEST);

        // Título centrado de la pantalla
        JLabel titulo = new JLabel("Nuevo cliente");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));

        JPanel centroTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centroTitulo.add(titulo);

        superior.add(centroTitulo, BorderLayout.CENTER);

        /*
         * =========================
         * PARTE CENTRAL (FORMULARIO)
         * =========================
         * Aquí se introducen los datos del cliente
         */
        JPanel formulario = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        // Espaciado interno entre elementos del formulario
        gbc.insets = new Insets(6, 8, 6, 8);

        // Alineación a la izquierda por defecto
        gbc.anchor = GridBagConstraints.WEST;

        /*
         * Fila 1: Nombre
         */

        gbc.gridx = 0;
        gbc.gridy = 0;
        formulario.add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(campoNombre, gbc);

        /*
         * Fila 2: Tipo de cliente
         */

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        formulario.add(new JLabel("Tipo:"), gbc);

        gbc.gridx = 1;
        formulario.add(comboTipo, gbc);

        /*
         * Contenedor para centrar el formulario en la pantalla
         */
        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        // Espacio flexible arriba
        centro.add(Box.createVerticalGlue());

        JPanel wrapForm = new JPanel(new FlowLayout(FlowLayout.CENTER));
        wrapForm.add(formulario);

        centro.add(wrapForm);

        // Espacio flexible abajo
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
         * BOTONES (GUARDAR / CANCELAR)
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
         * =========================
         * AÑADIR PARTES A LA PANTALLA
         * =========================
         */
        add(superior, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
        add(inferior, BorderLayout.SOUTH);

        /*
         * =========================
         * LIMPIEZA AUTOMÁTICA
         * =========================
         * Cuando esta pantalla se muestra, se resetea el formulario
         */
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                limpiarFormulario();
            }
        });

        /*
         * Hace que el campo de texto se adapte al tamaño del panel
         */
        campoNombre.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, campoNombre.getPreferredSize().height)
        );
    }

    /**
     * Limpia el formulario para que no queden datos anteriores
     */
    private void limpiarFormulario() {
        campoNombre.setText("");
        comboTipo.setSelectedIndex(0);
        mensajeValidacion.setText(" ");
    }

    /**
     * Vuelve a la pantalla de lista de clientes
     */
    private void volverLista() {
        mensajeValidacion.setText(" ");
        CardLayout cl = (CardLayout) tarjetas.getLayout();
        cl.show(tarjetas, "Clientes");
    }

    /**
     * Guarda el cliente en la base de datos
     * Este es el método principal de la pantalla
     */
    private void guardar() {

        // Limpia mensajes anteriores
        mensajeValidacion.setText(" ");

        // Lee el nombre escrito por el usuario
        String nombre = campoNombre.getText() != null
                ? campoNombre.getText().trim()
                : "";

        // Validación: el nombre no puede estar vacío
        if (nombre.isEmpty()) {
            mensajeValidacion.setText("El nombre no puede estar vacío.");
            return;
        }

        // Se obtiene el tipo seleccionado en el desplegable
        TipoCliente tipo = (TipoCliente) comboTipo.getSelectedItem();

        // Se crea el objeto Cliente y se guarda en la base de datos
        clienteDAO.añadirCliente(new Cliente(nombre, tipo));

        // Se actualiza la lista de clientes para que aparezca el nuevo
        clientesPanel.refreshGrid();

        // Se vuelve a la pantalla de lista de clientes
        CardLayout cl = (CardLayout) tarjetas.getLayout();
        cl.show(tarjetas, "Clientes");

        // Se limpia el formulario para la próxima vez
        limpiarFormulario();
    }
}