import Arreglos.Documento;
import Arreglos.ListaDobleCircular;
import Indice.Buscador;
import Indice.IndiceInvertido;
import Persistencia.Serializar;
import Persistencia.Archivos;

import java.util.Scanner;


public class principal {
    public static void main(String[] args) {
        Scanner s = new Scanner(System.in);

        Archivos archivos = new Archivos();
        IndiceInvertido indice = IndiceInvertido.getInstance(); // Singleton
        Buscador buscador = new Buscador(indice);
        Serializar serializar = new Serializar();
        System.out.println("\t\t\t\t\t\t\t¡Bienvenido!");
        System.out.println("\tEste sistema te permitirá buscar términos dentro de documentos" );

        //archivos.cargarDocumentos();
        while (true) {
            System.out.println("\n °°°°°°°°°°°°°°°°°°°°°° MENU °°°°°°°°°°°°°°°°°°°°°°");
            System.out.println("1. Realizar una busqueda");
            System.out.println("2. Mostrar estadísticas");
            System.out.println("3. Aplicar Ley de Zipf");
            System.out.println("4. Guardar índice");
            System.out.println("5. Cargar índice");
            System.out.println("0. Salir");
            System.out.print("Opción: ");

            int opcion;
            try {
                opcion = Integer.parseInt(s.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opción inválida. Intente de nuevo.");
                continue;
            }

            switch (opcion) {

                case 1: {
                    System.out.print("Ingrese tu palabra o frase a consultar: ");
                    String consulta = s.nextLine();
                    ListaDobleCircular<Documento> resultados = buscador.buscar(consulta);

                    if (resultados.vacia()) {
                        System.out.println("No se encontraron resultados.");
                    } else {
                        System.out.println("Resultados encontrados:");
                        resultados.imprimir();
                    }
                    break;
                }

                case 2: {
                    System.out.println(indice.mostrarEstadisticas());
                    break;
                }

               /* case 3: {
                    System.out.print("Ingrese percentil para aplicar la Ley de Zipf: ");
                    double percentil;
                    try {
                        percentil = Double.parseDouble(s.nextLine());
                        indice.aplicarLeyDeZipf(percentil);
                        System.out.println("Ley de Zipf aplicada.");
                    } catch (NumberFormatException e) {
                        System.out.println("Valor inválido. Intente nuevamente.");
                    }
                    break;
                }*/

                case 4: {
                    serializar.guardarObjeto("indice.bin", indice);
                    System.out.println("Indice guardado.");
                    break;
                }

                case 5: {
                    IndiceInvertido cargado = (IndiceInvertido) serializar.cargarObjeto("indice.bin");
                    if (cargado != null) {
                        indice = cargado;
                        buscador = new Buscador(indice);
                        System.out.println("Indice cargado correctamente.");
                    } else {
                        System.out.println("No se pudo cargar el indice.");
                    }
                    break;
                }

                case 0: {
                    System.out.println("Gracias por usar nuestro sistema.");
                    return;
                }

                default:
                    System.out.println("Opción inválida. Intente de nuevo.");
            }
        }
    }
}

