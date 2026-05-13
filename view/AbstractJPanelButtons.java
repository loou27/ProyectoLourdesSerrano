package view;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public abstract class AbstractJPanelButtons extends JPanel implements ActionListener {
    protected JPanel jPanel;

    protected abstract String getTitulo();
    protected abstract List<String> getBotones();

    public AbstractJPanelButtons(JPanel panel) {
        this.jPanel = panel;
        setLayout(new BorderLayout());

        JPanel cabecera = new JPanel();
        cabecera.setLayout(new BorderLayout());

        JLabel titulo = new JLabel(this.getTitulo());
        titulo.setFont(new Font("TimesRoman", Font.BOLD, 30));
        titulo.setHorizontalAlignment(JLabel.CENTER);

        cabecera.add(titulo, BorderLayout.NORTH);

        cabecera.add(getJButton( "Menu principal"), BorderLayout.WEST);

        add(cabecera, BorderLayout.NORTH);

        //Creamos grid y botones
        JPanel botonesGrid = new JPanel();
        botonesGrid.setLayout(new GridBagLayout());


        for (String boton : this.getBotones()) {
            botonesGrid.add(getJButton(boton));
        }
        
        add(botonesGrid, BorderLayout.CENTER);
    }

    private JButton getJButton(String text, String actionCommand) {
        JButton button = new JButton();
        button.setText(text);
        button.setActionCommand(actionCommand);
        button.addActionListener(this);

        return button;
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
        CardLayout cardLayout = (CardLayout) this.jPanel.getLayout();

        cardLayout.show(this.jPanel, e.getActionCommand());
    }

}
