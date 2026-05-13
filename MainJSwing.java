
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.CardLayout;
import java.awt.Dimension;

import view.*;

public class MainJSwing {
        private static void GUI() {
        //Creamos la ventana de Windows
        JFrame frame = new JFrame("Cafetería Ayala");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension (720, 480));


        JPanel contentPane = new JPanel();

        contentPane.setBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        );
        contentPane.setLayout(new CardLayout());

        //Creamos el mainJPanel
        MainJPanel mainJPanel = new MainJPanel(contentPane);
        ClientesJPanel clientesJPanel = new ClientesJPanel(contentPane);
        ProductosJPanel productosJPanel = new ProductosJPanel(contentPane);

        contentPane.add(mainJPanel, "Menu principal");
        contentPane.add(clientesJPanel, "Clientes");
        contentPane.add(productosJPanel, "Productos");

        //Asignamos el JPanel a la ventana
        contentPane.setOpaque(true);
        frame.setContentPane(contentPane);

        //Mostramos la ventana
        frame.pack();
        frame.setVisible(true);
    }


    public static void main(String[] args) {
        //Creamos nuevo sub-proceso para poder escuchar eventos
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Mostramos la interfaz de usuario
                GUI(); 
            }
        });
    }
}