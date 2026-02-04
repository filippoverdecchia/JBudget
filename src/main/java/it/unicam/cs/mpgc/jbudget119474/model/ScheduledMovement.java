package it.unicam.cs.mpgc.jbudget119474.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.ArrayList;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduledMovement {

    private Movement baseMovement;

    @JsonIgnore
    private RatePlan plan;

    public ScheduledMovement() {
    }

    public ScheduledMovement(Movement baseMovement, RatePlan plan) {
        this.baseMovement = baseMovement;
        this.plan = plan;
    }

    public Movement getBaseMovement() {
        return baseMovement;
    }

    public RatePlan getPlan() {
        return plan;
    }

    public LocalDate getDate() {
        if (baseMovement == null) {
            return null;
        }
        return baseMovement.getDate();
    }

    public Movement toConcreteMovement() {
        if (baseMovement == null) {
            return null;
        }

        return new Movement(
                baseMovement.getDate(),
                baseMovement.getDescription(),
                baseMovement.getAmount(),
                new ArrayList<>(baseMovement.getTags())
        );
    }

    public ScheduledMovement nextOccurrence() {

        if (baseMovement == null || plan == null) {
            return null;
        }

        LocalDate nextDate =
                plan.getNextDateAfter(
                        baseMovement.getDate());

        if (nextDate == null) {
            return null;
        }

        Movement next =
                new Movement(
                        nextDate,
                        baseMovement.getDescription(),
                        baseMovement.getAmount(),
                        new ArrayList<>(
                                baseMovement.getTags()));

        return new ScheduledMovement(next, plan);
    }
}
