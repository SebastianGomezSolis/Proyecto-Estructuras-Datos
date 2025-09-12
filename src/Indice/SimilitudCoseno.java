package Indice;

import Arreglos.Vector;

// Esta clase implementa la estrategia de cálculo de similitud usando el coseno.
// Forma parte del patrón estrategia, implementando la interfaz EstrategiaSimilitud.
public class SimilitudCoseno implements EstrategiaSimilitud {

    // Implementación del método calcular, que recibe dos vectores:
    // uno que representa la consulta, y otro que representa un documento.
    // Devuelve la similitud del coseno entre ambos.
    @Override
    public double calcular(Vector consulta, Vector documento) {
        return consulta.cosineSimilarity(documento); // Llama al método definido en la clase Vector
    }
}
