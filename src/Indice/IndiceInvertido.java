package Indice;

import Arreglos.ListaDobleCircular;
import Arreglos.Documento;
import Arreglos.Vector;
import Utilidades.Ordenador;

import java.io.*;

public class IndiceInvertido implements Serializable {
    private static final long serialVersionUID = 1L;

    // === Singleton ===
    private static IndiceInvertido instancia;

    public static IndiceInvertido getInstance() {
        if (instancia == null) {
            instancia = new IndiceInvertido();
        }
        return instancia;
    }

    // === Atributos ===
    private ListaDobleCircular<TerminoEntry> indice;
    private ListaDobleCircular<Documento> documentos;

    // === Constructor privado (para Singleton) ===
    private IndiceInvertido() {
        this.indice = new ListaDobleCircular<>();
        this.documentos = new ListaDobleCircular<>();
    }

    // === Construcción del índice ===
    public void construirIndice(ListaDobleCircular<Documento> docs) {
        for (Documento doc : docs) {
            documentos.insertar(doc);
            String[] palabras = doc.getContenido().split("\\s+"); // aquí deberías limpiar con stopwords
            for (String p : palabras) {
                if (!p.isEmpty()) {
                    meterTermino(p.toLowerCase(), doc);
                }
            }
        }
    }

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

    public TerminoEntry buscarTermino(String termino) {
        for (TerminoEntry t : indice) {
            if (t.getTermino().equals(termino)) return t;
        }
        return null;
    }

    // === TF-IDF ===
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

    public double calcularIDF(String termino) {
        TerminoEntry t = buscarTermino(termino);
        if (t == null) return 0;
        return Math.log((double) documentos.tamano() / (1 + t.cuantosDocumentos()));
    }

    // === Ley de Zipf ===
    public void aplicarZipf(int percentilTop) {
        int n = indice.tamano();
        if (n == 0 || percentilTop <= 0) return;
        if (percentilTop > 100) percentilTop = 100;

        int[] frec = new int[n];
        TerminoEntry[] ref = new TerminoEntry[n];
        int i = 0;
        for (TerminoEntry t : indice) {
            frec[i] = t.getVeces();
            ref[i] = t;
            i++;
        }

        Ordenador.radixSortPareadoEnteros(frec, ref);

        int cortar = (percentilTop * n) / 100;
        int dejar = n - cortar;
        if (dejar < 0) dejar = 0;

        ListaDobleCircular<TerminoEntry> nuevo = new ListaDobleCircular<>();
        for (int k = 0; k < dejar; k++) nuevo.insertar(ref[k]);
        this.indice = nuevo;
    }

    // === Guardar / Cargar índice en binario ===
    public boolean guardarIndice(String ruta) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta))) {
            oos.writeObject(this);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static IndiceInvertido cargarIndice(String ruta) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ruta))) {
            instancia = (IndiceInvertido) ois.readObject();
            return instancia;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    // === Getters ===
    public ListaDobleCircular<TerminoEntry> getIndice() { return indice; }
    public ListaDobleCircular<Documento> getDocumentos() { return documentos; }
}
