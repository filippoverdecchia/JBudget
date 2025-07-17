package it.unicam.cs.mpgc.jbudget119474.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RatePlan {

    private final List<ScheduledMovement> listaRate;

    public RatePlan(LocalDate dataInizio, int numeroMesi, double totale, String descrizione) {
        listaRate = new ArrayList<>();

        if (numeroMesi <= 0) {
            throw new IllegalArgumentException("Il numero di mesi deve essere maggiore di zero.");
        }

        double importoPerMese = totale / numeroMesi;

        // Genera le rate mensili partendo dalla data iniziale
        for (int i = 1; i <= numeroMesi; i++) {
            LocalDate dataRata = dataInizio.plusMonths(i - 1);
            String desc = descrizione + " - Rata " + i;

            Movement movimento = new Movement(dataRata, desc, -importoPerMese);
            ScheduledMovement scheduled = new ScheduledMovement(movimento, this);

            listaRate.add(scheduled);
        }
    }

    public LocalDate getProssimaData(LocalDate oggi) {
        for (ScheduledMovement r : listaRate) {
            if (r.getDate().isAfter(oggi)) {
                return r.getDate();
            }
        }
        return null; // Nessuna rata futura
    }

    public List<ScheduledMovement> getListaRate() {
        return listaRate;
    }
}
