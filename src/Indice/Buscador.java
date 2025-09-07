package Indice;

import Arreglos.*;
import Utilidades.Ordenador;

public class Buscador {
    private IndiceInvertido indice;
    private EstrategiaSimilitud estrategia = new SimilitudCoseno();
    private ProcesarTexto procesar = new ProcesarTexto();

    // si quieres manejar stopwords fijas
    private static final String[] STOPWORDS = {"el","la","los","las","un","una","de","y"};

    public Buscador(IndiceInvertido indice) {
        this.indice = indice;
    }

    public ListaDobleCircular<ResultadoBusqueda> buscar(String consulta) {
        // limpiar consulta con stopwords (aquí puedes pasar null si no usas)
        String[] terminos = procesar.limpiar(consulta, STOPWORDS);
        Vector vectorConsulta = vecConsulta(terminos);

        ListaDobleCircular<ResultadoBusqueda> resultados = new ListaDobleCircular<>();
        int n = indice.getDocumentos().tamano();

        if (n == 0) return resultados;

        Documento[] docsArr = new Documento[n];
        double[] simsArr = new double[n];
        int count = 0;

        for (Documento doc : indice.getDocumentos()) {
            Vector vectorDoc = indice.obtenerVectorDocumento(doc.getId());
            double sim = estrategia.calcular(vectorConsulta, vectorDoc);
            if (sim > 0) {
                docsArr[count] = doc;
                simsArr[count] = sim;
                count++;
            }
        }

        if (count == 0) return resultados;

        // recortamos arreglos a count
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

        for (int k = count - 1; k >= 0; k--) {
            resultados.insertar(new ResultadoBusqueda(docsValidos[idx[k]],simsValidos[idx[k]]));
        }

        return resultados;
    }

    private Vector vecConsulta(String[] texto) {
        int size = indice.getIndice().tamano();
        Vector vector = new Vector(size);

        for (TerminoEntry t : indice.getIndice()) {
            int tf = 0;
            for (int i = 0; i < texto.length; i++) {
                if (texto[i].equals(t.getTermino())) {
                    tf++;
                }
            }
            double idf = indice.calcularIDF(t.getTermino());
            vector.insertar(tf * idf);
        }
        return vector;
    }
}
