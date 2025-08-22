package test.Arreglos;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Arreglos.ListaDobleCircular;

public class ListaDobleCircularTest {
    private ListaDobleCircular<Integer> lista;

    @BeforeEach
    void setUp() {
        lista = new ListaDobleCircular<>();
    }

    @Test
    void testInsertarYImprimir() {
        lista.insertar(10);
        lista.insertar(20);
        lista.insertar(30);
        assertTrue(lista.buscar(10));
        assertTrue(lista.buscar(20));
        assertTrue(lista.buscar(30));
    }

    @Test
    void testEliminar() {
        lista.insertar(1);
        lista.insertar(2);
        lista.insertar(3);

        lista.eliminar(2);
        assertFalse(lista.buscar(2));
        assertTrue(lista.buscar(1));
        assertTrue(lista.buscar(3));
    }

    @Test
    void testModificar() {
        lista.insertar(5);
        lista.modificar(5, 99);
        assertTrue(lista.buscar(99));
        assertFalse(lista.buscar(5));
    }

    @Test
    void testVacia() {
        assertTrue(lista.vacia());
        lista.insertar(1);
        assertFalse(lista.vacia());
    }

    @Test
    void testRecorrer() {
        lista.insertar(7);
        lista.insertar(8);
        lista.insertar(9);

        int count = lista.tamano();
        assertEquals(3, count);
    }
}

