package it.unicam.cs.mpgc.jbudget119474.persistence;

import it.unicam.cs.mpgc.jbudget119474.repository.MovementRepository;
import it.unicam.cs.mpgc.jbudget119474.model.TagTree;

import java.io.IOException;

public interface PersistenceManager {
    void salva(MovementRepository repository, TagTree tagTree, String filename) throws IOException;
    void carica(String filename, MovementRepository repository, TagTree tagTree) throws IOException;
}
