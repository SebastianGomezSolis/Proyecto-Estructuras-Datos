import Arreglos.Documento;
import Arreglos.ListaDobleCircular;
import Arreglos.Nodo;
import Indice.*;
import Persistencia.Archivos;
import Persistencia.Serializar;

import java.util.Scanner;
public class principal {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        IndiceInvertido indice = IndiceInvertido.getInstance();
        Buscador buscador = new Buscador(indice);
        Serializar serializar = new Serializar();
        Archivos gestorArchivos = new Archivos();
        System.out.println("\t\t\tDIOS AYUDA");
        System.out.println("\t\t\t¡Bienvenido al Sistema de Búsqueda!");
        System.out.println("\tEste sistema te permitirá buscar términos dentro de documentos");

        while (true) {
            System.out.println("\n°°°°°°°°°°°°°°°°°°°°°° MENÚ PRINCIPAL °°°°°°°°°°°°°°°°°°°°°°");
            System.out.println("1. Cargar documentos desde carpeta (crear índice)");
            System.out.println("2. Actualizar índice con nuevos documentos");
            System.out.println("3. Realizar una búsqueda");
            System.out.println("4. Mostrar estadísticas del índice");
            System.out.println("5. Aplicar Ley de Zipf");
            System.out.println("6. Cambiar estrategia de similitud");
            System.out.println("7. Guardar índice en archivo");
            System.out.println("8. Cargar índice desde archivo");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");


            int opcion;
            try {
                opcion = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opción inválida. Intente de nuevo.");
                continue;
            }

            switch (opcion) {
                case 1 -> {
                    System.out.print("Ingrese la ruta de la carpeta: ");
                    String ruta = sc.nextLine();
                    ListaDobleCircular<Documento> docs = gestorArchivos.cargarDesdeCarpeta(ruta);
                    if (docs.vacia()) {
                        System.out.println("No se encontraron documentos válidos en la carpeta.");
                    } else {
                        indice.construirIndice(docs);
                        System.out.println(" Índice construido con " + docs.tamano() + " documentos.");
                    }
                }

                case 2 -> {
                    System.out.print("Ingrese la ruta de la carpeta con nuevos documentos: ");
                    String ruta = sc.nextLine();
                    ListaDobleCircular<Documento> nuevos = gestorArchivos.cargarDesdeCarpeta(ruta);
                    if (nuevos.vacia()) {
                        System.out.println("No se encontraron nuevos documentos.");
                    } else {
                        indice.actualizarIndice(nuevos);
                        System.out.println(" Índice actualizado con " + nuevos.tamano() + " documentos nuevos.");
                    }
                }

                case 3 -> {
                    System.out.print("Ingrese su consulta de búsqueda: ");
                    String consulta = sc.nextLine();
                    if (consulta.trim().isEmpty()) {
                        System.out.println("La consulta no puede estar vacía.");
                    } else {
                        ListaDobleCircular<Documento> resultados = buscador.buscar(consulta);
                        if (resultados.vacia()) {
                            System.out.println("No se encontraron resultados para: '" + consulta + "'");
                        } else {
                            System.out.println("\nResultados encontrados (ordenados por relevancia):");
                            Nodo<Documento> actual = resultados.getRoot();
                            int contador = 1;
                            do {
                                Documento doc = actual.getDato();
                                System.out.println(contador + ". " + doc.getId() +
                                        " - Relevancia: " + String.format("%.4f", doc.getRelevancia()));
                                actual = actual.getSiguiente();
                                contador++;
                            } while (actual != resultados.getRoot());
                        }
                    }
                }

                case 4 -> {
                    System.out.println(indice.mostrarEstadisticas());
                }

                case 5 -> {
                    System.out.print("Ingrese el percentil para aplicar la Ley de Zipf (0-100): ");
                    try {
                        double percentil = Double.parseDouble(sc.nextLine());
                        if (percentil > 0 && percentil < 100) {
                            indice.aplicarLeyDeZipf(percentil);
                            System.out.println("Ley de Zipf aplicada con percentil: " + percentil + "%");
                        } else {
                            System.out.println("El percentil debe estar entre 0 y 100.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Valor inválido. Debe ingresar un número.");
                    }
                }

                case 6 -> {
                    System.out.println("Seleccione la estrategia de similitud:");
                    System.out.println("1. Similitud Coseno (recomendado)");
                    System.out.println("2. Producto Punto");
                    System.out.print("Opción: ");

                    try {
                        int opcionEstrategia = Integer.parseInt(sc.nextLine());
                        if (opcionEstrategia == 1) {
                            buscador.setEstrategia(new SimilitudCoseno());
                            System.out.println("Estrategia cambiada a: Similitud Coseno");
                        } else if (opcionEstrategia == 2) {
                            buscador.setEstrategia(new SimilitudProductoPunto());
                            System.out.println(" Estrategia cambiada a: Producto Punto");
                        } else {
                            System.out.println(" Opción no válida.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Opción inválida.");
                    }
                }

                case 7 -> {
                    if (serializar.guardarObjeto("indice.dat", indice)) {
                        System.out.println(" Índice guardado correctamente en 'indice.dat'");
                    } else {
                        System.out.println(" Error al guardar el índice.");
                    }
                }

                case 8 -> {
                    Object objetoCargado = serializar.cargarObjeto("indice.dat");
                    if (objetoCargado instanceof IndiceInvertido) {
                        indice = (IndiceInvertido) objetoCargado;
                        buscador = new Buscador(indice);
                        System.out.println(" Índice cargado correctamente desde 'indice.dat'");
                    } else {
                        System.out.println(" No se pudo cargar el índice o el archivo no existe.");
                    }
                }

                case 0 -> {
                    System.out.println("Gracias por usar el sistema. ¡Hasta pronto!");
                    return;
                }

                default -> System.out.println(" Opción inválida. Intente de nuevo.");
            }
        }
    }
}