package Indice;
import Arreglos.ListaDobleCircular;
import java.io.Serializable;

public class TerminoEntry implements Serializable {
    private static final long serialVersionUID = 1L;

    private String termino;
    private ListaDobleCircular<Posteo> posteos;
    private int veces; // total en corpus

    public TerminoEntry(String termino) {
        this.termino = termino;
        this.posteos = new ListaDobleCircular<>();
        this.veces = 0;
    }

    public String getTermino() { return termino; }

    public void agregarOcurrencia(String docId) {
        for (Posteo p : posteos) {
            if (p.getDocId().equals(docId)) {
                p.incrementar();
                veces++;
                return;
            }
        }
        posteos.insertar(new Posteo(docId));
        veces++;
    }

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
