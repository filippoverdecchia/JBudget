package it.unicam.cs.mpgc.jbudget119474.stats;

import it.unicam.cs.mpgc.jbudget119474.model.Movement;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatisticsService {

    private final List<Movement> movements;

    public StatisticsService(List<Movement> movements) {
        this.movements = movements;
    }

    public Map<String, Double> comparePeriods(
            LocalDate start1, LocalDate end1,
            LocalDate start2, LocalDate end2) {

        if (start1 == null || end1 == null || start2 == null || end2 == null) {
            throw new IllegalArgumentException();
        }

        if (start1.isAfter(end1) || start2.isAfter(end2)) {
            throw new IllegalArgumentException();
        }

        double total1 = movements.stream()
                .filter(m -> !m.getDate().isBefore(start1)
                        && !m.getDate().isAfter(end1))
                .mapToDouble(Movement::getAmount)
                .sum();

        double total2 = movements.stream()
                .filter(m -> !m.getDate().isBefore(start2)
                        && !m.getDate().isAfter(end2))
                .mapToDouble(Movement::getAmount)
                .sum();

        Map<String, Double> result = new HashMap<>();
        result.put("Periodo 1", total1);
        result.put("Periodo 2", total2);
        result.put("Differenza", total2 - total1);

        return result;
    }
}
