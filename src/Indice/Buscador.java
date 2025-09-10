package Indice;

import Arreglos.*;
import Utilidades.Ordenador;

public class Buscador {
    private IndiceInvertido indice;
    private EstrategiaSimilitud estrategia = new SimilitudCoseno();
    private ProcesarTexto procesar = new ProcesarTexto();

    // Stopwords fijas para filtrar términos comunes si se desea
    private static final String[] STOPWORDS = {"el","la","los","las","un","una","de","y"};

    // Constructor del buscador que recibe la instancia del índice invertido.
    public Buscador(IndiceInvertido indice) {
        this.indice = indice;
    }

    // Se realiza una búsqueda para la consulta dada.
    // Devuelve una lista con los resultados ordenados por similitud descendente.
    public ListaDobleCircular<ResultadoBusqueda> buscar(String consulta) {
        // Se limpia la consulta con stopwords (aquí puedes pasar null si no usas)
        String[] terminos = procesar.limpiar(consulta, STOPWORDS);
        // Construimos el vector TF-IDF de la consulta usando los términos resultantes
        Vector vectorConsulta = vecConsulta(terminos);
        // Lista de resultados que devolveremos (inicialmente vacía)
        ListaDobleCircular<ResultadoBusqueda> resultados = new ListaDobleCircular<>();
        int n = indice.getDocumentos().tamano();

        if (n == 0) return resultados;

        // Arrays auxiliares para almacenar temporalmente documentos y sus similitudes
        Documento[] docsArr = new Documento[n];
        double[] simsArr = new double[n];
        int count = 0;

        // Recorremos todos los documentos almacenados en el índice
        for (Documento doc : indice.getDocumentos()) {
            // Obtenemos el vector TF-IDF del documento actual
            Vector vectorDoc = indice.obtenerVectorDocumento(doc.getId());
            // Calculamos similitud entre vector de consulta y vector del documento
            double sim = estrategia.calcular(vectorConsulta, vectorDoc);
            // Si la similitud es mayor que 0, la almacenamos
            if (sim > 0) {
                docsArr[count] = doc;
                simsArr[count] = sim;
                count++;
            }
        }

        // Si ningún documento tuvo similitud positiva, devolvemos la lista vacía
        if (count == 0) return resultados;

        // Recortamos arreglos a count
        Documento[] docsValidos = new Documento[count];
        double[] simsValidos = new double[count];
        for (int i = 0; i < count; i++) {
            docsValidos[i] = docsArr[i];
            simsValidos[i] = simsArr[i];
        }

        // === ORDENAMIENTO POR SIMILITUD ===
        int factor = 1000000; // precisión de 6 decimales
        int[] simsEnteros = new int[count];
        int[] idx = new int[count];
        for (int i = 0; i < count; i++) {
            simsEnteros[i] = (int) (simsValidos[i] * factor);
            idx[i] = i;
        }

        Ordenador.radixSortPareadoEnteros(simsEnteros, idx);

        // Insertamos en la lista de resultados en orden descendente de similitud:
        // recorremos idx de mayor a menor (por eso empezamos en count - 1)
        for (int k = count - 1; k >= 0; k--) {
            resultados.insertar(new ResultadoBusqueda(docsValidos[idx[k]],simsValidos[idx[k]]));
        }

        return resultados;
    }

    // Metodo para crear
    private Vector vecConsulta(String[] texto) {
        int size = indice.getIndice().tamano();
        Vector vector = new Vector(size);

        // Recorremos cada término (TerminoEntry) en el índice en el mismo orden que fue guardado
        for (TerminoEntry t : indice.getIndice()) {
            int tf = 0;
            for (int i = 0; i < texto.length; i++) {
                if (texto[i].equals(t.getTermino())) {
                    tf++;
                }
            }
            // Calculamos el IDF del término usando el método del índice (log(N/(1+DF)))
            double idf = indice.calcularIDF(t.getTermino());
            vector.insertar(tf * idf);
        }
        // Devolvemos el vector construido para la consulta
        return vector;
    }
}
