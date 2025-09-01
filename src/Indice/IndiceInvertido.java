package Indice;

import Arreglos.Documento;
import Arreglos.ListaDobleCircular;
import Arreglos.Nodo;
import Arreglos.Vector;

import java.io.Serializable;

public class IndiceInvertido implements Serializable {
    private static IndiceInvertido instance;

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



    private IndiceInvertido() {
        this.indice = new ListaDobleCircular<>();
        this.documentos = new ListaDobleCircular<>();
    }


    public static IndiceInvertido getInstance() {
        if (instance == null) {
            instance = new IndiceInvertido();
        }
        return instance;
    }
    // Procesa un documento y lo agrega a la lista de documentos (si no estaba)
    // y mete cada palabra al índice, filtrando stopwords
    private void procesarDocumento(Documento doc) {
        //si el doc no esta registrado, se guarda
        if (!yaEstaElDocumento(doc.getId())) {
            documentos.insertar(doc);
        }

        ProcesarTexto procesador = new ProcesarTexto();
        String[] palabrasDelDoc = doc.getContenido().split("\\W+");

        for (String palabra : palabrasDelDoc) {
            String normalizada = procesador.normalizar(palabra);
            if (!esStopword(normalizada) && normalizada.length() > 0) {
                meterTermino(normalizada, doc);
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

        Nodo<Documento> docNode = documentos.getRoot();
        if (docNode != null) { // ⚠️ Verificación importante
            do {
                Documento doc = docNode.getDato();
                Vector vec = obtenerVectorDocumento(doc.getId());
                doc.setVectorTFIDF(vec);
                docNode = docNode.getSiguiente();
            } while (docNode != documentos.getRoot());
        }

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
        //System.out.println("DEBUG - Construyendo vector para: " + docId);

        int size = indice.tamano();
        Vector vector = new Vector(size);

        Nodo<TerminoEntry> actual = indice.getRoot();
        if (actual == null) return vector;

        do {
            TerminoEntry terminoEntry = actual.getDato();
            int tf = terminoEntry.getFrecuenciaEnDocumento(docId);
            double idf = calcularIDF(terminoEntry.getTermino());

            //System.out.println("DEBUG - " + terminoEntry.getTermino() +
                  // ": TF=" + tf + ", IDF=" + idf);

            vector.insertar(tf * idf);

            actual = actual.getSiguiente();
        } while (actual != indice.getRoot());

        return vector;
    }

    public void actualizarIndice(Arreglos.ListaDobleCircular<Arreglos.Documento> nuevosDocs) {
        if (nuevosDocs == null || nuevosDocs.vacia()) return;

        Arreglos.Nodo<Arreglos.Documento> actual = nuevosDocs.getRoot();
        do {
            procesarDocumento(actual.getDato()); // usa tu método privado existente
            actual = actual.getSiguiente();
        } while (actual != nuevosDocs.getRoot());
    }


}