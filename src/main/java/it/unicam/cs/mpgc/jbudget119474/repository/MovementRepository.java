package it.unicam.cs.mpgc.jbudget119474.repository;

import it.unicam.cs.mpgc.jbudget119474.model.*;

import java.time.LocalDate;
import java.util.List;

public interface MovementRepository {
    void add(Movement m);
    void addAll(List<Movement> list);
    void clear();
    List<Movement> getAll();
    void setAll(List<Movement> list);
    double getTotalBalance();
    double getBalanceForTag(Tag tag, TagTree tree);
    void addScheduled(ScheduledMovement s);
    void applyScheduledMovements(LocalDate today);
    List<ScheduledMovement> getScheduled();
}
