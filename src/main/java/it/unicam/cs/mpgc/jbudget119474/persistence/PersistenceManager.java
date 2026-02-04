package it.unicam.cs.mpgc.jbudget119474.persistence;

import it.unicam.cs.mpgc.jbudget119474.repository.MovementRepository;
import it.unicam.cs.mpgc.jbudget119474.model.TagTree;

import java.io.IOException;

public interface PersistenceManager {

    void save(MovementRepository repository,
              TagTree tagTree,
              String fileName) throws IOException;

    void load(String fileName,
              MovementRepository repository,
              TagTree tagTree) throws IOException;
}
