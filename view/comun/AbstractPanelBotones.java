package view.comun;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel base con cabecera (título, menú, acciones) y zona central de rejilla.
 *
 * IDEA PRINCIPAL:
 * Esta clase evita repetir código en todas las pantallas.
 *
 * En vez de crear cada vista desde cero:
 * - Clientes
 * - Productos
 * - Pedidos
 *
 * todas heredan de aquí y solo cambian:
 * - título
 * - lista de botones
 * - acciones concretas
 */
public abstract class AbstractPanelBotones extends JPanel implements ActionListener {

    // Panel principal de navegación (usa CardLayout)
    // Es el contenedor que cambia de pantalla
    protected JPanel jPanel;

    // Panel donde se colocan los botones en forma de rejilla
    protected JPanel botonesGrid;

    /**
     * Cada clase hija define el título que aparece arriba.
     */
    protected abstract String getTitulo();

    /**
     * Cada clase hija define qué botones aparecen en pantalla.
     *
     * Ejemplo:
     * - Clientes → nombres de clientes
     * - Productos → nombres de productos
     */
    protected abstract List<String> getBotones();

    /**
     * Controla si se muestran los botones "Añadir" y "Actualizar"
     */
    protected boolean mostrarBotonAñadir() {
        return true;
    }

    /**
     * Acción al pulsar "Añadir"
     * (se sobrescribe en clases hijas)
     */
    protected void añadirRegistro() {
        // vacío por defecto
    }

    /**
     * Acción al pulsar "Actualizar"
     * (se sobrescribe en clases hijas)
     */
    protected void actualizarRegistro() {
        // vacío por defecto
    }

    /**
     * Constructor principal del panel.
     *
     * Recibe el panel general donde se hace el cambio de pantallas.
     */
    public AbstractPanelBotones(JPanel panel) {

        this.jPanel = panel;

        // Layout principal de la pantalla
        setLayout(new BorderLayout(0, 12));

        /*
         * =========================
         * CABECERA SUPERIOR
         * =========================
         */
        JPanel cabecera = new JPanel();
        cabecera.setLayout(new BorderLayout());

        // Título dinámico (lo define la clase hija)
        JLabel titulo = new JLabel(this.getTitulo());
        titulo.setFont(new Font("TimesRoman", Font.BOLD, 30));
        titulo.setHorizontalAlignment(JLabel.CENTER);

        cabecera.add(titulo, BorderLayout.NORTH);

        // Botón para volver al menú principal
        cabecera.add(getJButton("Menu principal"), BorderLayout.WEST);

        /*
         * Botones de acciones (lado derecho)
         * Solo aparecen si la pantalla lo permite
         */
        if (mostrarBotonAñadir()) {
            JPanel accionesEste = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

            accionesEste.add(getJButton("Añadir"));
            accionesEste.add(getJButton("Actualizar"));

            cabecera.add(accionesEste, BorderLayout.EAST);
        }

        add(cabecera, BorderLayout.NORTH);

        /*
         * =========================
         * ZONA CENTRAL (BOTONES)
         * =========================
         *
         * Aquí se colocan los botones dinámicos
         * (clientes, productos, etc.)
         */
        this.botonesGrid = new JPanel();

        poblarAreaBotones();

        /*
         * Scroll para cuando hay muchos botones
         */
        JScrollPane scrollBotones = new JScrollPane(this.botonesGrid);

        scrollBotones.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        scrollBotones.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        scrollBotones.setBorder(null);

        scrollBotones.getVerticalScrollBar().setUnitIncrement(24);

        add(scrollBotones, BorderLayout.CENTER);
    }

    /**
     * Refresca la rejilla de botones.
     *
     * Se usa cuando cambian datos en la base de datos.
     * Ejemplo:
     * - se añade cliente
     * - se elimina producto
     */
    public void refreshGrid() {
        poblarAreaBotones();
        botonesGrid.revalidate();
        botonesGrid.repaint();
    }

    // Número fijo de columnas en la rejilla
    protected static final int COLUMNAS_BOTONES = 4;

    /**
     * Construye la rejilla de botones.
     *
     * IMPORTANTE:
     * - convierte una lista de Strings en botones
     * - los coloca en filas y columnas
     */
    protected void poblarAreaBotones() {

        // Limpia lo anterior
        botonesGrid.removeAll();

        // Layout tipo rejilla flexible
        botonesGrid.setLayout(new GridBagLayout());

        List<String> etiquetas = getBotones();

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(6, 6, 6, 6);

        // Recorre todos los elementos y los convierte en botones
        for (int i = 0; i < etiquetas.size(); i++) {

            gbc.gridx = i % COLUMNAS_BOTONES; // columna
            gbc.gridy = i / COLUMNAS_BOTONES; // fila

            botonesGrid.add(crearBotonAccion(etiquetas.get(i)), gbc);
        }
    }

    /**
     * Crea un botón individual.
     *
     * Se puede sobrescribir si se quiere personalizar botones.
     */
    protected JButton crearBotonAccion(String texto) {
        return getJButton(texto);
    }

    /**
     * Crea un botón estándar con acción automática.
     *
     * TODOS los botones:
     * - tienen texto
     * - tienen actionCommand
     * - escuchan eventos del mismo panel
     */
    private JButton getJButton(String name) {

        JButton button = new JButton();

        button.setText(name);
        button.setActionCommand(name);

        // Este panel escucha todos los botones
        button.addActionListener(this);

        return button;
    }

    /**
     * Manejo de eventos de todos los botones.
     *
     * LÓGICA:
     * - Si es "Añadir" → abre formulario de alta
     * - Si es "Actualizar" → abre edición
     * - Si no → cambia de pantalla (CardLayout)
     */
    public void actionPerformed(ActionEvent e) {

        System.out.println(e.getActionCommand());

        if ("Añadir".equals(e.getActionCommand())) {
            añadirRegistro();
            return;
        }

        if ("Actualizar".equals(e.getActionCommand())) {
            actualizarRegistro();
            return;
        }

        // Cambio de pantalla general
        CardLayout cardLayout = (CardLayout) this.jPanel.getLayout();

        cardLayout.show(this.jPanel, e.getActionCommand());
    }
}