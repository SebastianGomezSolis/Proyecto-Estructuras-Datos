package Arreglos;

import Indice.TerminoEntry;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;

public class ListaDobleCircular<T> implements Serializable, Iterable<T> {
    private Nodo<T> root;

    // Constructor
    public ListaDobleCircular() {
        this.root = null;
    }

    public Nodo<T> getRoot() {
        return root;
    }

    public void setRoot(Nodo<T> root) {
        this.root = root;
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

    public int tamano() {
        if (vacia()) {
            return 0;
        }
        int contador = 0;
        Nodo<T> actual = root;
        do {
            contador++;
            actual = actual.getSiguiente();
        } while (actual != root);
        return contador;
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
                // Insertar antes de 'actual'
                Nodo<T> ant = actual.getAnterior();
                nuevo.setSiguiente(actual);
                nuevo.setAnterior(ant);
                ant.setSiguiente(nuevo);
                actual.setAnterior(nuevo);
                if (actual == root) root = nuevo; // actualizar root si es necesario
                return;
            }
            actual = actual.getSiguiente();
        } while (actual != root);

        // Si es mayor que todos, insertar al final
        Nodo<T> cola = root.getAnterior();
        nuevo.setSiguiente(root);
        nuevo.setAnterior(cola);
        cola.setSiguiente(nuevo);
        root.setAnterior(nuevo);
    }

    // Cuenta nodos
    public int contarNodos() {
        return tamano();
    }

    // Ordena descendente (Bubble sort)
    public void ordenarPorFrecuencia() {
        if (vacia()) return;

        boolean cambiado;
        do {
            cambiado = false;
            Nodo<T> actual = root;
            do {
                Nodo<T> siguiente = actual.getSiguiente();
                if (siguiente != root) {
                    TerminoEntry a = (TerminoEntry) actual.getDato();
                    TerminoEntry b = (TerminoEntry) siguiente.getDato();

                    if (a.getVeces() < b.getVeces()) {
                        // Intercambiamos solo los datos
                        actual.setDato((T) b);
                        siguiente.setDato((T) a);
                        cambiado = true;
                    }
                }
                actual = actual.getSiguiente();
            } while (actual != root);
        } while (cambiado);
    }

    public Iterator<T> iterator() {
        return new IteratorListaDobleCircular<>(this);
    }

    // MergeSort para ordenar la lista circular doblemente enlazada
    public void ordenarPorRelevancia() {
        if (root == null || root.getSiguiente() == root) return;
        root.setAnterior(null); // romper circularidad temporal
        root = mergeSort(root);
        // volver a conectar en circular
        Nodo<T> ultimo = root;
        while (ultimo.getSiguiente() != null) {
            ultimo = ultimo.getSiguiente();
        }
        root.setAnterior(ultimo);
        ultimo.setSiguiente(root);
    }

    private Nodo<T> mergeSort(Nodo<T> head) {
        if (head == null || head.getSiguiente() == null) return head;

        Nodo<T> medio = getMedio(head);
        Nodo<T> siguienteMedio = medio.getSiguiente();
        medio.setSiguiente(null);

        Nodo<T> izquierda = mergeSort(head);
        Nodo<T> derecha = mergeSort(siguienteMedio);

        return merge(izquierda, derecha);
    }

    private Nodo<T> merge(Nodo<T> izquierda, Nodo<T> derecha) {
        if (izquierda == null) return derecha;
        if (derecha == null) return izquierda;

        Documento dIzq = (Documento) izquierda.getDato();
        Documento dDer = (Documento) derecha.getDato();

        if (dIzq.getRelevancia() >= dDer.getRelevancia()) {
            izquierda.setSiguiente(merge(izquierda.getSiguiente(), derecha));
            izquierda.getSiguiente().setAnterior(izquierda);
            izquierda.setAnterior(null);
            return izquierda;
        } else {
            derecha.setSiguiente(merge(izquierda, derecha.getSiguiente()));
            derecha.getSiguiente().setAnterior(derecha);
            derecha.setAnterior(null);
            return derecha;
        }
    }

    private Nodo<T> getMedio(Nodo<T> head) {
        if (head == null) return head;
        Nodo<T> lento = head;
        Nodo<T> rapido = head;
        while (rapido.getSiguiente() != null && rapido.getSiguiente().getSiguiente() != null) {
            lento = lento.getSiguiente();
            rapido = rapido.getSiguiente().getSiguiente();
        }
        return lento;
    }

}
