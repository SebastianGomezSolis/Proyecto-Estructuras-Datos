package Indice;

import Arreglos.ListaDobleCircular;
import java.io.Serializable;

// Clase que representa un término dentro del índice invertido.
public class TerminoEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private String termino;
    private ListaDobleCircular<Posteo> posteos;
    private int veces; // total en corpus

    // Constructor que inicializa un término vacío
    public TerminoEntry(String termino) {
        this.termino = termino;
        this.posteos = new ListaDobleCircular<>();
        this.veces = 0;
    }

    public String getTermino() { return termino; }

    // Se agrega una ocurrencia de este término en un documento.
    // Si el documento ya está en la lista de posteos, incrementa su frecuencia.
    // Si no, crea un nuevo posteo para ese documento.
    public void agregarOcurrencia(String docId) {
        // Recorremos la lista de posteos existentes
        for (Posteo p : posteos) {
            // Si ya existe un posteo para este doc
            if (p.getDocId().equals(docId)) {
                p.incrementar();
                veces++;
                return;
            }
        }
        // Si no existía un posteo para este doc, creamos uno nuevo
        posteos.insertar(new Posteo(docId));
        veces++;
    }

    // Devuelve la frecuencia (tf) de este término en un documento específico
    public int tfEnDocumento(String docId) {
        for (Posteo p : posteos) {
            if (p.getDocId().equals(docId)) return p.getTf();
        }
        return 0;
    }

    public int cuantosDocumentos() { return posteos.tamano(); }
    public int getVeces() { return veces; }
    public ListaDobleCircular<Posteo> getPosteos() { return posteos; }

    public void setVeces(int veces) {this.veces = veces;}
}
