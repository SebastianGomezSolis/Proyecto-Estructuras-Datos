package Indice;

import Arreglos.Documento;

public class ResultadoBusqueda {
    private Documento documento;
    private double similitud;

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

