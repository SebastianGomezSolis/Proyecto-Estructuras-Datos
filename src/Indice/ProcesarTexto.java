package Indice;

import java.text.Normalizer;

public class ProcesarTexto {

    // Divide un texto en tokens (palabras) usando espacios en blanco como separadores.
    public String[] tokenizar(String texto) {
        return texto.split("\\s+"); // solo usamos split
    }

    // Normaliza una palabra:
    // - La pasa a minúsculas
    // - Elimina tildes usando Normalizer
    // - Deja solo letras a-z y ñ (filtra todo lo demás)
    public String normalizar(String texto) {
        if (texto == null) return "";
        texto = texto.toLowerCase();
        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        // Convierte caracteres con tilde a base + tilde separado (ejemplo: á -> a +  ́)
        texto = texto.replaceAll("[^a-záéíóúñ]", ""); // dejamos letras y ñ
        return texto;
    }

    // Procesa un texto completo:
    // - Tokeniza en palabras
    // - Normaliza cada palabra
    // - Elimina stopwords si están definidas
    // - Descarta tokens vacíos
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

    // Verifica si una palabra está en la lista de stopwords.
    private boolean esStopword(String palabra, String[] stopwords) {
        for (int i = 0; i < stopwords.length; i++) {
            if (stopwords[i].equals(palabra)) return true;
        }
        return false;
    }
}
