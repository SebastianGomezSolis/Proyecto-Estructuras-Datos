package Indice;

import Arreglos.Vector;

public class SimilitudCoseno implements EstrategiaSimilitud {
    @Override
    public double calcular(Vector consulta, Vector documento) {
        return consulta.cosineSimilarity(documento);
    }
}
