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
    public ListaDobleCircular<Documento> buscar(String consulta) {
        // Paso 1: procesar texto (tokenizar, normalizar, quitar stopwords)
        String[] terminos = procesador.textLimpio(consulta, indice.getStopwords());

        // Paso 2: construir vector de la consulta
        Vector vectorConsulta = construirVectorConsulta(terminos);

        // Paso 3: recorrer todos los documentos y calcular similitud
        ListaDobleCircular<Documento> documentos = indice.getDocumentos();
        ListaDobleCircular<Documento> resultados = new ListaDobleCircular<>();
        ListaDobleCircular<Double> similitudes = new ListaDobleCircular<>();

        Nodo<Documento> actualDoc = documentos.getRoot();
        if (actualDoc == null) return resultados;

        do {
            Documento doc = actualDoc.getDato();
            Vector vectorDoc = indice.obtenerVectorDocumento(doc.getId());

            if (vectorDoc != null) {
                double sim = vectorConsulta.cosineSimilarity(vectorDoc);
                if (sim > 0) {
                    resultados.insertar(doc);
                    similitudes.insertar(sim);
                }
            }

            actualDoc = actualDoc.getSiguiente();
        } while (actualDoc != documentos.getRoot());

        // Paso 4: ordenar resultados por similitud descendente
        ordenarPorSimilitud(resultados, similitudes);

        return resultados;
    }
}
