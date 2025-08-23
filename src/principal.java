import Arreglos.ListaDobleCircular;
import Arreglos.Vector;
import Indice.*;
import Indice.ProcesarTexto;

public class principal {
    public static void main(String[] args) {
//        ListaDobleCircular<Integer> lista = new ListaDobleCircular<>();
//        lista.insertar(1);
//        lista.insertar(2);
//        lista.insertar(3);
//        lista.insertar(4);
//        lista.insertar(5);
//
//        lista.imprimir();
//
//        System.out.println("Eliminando el elemento 2");
//        lista.eliminar(2);
//
//        System.out.println("Lista despues de eliminar el elemento 2");
//        lista.imprimir();
//
//        System.out.println("Buscando el elemento 3: " + lista.buscar(3));
//        System.out.println("Buscando el elemento 6: " + lista.buscar(6));
//        System.out.println("Buscando el elemento 1: " + lista.buscar(2));
//
//        lista.modificar( 1 , 10);
//
//        System.out.println("Lista despues de modificar el elemento 1");
//        lista.imprimir();

       /* Vector v1 = new Vector(3);
        Vector v2 = new Vector(3);

        // Insertar datos en v1
        v1.insertar(1);
        v1.insertar(2);
        v1.insertar(3);
        System.out.println("v1 después de insertar: " + v1.mostrar());
        System.out.println("Tamaño v1: " + v1.getSize() + " | Capacidad v1: " + v1.getTam());

        // Insertar datos en v2
        v2.insertar(4);
        v2.insertar(5);
        v2.insertar(6);
        System.out.println("v2 después de insertar: " + v2.mostrar());
        System.out.println("Tamaño v2: " + v2.getSize() + " | Capacidad v2: " + v2.getTam());

        // Buscar elementos
        System.out.println("¿v1 contiene 2? " + v1.buscar(2));
        System.out.println("¿v1 contiene 10? " + v1.buscar(10));

        // Eliminar un valor de v1
        v1.eliminar(2);
        System.out.println("v1 después de eliminar 2: " + v1.mostrar());

        // Insertar más valores para probar redimensionamiento
        v1.insertar(7);
        v1.insertar(8);
        v1.insertar(9);  // Aquí debería duplicarse la capacidad
        System.out.println("v1 tras redimensionar e insertar más valores: " + v1.mostrar());
        System.out.println("Tamaño v1: " + v1.getSize() + " | Capacidad v1: " + v1.getTam());

        // Calcular producto punto
        Vector v3 = new Vector(3);
        v3.insertar(1);
        v3.insertar(2);
        v3.insertar(3);

        Vector v4 = new Vector(3);
        v4.insertar(4);
        v4.insertar(5);
        v4.insertar(6);

        System.out.println("v3 = " + v3.mostrar());
        System.out.println("v4 = " + v4.mostrar());
        System.out.println("Producto punto v3 · v4 = " + v3.productoPunto(v4)); // 32

        // Crear el índice
        IndiceInvertido indice = new IndiceInvertido();

        // Cargar documentos desde la carpeta
        String rutaCarpeta = "C:\\Users\\ashle\\Desktop\\Proyecto-Estructuras-Datos\\src\\Documentos";
        ResultadosCargaDoc resultado = indice.cargarDocumentosDesdeRuta(rutaCarpeta);

        if (resultado.isExito()) {
            System.out.println("Carga exitosa!");
            System.out.println(resultado.toString());
            System.out.println(indice.mostrarEstadisticas());
        } else {
            System.out.println("No se pudieron cargar documentos");
            System.out.println(resultado.toString());
            return;
        }

        // --- Búsqueda antes de aplicar Ley de Zipf ---
        System.out.println("\n--- Buscando antes de aplicar Ley de Zipf ---");
        buscarTermino(indice, "java");
        buscarTermino(indice, "clase");
        buscarTermino(indice, "documento");

        // Aplicar Ley de Zipf (ejemplo 5%)
        indice.aplicarLeyDeZipf(5.0);
        System.out.println("\nÍndice después de aplicar Ley de Zipf (5.0%):");
        System.out.println(indice.mostrarEstadisticas());

        // --- Búsqueda después de aplicar Ley de Zipf ---
        System.out.println("\n--- Buscando después de aplicar Ley de Zipf ---");
        buscarTermino(indice, "java");
        buscarTermino(indice, "clase");
        buscarTermino(indice, "documento");
    }

    // Método auxiliar para búsqueda y mostrar resultados
    private static void buscarTermino(IndiceInvertido indice, String termino) {
        System.out.println("\nBuscando término '" + termino + "'...");
        ListaDobleCircular<String> docs = indice.buscar(termino);
        if (!docs.vacia()) {
            System.out.println("Documentos que contienen '" + termino + "':");
            docs.imprimir();
        } else {
            System.out.println("No se encontró el término '" + termino + "'");
        }
    }*/
        IndiceInvertido stop = new IndiceInvertido();

        ProcesarTexto p = new ProcesarTexto();
        String texto = "¡El Gato, comé Péscádo!";
        String[] tokens = p.tokenizar(texto);

        for (String t : tokens) {
            System.out.println(t);
        }

        System.out.println(p.normalizar(texto));
        String[] palabras = p.textLimpio(texto, stop.getStopwords());
        for (String palabra : palabras) {
            System.out.println(palabra);

        }
    }
}

