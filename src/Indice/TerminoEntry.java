package Indice;

import Arreglos.ListaDobleCircular;
import Arreglos.Nodo;

public class TerminoEntry {
    private String termino;
    private ListaDobleCircular<String> documentosIds;
    private int veces;

    public TerminoEntry(String termino) {
        this.termino = termino;
        this.documentosIds = new ListaDobleCircular<>();
        this.veces = 0;
    }

    public void agregarDocumento(String idDoc) {
        if (!yaEsta(idDoc)) {
            documentosIds.insertar(idDoc);
        }
        veces++;
    }

    private boolean yaEsta(String idDoc) {
        if (documentosIds.vacia()) return false;

        Nodo<String> actual = documentosIds.getRoot();
        do {
            if (actual.getDato().equals(idDoc)) return true;
            actual = actual.getSiguiente();
        } while (actual != documentosIds.getRoot());

        return false;
    }

    public int cuantosDocumentos() {
        return documentosIds.tamano();
    }

    public String getTermino() {
        return termino;
    }

    public ListaDobleCircular<String> getDocumentosIds() {
        return documentosIds;
    }

    public int getVeces() {
        return veces;
    }

    @Override
    public String toString() {
        return termino + " (" + veces + " veces) en " + cuantosDocumentos() + " docs";
    }
}
