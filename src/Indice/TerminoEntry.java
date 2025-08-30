package Indice;

import Arreglos.ListaDobleCircular;
import Arreglos.Nodo;

import java.io.Serializable;

public class TerminoEntry implements Serializable {
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

    public int compararAlfabeticamente(TerminoEntry otro) {
        return this.termino.compareToIgnoreCase(otro.termino);
    }

    // Método que compara el término interno con otro término dado
    public boolean esIgual(String otroTermino) {
        // Ignora mayúsculas/minúsculas para que la búsqueda sea más flexible
        return this.termino.equalsIgnoreCase(otroTermino);
    }

    public int getFrecuenciaEnDocumento(String docId) {
        int count = 0;
        if (documentosIds.vacia()) return 0;

        Nodo<String> actual = documentosIds.getRoot();
        do {
            if (actual.getDato().equals(docId)) {
                count++;
            }
            actual = actual.getSiguiente();
        } while (actual != documentosIds.getRoot());

        return count;
    }


}
