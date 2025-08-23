package Indice;

import Arreglos.Documento;
import Arreglos.ListaDobleCircular;
import Arreglos.Nodo;
import Arreglos.Vector;


public class IndiceInvertido {
    private ListaDobleCircular<TerminoEntry> indice;
    private ListaDobleCircular<Documento> documentos;

    //array básico con algunas stopwords
    private String[] stopwords = {
            "el", "la", "de", "que", "y", "a", "en", "un", "es", "se", "no", "te", "lo", "le",
            "da", "su", "por", "son", "con", "para", "al", "del", "los", "las", "uno", "una",
            "sobre", "todo", "también", "tras", "otro", "algún", "tanto", "muy", "ya", "pero",
            "si", "o", "este", "esta", "hasta", "donde", "quien", "desde", "todos", "durante",
            "algunos", "muchos", "mucho", "poco", "más", "menos", "ante", "bajo", "como", "sin"
    };

    public IndiceInvertido() {
        this.indice = new ListaDobleCircular<>();
        this.documentos = new ListaDobleCircular<>();
    }

    //Aqui lo que se realiza es leer un txt y poder devolver un doc con id, content y ruta
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

    // Procesa un documento y lo agrega a la lista de documentos (si no estaba)
    // y mete cada palabra al índice, filtrando stopwords
    private void procesarDocumento(Documento doc) {
        //si el doc no esta registrado, se guarda
        if (!yaEstaElDocumento(doc.getId())) {
            documentos.insertar(doc);
        }

        // Dividimos el texto del documento en palabras
        //split("\\W+") lo que hace es dividir el texto en partes usando cualquier carácter que no sea letra o número como separador
        String[] palabrasDelDoc = doc.getContenido().split("\\W+");

        // Recorremos cada palabra obtenida
        for (String palabra : palabrasDelDoc) {
            // Convertimos a minúsculas y eliminamos espacios al inicio y final
            //toLowerCase sirve para convertir las letras de min a mayus y se consideren igual
            //trim elimina los espacios al inicio y final de la palabra, por lo que ayuda a evitar espacios en blanco
            palabra = palabra.toLowerCase().trim();

            // Solo agregamos la palabra si no es una stopword y no está vacía
            if (!esStopword(palabra) && palabra.length() > 0) {
                // La agregamos al índice invertido junto con el documento al que pertenece
                meterTermino(palabra, doc);
            }
        }

    }

    // Verifica si un documento ya fue agregado al índice usando su ID
    private boolean yaEstaElDocumento(String id) {
        // Si la lista de documentos está vacía, no esta
        if (documentos.vacia()) return false;

        // Empezamos desde el primer nodo de la lista de documentos
        Nodo<Documento> actual = documentos.getRoot();

        // Recorremos la lista circular
        do {
            // Si encontramos un documento con el mismo ID, devolvemos true
            if (actual.getDato().getId().equals(id)) return true;

            // Avanzamos al siguiente nodo
            actual = actual.getSiguiente();
        } while (actual != documentos.getRoot()); // Terminamos cuando volvemos al inicio

        // Si recorrimos toda la lista y no lo encontramos, devolvemos false
        return false;
    }


    // Agrega un término al índice invertido, vinculándolo con un documento específico
    private void meterTermino(String termino, Documento doc) {

        // Primero buscamos si el término ya está en el índice
        TerminoEntry entrada = buscarEnIndice(termino);

        if (entrada == null) {
            // Si no existe, creamos una nueva entrada para este término
            entrada = new TerminoEntry(termino);

            // Asociamos este documento a la entrada del término
            entrada.agregarDocumento(doc.getId());

            // Insertamos la entrada en la lista de forma ordenada alfabéticamente
            // Se compara ignorando mayúsculas/minúsculas
            indice.insertarOrdenado((a, b) -> a.compararAlfabeticamente(b), entrada);

        } else {
            // Si el término ya existe en el índice, simplemente agregamos el documento
            // para que quede registrado que este documento contiene el término
            entrada.agregarDocumento(doc.getId());
        }
    }


    // Método que busca un término en el índice y devuelve su entrada si existe
    private TerminoEntry buscarEnIndice(String termino) {
        // Si la lista de términos está vacía, no hay nada que buscar
        if (indice.vacia()) return null;

        // Comenzamos desde el primer nodo de la lista
        Nodo<TerminoEntry> actual = indice.getRoot();

        // Recorremos toda la lista circular
        do {
            // Preguntamos al objeto TerminoEntry si coincide con el término que buscamos
            if (actual.getDato().esIgual(termino)) {
                // Si coincide, devolvemos la entrada completa
                return actual.getDato();
            }

            // Avanzamos al siguiente nodo
            actual = actual.getSiguiente();
        } while (actual != indice.getRoot()); // Repetimos hasta volver al inicio

        // Si llegamos al final y no encontramos nada, devolvemos null
        return null;
    }


    // Método que verifica si una palabra es una stopword
    private boolean esStopword(String palabra) {
        // Recorremos el array de stopwords usando un índice
        for (int i = 0; i < stopwords.length; i++) {
            // Obtenemos la stopword en la posición i
            String palabraActual = stopwords[i];

            // Comparamos con la palabra que queremos verificar
            if (palabraActual.equals(palabra)) {
                // Si encontramos coincidencia, devolvemos true
                return true;
            }
        }
        // Si recorremos todo el array y no hay coincidencia, devolvemos false
        return false;
    }


    // Método que construye el índice invertido a partir de una lista de documentos
    public void construirIndice(ListaDobleCircular<Documento> listaDocumentos) {
        // Si la lista está vacía o es null, no hacemos nada
        if (listaDocumentos == null || listaDocumentos.vacia()) return;

        // Empezamos desde el primer nodo de la lista
        Nodo<Documento> actual = listaDocumentos.getRoot();

        // Recorremos toda la lista circular
        do {
            // Procesamos el documento actual para agregar sus palabras al índice
            procesarDocumento(actual.getDato());

            // Pasamos al siguiente nodo
            actual = actual.getSiguiente();
            // Continuamos hasta volver al nodo inicial (lista circular)
        } while (actual != listaDocumentos.getRoot());
    }


    public ResultadosCargaDoc cargarDocumentosDesdeRuta(String rutaCarpeta) {
        // Creamos un objeto File que apunta a la carpeta indicada
        java.io.File carpeta = new java.io.File(rutaCarpeta);

        // Verificamos que la carpeta exista y sea realmente un directorio
        if (!carpeta.exists() || !carpeta.isDirectory())
            return new ResultadosCargaDoc(0, 0, false); // si no es válida, devolvemos resultado vacío

        // Obtenemos todos los archivos dentro de la carpeta
        java.io.File[] archivos = carpeta.listFiles();

        // Si no hay archivos, devolvemos resultado vacío
        if (archivos == null || archivos.length == 0)
            return new ResultadosCargaDoc(0, 0, false);

        // Lista para almacenar los documentos que se pudieron cargar
        ListaDobleCircular<Documento> docsCargados = new ListaDobleCircular<>();

        // Contadores para seguimiento de documentos procesados y omitidos
        int procesados = 0;
        int omitidos = 0;

        // Recorremos todos los archivos usando un for tradicional
        for (int i = 0; i < archivos.length; i++) {
            java.io.File archivoActual = archivos[i]; // archivo actual en la iteración

            // Solo procesamos si es un archivo normal (no carpeta)
            if (archivoActual.isFile()) {
                // Intentamos leer el archivo y crear un objeto Documento
                Documento documentoLeido = cargarDocumento(archivoActual);

                if (documentoLeido != null) {
                    docsCargados.insertar(documentoLeido); // agregamos a la lista
                    procesados++; // incrementamos contador de procesados
                } else {
                    omitidos++; // si hubo error, contamos como omitido
                }
            } else {
                omitidos++; // ignoramos carpetas u otros tipos de archivo
            }
        }

        // Si procesamos al menos un documento, construimos el índice invertido
        if (procesados > 0)
            construirIndice(docsCargados);

        // Retornamos un objeto con el resumen de la operación
        return new ResultadosCargaDoc(procesados, omitidos, procesados > 0);
    }


    // Método para buscar un término en el índice invertido
    public ListaDobleCircular<String> buscar(String termino) {
        // Primero buscamos el término en nuestro índice, pasándolo a minúsculas
        TerminoEntry entrada = buscarEnIndice(termino.toLowerCase());

        // Si encontramos la entrada, devolvemos la lista de IDs de documentos donde aparece
        if (entrada != null) return entrada.getDocumentosIds();

        // Si no encontramos el término, devolvemos una lista vacía
        return new ListaDobleCircular<>();
    }

    // Método para limpiar todo el índice y la lista de documentos
    public void limpiarIndice() {
        // Reiniciamos la lista de términos
        indice = new ListaDobleCircular<>();

        // Reiniciamos la lista de documentos
        documentos = new ListaDobleCircular<>();
    }

    // Método que genera un resumen de estadísticas del índice invertido
    public String mostrarEstadisticas() {
        // Contamos cuántos documentos tenemos en el índice
        int totalDocs = documentos.tamano();

        // Contamos cuántos términos únicos hay en el índice
        int totalTerminos = indice.tamano();

        // Inicializamos el contador de palabras totales (considerando repeticiones)
        int totalPalabras = 0;

        // Variables para llevar seguimiento del término más frecuente
        int maxVeces = 0;
        String terminoMax = "";

        // Empezamos a recorrer la lista de términos desde la raíz
        Nodo<TerminoEntry> actual = indice.getRoot();

        // Si la lista no está vacía
        if (actual != null) {
            do {
                // Sumamos cuántas veces aparece este término al total de palabras
                totalPalabras += actual.getDato().getVeces();

                // Verificamos si este término es más frecuente que el anterior máximo
                if (actual.getDato().getVeces() > maxVeces) {
                    maxVeces = actual.getDato().getVeces();          // Guardamos la frecuencia máxima
                    terminoMax = actual.getDato().getTermino();      // Guardamos cuál es el término
                }

                // Pasamos al siguiente nodo en la lista
                actual = actual.getSiguiente();
            } while (actual != indice.getRoot()); // Repetimos hasta dar la vuelta completa
        }

        // Devolvemos un string que resume toda la información de manera legible
        return "Documentos: " + totalDocs +
                ", Términos: " + totalTerminos +
                ", Palabras totales indexadas: " + totalPalabras +
                ", Término más frecuente: '" + terminoMax + "' (" + maxVeces + " veces)";
    }



    public ListaDobleCircular<TerminoEntry> getIndice() {
        return indice;
    }

    public ListaDobleCircular<Documento> getDocumentos() {
        return documentos;
    }

    public String[] getStopwords() {return stopwords; }

    public void aplicarLeyDeZipf(double percentil) {
        // Si la lista está vacía o el percentil no es válido, no hacemos nada
        if (indice.vacia() || percentil <= 0 || percentil >= 100) return;

        // Ordenamos la lista de términos de mayor a menor frecuencia
        // Se asume que ListaDobleCircular tiene un método ordenarPorFrecuencia()
        indice.ordenarPorFrecuencia();

        // Calculamos el número total de términos
        int total = indice.contarNodos();
        // Calculamos cuántos términos vamos a mantener según el percentil
        int limite = (int)(total * (percentil / 100.0));

        // Creamos un nuevo índice para almacenar solo los términos más frecuentes
        ListaDobleCircular<TerminoEntry> nuevoIndice = new ListaDobleCircular<>();
        Nodo<TerminoEntry> actual = indice.getRoot();

        // Recorremos los términos hasta alcanzar el límite calculado
        for (int i = 0; i < limite; i++) {
            // Insertamos cada término en el nuevo índice
            nuevoIndice.insertar(actual.getDato());
            actual = actual.getSiguiente();
        }

        // Reemplazamos el índice original con el nuevo índice reducido
        indice = nuevoIndice;
    }

    public double calcularIDF(String termino) {
        int totalDocs = documentos.tamano();
        if (totalDocs == 0) return 0;

        TerminoEntry entrada = buscarEnIndice(termino);
        if (entrada == null) return 0;

        int docFreq = entrada.cuantosDocumentos();
        if (docFreq == 0) return 0;

        return Math.log((double) totalDocs / docFreq);
    }

    public Vector obtenerVectorDocumento(String docId) {
        int size = indice.tamano();
        Vector vector = new Vector(size);

        Nodo<TerminoEntry> actual = indice.getRoot();
        if (actual == null) return vector;

        do {
            TerminoEntry terminoEntry = actual.getDato();

            int tf = 0;
            ListaDobleCircular<String> docsIds = terminoEntry.getDocumentosIds();
            Nodo<String> nodoDocId = docsIds.getRoot();

            if (nodoDocId != null) {
                do {
                    if (nodoDocId.getDato().equals(docId)) {
                        tf++;
                    }
                    nodoDocId = nodoDocId.getSiguiente();
                } while (nodoDocId != docsIds.getRoot());
            }

            double idf = calcularIDF(terminoEntry.getTermino());
            double valor = tf * idf;

            vector.insertar(valor);

            actual = actual.getSiguiente();
        } while (actual != indice.getRoot());

        return vector;
    }
}