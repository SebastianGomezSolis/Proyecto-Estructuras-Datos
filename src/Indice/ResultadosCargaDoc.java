package Indice;

public class ResultadosCargaDoc {
    private int archivosProcesados;
    private int archivosOmitidos;
    private boolean exito;

    public ResultadosCargaDoc(int archivosProcesados, int archivosOmitidos, boolean exito) {
        this.archivosProcesados = archivosProcesados;
        this.archivosOmitidos = archivosOmitidos;
        this.exito = exito;
    }

    // Getters
    public int getArchivosProcesados() {
        return archivosProcesados;
    }

    public int getArchivosOmitidos() {
        return archivosOmitidos;
    }

    public boolean isExito() {
        return exito;
    }

    public int getTotalArchivos() {
        return archivosProcesados + archivosOmitidos;
    }

    // Setters
    public void setArchivosProcesados(int archivosProcesados) {
        this.archivosProcesados = archivosProcesados;
    }

    public void setArchivosOmitidos(int archivosOmitidos) {
        this.archivosOmitidos = archivosOmitidos;
    }

    public void setExito(boolean exito) {
        this.exito = exito;
    }

    @Override
    public String toString() {
        return String.format(
                "Carga de documentos %s:\n" +
                        "- Archivos procesados: %d\n" +
                        "- Archivos omitidos: %d\n" +
                        "- Total de archivos: %d",
                exito ? "exitosa" : "fallida",
                archivosProcesados,
                archivosOmitidos,
                getTotalArchivos()
        );
    }
}