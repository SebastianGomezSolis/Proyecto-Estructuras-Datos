package Indice;

import Arreglos.Documento;
import Arreglos.ListaDobleCircular;
import Arreglos.Nodo;


public class IndiceInvertido {
    private ListaDobleCircular<TerminoEntry> indice;
    private ListaDobleCircular<Documento> documentos;

    //array basico con algunas stopwords
    private String[] stopwords = {
            "el","la","de","que","y","a","en","un","es","se","no","te","lo","le",
            "da","su","por","son","con","para","al","del","los","las","uno","una",
            "sobre","todo","también","tras","otro","algún","tanto","muy","ya","pero",
            "si","o","este","esta","hasta","donde","quien","desde","todos","durante",
            "algunos","muchos","mucho","poco","más","menos","ante","bajo","como","sin"
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
                contenido.append(linea).append(" "); //y aqui lo que realizamos es agregar la linea mas un espacio
            }

            String id = archivo.getName(); //le damos nombre al archivo
            //creamos el documento con el texto completo y la ruta
            return new Documento(id, contenido.toString(), archivo.getAbsolutePath());
        } catch (Exception e) {
            return null;
        }
    }

    private void procesarDocumento(Documento doc) {
        if (!yaEstaElDocumento(doc.getId())) {
            documentos.insertar(doc);
        }

        String[] palabras = doc.getContenido().split("\\W+");
        for (String palabra : palabras) {
            palabra = palabra.toLowerCase().trim();
            if (!esStopword(palabra) && palabra.length() > 0) {
                meterTermino(palabra, doc);
            }
        }
    }

    private boolean yaEstaElDocumento(String id) {
        if (documentos.vacia()) return false;

        Nodo<Documento> actual = documentos.getRoot();
        do {
            if (actual.getDato().getId().equals(id)) return true;
            actual = actual.getSiguiente();
        } while (actual != documentos.getRoot());

        return false;
    }

    private void meterTermino(String termino, Documento doc) {
        TerminoEntry entrada = buscarEnIndice(termino);

        if (entrada == null) {
            entrada = new TerminoEntry(termino);
            entrada.agregarDocumento(doc.getId());

            // Insertamos de forma ordenada usando la lista
            indice.insertarOrdenado(
                    (a, b) -> a.getTermino().compareToIgnoreCase(b.getTermino()),
                    entrada
            );
        } else {
            // El término ya existe, solo agregamos el documento
            entrada.agregarDocumento(doc.getId());
        }
    }



    private TerminoEntry buscarEnIndice(String termino) {
        if (indice.vacia()) return null;

        Nodo<TerminoEntry> actual = indice.getRoot();
        do {
            if (actual.getDato().getTermino().equals(termino)) return actual.getDato();
            actual = actual.getSiguiente();
        } while (actual != indice.getRoot());

        return null;
    }

    private boolean esStopword(String palabra) {
        for (String stop : stopwords) {
            if (stop.equals(palabra)) return true;
        }
        return false;
    }

    public void construirIndice(ListaDobleCircular<Documento> listaDocumentos) {
        if (listaDocumentos == null || listaDocumentos.vacia()) return;

        Nodo<Documento> actual = listaDocumentos.getRoot();
        do {
            procesarDocumento(actual.getDato());
            actual = actual.getSiguiente();
        } while (actual != listaDocumentos.getRoot());
    }

    public ResultadosCargaDoc cargarDocumentosDesdeRuta(String rutaCarpeta) {
        java.io.File carpeta = new java.io.File(rutaCarpeta);
        if (!carpeta.exists() || !carpeta.isDirectory()) return new ResultadosCargaDoc(0,0,false);

        java.io.File[] archivos = carpeta.listFiles();
        if (archivos == null || archivos.length == 0) return new ResultadosCargaDoc(0,0,false);

        ListaDobleCircular<Documento> docsCargados = new ListaDobleCircular<>();
        int procesados=0, omitidos=0;

        for (java.io.File archivo: archivos) {
            if (archivo.isFile() && esArchivoTexto(archivo.getName())) {
                Documento doc = cargarDocumento(archivo);
                if (doc != null) { docsCargados.insertar(doc); procesados++; }
                else omitidos++;
            } else omitidos++;
        }

        if (procesados > 0) construirIndice(docsCargados);
        return new ResultadosCargaDoc(procesados, omitidos, procesados>0);
    }

    private boolean esArchivoTexto(String nombreArchivo) {
        String n = nombreArchivo.toLowerCase();
        return n.endsWith(".txt") || n.endsWith(".md") || n.endsWith(".rtf");
    }

    public ListaDobleCircular<String> buscar(String termino) {
        TerminoEntry entrada = buscarEnIndice(termino.toLowerCase());
        if (entrada != null) return entrada.getDocumentosIds();
        return new ListaDobleCircular<>();
    }

    public void limpiarIndice() {
        indice = new ListaDobleCircular<>();
        documentos = new ListaDobleCircular<>();
    }

    public String mostrarEstadisticas() {
        int totalDocs = documentos.tamano();
        int totalTerminos = indice.tamano();
        int totalPalabras = 0;
        int maxVeces = 0;
        String terminoMax = "";

        Nodo<TerminoEntry> actual = indice.getRoot();
        if (actual != null) {
            do {
                totalPalabras += actual.getDato().getVeces();
                if (actual.getDato().getVeces() > maxVeces) {
                    maxVeces = actual.getDato().getVeces();
                    terminoMax = actual.getDato().getTermino();
                }
                actual = actual.getSiguiente();
            } while (actual != indice.getRoot());
        }

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
        if (indice.vacia() || percentil <= 0 || percentil >= 100) return;

        // Ordenamos la lista descendente
        indice.ordenarPorFrecuencia();

        // Calculamos cuántos términos mantener
        int total = indice.contarNodos();
        int limite = (int)(total * (percentil / 100.0));

        // Creamos un nuevo índice con solo esos términos
        ListaDobleCircular<TerminoEntry> nuevoIndice = new ListaDobleCircular<>();
        Nodo<TerminoEntry> actual = indice.getRoot();

        for (int i = 0; i < limite; i++) {
            nuevoIndice.insertar(actual.getDato());
            actual = actual.getSiguiente();
        }

        indice = nuevoIndice;
    }



}
