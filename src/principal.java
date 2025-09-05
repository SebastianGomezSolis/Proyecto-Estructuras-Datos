import Arreglos.*;
import Indice.*;
import Utilidades.Ordenador;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

public class principal {
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        IndiceInvertido indice = IndiceInvertido.getInstance();
        Buscador buscador = new Buscador(indice);

        boolean salir = false;
        while (!salir) {
            System.out.println("\n===== MENU =====");
            System.out.println("1. Cargar documentos desde carpeta");
            System.out.println("2. Construir índice invertido");
            System.out.println("3. Realizar búsqueda");
            System.out.println("4. Guardar índice");
            System.out.println("5. Cargar índice");
            System.out.println("6. Ver términos del índice");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");
            int opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    System.out.print("Ingresa la ruta de la carpeta con documentos .txt: ");
                    String ruta = sc.nextLine();
                    ListaDobleCircular<Documento> docs = cargarDocumentos(ruta);
                    indice.construirIndice(docs);
                    System.out.println("Documentos cargados y añadidos.");
                    break;

                case 2:
                    System.out.println("Construyendo índice invertido...");
                    // si ya cargaste docs, el índice se construye en el paso anterior
                    System.out.println("Índice creado con " + indice.getIndice().tamano() + " términos.");
                    break;

                case 3:
                    System.out.print("Escribe tu consulta: ");
                    String consulta = sc.nextLine();
                    ListaDobleCircular<Documento> resultados = buscador.buscar(consulta);
                    if (resultados.vacia()) {
                        System.out.println("No se encontraron documentos.");
                    } else {
                        System.out.println("Resultados:");
                        for (Documento d : resultados) {
                            System.out.println(" - " + d.getId() + " (" + d.getRuta() + ")");
                        }
                    }
                    break;

                case 4:
                    System.out.print("Nombre del archivo binario para guardar: ");
                    String archivoGuardar = sc.nextLine();
                    if (indice.guardarIndice(archivoGuardar)) {
                        System.out.println("Índice guardado en " + archivoGuardar);
                    } else {
                        System.out.println("Error al guardar el índice.");
                    }
                    break;

                case 5:
                    System.out.print("Nombre del archivo binario a cargar: ");
                    String archivoCargar = sc.nextLine();
                    IndiceInvertido cargado = IndiceInvertido.cargarIndice(archivoCargar);
                    if (cargado != null) {
                        indice = cargado;
                        buscador = new Buscador(indice);
                        System.out.println("Índice cargado correctamente.");
                    } else {
                        System.out.println("Error al cargar índice.");
                    }
                    break;

                case 6:
//                    System.out.println("Términos en el índice:");
//                    TerminoEntry[] arr = indice.toArrayOrdenado();
//                    for (int i = 0; i < arr.length; i++) {
//                        System.out.println(arr[i]);
//                    }
//                    break;

                case 0:
                    salir = true;
                    System.out.println("Saliendo...");
                    break;

                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    // ==========================
    // Métodos de apoyo
    // ==========================

    private static ListaDobleCircular<Documento> cargarDocumentos(String rutaCarpeta) {
        ListaDobleCircular<Documento> docs = new ListaDobleCircular<>();
        File carpeta = new File(rutaCarpeta);
        if (!carpeta.exists() || !carpeta.isDirectory()) {
            System.out.println("La ruta no es válida.");
            return docs;
        }
        File[] archivos = carpeta.listFiles((dir, name) -> name.endsWith(".txt"));
        if (archivos != null) {
            for (File f : archivos) {
                try {
                    String contenido = new String(Files.readAllBytes(f.toPath()));
                    Documento d = new Documento(f.getName(), contenido, f.getAbsolutePath());
                    docs.insertar(d);
                } catch (IOException e) {
                    System.out.println("Error al leer archivo: " + f.getName());
                }
            }
        }
        return docs;
    }
}
