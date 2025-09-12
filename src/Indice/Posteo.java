package Indice;

import java.io.Serializable;

// Un posteo representa la aparición de un término en un documento.
// Es el par (docId, frecuencia).
// Por ejemplo: “en el documento D1, este término aparece 7 veces”.
// Cada término tendrá una lista de posteos, uno por documento distinto.
public class Posteo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String docId;  // ID del documento donde aparece el término
    private int tf;        // Frecuencia (cantidad de veces que aparece el término en ese documento)

    // Constructor: se crea un posteo con frecuencia inicial 1
    public Posteo(String docId) {
        this.docId = docId;
        this.tf = 1; // Al agregarlo por primera vez, el término aparece una vez
    }

    // Getter para el ID del documento
    public String getDocId() { return docId; }

    // Getter para la frecuencia del término
    public int getTf() { return tf; }

    // Incrementa la frecuencia (cuando se vuelve a encontrar el mismo término en el mismo documento)
    public void incrementar() { tf++; }
}
