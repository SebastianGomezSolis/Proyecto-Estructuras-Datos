package Indice;

import Arreglos.*;
import Utilidades.Ordenador;

// Clase encargada de realizar búsquedas sobre el índice invertido
public class Buscador {

    private IndiceInvertido indice; // Referencia al índice invertido sobre el que se buscará

    // Estrategia de similitud (por defecto: similitud de coseno)
    private EstrategiaSimilitud estrategia = new SimilitudCoseno();

    // Clase para limpiar texto (eliminar stopwords)
    private ProcesarTexto procesar = new ProcesarTexto();

    // Lista fija de palabras vacías (stopwords)
    private static final String[] STOPWORDS = {"el","la","los","las","un","una","de","y"};

    // Constructor que recibe el índice ya construido
    public Buscador(IndiceInvertido indice) {
        this.indice = indice;
    }

    // Metodo principal para realizar una búsqueda a partir de una cadena de texto (consulta)
    public ListaDobleCircular<ResultadoBusqueda> buscar(String consulta) {

        // 1. Limpiar la consulta (eliminar stopwords, etc.)
        String[] terminos = procesar.limpiar(consulta, STOPWORDS);

        // 2. Convertir la consulta en un vector TF-IDF
        Vector vectorConsulta = vecConsulta(terminos);

        // 3. Lista para almacenar resultados
        ListaDobleCircular<ResultadoBusqueda> resultados = new ListaDobleCircular<>();
        int n = indice.getDocumentos().tamano();

        // Si no hay documentos, no hay resultados
        if (n == 0) return resultados;

        // Arreglos auxiliares para almacenar documentos y sus similitudes
        Documento[] docsArr = new Documento[n];
        double[] simsArr = new double[n];
        int count = 0;

        // 4. Calcular similitud entre la consulta y cada documento del índice
        for (Documento doc : indice.getDocumentos()) {
            Vector vectorDoc = indice.obtenerVectorDocumento(doc.getId()); // vector del documento
            double sim = estrategia.calcular(vectorConsulta, vectorDoc);   // similitud entre consulta y documento

            if (sim > 0) {
                // Solo almacenamos los documentos con similitud > 0
                docsArr[count] = doc;
                simsArr[count] = sim;
                count++;
            }
        }

        // Si ningún documento tuvo similitud > 0, devolvemos lista vacía
        if (count == 0) return resultados;

        // 5. Recortar los arreglos a la cantidad real de resultados válidos
        Documento[] docsValidos = new Documento[count];
        double[] simsValidos = new double[count];
        for (int i = 0; i < count; i++) {
            docsValidos[i] = docsArr[i];
            simsValidos[i] = simsArr[i];
        }

        // === ORDENAMIENTO POR SIMILITUD DESCENDENTE ===
        int factor = 1000000; // factor de escala para evitar problemas con decimales al usar radixSort

        int[] simsEnteros = new int[count]; // similitudes convertidas a enteros
        int[] idx = new int[count];         // índices para mantener referencia a los documentos originales

        for (int i = 0; i < count; i++) {
            simsEnteros[i] = (int) (simsValidos[i] * factor); // pasar a entero
            idx[i] = i; // guardar posición original
        }

        // Ordenamos los arrays por similitud usando radixSort (menor a mayor)
        Ordenador.radixSortPareadoEnteros(simsEnteros, idx);

        // 6. Insertamos los resultados en orden descendente (más relevante primero)
        for (int k = count - 1; k >= 0; k--) {
            resultados.insertar(new ResultadoBusqueda(docsValidos[idx[k]], simsValidos[idx[k]]));
        }

        return resultados;
    }

    // Método auxiliar que genera el vector TF-IDF de la consulta
    private Vector vecConsulta(String[] texto) {
        int size = indice.getIndice().tamano(); // tamaño del vocabulario del índice
        Vector vector = new Vector(size);       // nuevo vector de ese tamaño

        // Recorremos todos los términos del índice (orden fijo)
        for (TerminoEntry t : indice.getIndice()) {
            int tf = 0;

            // Contamos cuántas veces aparece el término de índice en la consulta
            for (int i = 0; i < texto.length; i++) {
                if (texto[i].equals(t.getTermino())) {
                    tf++;
                }
            }

            // Obtenemos el IDF del término y calculamos el valor TF-IDF
            double idf = indice.calcularIDF(t.getTermino());
            vector.insertar(tf * idf); // lo insertamos en la posición correspondiente del vector
        }

        return vector;
    }
}
