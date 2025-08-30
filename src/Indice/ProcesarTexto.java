package Indice;
import java.text.Normalizer;

public class ProcesarTexto {

    public String[] tokenizar(String texto){
        return texto.split("\\s+");
    }

    public String normalizar(String texto){
        if(texto == null || texto.isEmpty()) {
            return "";
        }

        // Convertir a minúsculas
        texto = texto.toLowerCase();

        // Normalizar para separar caracteres base de diacríticos (acentos)
        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);

        // Eliminar TODOS los caracteres no ASCII (incluyendo acentos)
        texto = texto.replaceAll("[^\\p{ASCII}]", "");

        // Eliminar puntuación y caracteres especiales (opcional, por si acaso)
        texto = texto.replaceAll("[^a-z0-9]", "");

        return texto;
    }

    private boolean esStopword(String palabra, String[] stopwords) {
        for (String stop : stopwords) {
            if (stop.equals(palabra)) return true;
        }
        return false;
    }

    public String[] textLimpio(String text, String[] stopwords) {
        String[] tokens = tokenizar(text);
        String [] aux = new String[tokens.length];
        int i = 0;
        for (String t : tokens) {
            String normalizado = normalizar(t);
            if (!esStopword(normalizado, stopwords)) {
                aux[i] = normalizado;
                i++;
            }
        }
        String[] r= new String[i]; // copia la lista nuevamente, sin guardar espacios null
        for(int n = 0; n < i; n++){
            r[n] = aux[n];
        }
        return r;
    }

}
