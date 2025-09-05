package Indice;

import java.text.Normalizer;

public class ProcesarTexto {

    public String[] tokenizar(String texto) {
        return texto.split("\\s+"); // solo usamos split
    }

    public String normalizar(String texto) {
        if (texto == null) return "";
        texto = texto.toLowerCase();
        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        texto = texto.replaceAll("[^a-záéíóúñ]", ""); // dejamos letras y ñ
        return texto;
    }

    public String[] limpiar(String text, String[] stopwords) {
        String[] tokens = tokenizar(text);
        String[] aux = new String[tokens.length];
        int count = 0;

        for (int i = 0; i < tokens.length; i++) {
            String normalizado = normalizar(tokens[i]);
            if (normalizado.length() > 0 && !esStopword(normalizado, stopwords)) {
                aux[count] = normalizado;
                count++;
            }
        }

        // copiar solo los válidos a un nuevo arreglo exacto
        String[] resultado = new String[count];
        for (int i = 0; i < count; i++) {
            resultado[i] = aux[i];
        }

        return resultado;
    }

    private boolean esStopword(String palabra, String[] stopwords) {
        for (int i = 0; i < stopwords.length; i++) {
            if (stopwords[i].equals(palabra)) return true;
        }
        return false;
    }
}
