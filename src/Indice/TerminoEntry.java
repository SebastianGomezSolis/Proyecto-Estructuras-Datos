package Indice;

import Arreglos.ListaDobleCircular;
import java.io.Serializable;

// Clase que representa un término dentro del índice invertido.
// Contiene el término (palabra), la lista de documentos donde aparece (posteos),
// y la frecuencia total de aparición en el corpus.
public class TerminoEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private String termino;                        // El término (palabra)
    private ListaDobleCircular<Posteo> posteos;    // Lista de documentos donde aparece (con frecuencia)
    private int veces;                             // Frecuencia total del término en el corpus

    // Constructor que inicializa un término con su lista vacía de posteos
    public TerminoEntry(String termino) {
        this.termino = termino;
        this.posteos = new ListaDobleCircular<>();
        this.veces = 0;
    }

    // Devuelve el término (palabra)
    public String getTermino() { return termino; }

    // Se agrega una ocurrencia de este término en un documento.
    // Si ya hay un posteo para ese docId, incrementa su frecuencia.
    // Si no, se crea un nuevo posteo.
    public void agregarOcurrencia(String docId) {
        // Recorremos los posteos actuales del término
        for (Posteo p : posteos) {
            // Si ya existe un posteo para este documento
            if (p.getDocId().equals(docId)) {
                p.incrementar(); // Aumenta la frecuencia (tf) en ese documento
                veces++;         // Aumenta la frecuencia total del término en el corpus
                return;
            }
        }
        // Si no se encontró un posteo para ese documento, se crea uno nuevo
        posteos.insertar(new Posteo(docId));
        veces++; // Se suma la primera aparición en ese documento
    }

    // Devuelve cuántas veces aparece este término en un documento específico
    public int tfEnDocumento(String docId) {
        for (Posteo p : posteos) {
            if (p.getDocId().equals(docId)) return p.getTf();
        }
        return 0; // Si el término no aparece en ese documento
    }

    // Devuelve cuántos documentos diferentes contienen este término
    public int cuantosDocumentos() {
        return posteos.tamano();
    }

    // Devuelve la frecuencia total del término en todo el corpus
    public int getVeces() {
        return veces;
    }

    // Devuelve la lista de posteos (dónde aparece este término)
    public ListaDobleCircular<Posteo> getPosteos() {
        return posteos;
    }

    // Permite modificar directamente la frecuencia total (usado al cargar desde archivo)
    public void setVeces(int veces) {
        this.veces = veces;
    }
}
