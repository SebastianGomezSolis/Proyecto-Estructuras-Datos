package Arreglos;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;

public class ListaDobleCircular<T> implements Iterable<T>, Serializable {
    private Nodo<T> root;

    public ListaDobleCircular() {
        this.root = null;
    }

    public Nodo<T> getRoot() { return root; }
    public boolean vacia() { return root == null; }

    public void insertar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato, null, null);
        if (vacia()) {
            root = nuevo;
            root.setSiguiente(root);
            root.setAnterior(root);
        } else {
            Nodo<T> cola = root.getAnterior();
            nuevo.setSiguiente(root);
            nuevo.setAnterior(cola);
            cola.setSiguiente(nuevo);
            root.setAnterior(nuevo);
        }
    }

    public int tamano() {
        if (vacia()) return 0;
        int contador = 0;
        Nodo<T> actual = root;
        do {
            contador++;
            actual = actual.getSiguiente();
        } while (actual != root);
        return contador;
    }

    public void insertarOrdenado(Comparator<T> comparador, T dato) {
        Nodo<T> nuevo = new Nodo<>(dato, null, null);
        if (vacia()) {
            root = nuevo;
            root.setSiguiente(root);
            root.setAnterior(root);
            return;
        }

        Nodo<T> actual = root;
        do {
            if (comparador.compare(dato, actual.getDato()) < 0) {
                Nodo<T> ant = actual.getAnterior();
                nuevo.setSiguiente(actual);
                nuevo.setAnterior(ant);
                ant.setSiguiente(nuevo);
                actual.setAnterior(nuevo);
                if (actual == root) root = nuevo;
                return;
            }
            actual = actual.getSiguiente();
        } while (actual != root);

        Nodo<T> cola = root.getAnterior();
        nuevo.setSiguiente(root);
        nuevo.setAnterior(cola);
        cola.setSiguiente(nuevo);
        root.setAnterior(nuevo);
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = root;
            private boolean inicio = true;

            public boolean hasNext() {
                if (root == null) return false;
                if (inicio) return true;
                return actual != root;
            }

            public T next() {
                T dato = actual.getDato();
                actual = actual.getSiguiente();
                inicio = false;
                return dato;
            }
        };
    }
}
