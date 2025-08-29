package Indice;

import Arreglos.Documento;
import Arreglos.ListaDobleCircular;
import Arreglos.Vector;
import Arreglos.Nodo;

public class Buscador {
    private IndiceInvertido indice;
    private ProcesarTexto procesar;

    public Buscador(IndiceInvertido indice) {
        this.indice = indice;
        this.procesar = new ProcesarTexto();

    }

    private TerminoEntry[] ordenar() {
        int totalTerminos = indice.getIndice().tamano();
        TerminoEntry[] arreglo = new TerminoEntry[totalTerminos];
        Nodo<TerminoEntry> actual = indice.getIndice().getRoot();
        for (int i = 0; i < totalTerminos; i++) {
            arreglo[i] = actual.getDato();
            actual = actual.getSiguiente();
        }
        // Ordenamiento burbuja
        for (int i = 0; i < totalTerminos -1; i++) {
            for (int j = 0; j < totalTerminos - i -1; j++) {
                if (arreglo[j].getTermino().compareTo(arreglo[j+1].getTermino()) > 0) { // compareTo: comparar objetos entre si para poder ordenarlos
                    TerminoEntry aux = arreglo[j];
                    arreglo[j] = arreglo[j+1];
                    arreglo[j+1] = aux;
                }
            }
        }
        return arreglo;
    }
    //busqueda binaria
    public int busquedaBinaria(TerminoEntry[] arreglo, String terminoBuscado) {
        int inicio = 0;
        int fin = arreglo.length - 1;

        while (inicio <= fin) {
            int medio = (inicio + fin) / 2;
            int comparacion = arreglo[medio].getTermino().compareTo(terminoBuscado);
            if (comparacion == 0) {
                return medio; // encontrado
            } else if (comparacion < 0) {
                inicio = medio + 1;
            } else {
                fin = medio - 1;
            }
        }
        return -1; // no encontrado
    }

    private Vector vecConsulta(String[] texto, TerminoEntry[] ordenados) {
        int size = ordenados.length;
        Vector vector = new Vector(size);

        for (int i = 0; i < size; i++) {
            String termino = ordenados[i].getTermino();
            int tf = frecuencia(texto, termino);
            double idf = indice.calcularIDF(termino);
            vector.insertar(tf * idf);
        }
        return vector;
    }

    private int frecuencia(String[] arreglo, String texto) {
        int count = 0;
        for (int i = 0; i < arreglo.length; i++) {
            if (arreglo[i].equals(texto)) {
                count++;
            }
        }
        return count;
    }

    private void ordenarPorSimilitud(ListaDobleCircular<Documento> documentos, ListaDobleCircular<Double> similitudes) {
        int n = documentos.tamano();
        if (n <= 1) return;

        for (int i = 0; i < n - 1; i++) {
            Nodo<Documento> nodoDocumento = documentos.getRoot();
            Nodo<Double> nodoSimilitud = similitudes.getRoot();

            for (int j = 0; j < n - i - 1; j++) {
                Nodo<Documento> sigDoc = nodoDocumento.getSiguiente();
                Nodo<Double> sigSim = nodoSimilitud.getSiguiente();

                if (nodoSimilitud.getDato() < sigSim.getDato()) {
                    // Intercambiar documentos
                    Documento tempDoc = nodoDocumento.getDato();
                    nodoDocumento.setDato(sigDoc.getDato());
                    sigDoc.setDato(tempDoc);

                    // Intercambiar similitudes
                    Double tempSim = nodoSimilitud.getDato();
                    nodoSimilitud.setDato(sigSim.getDato());
                    sigSim.setDato(tempSim);
                }

                nodoDocumento = nodoDocumento.getSiguiente();
                nodoSimilitud = nodoSimilitud.getSiguiente();
            }
        }
    }

    public ListaDobleCircular<Documento> buscar(String consulta) {
        //limpiar
        String[] terminosConsulta = procesar.textLimpio(consulta, indice.getStopwords());
        //ordenamos
        TerminoEntry[] ordenados = ordenar();
        // TF-IDF
        Vector vectorConsulta = vecConsulta(terminosConsulta, ordenados);
        // similitud coseno
        ListaDobleCircular<Documento> documentos = indice.getDocumentos();
        ListaDobleCircular<Documento> resultados = new ListaDobleCircular<>();
        ListaDobleCircular<Double> similitudes = new ListaDobleCircular<>();

        Nodo<Documento> actualDocumento = documentos.getRoot();
        if (actualDocumento == null) return resultados;
        do {
            Documento doc = actualDocumento.getDato();
            Vector vectorDoc = indice.obtenerVectorDocumento(doc.getId());
            if (vectorDoc != null) {
                double sim = vectorConsulta.cosineSimilarity(vectorDoc);
                if (sim > 0) {
                    resultados.insertar(doc);
                    similitudes.insertar(sim);
                }
            }
            actualDocumento = actualDocumento.getSiguiente();
        } while (actualDocumento != documentos.getRoot());
        //ordenar resultados por similitud
        ordenarPorSimilitud(resultados, similitudes);
        return resultados;
    }
}
