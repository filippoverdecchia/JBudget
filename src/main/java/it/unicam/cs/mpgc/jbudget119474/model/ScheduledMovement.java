package it.unicam.cs.mpgc.jbudget119474.model;

import java.time.LocalDate;

public class ScheduledMovement {
    private Movement original;
    private RatePlan plan;

    public ScheduledMovement() {
    }

    public ScheduledMovement(Movement movement, RatePlan plan) {
        this.original = movement;
        this.plan = plan;
    }


    public Movement getOriginal() {
        return original;
    }

    public RatePlan getPlan() {
        return plan;
    }

    public LocalDate getDate() {
        return original.getData();
    }

    /**
     * Ritorna una copia del movimento attuale (per essere registrato).
     */
    public Movement toMovement() {
        return new Movement(original.getData(), original.getDescrizione(), original.getImporto(), original.getTag());
    }

    /**
     * Ritorna il prossimo movimento secondo il piano, oppure null se finito.
     */
    public ScheduledMovement next() {
        LocalDate nextDate = plan.getProssimaData(original.getData());
        if (nextDate == null) return null;

        Movement next = new Movement(nextDate, original.getDescrizione(), original.getImporto(), original.getTag());
        return new ScheduledMovement(next, plan);
    }
}
