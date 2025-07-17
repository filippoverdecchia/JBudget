package it.unicam.cs.mpgc.jbudget119474.model;

import java.util.*;

public class Tag {
    private String name;

    public Tag() {
        // Necessario per Jackson
    }

    public Tag(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    // Per funzionare correttamente nelle Mappe
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
