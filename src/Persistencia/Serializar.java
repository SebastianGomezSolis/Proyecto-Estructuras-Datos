// ==========================
// Package: Persistencia
// ==========================
package Persistencia;

import java.io.*;

public class Serializar {
    public boolean guardarObjeto(String ruta, Object objeto) {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(ruta))) {
            os.writeObject(objeto);
            return true;
        }catch (IOException e) {
                System.out.println("Error al guardar objeto: " + e.getMessage());
                e.printStackTrace(); // te muestra la línea exacta del error
                return false;
            }
    }

    public Object cargarObjeto(String ruta) {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(ruta))) {
            return is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return null;
        }
    }
}
