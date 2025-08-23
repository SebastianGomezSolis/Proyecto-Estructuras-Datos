package Arreglos;

public class Documento {
    private String id;        // normalmente el nombre del archivo
    private String contenido; // texto completo del archivo
    private String ruta;      // ruta absoluta en disco

    public Documento(String id, String contenido, String ruta) {
        this.id = id;
        this.contenido = contenido;
        this.ruta = ruta;
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
}

