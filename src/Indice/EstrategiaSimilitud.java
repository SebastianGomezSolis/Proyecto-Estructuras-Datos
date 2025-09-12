package Indice;

import Arreglos.Vector;

// Esta interfaz define una estrategia para calcular la similitud entre dos vectores.
// Es útil para aplicar el patrón de diseño "Estrategia", donde se pueden implementar
// múltiples formas de medir similitud entre una consulta y un documento (coseno, jaccard, etc.)
public interface EstrategiaSimilitud {

    // Método que debe implementar cualquier estrategia de similitud.
    // Recibe dos vectores: uno de la consulta y otro del documento.
    // Devuelve un valor double que representa qué tan similares son.
    double calcular(Vector consulta, Vector documento);
}
