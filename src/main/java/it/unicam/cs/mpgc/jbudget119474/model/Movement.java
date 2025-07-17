package it.unicam.cs.mpgc.jbudget119474.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Movement {

    private LocalDate data;
    private String descrizione;
    private double importo;
    private List<Tag> listaTag;

    public Movement() {
        // Necessario per Jackson o deserializzazione
        listaTag = new ArrayList<>();
    }

    public Movement(LocalDate data, String descrizione, double importo) {
        this.data = data;
        this.descrizione = descrizione;
        this.importo = importo;
        this.listaTag = new ArrayList<>();
    }

    public Movement(LocalDate data, String descrizione, double importo, List<Tag> tag) {
        this.data = data;
        this.descrizione = descrizione;
        this.importo = importo;
        this.listaTag = new ArrayList<>(tag);
    }

    public LocalDate getData() {
        return data;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public double getImporto() {
        return importo;
    }

    public List<Tag> getTag() {
        return listaTag;
    }

    public void aggiungiTag(Tag t) {
        listaTag.add(t);
    }

    public void clearTag() {
        listaTag.clear();
    }

    @Override
    public String toString() {
        return "Movimento del " + data + " - " + descrizione + ": " + importo + " €";
    }
}
