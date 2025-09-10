package Indice;

import Arreglos.ListaDobleCircular;
import Arreglos.Documento;
import Arreglos.Vector;
import Utilidades.Ordenador;

import java.io.*;

//  Funcionalidades:
//  - Construcción del índice a partir de documentos
//  - Inserción de términos
//  - Cálculo de TF-IDF
//  - Aplicación de la Ley de Zipf (eliminar términos más frecuentes)
//  - Guardar y cargar el índice en formato binario
public class IndiceInvertido implements Serializable {
    private static final long serialVersionUID = 1L;

    // === Singleton ===
    private static IndiceInvertido instancia;

    // Devuelve la instancia única del índice invertido.
    public static IndiceInvertido getInstance() {
        if (instancia == null) {
            instancia = new IndiceInvertido();
        }
        return instancia;
    }

    // === Atributos ===
    private ListaDobleCircular<TerminoEntry> indice;   // Lista de términos
    private ListaDobleCircular<Documento> documentos;  // Lista de documentos

    // === Constructor privado (para Singleton) ===
    private IndiceInvertido() {
        this.indice = new ListaDobleCircular<>();
        this.documentos = new ListaDobleCircular<>();
    }

    // === Construcción del índice ===
    // Construye el índice invertido a partir de los documentos.
    // Tokeniza cada documento y agrega sus términos al índice.
    public void construirIndice(ListaDobleCircular<Documento> docs) {
        for (Documento doc : docs) {
            documentos.insertar(doc);
            // Separar el contenido en palabras por espacios
            String[] palabras = doc.getContenido().split("\\s+"); // aquí deberías limpiar con stopwords
            for (String p : palabras) {
                if (!p.isEmpty()) {
                    meterTermino(p.toLowerCase(), doc);
                }
            }
        }
    }

    // Inserta un término en el índice.
    // Si ya existe, se actualiza su frecuencia.
    public void meterTermino(String termino, Documento doc) {
        TerminoEntry existente = buscarTermino(termino);
        if (existente == null) {
            TerminoEntry nuevo = new TerminoEntry(termino);
            nuevo.agregarOcurrencia(doc.getId());
            indice.insertar(nuevo);
        } else {
            existente.agregarOcurrencia(doc.getId());
        }
    }

    // Busca un término dentro del índice.
    public TerminoEntry buscarTermino(String termino) {
        for (TerminoEntry t : indice) {
            if (t.getTermino().equals(termino)) return t;
        }
        return null;
    }

    // === TF-IDF ===
    // Devuelve el vector TF-IDF de un documento.
    public Vector obtenerVectorDocumento(String docId) {
        double[] valores = new double[indice.tamano()];
        int i = 0;
        for (TerminoEntry t : indice) {
            int tf = t.tfEnDocumento(docId);
            if (tf > 0) {
                double idf = Math.log((double) documentos.tamano() / (1 + t.cuantosDocumentos()));
                valores[i] = tf * idf;
            } else {
                valores[i] = 0.0;
            }
            i++;
        }
        return new Vector(valores);
    }

    // Calcula el IDF de un término específico.
    public double calcularIDF(String termino) {
        TerminoEntry t = buscarTermino(termino);
        if (t == null) return 0;
        return Math.log((double) documentos.tamano() / (1 + t.cuantosDocumentos()));
    }

    // === Ley de Zipf ===
    // Aplica la eliminación de términos según la Ley de Zipf.
    // El parámetro percentilTop se interpreta como porcentaje de términos a eliminar.
    public void aplicarZipf(int percentilTop) {
        int n = indice.tamano();
        if (n == 0 || percentilTop <= 0) {
            System.out.println("No hay términos o percentil <= 0. No se aplica Zipf.");
            return;
        }
        if (percentilTop >= 100) {
            // Eliminar todo el vocabulario (caso extremo)
            this.indice = new ListaDobleCircular<>();
            return;
        }

        // Llenar arrays (frecuencias + referencias a TerminoEntry)
        int[] frec = new int[n];
        TerminoEntry[] ref = new TerminoEntry[n];
        int i = 0;
        for (TerminoEntry t : indice) {
            frec[i] = t.getVeces();   // frecuencia global del término en el corpus
            ref[i] = t;
            i++;
        }

        // Ordenar por frecuencia (radix pareado). Resultado: orden ASC (menor -> mayor)
        Ordenador.radixSortPareadoEnteros(frec, (Object[]) ref);

        // Decide cuántos eliminar (redondeo hacia arriba para percentiles pequeños)
        int cortar = (int) Math.ceil((percentilTop / 100.0) * n);
        if (cortar < 0) cortar = 0;
        if (cortar > n) cortar = n;
        int dejar = n - cortar;

        // Se contruye un nuevo índice conservando los términos menos frecuentes
        ListaDobleCircular<TerminoEntry> nuevo = new ListaDobleCircular<>();

        boolean ascendente = frec[0] <= frec[n - 1];

        if (ascendente) {
            // Se mantienen los primeros (los menos frecuentes)
            for (int k = 0; k < dejar; k++) {
                nuevo.insertar(ref[k]);
            }
        } else {
            // Si por alguna razón el array quedo descendente (mayor -> menor)
            // Los menos frecuentes están al final: mantenemos los últimos
            for (int k = cortar; k < n; k++) {
                nuevo.insertar(ref[k]);
            }
        }

        this.indice = nuevo;

    }

   // === Guardar / Cargar índice en binario ===
   public boolean guardarIndice(String archivo) {
       try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(archivo))) {
           // Guardar documentos
           oos.writeInt(documentos.tamano());
           for (Documento doc : documentos) {
               oos.writeObject(doc);
           }

           // Guardar términos
           oos.writeInt(indice.tamano());
           for (TerminoEntry entry : indice) {
               oos.writeUTF(entry.getTermino());
               oos.writeInt(entry.getVeces());

               // Guardar posteos
               oos.writeInt(entry.getPosteos().tamano());
               for (Posteo p : entry.getPosteos()) {
                   oos.writeObject(p);
               }
           }

           return true;
       } catch (IOException e) {
           e.printStackTrace();
           return false;
       }
   }

    public static IndiceInvertido cargarIndice(String archivo) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            IndiceInvertido indice = new IndiceInvertido();

            // Cargar documentos
            int numDocs = ois.readInt();
            for (int i = 0; i < numDocs; i++) {
                Documento doc = (Documento) ois.readObject();
                indice.documentos.insertar(doc);
            }

            // Cargar términos
            int numTerms = ois.readInt();
            for (int i = 0; i < numTerms; i++) {
                String termino = ois.readUTF();
                int veces = ois.readInt();

                TerminoEntry entry = new TerminoEntry(termino);
                entry.setVeces(veces);

                int n = ois.readInt();
                for (int j = 0; j < n; j++) {
                    Posteo p = (Posteo) ois.readObject();
                    entry.getPosteos().insertar(p);
                }

                indice.indice.insertar(entry);
            }

            return indice;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // === Getters ===
    public ListaDobleCircular<TerminoEntry> getIndice() { return indice; }
    public ListaDobleCircular<Documento> getDocumentos() { return documentos; }
    // === Setters ===
    public void setIndice(ListaDobleCircular<TerminoEntry> nuevoIndice) {this.indice = nuevoIndice; }
    public void setDocumentos(ListaDobleCircular<Documento> nuevosDocs) {this.documentos = nuevosDocs; }

}
