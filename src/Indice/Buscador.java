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

    public ListaDobleCircular<Documento> buscar(String consulta) {
        System.out.println("=== DEBUG BÚSQUEDA ===");
        System.out.println("DEBUG - Consulta original: '" + consulta + "'");

        // 1. Limpiar la consulta
        String[] terminosConsulta = procesar.textLimpio(consulta, indice.getStopwords());

        // Mostrar términos sin usar Arrays.toString()
        System.out.print("DEBUG - Términos después de limpieza: [");
        for (int i = 0; i < terminosConsulta.length; i++) {
            System.out.print("'" + terminosConsulta[i] + "'");
            if (i < terminosConsulta.length - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");

        if (terminosConsulta.length == 0) {
            System.out.println("DEBUG - No quedaron términos después de filtrar stopwords");
            return new ListaDobleCircular<>();
        }

        // 2. Verificar que el índice tenga datos
        System.out.println("DEBUG - Términos en índice: " + indice.getIndice().tamano());
        System.out.println("DEBUG - Documentos en índice: " + indice.getDocumentos().tamano());

        // 3. Ordenar términos del índice
        TerminoEntry[] ordenados = ordenar();
        System.out.println("DEBUG - Términos ordenados: " + ordenados.length);

        // Mostrar algunos términos del índice para verificar
        System.out.println("DEBUG - Primeros términos en índice ordenado:");
        for (int i = 0; i < Math.min(5, ordenados.length); i++) {
            System.out.println("  " + ordenados[i].getTermino() + " (" + ordenados[i].getVeces() + " veces)");
        }

        // 4. Crear vector de consulta
        Vector vectorConsulta = vecConsulta(terminosConsulta, ordenados);
        System.out.println("DEBUG - Vector consulta creado");

        // 5. Calcular similitudes
        ListaDobleCircular<Documento> documentos = indice.getDocumentos();
        ListaDobleCircular<Documento> resultados = new ListaDobleCircular<>();

        Nodo<Documento> actualDocumento = documentos.getRoot();
        if (actualDocumento == null) {
            System.out.println("DEBUG - No hay documentos para procesar");
            return resultados;
        }

        int docsProcessed = 0;
        do {
            Documento doc = actualDocumento.getDato();
            Vector vectorDoc = doc.getVectorTFIDF();

            System.out.println("DEBUG - Procesando documento: " + doc.getId());

            if (vectorDoc != null) {
                System.out.println("DEBUG - Vector documento existe");
                double sim = estrategia.calcular(vectorConsulta, vectorDoc);
                System.out.println("DEBUG - Similitud calculada: " + sim);

                if (sim > 0) {
                    doc.setRelevancia(sim);
                    resultados.insertar(doc);
                    System.out.println("DEBUG - Documento agregado a resultados con relevancia: " + sim);
                }
            } else {
                System.out.println("DEBUG - Vector TF-IDF del documento es null");
            }

            actualDocumento = actualDocumento.getSiguiente();
            docsProcessed++;
        } while (actualDocumento != documentos.getRoot());

        System.out.println("DEBUG - Documentos procesados: " + docsProcessed);
        System.out.println("DEBUG - Resultados encontrados: " + resultados.tamano());

        // 6. Ordenar resultados por relevancia
        if (!resultados.vacia()) {
            resultados.ordenarPorRelevancia();
        }

        System.out.println("=== FIN DEBUG BÚSQUEDA ===");
        return resultados;
    }

}