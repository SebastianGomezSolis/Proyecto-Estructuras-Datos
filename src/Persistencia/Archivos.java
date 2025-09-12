package Persistencia;

import Arreglos.Documento;
import Arreglos.ListaDobleCircular;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Archivos {

    public static ListaDobleCircular<Documento> cargarDocumentos(String rutaCarpeta) {
        // lista vacía
        ListaDobleCircular<Documento> docs = new ListaDobleCircular<>();

        //objeto File para la carpeta recibida por parametro.
        File carpeta = new File(rutaCarpeta);
        if (!carpeta.exists() || !carpeta.isDirectory()) { // verificar ruta
            System.out.println("La ruta no es válida.");
            return docs;
        }
        // obtener archivos .txt
        File[] archivos = carpeta.listFiles((dir, name) -> name.endsWith(".txt"));
        if (archivos != null) {
            for (File f : archivos) { // recorre cada .txt
                try {
                    // leer contenido y guardar
                    String contenido = new String(Files.readAllBytes(f.toPath()));
                    Documento d = new Documento(f.getName(), contenido, f.getAbsolutePath()); // crea un nuevo doc
                    docs.insertar(d); // agregamos a la lista
                } catch (IOException e) {
                    System.out.println("Error al leer archivo: " + f.getName());
                }
            }
        }
        return docs;
    }

}
