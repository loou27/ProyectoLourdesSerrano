package view.cliente;

import src.dao.ClienteDAO;
import src.dao.ClienteDAOImpl;
import src.model.Cliente;
import src.model.TipoCliente;

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

/** Alta de cliente dentro del contenido principal de la ventana (sin diálogos modales). */
public class AltaClientePanel extends JPanel {

    public static final String NOMBRE_TARJETA = "Alta cliente";

    private static final ClienteDAO clienteDAO = new ClienteDAOImpl();

    private final JPanel tarjetas;
    private final ClientesJPanel clientesPanel;

    private final JTextField campoNombre = new JTextField(24);
    private final JComboBox<TipoCliente> comboTipo = new JComboBox<>(TipoCliente.values());
    private final JLabel mensajeValidacion = new JLabel(" ");

    public AltaClientePanel(JPanel tarjetas, ClientesJPanel clientesPanel) {
        this.tarjetas = tarjetas;
        this.clientesPanel = clientesPanel;

        setLayout(new BorderLayout(8, 8));

        JPanel superior = new JPanel(new BorderLayout());
        JButton volver = new JButton("← Clientes");
        volver.addActionListener(e -> volverLista());
        superior.add(volver, BorderLayout.WEST);

        JLabel titulo = new JLabel("Nuevo cliente");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 22));
        JPanel centroTitulo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centroTitulo.add(titulo);
        superior.add(centroTitulo, BorderLayout.CENTER);

        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        formulario.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formulario.add(campoNombre, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
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

        addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 && isShowing()) {
                limpiarFormulario();
            }
        });

        campoNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, campoNombre.getPreferredSize().height));
    }

    private void limpiarFormulario() {
        campoNombre.setText("");
        comboTipo.setSelectedIndex(0);
        mensajeValidacion.setText(" ");
    }

    private void volverLista() {
        mensajeValidacion.setText(" ");
        CardLayout cl = (CardLayout) tarjetas.getLayout();
        cl.show(tarjetas, "Clientes");
    }

    private void guardar() {
        mensajeValidacion.setText(" ");
        String nombre = campoNombre.getText() != null ? campoNombre.getText().trim() : "";
        if (nombre.isEmpty()) {
            mensajeValidacion.setText("El nombre no puede estar vacío.");
            return;
        }

        TipoCliente tipo = (TipoCliente) comboTipo.getSelectedItem();
        clienteDAO.añadirCliente(new Cliente(nombre, tipo));
        clientesPanel.refreshGrid();
        CardLayout cl = (CardLayout) tarjetas.getLayout();
        cl.show(tarjetas, "Clientes");
        limpiarFormulario();
    }
}
