package Arreglos;

public class ListaDobleCircular<T> {
    private Nodo<T> root;

    // Constructor
    public ListaDobleCircular() {
        this.root = null;
    }

    // Metodo para ver si esta vacia
    public boolean vacia() {
        return this.root == null;
    }

    // Metodo insertar
    public void insertar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato, null, null);
        if (vacia()) {
            this.root = nuevo;
            this.root.setSiguiente(this.root);
            this.root.setAnterior(this.root);
        } else {
            Nodo<T> cola = root.getAnterior();
            nuevo.setSiguiente(root);
            nuevo.setAnterior(cola);
            cola.setSiguiente(nuevo);
            root.setAnterior(nuevo);
        }
    }

    // Metodo eliminar
    public void eliminar(T dato) {
        if (vacia()) {
            return;
        }
        Nodo<T> actual = root;
        do {
            if (actual.getDato().equals(dato)) {
                if (actual == root && actual.getSiguiente() == root) {
                    root = null;
                } else {
                    actual.getAnterior().setSiguiente(actual.getSiguiente());
                    actual.getSiguiente().setAnterior(actual.getAnterior());
                    if (actual == root) {
                        root = actual.getSiguiente();
                    }
                }
                return;
            }
            actual = actual.getSiguiente();
        } while (actual != null);
    }

    // Metodo para buscar un elemento
    public boolean buscar(T dato) {
        if (vacia()) {
            return false;
        }
        Nodo<T> actual = root;
        do {
            if (actual.getDato().equals(dato)) {
                return true;
            }
            actual = actual.getSiguiente();
        } while (actual != root);
        return false;
    }

    // Metodo para modificar un elemento
    public void modificar(T dato, T nuevoDato) {
        if (vacia()) {
            return;
        }
        Nodo<T> actual = root;
        do {
            if (actual.getDato().equals(dato)) {
                actual.setDato(nuevoDato);
                return;
            }
            actual = actual.getSiguiente();
        } while (actual != root);
    }

    // Metodo para imprimir toda la lista
    public void imprimir() {
        if (vacia()) {
            System.out.println("La lista esta vacia");
        }
        Nodo<T> actual = root;
        do {
            System.out.print(actual.getDato() + " ");
            actual = actual.getSiguiente();
        } while (actual != root);
        System.out.println();
    }
}
