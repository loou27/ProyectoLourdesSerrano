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
 */
public abstract class AbstractCatalogoProductosPanel extends JPanel {

    private List<Producto> ultimaLista = new ArrayList<>();
    private int columnasActivas = -1;

    protected abstract void alSeleccionarProducto(Producto producto);

    /** Si es true (p. ej. detalle pedido), se recalculan columnas al redimensionar. */
    protected boolean usarRejillaAdaptableAncho() {
        return false;
    }

    /** Columnas efectivas según ancho disponible (1–COLUMNAS_BOTONES). Más conservador para nombres largos. */
    protected int columnasParaAnchoDisponible(int anchoPixels) {
        if (anchoPixels <= 0) {
            return AbstractPanelBotones.COLUMNAS_BOTONES;
        }
        int margen = 28;
        int minAnchoColumna = 118;
        int hgap = 12;
        int usable = Math.max(1, anchoPixels - margen);
        int c = usable / (minAnchoColumna + hgap);
        return Math.max(1, Math.min(AbstractPanelBotones.COLUMNAS_BOTONES, c));
    }

    protected AbstractCatalogoProductosPanel() {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (!usarRejillaAdaptableAncho() || ultimaLista.isEmpty()) {
                    return;
                }
                int cols = columnasParaAnchoDisponible(getWidth());
                if (cols != columnasActivas) {
                    poblarInterno(ultimaLista, cols);
                }
            }
        });
    }

    public final void refrescarCatalogo(List<Producto> lista) {
        ultimaLista = new ArrayList<>(lista);
        int cols = usarRejillaAdaptableAncho()
            ? columnasParaAnchoDisponible(getWidth())
            : AbstractPanelBotones.COLUMNAS_BOTONES;
        poblarInterno(ultimaLista, cols);
    }

    private void poblarInterno(List<Producto> lista, int numColumnas) {
        columnasActivas = numColumnas;

        removeAll();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        for (TipoProducto tipo : TipoProducto.values()) {
            JPanel bloque = new JPanel(new BorderLayout(0, 8));
            bloque.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel cabeceraTipo = new JLabel(etiquetaGrupoProducto(tipo));
            cabeceraTipo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
            JPanel filaTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            filaTitulo.setOpaque(false);
            filaTitulo.add(cabeceraTipo);
            bloque.add(filaTitulo, BorderLayout.NORTH);

            JPanel gridTipo = new JPanel(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.weighty = 0;
            gbc.insets = new Insets(6, 6, 6, 6);

            int i = 0;
            for (Producto p : lista) {
                if (p.getTipo() != tipo) {
                    continue;
                }
                gbc.gridx = i % numColumnas;
                gbc.gridy = i / numColumnas;

                JButton boton = new JButton(p.getNombre());
                boton.setActionCommand(p.getNombre());
                boton.setHorizontalAlignment(SwingConstants.CENTER);
                Producto prodRef = p;
                boton.addActionListener(ev -> alSeleccionarProducto(prodRef));

                gridTipo.add(boton, gbc);
                i++;
            }

            bloque.add(gridTipo, BorderLayout.CENTER);

            bloque.invalidate();
            Dimension prefBloque = bloque.getPreferredSize();
            bloque.setMaximumSize(new Dimension(Integer.MAX_VALUE, prefBloque.height));

            add(bloque);
            add(Box.createVerticalStrut(16));
        }

        revalidate();
        repaint();
    }

    private static String etiquetaGrupoProducto(TipoProducto tipo) {
        return switch (tipo) {
            case COMIDA -> "Comida";
            case BEBIDA -> "Bebida";
        };
    }
}
