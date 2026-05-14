import src.dao.*;
import src.model.*;

import java.util.List;
import java.util.Scanner;

public class Main {

    // Implementamos las interfaces DAO
    static ClienteDAO clienteDAO = new ClienteDAOImpl();
    static ProductoDAO productoDAO = new ProductoDAOImpl();
    static PedidoDAO pedidoDAO = new PedidoDAOImpl();

    // Scanner global para no tener que añadirlo a los métodos
    static Scanner entrada = new Scanner(System.in);

    public static void main(String[] args) {
        int opcion = -1;
        do {

            System.out.println("===== CAFETERÍA IES FRANCISCO AYALA =====");
            System.out.println("1. Gestión clientes");
            System.out.println("2. Gestión productos");
            System.out.println("3. Gestión pedidos");
            System.out.println("0. Salir");
            System.out.print("Opción: ");

            opcion = Integer.parseInt(entrada.nextLine());

            switch (opcion) {
                case 1:
                    menuClientes();
                    break;
                case 2:
                    menuProductos();
                    break;
                case 3:
                    menuPedidos();
                    break;
                case 0:
                    System.out.println("Saliendo...");
                    break;
                default:
                    System.out.println("Opción incorrecta");
            }
        } while (opcion != 0);
    }

    // MENU CLIENTES
    public static void menuClientes() {
        int opcion = -1;
        do {
            System.out.println("===== CLIENTES =====");
            System.out.println("1. Añadir cliente");
            System.out.println("2. Buscar cliente");
            System.out.println("3. Listar clientes");
            System.out.println("4. Borrar cliente");
            System.out.println("0. Volver");

            opcion = Integer.parseInt(entrada.nextLine());

            switch (opcion) {
                case 1:
                    añadirCliente();
                    break;
                case 2:
                    buscarCliente();
                    break;
                case 3:
                    listarClientes();
                    break;
                case 4:
                    borrarCliente();
                    break;
            }
        } while (opcion != 0);
    }

    // MENU PRODUCTOS
    public static void menuProductos() {
        int opcion = -1;
        do {
            System.out.println("===== PRODUCTOS =====");
            System.out.println("1. Añadir producto");
            System.out.println("2. Listar productos");
            System.out.println("3. Modificar producto");
            System.out.println("4. Borrar producto");
            System.out.println("0. Volver");

            opcion = Integer.parseInt(entrada.nextLine());

            switch (opcion) {
                case 1:
                    añadirProducto();
                    break;
                case 2:
                    listarProductos();
                    break;
                case 3:
                    modificarProducto();
                    break;
                case 4:
                    borrarProducto();
                    break;
            }
        } while (opcion != 0);
    }

    // MENU PEDIDOS
    public static void menuPedidos() {

        int opcion = -1;
        do {
            System.out.println("===== PEDIDOS =====");
            System.out.println("1. Añadir pedido");
            System.out.println("2. Buscar pedido");
            System.out.println("3. Listar pedidos");
            System.out.println("5. Modificar pedido");
            System.out.println("6. Borrar pedido");
            System.out.println("0. Volver");

            opcion = Integer.parseInt(entrada.nextLine());

            switch (opcion) {
                case 1:
                    añadirPedido();
                    break;
                case 2:
                    buscarPedido();
                    break;
                case 3:
                    listarPedidos();
                    break;
                case 5:
                    modificarPedido();
                    break;
                case 6:
                    borrarPedido();
                    break;
            }
        } while (opcion != 0);
    }

    // METODOS CLIENTES
    public static void añadirCliente() {
        Cliente cliente = new Cliente();

        System.out.print("Nombre: ");
        cliente.setNombre(entrada.nextLine());

        System.out.print("Tipo (ALUMNO / PROFESOR): ");
        cliente.setTipoCliente(TipoCliente.valueOf(entrada.nextLine().toUpperCase()));

        clienteDAO.añadirCliente(cliente);
        System.out.println("Cliente añadido.");
    }

    public static void buscarCliente() {
        System.out.print("Nombre cliente: ");
        String nombre = entrada.nextLine();

        Cliente cliente = clienteDAO.buscarCliente(nombre);

        if (cliente != null) {
            System.out.println(cliente.getNombre());
            System.out.println(cliente.getTipoCliente());
        } else {
            System.out.println("Cliente no encontrado");
        }
    }

    public static void listarClientes() {
        List<Cliente> lista = clienteDAO.listarClientes();

        for (Cliente c : lista) {
            System.out.println(c.getId() + " - " + c.getNombre() + " - " + c.getTipoCliente());
        }
    }

    public static void borrarCliente() {
        System.out.print("ID cliente a borrar: ");
        int id = Integer.parseInt(entrada.nextLine());

        clienteDAO.borrarCliente(id);

        System.out.println("Cliente eliminado");
    }

    // METODOS PRODUCTOS
    public static void añadirProducto() {
        Producto producto = new Producto();

        System.out.print("Nombre: ");
        producto.setNombre(entrada.nextLine());

        System.out.print("Precio: ");
        producto.setPrecio(Double.parseDouble(entrada.nextLine()));

        System.out.print("Cantidad: ");
        producto.setCantidad(Integer.parseInt(entrada.nextLine()));

        System.out.print("Tipo (COMIDA / BEBIDA): ");
        producto.setTipo(TipoProducto.valueOf(entrada.nextLine().toUpperCase()));

        productoDAO.añadirProducto(producto);

        System.out.println("Producto añadido.");
    }

    public static void listarProductos() {
        List<Producto> lista = productoDAO.listarProductos();

        for (Producto p : lista) {
            System.out.println(p.getId() + " - " + p.getNombre() + " - " + p.getPrecio() + "€ - " + p.getCantidad() + " unidades");
        }
    }

    public static void modificarProducto() {
        Producto producto = new Producto();

        System.out.print("ID producto: ");
        producto.setId(Integer.parseInt(entrada.nextLine()));

        System.out.print("Nuevo precio: ");
        producto.setPrecio(Double.parseDouble(entrada.nextLine()));

        System.out.print("Nueva cantidad: ");
        producto.setCantidad(Integer.parseInt(entrada.nextLine()));

        productoDAO.modificarProducto(producto);

        System.out.println("Producto modificado.");
    }

    public static void borrarProducto() {
        System.out.print("ID producto a borrar: ");
        int id = Integer.parseInt(entrada.nextLine());

        productoDAO.borrarProducto(id);

        System.out.println("Producto eliminado");
    }

    // METODOS PEDIDOS
    public static void añadirPedido() {
        Pedido pedido = new Pedido();

        System.out.print("ID cliente: ");
        pedido.setClienteId(Integer.parseInt(entrada.nextLine()));

        System.out.print("Método pago (EFECTIVO / TARJETA / BIZUM): ");
        pedido.setMetodoPago(MetodoPago.valueOf(entrada.nextLine().toUpperCase()));

        System.out.print("Precio: ");
        pedido.setPrecio(Double.parseDouble(entrada.nextLine()));

        System.out.print("Estado (ABIERTO / CERRADO): ");
        pedido.setEstado(Estado.valueOf(entrada.nextLine().toUpperCase()));

        pedidoDAO.añadirPedido(pedido);

        System.out.println("Pedido añadido.");
    }

    public static void buscarPedido() {
        Pedido pedido = pedidoDAO.buscarPedido("nombre");

        if (pedido != null) {
            System.out.println(pedido.getId() + " - " + pedido.getPrecio() + "€ - " + pedido.getEstado());
        } else {
            System.out.println("Pedido no encontrado.");
        }
    }

    public static void listarPedidos() {
        List<Pedido> lista = pedidoDAO.listarPedidos();

        for (Pedido p : lista) {
            System.out.println(p.getId() + " - Cliente " + p.getClienteId() + " - " + p.getPrecio() + "€");
        }
    }

    public static void modificarPedido() {
        Pedido pedido = new Pedido();

        System.out.print("ID pedido: ");
        pedido.setId(Integer.parseInt(entrada.nextLine()));

        System.out.print("Nuevo estado (ABIERTO / CERRADO): ");
        pedido.setEstado(Estado.valueOf(entrada.nextLine().toUpperCase()));

        pedidoDAO.modificarPedido(pedido);

        System.out.println("Pedido modificado.");
    }

    public static void borrarPedido() {
        System.out.print("ID pedido a borrar: ");
        int id = Integer.parseInt(entrada.nextLine());

        pedidoDAO.borrarPedido(id);

        System.out.println("Pedido eliminado.");
    }
}