package it.unicam.cs.mpgc.jbudget119474.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.unicam.cs.mpgc.jbudget119474.model.*;
import it.unicam.cs.mpgc.jbudget119474.repository.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class JsonPersistence implements PersistenceManager{

    private final ObjectMapper mapper;

    public JsonPersistence() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    public void salva(MovementRepository repoMovimenti, TagTree alberoTag, String nomeFile) throws IOException {
        ObjectNode nodoRadice = mapper.createObjectNode();

        // Salva la lista dei movimenti
        nodoRadice.putPOJO("movimenti", repoMovimenti.getAll());

        // Costruisce la gerarchia dei tag come lista di mappe
        List<Map<String, String>> gerarchiaTag = new ArrayList<>();
        for (Tag figlio : alberoTag.getChildren(alberoTag.getRoot())) {
            aggiungiGerarchia(gerarchiaTag, alberoTag, alberoTag.getRoot(), figlio);
        }
        nodoRadice.putPOJO("tagTree", gerarchiaTag);

        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(nomeFile), nodoRadice);
    }

    public void carica(String nomeFile, MovementRepository repo, TagTree alberoTag) throws IOException {
        ObjectNode nodoRadice = (ObjectNode) mapper.readTree(new File(nomeFile));

        List<Movement> listaMovimenti = mapper.convertValue(nodoRadice.get("movimenti"), new TypeReference<>() {});
        List<Map<String, String>> gerarchiaTag = mapper.convertValue(nodoRadice.get("tagTree"), new TypeReference<>() {});

        // Aggiunge i movimenti
        repo.setAll(listaMovimenti);

        // Ricostruisce la gerarchia
        for (Map<String, String> mappa : gerarchiaTag) {
            Tag padre = new Tag(mappa.get("parent"));
            Tag figlio = new Tag(mappa.get("child"));
            alberoTag.addSubTag(padre, figlio);
        }

        // Ricollega i tag ai movimenti usando quelli dell'albero
        for (Movement mov : listaMovimenti) {
            Set<Tag> tagAggiornati = new HashSet<>();
            for (Tag t : mov.getTag()) {
                tagAggiornati.add(alberoTag.getOrCreateTag(t.getName()));
            }
            mov.clearTag();
            for (Tag t : tagAggiornati) {
                mov.aggiungiTag(t);
            }
        }
    }

    private void aggiungiGerarchia(List<Map<String, String>> lista, TagTree albero, Tag padre, Tag figlio) {
        Map<String, String> coppia = new HashMap<>();
        coppia.put("parent", padre.getName());
        coppia.put("child", figlio.getName());
        lista.add(coppia);

        for (Tag sotto : albero.getChildren(figlio)) {
            aggiungiGerarchia(lista, albero, figlio, sotto);
        }
    }
}
