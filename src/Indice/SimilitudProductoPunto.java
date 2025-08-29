package Indice;

import Arreglos.Vector;

public class SimilitudProductoPunto implements EstrategiaSimilitud {
    @Override
    public double calcular(Vector consulta, Vector documento) {
        return consulta.productoPunto(documento);
    }
}
