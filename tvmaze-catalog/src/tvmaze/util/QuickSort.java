package src.tvmaze.util;
import src.tvmaze.model.Serie;
import java.util.List;

public class QuickSort {

    public static void ordenarSeriesPorNota(List<Serie> series) {
        if (series == null || series.size() <= 1) {
            return;
        }
        quickSort(series, 0, series.size() - 1);
    }

    private static void quickSort(List<Serie> series, int low, int high) {
        if (low < high) {
            int pi = partition(series, low, high);
            quickSort(series, low, pi - 1);
            quickSort(series, pi + 1, high);
        }
    }

    private static int partition(List<Serie> series, int low, int high) {
        Serie pivot = series.get(high);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (compararSeries(series.get(j), pivot) > 0) {
                i++;
                swap(series, i, j);
            }
        }

        swap(series, i + 1, high);
        return i + 1;
    }

    private static void swap(List<Serie> series, int i, int j) {
        Serie temp = series.get(i);
        series.set(i, series.get(j));
        series.set(j, temp);
    }

    /**
     * Compara duas séries por nota (decrescente), depois nome, depois ID
     * Retorna > 0 se s1 deve vir antes de s2
     */
    private static int compararSeries(Serie s1, Serie s2) {
        // Comparar por nota (decrescente)
        Double nota1 = s1.getNotaMedia() != null ? s1.getNotaMedia() : 0.0;
        Double nota2 = s2.getNotaMedia() != null ? s2.getNotaMedia() : 0.0;

        int notaComp = nota2.compareTo(nota1); // Invertido para ordem decrescente

        if (notaComp != 0) {
            return notaComp;
        }

        // Desempate por nome
        int nomeComp = s1.getNome().compareToIgnoreCase(s2.getNome());

        if (nomeComp != 0) {
            return nomeComp;
        }

        // Desempate por ID
        return Integer.compare(s1.getIdExterno(), s2.getIdExterno());
    }
}
