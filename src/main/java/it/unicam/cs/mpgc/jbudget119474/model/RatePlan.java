package it.unicam.cs.mpgc.jbudget119474.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RatePlan {

    private final List<ScheduledMovement> installments;

    public RatePlan(LocalDate startDate, int months, double totalAmount, String description) {
        if (startDate == null) {
            throw new IllegalArgumentException("startDate cannot be null.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description cannot be null or blank.");
        }
        if (months <= 0) {
            throw new IllegalArgumentException("months must be greater than zero.");
        }
        if (Double.isNaN(totalAmount) || Double.isInfinite(totalAmount)) {
            throw new IllegalArgumentException("totalAmount is invalid.");
        }

        this.installments = new ArrayList<>();

        double amountPerMonth = totalAmount / months;

        for (int i = 1; i <= months; i++) {
            LocalDate installmentDate = startDate.plusMonths(i - 1);
            String installmentDescription = description + " - Installment " + i;

            Movement movement = new Movement(
                    installmentDate,
                    installmentDescription,
                    amountPerMonth
            );

            installments.add(new ScheduledMovement(movement, this));
        }
    }

    public LocalDate getNextDateAfter(LocalDate date) {
        return nextDateAfter(date);
    }

    public LocalDate nextDateAfter(LocalDate date) {
        if (date == null) {
            return null;
        }

        return installments.stream()
                .map(ScheduledMovement::getDate)
                .filter(d -> d != null && d.isAfter(date))
                .min(LocalDate::compareTo)
                .orElse(null);
    }

    public List<ScheduledMovement> getInstallments() {
        return Collections.unmodifiableList(installments);
    }
}
