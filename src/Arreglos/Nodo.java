package Arreglos;
import java.io.Serializable;

public class Nodo<T> implements Serializable {
    private T dato;
    private Nodo<T> siguiente;
    private Nodo<T> anterior;

    public Nodo(T dato, Nodo<T> siguiente, Nodo<T> anterior) {
        this.dato = dato;
        this.siguiente = siguiente;
        this.anterior = anterior;
    }

    public T getDato() { return dato; }
    public Nodo<T> getSiguiente() { return siguiente; }
    public Nodo<T> getAnterior() { return anterior; }

    public void setDato(T dato) { this.dato = dato; }
    public void setSiguiente(Nodo<T> siguiente) { this.siguiente = siguiente; }
    public void setAnterior(Nodo<T> anterior) { this.anterior = anterior; }
}
