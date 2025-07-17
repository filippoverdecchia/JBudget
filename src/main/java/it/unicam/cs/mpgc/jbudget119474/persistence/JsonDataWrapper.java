package it.unicam.cs.mpgc.jbudget119474.persistence;

import it.unicam.cs.mpgc.jbudget119474.model.Movement;

import java.util.List;
import java.util.Map;

/**
 * Classe d'appoggio per rappresentare i dati da salvare in JSON.
 */
public class JsonDataWrapper {

    public List<Movement> movimenti;
    public Map<String, List<String>> gerarchiaTag;

    public JsonDataWrapper() {
        // Costruttore vuoto richiesto per Jackson
    }

    public JsonDataWrapper(List<Movement> movimenti, Map<String, List<String>> gerarchiaTag) {
        this.movimenti = movimenti;
        this.gerarchiaTag = gerarchiaTag;
    }
}
