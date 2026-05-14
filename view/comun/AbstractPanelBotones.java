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

/** Panel base con cabecera (título, menú, acciones) y zona central de rejilla o contenido personalizado. */
public abstract class AbstractPanelBotones extends JPanel implements ActionListener {
    protected JPanel jPanel;
    protected JPanel botonesGrid;

    protected abstract String getTitulo();

    protected abstract List<String> getBotones();

    protected boolean mostrarBotonAñadir() {
        return true;
    }

    protected void añadirRegistro() {
        // Sobrescrito en vistas que muestran el botón "Añadir"
    }

    protected void actualizarRegistro() {
        // Sobrescrito en vistas que muestran el botón "Actualizar"
    }

    public AbstractPanelBotones(JPanel panel) {
        this.jPanel = panel;
        setLayout(new BorderLayout(0, 12));

        JPanel cabecera = new JPanel();
        cabecera.setLayout(new BorderLayout());

        JLabel titulo = new JLabel(this.getTitulo());
        titulo.setFont(new Font("TimesRoman", Font.BOLD, 30));
        titulo.setHorizontalAlignment(JLabel.CENTER);

        cabecera.add(titulo, BorderLayout.NORTH);

        cabecera.add(getJButton("Menu principal"), BorderLayout.WEST);

        if (mostrarBotonAñadir()) {
            JPanel accionesEste = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            accionesEste.add(getJButton("Añadir"));
            accionesEste.add(getJButton("Actualizar"));
            cabecera.add(accionesEste, BorderLayout.EAST);
        }

        add(cabecera, BorderLayout.NORTH);

        // Rejilla fija de 4 columnas; filas = ceil(cantidad / 4)
        this.botonesGrid = new JPanel();
        poblarAreaBotones();

        JScrollPane scrollBotones = new JScrollPane(this.botonesGrid);
        scrollBotones.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollBotones.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollBotones.setBorder(null);
        scrollBotones.getVerticalScrollBar().setUnitIncrement(24);

        add(scrollBotones, BorderLayout.CENTER);
    }

    public void refreshGrid() {
        poblarAreaBotones();
        botonesGrid.revalidate();
        botonesGrid.repaint();
    }

    protected static final int COLUMNAS_BOTONES = 4;

    /**
     * Rellena {@link #botonesGrid}. Por defecto: rejilla de 4 columnas con {@link #getBotones()}.
     */
    protected void poblarAreaBotones() {
        botonesGrid.removeAll();
        botonesGrid.setLayout(new GridBagLayout());

        List<String> etiquetas = getBotones();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.insets = new Insets(6, 6, 6, 6);

        for (int i = 0; i < etiquetas.size(); i++) {
            gbc.gridx = i % COLUMNAS_BOTONES;
            gbc.gridy = i / COLUMNAS_BOTONES;
            botonesGrid.add(crearBotonAccion(etiquetas.get(i)), gbc);
        }
    }

    /** Botón que usa esta vista como listener (misma semántica que la rejilla por defecto). */
    protected JButton crearBotonAccion(String texto) {
        return getJButton(texto);
    }

    private JButton getJButton(String name) {
        JButton button = new JButton();
        button.setText(name);
        button.setActionCommand(name);
        button.addActionListener(this);

        return button;
    }

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
        CardLayout cardLayout = (CardLayout) this.jPanel.getLayout();

        cardLayout.show(this.jPanel, e.getActionCommand());
    }
}
