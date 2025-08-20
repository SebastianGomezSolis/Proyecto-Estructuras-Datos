import Arreglos.ListaDobleCircular;

public class principal {
    public static void main(String[] args) {
        ListaDobleCircular<Integer> lista = new ListaDobleCircular<>();
        lista.insertar(1);
        lista.insertar(2);
        lista.insertar(3);
        lista.insertar(4);
        lista.insertar(5);

        lista.imprimir();

        System.out.println("Eliminando el elemento 2");
        lista.eliminar(2);

        System.out.println("Lista despues de eliminar el elemento 2");
        lista.imprimir();

        System.out.println("Buscando el elemento 3: " + lista.buscar(3));
        System.out.println("Buscando el elemento 6: " + lista.buscar(6));
        System.out.println("Buscando el elemento 1: " + lista.buscar(2));

        lista.modificar( 1 , 10);

        System.out.println("Lista despues de modificar el elemento 1");
        lista.imprimir();

    }
}
