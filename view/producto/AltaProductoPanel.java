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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/** 
 * Alta de producto dentro del contenido principal de la ventana
 * (sin diálogos modales).
 * 
 * Este panel permite:
 * - Crear nuevos productos
 * - Introducir nombre, cantidad, precio y tipo
 * - Validar datos antes de guardar
 * - Volver automáticamente a la lista de productos
 */
public class AltaProductoPanel extends JPanel {

    // Nombre identificador de esta pantalla dentro del CardLayout
    public static final String NOMBRE_TARJETA = "Alta producto";

    // DAO encargado de insertar productos en base de datos
    private static final ProductoDAO productoDAO = new ProductoDAOImpl();

    // Panel principal que contiene todas las tarjetas
    private final JPanel tarjetas;

    // Referencia al panel de productos para refrescar la lista
    // después de insertar un nuevo producto
    private final ProductosJPanel productosPanel;

    /*
     * ===================== COMPONENTES DEL FORMULARIO =====================
     */

    // Campo de texto para nombre del producto
    private final JTextField campoNombre = new JTextField(24);

    // Campo para cantidad/stock
    private final JTextField campoCantidad = new JTextField(8);

    // Campo para precio del producto
    private final JTextField campoPrecio = new JTextField(8);

    // ComboBox con los valores del enum TipoProducto
    // Ejemplo: COMIDA, BEBIDA
    private final JComboBox<TipoProducto> comboTipo =
        new JComboBox<>(TipoProducto.values());

    // Label para mostrar errores de validación
    private final JLabel mensajeValidacion = new JLabel(" ");

    /**
     * Constructor principal del panel.
     * 
     * @param tarjetas panel principal con CardLayout
     * @param productosPanel panel de productos para refrescar la vista
     */
    public AltaProductoPanel(JPanel tarjetas, ProductosJPanel productosPanel) {

        this.tarjetas = tarjetas;
        this.productosPanel = productosPanel;

        // Layout principal:
        // NORTH -> cabecera
        // CENTER -> formulario
        // SOUTH -> botones + errores
        setLayout(new BorderLayout(8, 8));

        /*
         * ===================== CABECERA =====================
         */

        JPanel superior = new JPanel(new BorderLayout());

        // Botón para volver a la lista de productos
        JButton volver = new JButton("← Productos");

        // Listener que cambia la tarjeta visible
        volver.addActionListener(e -> volverLista());

        superior.add(volver, BorderLayout.WEST);

        // Título principal
        JLabel titulo = new JLabel("Nuevo producto");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));

        JPanel centroTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centroTitulo.add(titulo);

        superior.add(centroTitulo, BorderLayout.CENTER);

        /*
         * ===================== FORMULARIO =====================
         * 
         * GridBagLayout permite posicionar componentes
         * como si fuera una cuadrícula flexible.
         */

        JPanel formulario = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        // Márgenes internos entre componentes
        gbc.insets = new Insets(6, 8, 6, 8);

        // Alineación hacia la izquierda
        gbc.anchor = GridBagConstraints.WEST;

        /*
         * Variable auxiliar para controlar filas.
         * 
         * Así evitamos escribir números manuales:
         * fila 0, fila 1, fila 2...
         */
        int row = 0;

        /*
         * ===================== CAMPO NOMBRE =====================
         */

        gbc.gridx = 0;
        gbc.gridy = row;

        formulario.add(new JLabel("Nombre:"), gbc);

        gbc.gridx = 1;

        // Permite que el campo crezca horizontalmente
        gbc.weightx = 1;

        gbc.fill = GridBagConstraints.HORIZONTAL;

        formulario.add(campoNombre, gbc);

        /*
         * ===================== CAMPO CANTIDAD =====================
         */

        row++;

        gbc.gridy = row;
        gbc.gridx = 0;

        // Restauramos configuraciones anteriores
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;

        formulario.add(new JLabel("Cantidad:"), gbc);

        gbc.gridx = 1;

        formulario.add(campoCantidad, gbc);

        /*
         * ===================== CAMPO PRECIO =====================
         */

        row++;

        gbc.gridy = row;
        gbc.gridx = 0;

        formulario.add(new JLabel("Precio:"), gbc);

        gbc.gridx = 1;

        formulario.add(campoPrecio, gbc);

        /*
         * ===================== COMBO TIPO =====================
         */

        row++;

        gbc.gridy = row;
        gbc.gridx = 0;

        formulario.add(new JLabel("Tipo:"), gbc);

        gbc.gridx = 1;

        formulario.add(comboTipo, gbc);

        /*
         * ===================== CENTRADO DEL FORMULARIO =====================
         * 
         * Se usa BoxLayout + Glue para empujar el formulario
         * hacia el centro vertical de la ventana.
         */

        JPanel centro = new JPanel();

        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        centro.add(Box.createVerticalGlue());

        JPanel wrapForm = new JPanel(new FlowLayout(FlowLayout.CENTER));

        wrapForm.add(formulario);

        centro.add(wrapForm);

        centro.add(Box.createVerticalGlue());

        /*
         * ===================== MENSAJES DE ERROR =====================
         */

        mensajeValidacion.setForeground(Color.RED);

        mensajeValidacion.setAlignmentX(Component.CENTER_ALIGNMENT);

        /*
         * ===================== PARTE INFERIOR =====================
         * Contiene:
         * - Mensajes de error
         * - Botones guardar/cancelar
         */

        JPanel inferior = new JPanel();

        inferior.setLayout(new BoxLayout(inferior, BoxLayout.Y_AXIS));

        // Línea de errores
        JPanel lineaError = new JPanel(new FlowLayout(FlowLayout.CENTER));

        lineaError.add(mensajeValidacion);

        inferior.add(lineaError);

        /*
         * ===================== BOTONERA =====================
         */

        JPanel botonera = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));

        // Botón guardar
        JButton guardar = new JButton("Guardar");

        guardar.addActionListener(e -> guardar());

        // Botón cancelar
        JButton cancelar = new JButton("Cancelar");

        cancelar.addActionListener(e -> volverLista());

        botonera.add(cancelar);
        botonera.add(guardar);

        inferior.add(botonera);

        /*
         * Añadimos las 3 zonas principales al panel
         */

        add(superior, BorderLayout.NORTH);

        add(centro, BorderLayout.CENTER);

        add(inferior, BorderLayout.SOUTH);

        /*
         * Listener que detecta cuándo este panel
         * pasa a mostrarse en pantalla.
         * 
         * Cada vez que aparece:
         * - se limpia el formulario
         * - se eliminan mensajes anteriores
         */
        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0
                    && isShowing()) {

                limpiarFormulario();
            }
        });

        /*
         * Permite que los campos crezcan horizontalmente
         * sin deformarse verticalmente.
         */

        campoNombre.setMaximumSize(
            new Dimension(Integer.MAX_VALUE,
            campoNombre.getPreferredSize().height)
        );

        campoCantidad.setMaximumSize(
            new Dimension(Integer.MAX_VALUE,
            campoCantidad.getPreferredSize().height)
        );

        campoPrecio.setMaximumSize(
            new Dimension(Integer.MAX_VALUE,
            campoPrecio.getPreferredSize().height)
        );
    }

    /**
     * Limpia todos los campos del formulario.
     * 
     * También:
     * - reinicia el ComboBox
     * - borra errores
     */
    private void limpiarFormulario() {

        campoNombre.setText("");

        campoCantidad.setText("");

        campoPrecio.setText("");

        comboTipo.setSelectedIndex(0);

        mensajeValidacion.setText(" ");
    }

    /**
     * Vuelve a la lista principal de productos.
     */
    private void volverLista() {

        mensajeValidacion.setText(" ");

        CardLayout cl = (CardLayout) tarjetas.getLayout();

        cl.show(tarjetas, "Productos");
    }

    /**
     * Valida y guarda el producto.
     * 
     * Flujo:
     * 1. Validar nombre
     * 2. Validar cantidad
     * 3. Validar precio
     * 4. Crear producto
     * 5. Insertar en BD
     * 6. Refrescar lista
     * 7. Volver a pantalla productos
     */
    private void guardar() {

        // Limpiamos errores anteriores
        mensajeValidacion.setText(" ");

        /*
         * ===================== VALIDACIÓN NOMBRE =====================
         */

        String nombre =
            campoNombre.getText() != null
                ? campoNombre.getText().trim()
                : "";

        if (nombre.isEmpty()) {

            mensajeValidacion.setText(
                "El nombre no puede estar vacío."
            );

            return;
        }

        /*
         * ===================== VALIDACIÓN CANTIDAD =====================
         * 
         * Integer.parseInt puede lanzar excepción
         * si el usuario escribe texto no numérico.
         */

        int cantidad;

        try {

            cantidad = Integer.parseInt(
                campoCantidad.getText().trim()
            );

            // No permitimos negativos
            if (cantidad < 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException ex) {

            mensajeValidacion.setText(
                "La cantidad debe ser un número entero ≥ 0."
            );

            return;
        }

        /*
         * ===================== VALIDACIÓN PRECIO =====================
         */

        double precio;

        try {

            // Permitimos escribir coma decimal
            precio = Double.parseDouble(
                campoPrecio.getText()
                    .trim()
                    .replace(',', '.')
            );

            if (precio < 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException ex) {

            mensajeValidacion.setText(
                "El precio debe ser un número ≥ 0."
            );

            return;
        }

        /*
         * ===================== CREACIÓN DEL PRODUCTO =====================
         */

        TipoProducto tipo =
            (TipoProducto) comboTipo.getSelectedItem();

        // Insertamos producto en BD
        productoDAO.añadirProducto(
            new Producto(nombre, cantidad, precio, tipo)
        );

        /*
         * Refrescamos la pantalla de productos
         * para que aparezca el nuevo producto
         */
        productosPanel.refreshGrid();

        /*
         * Volvemos automáticamente a la lista
         * de productos
         */
        CardLayout cl = (CardLayout) tarjetas.getLayout();

        cl.show(tarjetas, "Productos");

        // Dejamos el formulario limpio
        limpiarFormulario();
    }
}