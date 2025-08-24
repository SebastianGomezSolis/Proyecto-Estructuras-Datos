package Persistencia;
import java.io.*;

public class Serializar {

    public boolean guardarObjeto(String rutaArchivo, Object objeto) {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            os.writeObject(objeto);
            return true;
        } catch (IOException e) {
            System.err.println("Error guardando archivo: " + e.getMessage());
            return false;
        }
    }

    public Object cargarObjeto(String rutaArchivo) {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(rutaArchivo))) {
            return is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error leyendo archivo: " + e.getMessage());
            return null;
        }
    }


}
