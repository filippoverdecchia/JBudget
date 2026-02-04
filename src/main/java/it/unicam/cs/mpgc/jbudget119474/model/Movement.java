package it.unicam.cs.mpgc.jbudget119474.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Movement {

    private LocalDate date;
    private String description;
    private double amount;
    private List<Tag> tags;

    public Movement() {
        this.tags = new ArrayList<>();
    }

    public Movement(LocalDate date, String description, double amount) {
        this.date = date;
        this.description = description;

        if (Double.isNaN(amount) || Double.isInfinite(amount)) {
            throw new IllegalArgumentException("Invalid amount");
        }

        this.amount = amount;
        this.tags = new ArrayList<>();
    }

    public Movement(LocalDate date, String description, double amount, List<Tag> tags) {
        this.date = date;
        this.description = description;

        if (Double.isNaN(amount) || Double.isInfinite(amount)) {
            throw new IllegalArgumentException("Invalid amount");
        }

        this.amount = amount;
        this.tags = new ArrayList<>(tags);
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public List<Tag> getTags() {
        return Collections.unmodifiableList(tags);
    }

    public void addTag(Tag tag) {
        if (tag == null) {
            throw new IllegalArgumentException("tag cannot be null");
        }
        tags.add(tag);
    }

    public void clearTags() {
        tags.clear();
    }

    @Override
    public String toString() {
        return "Movement on " + date + " - " + description + ": " + amount + " €";
    }
}
