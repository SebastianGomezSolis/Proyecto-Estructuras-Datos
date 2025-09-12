import Arreglos.*;
import Indice.*;
import java.util.Scanner;
import Persistencia.Archivos;

// Main que gestiona la interacción con el usuario:
// - Cargar documentos
// - Construir índice
// - Aplicar Ley de Zipf (pregunta al usuario el percentil a eliminar)
// - Realizar búsquedas
// - Guardar/cargar índice


public class principal {
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        IndiceInvertido indice = IndiceInvertido.getInstance();
        Buscador buscador = new Buscador(indice);
        Archivos arch = new Archivos();

        boolean salir = false;
        while (!salir) {
            System.out.println("\n===== MENU =====");
            System.out.println("1. Cargar documentos desde carpeta");
            System.out.println("2. Construir índice invertido");
            System.out.println("3. Aplicar ley de Zipf");
            System.out.println("4. Realizar búsqueda");
            System.out.println("5. Guardar índice");
            System.out.println("6. Cargar índice");
            System.out.println("7. Ver términos del índice");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");
            int opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1: {
                    System.out.print("Ingresa la ruta de la carpeta: ");
                    String ruta = sc.nextLine();

                    ListaDobleCircular<Documento> docs = arch.cargarDocumentos(ruta);

                    if (!docs.vacia()) {
                        indice.construirIndice(docs);
                        System.out.println("Documentos cargados y enviados al índice.");
                    }
                    break;
                }

                case 2:
                    System.out.println("Construyendo índice invertido...");
                    System.out.println("Índice creado con " + indice.getIndice().tamano() + " términos.");
                    break;

                case 3:
                    int percentil = -1;
                    while (true) {
                        System.out.print("Ingrese percentil a eliminar (0–10): ");
                        String linea = sc.nextLine().trim();
                        try {
                            percentil = Integer.parseInt(linea);
                        } catch (NumberFormatException e) {
                            System.out.println("Ingrese un número entero");
                            continue;
                        }
                        if (percentil >= 0 && percentil <= 10) break;
                        System.out.println("Debe estar entre 0 y 10.");
                    }

                    if (percentil > 0) {
                        int antes = indice.getIndice().tamano();
                        indice.aplicarZipf(percentil); // aplicar Zipf con percentil
                        int despues = indice.getIndice().tamano();
                        System.out.println("Se eliminaron " + (antes - despues) + " términos.");
                        System.out.println("Índice después de aplicar Zipf: " + despues + " términos.");
                    } else {
                        System.out.println("Percentil 0: no se eliminarán términos.");
                    }
                    break;

                case 4:
                    System.out.print("Escribe tu consulta: ");
                    String consulta = sc.nextLine();
                    ListaDobleCircular<ResultadoBusqueda> resultados = buscador.buscar(consulta);

                    if (resultados.vacia()) {
                        System.out.println("No se encontraron documentos.");
                    } else {
                        System.out.println("Resultados:");
                        for (ResultadoBusqueda r : resultados) {
                            System.out.printf(" - %s | Similitud: %.3f%n",
                                    r.getDocumento().getId(),
                                    r.getSimilitud());
                        }
                    }
                    break;


                case 5:
                    System.out.print("Nombre del archivo binario para guardar: ");
                    String archivoGuardar = sc.nextLine();
                    if (indice.guardarIndice(archivoGuardar)) {
                        System.out.println("Índice guardado en " + archivoGuardar);
                    } else {
                        System.out.println("Error al guardar el índice.");
                    }
                    break;

                case 6:
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

                case 7:
                    System.out.println("=== Términos del índice ===");
                    int i = 1;
                    for (TerminoEntry t : indice.getIndice()) {
                        System.out.print(i + ". " + t.getTermino() + " (Veces: " + t.getVeces() + ")");

                        // Mostrar documentos donde aparece
                        System.out.print(" [Documentos: ");
                        for (Posteo p : t.getPosteos()) {
                            System.out.print(p.getDocId() + "(" + p.getTf() + ") ");
                        }
                        System.out.println("]");
                        i++;
                    }
                    System.out.println("==========================");
                    break;

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

    /*private static ListaDobleCircular<Documento> cargarDocumentos(String rutaCarpeta) {
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
    }*/
}