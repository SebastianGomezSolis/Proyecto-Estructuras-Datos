package Arreglos;

public class Documento {
    private String id;        // normalmente el nombre del archivo
    private String contenido; // texto completo del archivo
    private String ruta;      // ruta absoluta en disco
    private double relevancia;

    public Documento(String id, String contenido, String ruta) {
        this.id = id;
        this.contenido = contenido;
        this.ruta = ruta;
        this.relevancia = 0.0;
    }

    public String getId() {
        return id;
    }
    public String getContenido() {
        return contenido;
    }
    public String getRuta() {
        return ruta;
    }

    public double getRelevancia() {
        return relevancia;
    }

    public void setRelevancia(double relevancia) {
        this.relevancia = relevancia;
    }

}

