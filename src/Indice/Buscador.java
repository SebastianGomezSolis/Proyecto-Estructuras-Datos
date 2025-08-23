package Indice;
import Arreglos.Documento;
import Arreglos.ListaDobleCircular;
import Arreglos.Vector;
import Arreglos.Nodo;

public class Buscador {
    private IndiceInvertido indice;
    private ProcesarTexto procesar;
    private TerminoEntry[] ordenados;

    public Buscador(IndiceInvertido indice) {
        this.indice = indice;
        this.procesar = new ProcesarTexto();

    }
    public Documento[] buscar (String consulta){

    }
}
