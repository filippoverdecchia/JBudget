package it.unicam.cs.mpgc.jbudget119474.repository;

import it.unicam.cs.mpgc.jbudget119474.model.*;

import java.time.LocalDate;
import java.util.*;

public class MovementRepositoryImpl implements MovementRepository {

    private final List<Movement> movements = new ArrayList<>();
    private final List<ScheduledMovement> scheduledMovements = new ArrayList<>();

    @Override
    public void add(Movement movement) {
        movements.add(movement);
    }

    @Override
    public void addAll(List<Movement> movements) {
        this.movements.addAll(movements);
    }

    @Override
    public void setAll(List<Movement> movements) {
        clear();
        this.movements.addAll(movements);
    }

    @Override
    public void clear() {
        movements.clear();
        scheduledMovements.clear();
    }

    @Override
    public List<Movement> getAll() {
        return new ArrayList<>(movements);
    }

    @Override
    public double getTotalBalance() {
        return movements.stream()
                .mapToDouble(Movement::getAmount)
                .sum();
    }

    @Override
    public double getBalanceForTag(Tag tag, TagTree tagTree) {
        Set<Tag> validTags = tagTree.getAllSubTagsIncluding(tag);

        return movements.stream()
                .filter(m -> m.getTags().stream().anyMatch(validTags::contains))
                .mapToDouble(Movement::getAmount)
                .sum();
    }

    @Override
    public void addScheduled(ScheduledMovement scheduled) {
        scheduledMovements.add(scheduled);
    }

    @Override
    public List<ScheduledMovement> getScheduled() {
        return new ArrayList<>(scheduledMovements);
    }

    @Override
    public void applyScheduledMovements(LocalDate today) {
        Iterator<ScheduledMovement> iterator = scheduledMovements.iterator();

        while (iterator.hasNext()) {
            ScheduledMovement sm = iterator.next();

            LocalDate date = sm.getDate();
            if (date != null && !date.isAfter(today)) {
                Movement concrete = sm.toConcreteMovement();
                if (concrete != null) {
                    movements.add(concrete);
                }
                iterator.remove();
            }
        }
    }
}
