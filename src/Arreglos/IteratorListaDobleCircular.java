package Arreglos;

import java.util.Iterator;

public class IteratorListaDobleCircular<T> implements Iterator<T> {
    private Nodo<T> actual;
    private Nodo<T> root;
    private boolean inicio;

    public IteratorListaDobleCircular(ListaDobleCircular<T> lista) {
        this.root = lista.getRoot();
        this.actual = root;
        this.inicio = true;
    }

    public boolean hasNext() {
        if (root == null) return false;
        if (inicio) return true;
        return actual != root;
    }

    public T next() {
        if (!hasNext()) throw new IllegalStateException("No hay más elementos");
        T dato = actual.getDato();
        actual = actual.getSiguiente();
        inicio = false;
        return dato;
    }
}
