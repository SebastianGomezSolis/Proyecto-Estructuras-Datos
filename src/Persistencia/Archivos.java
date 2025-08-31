package Persistencia;

import Arreglos.Documento;
import Arreglos.ListaDobleCircular;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;

public class Archivos {

    // Método para cargar un documento desde un archivo
    public Documento cargarDocumento(File archivo) {
        try (BufferedReader reader = new BufferedReader(
                new FileReader(archivo, StandardCharsets.UTF_8))) {

            StringBuilder contenido = new StringBuilder();
            String linea;

            while ((linea = reader.readLine()) != null) {
                contenido.append(linea).append(" ");
            }

            String id = archivo.getName();
            return new Documento(id, contenido.toString(), archivo.getAbsolutePath());

        } catch (Exception e) {
            System.err.println(" Error leyendo archivo: " + archivo.getName());
            return null;
        }
    }

    // Método para cargar todos los .txt desde una carpeta
    public ListaDobleCircular<Documento> cargarDesdeCarpeta(String rutaCarpeta) {
        ListaDobleCircular<Documento> documentos = new ListaDobleCircular<>();
        File carpeta = new File(rutaCarpeta);

        if (!carpeta.exists() || !carpeta.isDirectory()) {
            System.out.println(" Carpeta inválida: " + rutaCarpeta);
            return documentos;
        }

        File[] archivos = carpeta.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

        if (archivos == null || archivos.length == 0) {
            System.out.println(" No se encontraron archivos en la carpeta.");
            return documentos;
        }

        for (File archivo : archivos) {
            Documento doc = cargarDocumento(archivo);
            if (doc != null) {
                documentos.insertar(doc);
            } else {
                System.out.println("No se pudo leer: " + archivo.getName());
            }
        }
        return documentos;
    }
}
