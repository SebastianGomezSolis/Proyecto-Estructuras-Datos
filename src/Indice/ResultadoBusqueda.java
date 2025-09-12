package Indice;

import Arreglos.Documento;

// Esta clase representa un resultado de búsqueda.
// Guarda un documento junto con su puntuación de similitud
// (por ejemplo, usando el coseno entre vectores TF-IDF).
public class ResultadoBusqueda {
    private Documento documento; // Documento que coincide con la búsqueda
    private double similitud; // Valor numérico de similitud con la consulta

    public ResultadoBusqueda(Documento documento, double similitud) {
        this.documento = documento;
        this.similitud = similitud;
    }

    public Documento getDocumento() {
        return documento;
    }

    public double getSimilitud() {
        return similitud;
    }
}

