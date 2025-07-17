package it.unicam.cs.mpgc.jbudget119474.stats;

import it.unicam.cs.mpgc.jbudget119474.model.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servizio per calcolare statistiche sui movimenti.
 */
public class StatisticsService {

    private final List<Movement> listaMovimenti;

    public StatisticsService(List<Movement> movimenti, TagTree tagTree) {
        this.listaMovimenti = movimenti;
    }

    /**
     * Confronta la somma degli importi tra due intervalli temporali.
     *
     * @param inizio1 data di inizio periodo 1
     * @param fine1   data di fine periodo 1
     * @param inizio2 data di inizio periodo 2
     * @param fine2   data di fine periodo 2
     * @param tagTree albero dei tag (non usato in questa versione, ma presente)
     * @return mappa con totali dei due periodi e differenza
     */
    public Map<String, Double> confrontaPeriodi(LocalDate inizio1, LocalDate fine1,
                                                LocalDate inizio2, LocalDate fine2, TagTree tagTree) {
        double totale1 = listaMovimenti.stream()
                .filter(m -> !m.getData().isBefore(inizio1) && !m.getData().isAfter(fine1))
                .mapToDouble(Movement::getImporto).sum();

        double totale2 = listaMovimenti.stream()
                .filter(m -> !m.getData().isBefore(inizio2) && !m.getData().isAfter(fine2))
                .mapToDouble(Movement::getImporto).sum();
        Map<String, Double> risultato = new HashMap<>();
        risultato.put("Periodo 1", totale1);
        risultato.put("Periodo 2", totale2);
        risultato.put("Differenza", totale2 - totale1);
        return risultato;
    }
}
