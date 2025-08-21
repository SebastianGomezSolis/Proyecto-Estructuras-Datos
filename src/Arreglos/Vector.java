package Arreglos;

public class Vector {
    private double[] vector;
    private int tam;
    private int size;

    public Vector(int tam) {
        this.vector = new double[tam];
        this.tam = tam;
        this.size = 0;
    }

    public void insertar(double dato){
        if (size == tam) {
            tam = tam * 2;
            double[] nuevoVector = new double[tam];
            for (int i = 0; i < size; i++) {
                nuevoVector[i] = vector[i];
            }
            vector = nuevoVector;
        }
        vector[size] = dato;
        size++;
    }

    public void eliminar(double valor) {
        for (int i = 0; i < size; i++) {
            if (vector[i] == valor) {
                for (int j = i; j < size - 1; j++) {
                    vector[j] = vector[j + 1];
                }
                size--;
            }
        }
    }

    public boolean buscar(double valor) {
        for (int i = 0; i < size; i++) {
            if (vector[i] == valor) {
                return true;
            }
        }
        return false;
    }

    public int getSize() {
        return size;
    }

    public int getTam() {
        return tam;
    }

    public double productoPunto(Vector otro) {
        if (this.size != otro.size) {
            throw new IllegalArgumentException("los vectores poseen tamanios diferentes");
        }
        double producto = 0;
        for (int i = 0; i < size; i++) {
            producto += this.vector[i] * otro.vector[i];
        }
        return producto;
    }

    public String mostrar() {
        String cadena = "[";
        for (int i = 0; i < size; i++) {
            cadena += vector[i];
            if (i < size - 1) {
                cadena += ", ";
            }
        }
        cadena += "]";
        return cadena;
    }
}
