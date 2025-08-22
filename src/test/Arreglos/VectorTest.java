package test.Arreglos;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import Arreglos.Vector;

public class VectorTest {
    private Vector v1;
    private Vector v2;

    @BeforeEach
    void setUp() {
        v1 = new Vector(3);
        v2 = new Vector(3);

        v1.insertar(1);
        v1.insertar(2);
        v1.insertar(3);

        v2.insertar(4);
        v2.insertar(5);
        v2.insertar(6);
    }

    @Test
    void testInsertarYMostrar() {
        assertEquals("[1.0, 2.0, 3.0]", v1.mostrar());
    }

    @Test
    void testEliminar() {
        v1.eliminar(2);
        assertEquals("[1.0, 3.0]", v1.mostrar());
    }

    @Test
    void testBuscar() {
        assertTrue(v1.buscar(2));
        assertFalse(v1.buscar(99));
    }

    @Test
    void testProductoPunto() {
        assertEquals(32.0, v1.productoPunto(v2));
    }

    @Test
    void testMagnitude() {
        assertEquals(Math.sqrt(14), v1.magnitude());
    }

    @Test
    void testCosineSimilarity() {
        double cosSim = v1.cosineSimilarity(v2);
        assertEquals(0.9746318461970762, cosSim, 1e-9);
    }
}

