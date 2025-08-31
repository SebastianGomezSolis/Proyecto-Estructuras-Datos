package Indice;

import Arreglos.Documento;
import Arreglos.ListaDobleCircular;
import Arreglos.Vector;
import Arreglos.Nodo;

import java.util.Arrays;

public class Buscador {
    private IndiceInvertido indice;
    private ProcesarTexto procesar;
    private EstrategiaSimilitud estrategia;

    public Buscador(IndiceInvertido indice) {
        this.indice = indice;
        this.procesar = new ProcesarTexto();
        this.estrategia = new SimilitudCoseno(); // Estrategia por defecto
    }

    public void setEstrategia(EstrategiaSimilitud estrategia) {

        this.estrategia = estrategia;
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
        for (int i = 0; i < totalTerminos - 1; i++) {
            for (int j = 0; j < totalTerminos - i - 1; j++) {
                if (arreglo[j].getTermino().compareTo(arreglo[j + 1].getTermino()) > 0) { // compareTo: comparar objetos entre si para poder ordenarlos
                    TerminoEntry aux = arreglo[j];
                    arreglo[j] = arreglo[j + 1];
                    arreglo[j + 1] = aux;
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

        //System.out.println("DEBUG - Términos en consulta: " + Arrays.toString(texto));
        //System.out.println("DEBUG - Términos en índice: " + ordenados.length);

        for (int i = 0; i < size; i++) {
            String termino = ordenados[i].getTermino();
            int tf = frecuencia(texto, termino);
            double idf = indice.calcularIDF(termino);

            //System.out.println("DEBUG - " + termino + ": TF=" + tf + ", IDF=" + idf);

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

        Documento[] docs = new Documento[n];
        Double[] sims = new Double[n];

        Nodo<Documento> nodoDoc = documentos.getRoot();
        Nodo<Double> nodoSim = similitudes.getRoot();

        for (int i = 0; i < n; i++) {
            docs[i] = nodoDoc.getDato();
            sims[i] = nodoSim.getDato();
            nodoDoc = nodoDoc.getSiguiente();
            nodoSim = nodoSim.getSiguiente();
        }

        // Selection sort descendente por similitud
        for (int i = 0; i < n - 1; i++) {
            int maxIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (sims[j] > sims[maxIdx]) {
                    maxIdx = j;
                }
            }
            // Intercambiar similitud
            double tempSim = sims[i];
            sims[i] = sims[maxIdx];
            sims[maxIdx] = tempSim;

            // Intercambiar documento
            Documento tempDoc = docs[i];
            docs[i] = docs[maxIdx];
            docs[maxIdx] = tempDoc;
        }

        // Reconstruir lista de documentos ordenada
        documentos.setRoot(null);
        for (int i = 0; i < n; i++) {
            documentos.insertar(docs[i]);
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
                double sim = estrategia.calcular(vectorConsulta, vectorDoc);
                if (sim > 0) {
                    doc.setRelevancia(sim);
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
