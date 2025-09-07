package Arreglos;

public class Vector {
    private double[] vector;
    private int size;

    // Constructor para inicializar con arreglo ya existente
    public Vector(double[] valores) {
        this.vector = valores;
        this.size = valores.length;
    }

    // Constructor para inicializar con capacidad fija
    public Vector(int tam) {
        this.vector = new double[tam];
        this.size = 0;
    }

    // Insertar un valor al final
    public void insertar(double dato) {
        if (size == vector.length) {
            double[] nuevo = new double[size * 2 + 1]; // +1 por seguridad si size=0
            System.arraycopy(vector, 0, nuevo, 0, size);
            vector = nuevo;
        }
        vector[size++] = dato;
    }

    // Producto punto
    public double productoPunto(Vector otro) {
        if (this.size != otro.size) {
            throw new IllegalArgumentException("Vectores de distinto tamaño");
        }
        double producto = 0;
        for (int i = 0; i < size; i++) {
            producto += this.vector[i] * otro.vector[i];
        }
        return producto;
    }

    // Magnitud del vector
    public double magnitude() {
        double suma = 0;
        for (int i = 0; i < size; i++) {
            suma += vector[i] * vector[i];
        }
        return Math.sqrt(suma);
    }

    // Similitud coseno
    public double cosineSimilarity(Vector otro) {
        double numerador = this.productoPunto(otro);
        double denominador = this.magnitude() * otro.magnitude();
        if (denominador == 0) return 0.0;
        return numerador / denominador;
    }

    // Getters útiles
    public int getSize() {
        return size;
    }

    public double get(int i) {
        return vector[i];
    }

    public double[] getArray() {
        return vector;
    }
}
