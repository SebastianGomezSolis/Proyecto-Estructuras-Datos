package Utilidades;

public class Ordenador {

    // ------------ RADIX ENTEROS ------------
    public static void radixSortEnteros(int[] arr) {
        if (arr == null || arr.length <= 1) return;
        int max = getMax(arr);
        for (int exp = 1; max / exp > 0; exp *= 10)
            countingSortEnteros(arr, exp);
    }

    private static void countingSortEnteros(int[] arr, int exp) {
        int n = arr.length;
        int[] salida = new int[n];
        int[] conteo = new int[10];

        for (int i = 0; i < n; i++) conteo[(arr[i] / exp) % 10]++;
        for (int i = 1; i < 10; i++) conteo[i] += conteo[i - 1];
        for (int i = n - 1; i >= 0; i--) {
            int idx = (arr[i] / exp) % 10;
            salida[--conteo[idx]] = arr[i];
        }
        System.arraycopy(salida, 0, arr, 0, n);
    }

    // ------------ RADIX PAREADO (enteros + Object[]) ------------
    public static void radixSortPareadoEnteros(int[] arr, Object[] aux) {
        if (arr == null || arr.length <= 1) return;
        int max = getMax(arr);
        for (int exp = 1; max / exp > 0; exp *= 10)
            countingSortPareado(arr, aux, exp);
    }

    private static void countingSortPareado(int[] arr, Object[] aux, int exp) {
        int n = arr.length;
        int[] salida = new int[n];
        Object[] auxOut = new Object[n];
        int[] conteo = new int[10];

        for (int i = 0; i < n; i++) conteo[(arr[i] / exp) % 10]++;
        for (int i = 1; i < 10; i++) conteo[i] += conteo[i - 1];
        for (int i = n - 1; i >= 0; i--) {
            int idx = (arr[i] / exp) % 10;
            int pos = --conteo[idx];
            salida[pos] = arr[i];
            auxOut[pos] = aux[i];
        }
        for (int i = 0; i < n; i++) {
            arr[i] = salida[i];
            aux[i] = auxOut[i];
        }
    }

    // ------------ NUEVO: RADIX PAREADO (enteros + int[]) ------------
    public static void radixSortPareadoEnteros(int[] arr, int[] aux) {
        if (arr == null || arr.length <= 1) return;
        if (aux == null || aux.length != arr.length) throw new IllegalArgumentException("Aux debe tener mismo tamaño que arr");
        int max = getMax(arr);
        for (int exp = 1; max / exp > 0; exp *= 10)
            countingSortPareadoInt(arr, aux, exp);
    }

    private static void countingSortPareadoInt(int[] arr, int[] aux, int exp) {
        int n = arr.length;
        int[] salida = new int[n];
        int[] auxOut = new int[n];
        int[] conteo = new int[10];

        for (int i = 0; i < n; i++) conteo[(arr[i] / exp) % 10]++;
        for (int i = 1; i < 10; i++) conteo[i] += conteo[i - 1];
        for (int i = n - 1; i >= 0; i--) {
            int idx = (arr[i] / exp) % 10;
            int pos = --conteo[idx];
            salida[pos] = arr[i];
            auxOut[pos] = aux[i];
        }
        for (int i = 0; i < n; i++) {
            arr[i] = salida[i];
            aux[i] = auxOut[i];
        }
    }

    private static int getMax(int[] arr) {
        int max = arr[0];
        for (int val : arr) if (val > max) max = val;
        return max;
    }

    // ------------ RADIX STRINGS (LSD) ------------
    public static void radixSortStrings(String[] arr) {
        if (arr == null || arr.length <= 1) return;
        int n = arr.length;
        int maxLen = 0;
        for (String s : arr) if (s.length() > maxLen) maxLen = s.length();

        for (int pos = maxLen - 1; pos >= 0; pos--)
            countingSortStrings(arr, pos);
    }

    private static void countingSortStrings(String[] arr, int pos) {
        int n = arr.length;
        String[] salida = new String[n];
        int[] conteo = new int[257]; // 256 ASCII + 1 para -1 (pos fuera de rango)

        for (int i = 0; i < n; i++) {
            int c = charAt(arr[i], pos) + 1; // +1 para desplazar -1 -> 0
            conteo[c]++;
        }

        for (int i = 1; i < conteo.length; i++) conteo[i] += conteo[i - 1];

        for (int i = n - 1; i >= 0; i--) {
            int c = charAt(arr[i], pos) + 1;
            salida[--conteo[c]] = arr[i];
        }

        System.arraycopy(salida, 0, arr, 0, n);
    }

    private static int charAt(String s, int pos) {
        if (pos < s.length()) return s.charAt(pos);
        return -1;
    }
}
