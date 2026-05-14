package view.principal;

import view.comun.AbstractPanelBotones;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

/**
 * Panel principal de la aplicación.
 * 
 * Esta clase representa el menú principal que ve el usuario
 * al iniciar la aplicación.
 * 
 * Hereda de AbstractPanelBotones, por lo que reutiliza:
 * - La cabecera automática
 * - La rejilla de botones
 * - La navegación mediante CardLayout
 * 
 * En este panel se muestran únicamente las secciones principales:
 * - Clientes
 * - Productos
 * - Pedidos
 */
public class MainJPanel extends AbstractPanelBotones {

    /**
     * Constructor del panel principal.
     * 
     * @param panel contenedor principal que usa CardLayout
     */
    public MainJPanel(JPanel panel) {

        // Llama al constructor de la clase padre.
        // El padre se encargará de construir automáticamente:
        // - El título
        // - La botonera
        // - El sistema de navegación
        super(panel);
    }

    /**
     * Sobrescribimos este método para ocultar los botones
     * "Añadir" y "Actualizar".
     * 
     * En el menú principal no tendría sentido mostrar acciones
     * CRUD porque simplemente es una pantalla de navegación.
     * 
     * @return false para que no aparezcan esos botones
     */
    @Override
    protected boolean mostrarBotonAñadir() {
        return false;
    }

    /**
     * Devuelve el título que aparecerá en la cabecera.
     * 
     * AbstractPanelBotones usa este método para colocar
     * automáticamente el JLabel superior.
     * 
     * @return título principal de la aplicación
     */
    @Override
    protected String getTitulo() {
        return "Cafetería Ayala";
    }

    /**
     * Devuelve la lista de botones que aparecerán
     * en la rejilla central.
     * 
     * Cada String se convierte automáticamente en un JButton
     * dentro de AbstractPanelBotones.
     * 
     * Además:
     * - El texto del botón será el nombre de la tarjeta
     * - El CardLayout navegará a una pantalla con ese nombre
     * 
     * Ejemplo:
     * "Clientes" -> abre panel Clientes
     * 
     * @return lista de nombres de botones
     */
    @Override
    protected List<String> getBotones() {

        // Lista dinámica donde almacenaremos los botones
        List<String> botones = new ArrayList<>();

        // Añadimos acceso al módulo de clientes
        botones.add("Clientes");

        // Añadimos acceso al módulo de productos
        botones.add("Productos");

        // Añadimos acceso al módulo de pedidos
        botones.add("Pedidos");

        // Devolvemos la lista completa
        return botones;
    }
}