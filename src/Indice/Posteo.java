package Indice;
import java.io.Serializable;

//Un posteo es el par (docId, frecuencia).
//Es decir: “en el documento D1, este término aparece 7 veces”.
//Entonces, cada término tendrá una lista de posteos (uno por documento distinto).
public class Posteo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String docId;
    private int tf;

    public Posteo(String docId) {
        this.docId = docId;
        this.tf = 1;
    }

    public String getDocId() { return docId; }
    public int getTf() { return tf; }
    public void incrementar() { tf++; }
}
