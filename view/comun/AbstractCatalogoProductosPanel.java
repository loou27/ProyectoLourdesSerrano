package view.comun;

import src.model.Producto;
import src.model.TipoProducto;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Catálogo de productos agrupado por tipo (Comida / Bebida), misma estructura visual en Productos y en Pedido.
 * En modo adaptable ({@link #usarRejillaAdaptableAncho()}), el número de columnas depende del ancho para evitar overflow horizontal.
 *
 * CLAVE:
 * Esta clase NO es una pantalla concreta, es una plantilla reutilizable.
 * Otras pantallas la extienden y solo deciden qué pasa cuando se selecciona un producto.
 */
public abstract class AbstractCatalogoProductosPanel extends JPanel {

    // Guarda la última lista de productos recibida
    // Se usa para poder reconstruir la pantalla cuando cambia el tamaño de la ventana
    private List<Producto> ultimaLista = new ArrayList<>();

    // Guarda cuántas columnas se están usando actualmente en la rejilla
    private int columnasActivas = -1;

    /**
     * Método abstracto:
     * Cada clase hija define qué pasa cuando se pulsa un producto.
     *
     * Ejemplo:
     * - en pedidos → añadir producto al pedido
     * - en catálogo → mostrar detalles
     */
    protected abstract void alSeleccionarProducto(Producto producto);

    /** Si es true (p. ej. detalle pedido), se recalculan columnas al redimensionar. */
    protected boolean usarRejillaAdaptableAncho() {
        return false;
    }

    /**
     * Calcula cuántas columnas caben según el ancho disponible.
     *
     * Idea sencilla:
     * - ventana grande → más botones en fila
     * - ventana pequeña → menos botones
     *
     * Esto evita que los botones se salgan de la pantalla.
     */
    protected int columnasParaAnchoDisponible(int anchoPixels) {

        if (anchoPixels <= 0) {
            return AbstractPanelBotones.COLUMNAS_BOTONES;
        }

        int margen = 28;              // espacio que no se usa en los bordes
        int minAnchoColumna = 118;    // tamaño mínimo que necesita un botón
        int hgap = 12;                // separación entre botones

        int usable = Math.max(1, anchoPixels - margen);

        // calcula cuántas columnas caben
        int c = usable / (minAnchoColumna + hgap);

        // limita entre 1 y el máximo permitido del sistema
        return Math.max(1, Math.min(AbstractPanelBotones.COLUMNAS_BOTONES, c));
    }

    protected AbstractCatalogoProductosPanel() {

        // Listener que detecta cuando la pantalla cambia de tamaño
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                // Si no está en modo adaptable o no hay productos, no hace nada
                if (!usarRejillaAdaptableAncho() || ultimaLista.isEmpty()) {
                    return;
                }

                int cols = columnasParaAnchoDisponible(getWidth());

                // Si cambia el número de columnas, se reconstruye la pantalla
                if (cols != columnasActivas) {
                    poblarInterno(ultimaLista, cols);
                }
            }
        });
    }

    /**
     * Recarga el catálogo completo con una nueva lista de productos.
     *
     * Esto se llama cada vez que:
     * - cambia el inventario
     * - se abre la pantalla
     * - se refresca la vista
     */
    public final void refrescarCatalogo(List<Producto> lista) {

        // Guardamos copia de la lista para poder reutilizarla
        ultimaLista = new ArrayList<>(lista);

        int cols = usarRejillaAdaptableAncho()
                ? columnasParaAnchoDisponible(getWidth())
                : AbstractPanelBotones.COLUMNAS_BOTONES;

        poblarInterno(ultimaLista, cols);
    }

    /**
     * Construye toda la interfaz visual del catálogo.
     *
     * Aquí se organiza todo:
     * - separación por tipo (Comida / Bebida)
     * - creación de botones
     * - distribución en rejilla
     */
    private void poblarInterno(List<Producto> lista, int numColumnas) {

        columnasActivas = numColumnas;

        // Limpia toda la pantalla antes de reconstruirla
        removeAll();

        // Layout vertical: cada tipo de producto es un bloque independiente
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Recorre todos los tipos de producto (COMIDA / BEBIDA)
        for (TipoProducto tipo : TipoProducto.values()) {

            // Contenedor del grupo de productos de un tipo
            JPanel bloque = new JPanel(new BorderLayout(0, 8));
            bloque.setAlignmentX(Component.LEFT_ALIGNMENT);

            // =========================
            // CABECERA DEL GRUPO
            // =========================
            JLabel cabeceraTipo = new JLabel(etiquetaGrupoProducto(tipo));
            cabeceraTipo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));

            JPanel filaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            filaTitulo.setOpaque(false);
            filaTitulo.add(cabeceraTipo);

            bloque.add(filaTitulo, BorderLayout.NORTH);

            // =========================
            // REJILLA DE BOTONES
            // =========================
            JPanel gridTipo = new JPanel(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.weighty = 0;
            gbc.insets = new Insets(6, 6, 6, 6);

            int i = 0;

            // Recorre todos los productos recibidos
            for (Producto p : lista) {

                // Filtra por tipo (solo los de este grupo)
                if (p.getTipo() != tipo) {
                    continue;
                }

                // Calcula posición en la rejilla
                gbc.gridx = i % numColumnas;
                gbc.gridy = i / numColumnas;

                // Botón que representa el producto
                JButton boton = new JButton(p.getNombre());

                boton.setActionCommand(p.getNombre());
                boton.setHorizontalAlignment(SwingConstants.CENTER);

                // Guardamos referencia del producto para usarlo en el click
                Producto prodRef = p;

                // Acción al pulsar el botón
                boton.addActionListener(ev -> alSeleccionarProducto(prodRef));

                gridTipo.add(boton, gbc);
                i++;
            }

            bloque.add(gridTipo, BorderLayout.CENTER);

            // Evita que el bloque crezca de forma incorrecta en altura
            bloque.invalidate();
            Dimension prefBloque = bloque.getPreferredSize();
            bloque.setMaximumSize(new Dimension(Integer.MAX_VALUE, prefBloque.height));

            add(bloque);

            // Espacio entre grupos (Comida / Bebida)
            add(Box.createVerticalStrut(16));
        }

        // Fuerza actualización visual de la interfaz
        revalidate();
        repaint();
    }

    /**
     * Traduce el enum TipoProducto a texto visible en pantalla.
     */
    private static String etiquetaGrupoProducto(TipoProducto tipo) {
        return switch (tipo) {
            case COMIDA -> "Comida";
            case BEBIDA -> "Bebida";
        };
    }
}