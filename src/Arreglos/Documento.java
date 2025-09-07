// ==========================
// Package: Arreglos
// ==========================
package Arreglos;

import java.io.Serializable;

public class Documento implements Serializable{
    private String id;        // nombre del archivo
    private String contenido; // texto completo del archivo
    private String ruta;      // ruta absoluta en disco

    public Documento(String id, String contenido, String ruta) {
        this.id = id;
        this.contenido = contenido;
        this.ruta = ruta;
    }

    public String getId() { return id; }
    public String getContenido() { return contenido; }
    public String getRuta() { return ruta; }
}
