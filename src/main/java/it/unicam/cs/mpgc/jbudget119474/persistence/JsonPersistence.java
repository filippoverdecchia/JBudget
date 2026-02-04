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

public class JsonPersistence implements PersistenceManager {

    private final ObjectMapper mapper;

    public JsonPersistence() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    public void save(MovementRepository movementRepository,
                     TagTree tagTree,
                     String fileName) throws IOException {

        ObjectNode rootNode = mapper.createObjectNode();

        rootNode.putPOJO("movimenti", movementRepository.getAll());
        rootNode.putPOJO("schedulati", movementRepository.getScheduled());

        List<Map<String, String>> tagHierarchy = new ArrayList<>();
        for (Tag child : tagTree.getChildren(tagTree.getRoot())) {
            addHierarchy(tagHierarchy, tagTree, tagTree.getRoot(), child);
        }

        rootNode.putPOJO("tagTree", tagHierarchy);

        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(new File(fileName), rootNode);
    }

    public void load(String fileName,
                     MovementRepository repository,
                     TagTree tagTree) throws IOException {

        ObjectNode rootNode =
                (ObjectNode) mapper.readTree(new File(fileName));

        List<Movement> movements =
                rootNode.hasNonNull("movimenti")
                        ? mapper.convertValue(
                                rootNode.get("movimenti"),
                                new TypeReference<List<Movement>>() {})
                        : new ArrayList<>();

        List<ScheduledMovement> scheduled =
                rootNode.hasNonNull("schedulati")
                        ? mapper.convertValue(
                                rootNode.get("schedulati"),
                                new TypeReference<List<ScheduledMovement>>() {})
                        : new ArrayList<>();

        List<Map<String, String>> tagHierarchy =
                rootNode.hasNonNull("tagTree")
                        ? mapper.convertValue(
                                rootNode.get("tagTree"),
                                new TypeReference<List<Map<String, String>>>() {})
                        : new ArrayList<>();

        repository.setAll(movements);

        for (Map<String, String> map : tagHierarchy) {
            Tag parent = new Tag(map.get("parent"));
            Tag child = new Tag(map.get("child"));
            tagTree.addSubTag(parent, child);
        }

        for (Movement movement : movements) {
            realignTags(movement, tagTree);
        }

        for (ScheduledMovement sm : scheduled) {
            Movement base = sm.getBaseMovement();
            if (base != null) {
                realignTags(base, tagTree);
            }
            repository.addScheduled(sm);
        }
    }

    private void addHierarchy(List<Map<String, String>> list,
                              TagTree tree,
                              Tag parent,
                              Tag child) {

        Map<String, String> pair = new HashMap<>();
        pair.put("parent", parent.getName());
        pair.put("child", child.getName());
        list.add(pair);

        for (Tag sub : tree.getChildren(child)) {
            addHierarchy(list, tree, child, sub);
        }
    }

    private void realignTags(Movement movement, TagTree tagTree) {

        Set<Tag> newTags = new HashSet<>();

        for (Tag t : movement.getTags()) {
            newTags.add(tagTree.getOrCreateTag(t.getName()));
        }

        movement.clearTags();

        for (Tag t : newTags) {
            movement.addTag(t);
        }
    }
}
