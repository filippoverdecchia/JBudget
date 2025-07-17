package it.unicam.cs.mpgc.jbudget119474.model;

import java.util.*;

public interface TagTree {
    Tag getRoot();
    Tag getParent(Tag tag);
    Tag getOrCreateTag(String path);
    Collection<Tag> getChildren(Tag tag);
    void addSubTag(Tag parent, Tag child);
    Set<Tag> getAllSubTagsIncluding(Tag tag);

}
