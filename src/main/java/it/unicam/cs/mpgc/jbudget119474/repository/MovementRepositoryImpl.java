package it.unicam.cs.mpgc.jbudget119474.repository;

import it.unicam.cs.mpgc.jbudget119474.model.*;

import java.time.LocalDate;
import java.util.*;

/**
 * Implementazione semplice di un repository di movimenti e movimenti pianificati.
 */
public class MovementRepositoryImpl implements MovementRepository {

    private final List<Movement> listaMovimenti = new ArrayList<>();
    private final List<ScheduledMovement> listaSchedulati = new ArrayList<>();

    @Override
    public void add(Movement movimento) {
        listaMovimenti.add(movimento);
    }

    @Override
    public void addAll(List<Movement> movimenti) {
        listaMovimenti.addAll(movimenti);
    }

    @Override
    public void setAll(List<Movement> movimenti) {
        clear();
        listaMovimenti.addAll(movimenti);
    }

    @Override
    public void clear() {
        listaMovimenti.clear();
        listaSchedulati.clear();
    }

    @Override
    public List<Movement> getAll() {
        applicaSchedulati();
        return new ArrayList<>(listaMovimenti);
    }

    @Override
    public double getTotalBalance() {
        return listaMovimenti.stream()
                .mapToDouble(Movement::getImporto)
                .sum();
    }

    @Override
    public double getBalanceForTag(Tag tag, TagTree alberoTag) {
        Set<Tag> tagValidi = alberoTag.getAllSubTagsIncluding(tag);
        return listaMovimenti.stream()
                .filter(m -> m.getTag().stream().anyMatch(tagValidi::contains))
                .mapToDouble(Movement::getImporto)
                .sum();
    }

    @Override
    public void addScheduled(ScheduledMovement schedulato) {
        listaSchedulati.add(schedulato);
    }

    @Override
    public List<ScheduledMovement> getScheduled() {
        return new ArrayList<>(listaSchedulati);
    }

    private void applicaSchedulati() {
        Iterator<ScheduledMovement> iteratore = listaSchedulati.iterator();
        List<ScheduledMovement> daAggiungere = new ArrayList<>();

        while (iteratore.hasNext()) {
            ScheduledMovement sm = iteratore.next();
            if (!sm.getDate().isAfter(LocalDate.now())) {
                listaMovimenti.add(sm.toMovement());
                ScheduledMovement prossimo = sm.next();
                if (prossimo != null) {
                    daAggiungere.add(prossimo);
                }
                iteratore.remove();
            }
        }

        listaSchedulati.addAll(daAggiungere);
    }
}
