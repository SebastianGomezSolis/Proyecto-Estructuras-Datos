package Persistencia;

import Arreglos.Documento;
import Arreglos.ListaDobleCircular;
import java.io.File;

public class Archivos {
    // Clase responsable de cargar archivos
    private Documento cargarDocumento(java.io.File archivo) {
        // try-with-resources para cerrar el reader automáticamente, esto es incluso si ocurre alguna excepción
        //BufferedReader es el recurso en este caso, esta es una clase que permite leer texto linea por linea de una forma eficiente
        try (java.io.BufferedReader reader = new java.io.BufferedReader(
                // leemos como UTF-8
                //FileReader sirve para abrir el archivo, archivo es un objeto file que apunta al archivo que queremos
                //StandardCharsets.UTF_8, esto permite poder soportar caracteres especiales (tildes, ñ, etc)
                new java.io.FileReader(archivo, java.nio.charset.StandardCharsets.UTF_8))) {

            //aqui guardamos-acumulamos el texto
            StringBuilder contenido = new StringBuilder();
            String linea;

            //leemos linea por linea
            while ((linea = reader.readLine()) != null) {
                contenido.append(linea).append(" "); //y aquí lo que realizamos es agregar la línea más un espacio
            }

            String id = archivo.getName(); //le damos nombre al archivo
            //creamos el documento con el texto completo y la ruta
            return new Documento(id, contenido.toString(), archivo.getAbsolutePath());
        } catch (Exception e) {
            return null;
        }
    }

    public ListaDobleCircular<Documento> cargarDesdeCarpeta(String rutaCarpeta) {
        ListaDobleCircular<Documento> documentos = new ListaDobleCircular<>();

        File carpeta = new File(rutaCarpeta);
        if (!carpeta.exists() || !carpeta.isDirectory()) {
            System.out.println("Carpeta inválida: " + rutaCarpeta);
            return documentos;
        }
        File[] archivos = carpeta.listFiles();
        if (archivos == null || archivos.length == 0) {
            System.out.println("No se encontraron archivos en la carpeta.");
            return documentos;
        }
        for (File archivo : archivos) {
            if (archivo.isFile() && archivo.getName().endsWith(".txt")) {
                Documento doc = cargarDocumento(archivo);
                if (doc != null) {
                    documentos.insertar(doc);
                } else {
                    System.out.println(" No se pudo leer: " + archivo.getName());
                }
            }
        }
        return documentos;
    }
}
