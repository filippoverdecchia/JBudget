package it.unicam.cs.mpgc.jbudget119474.model;

import java.util.*;

public class TagTreeImpl implements TagTree {

    private final Map<Tag, Tag> parentMap;
    private final Map<Tag, List<Tag>> childrenMap;
    private final Tag root;

    public TagTreeImpl(String rootName) {
        this.root = new Tag(rootName);
        this.parentMap = new HashMap<>();
        this.childrenMap = new HashMap<>();
        childrenMap.put(root, new ArrayList<>());
    }

    @Override
    public Tag getRoot() {
        return root;
    }

    @Override
    public Tag getParent(Tag tag) {
        return parentMap.get(tag);
    }

    @Override
    public Collection<Tag> getChildren(Tag tag) {
        List<Tag> children = childrenMap.get(tag);
        return children == null ? List.of() : Collections.unmodifiableList(children);
    }

    @Override
    public void addSubTag(Tag parent, Tag child) {
        Objects.requireNonNull(parent, "parent");
        Objects.requireNonNull(child, "child");

        childrenMap.computeIfAbsent(parent, k -> new ArrayList<>());

        if (!childrenMap.get(parent).contains(child)) {
            childrenMap.get(parent).add(child);
        }

        parentMap.putIfAbsent(child, parent);
        childrenMap.computeIfAbsent(child, k -> new ArrayList<>());
    }

    @Override
    public Set<Tag> getAllSubTagsIncluding(Tag tag) {
        Set<Tag> result = new HashSet<>();
        collectRecursively(tag, result);
        return result;
    }

    private void collectRecursively(Tag current, Set<Tag> visited) {
        if (visited.contains(current)) {
            return;
        }

        visited.add(current);

        for (Tag child : getChildren(current)) {
            collectRecursively(child, visited);
        }
    }

    @Override
    public Tag getOrCreateTag(String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Tag path vuoto");
        }

        String[] sections = path.split(":");
        Tag current = root;

        for (String part : sections) {
            String name = part.trim();
            if (name.isEmpty()) {
                continue;
            }

            Tag next = new Tag(name);

            for (Tag child : getChildren(current)) {
                if (child.equals(next)) {
                    next = child;
                    break;
                }
            }

            if (!childrenMap.get(current).contains(next)) {
                addSubTag(current, next);
            }

            current = next;
        }

        return current;
    }
}
