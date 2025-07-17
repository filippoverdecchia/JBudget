package it.unicam.cs.mpgc.jbudget119474.model;

import java.util.*;

public class TagTreeImpl implements TagTree {

    private final Map<Tag, Tag> mappaPadri;
    private final Map<Tag, List<Tag>> mappaFigli;
    private final Tag radice;

    public TagTreeImpl(String nomeRadice) {
        this.radice = new Tag(nomeRadice);
        this.mappaPadri = new HashMap<>();
        this.mappaFigli = new HashMap<>();
        mappaFigli.put(radice, new ArrayList<>());
    }

    @Override
    public Tag getRoot() {
        return radice;
    }

    @Override
    public Tag getParent(Tag tag) {
        return mappaPadri.get(tag);
    }

    @Override
    public Collection<Tag> getChildren(Tag tag) {
        if (mappaFigli.containsKey(tag)) {
            return mappaFigli.get(tag);
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void addSubTag(Tag padre, Tag figlio) {
        if (!mappaFigli.containsKey(padre)) {
            mappaFigli.put(padre, new ArrayList<>());
        }
        mappaFigli.get(padre).add(figlio);
        mappaPadri.put(figlio, padre);
        if (!mappaFigli.containsKey(figlio)) {
            mappaFigli.put(figlio, new ArrayList<>());
        }
    }

    @Override
    public Set<Tag> getAllSubTagsIncluding(Tag tag) {
        Set<Tag> insieme = new HashSet<>();
        aggiungiRicorsivamente(tag, insieme);
        return insieme;
    }

    private void aggiungiRicorsivamente(Tag tagCorrente, Set<Tag> accumulo) {
        accumulo.add(tagCorrente);
        for (Tag figlio : getChildren(tagCorrente)) {
            aggiungiRicorsivamente(figlio, accumulo);
        }
    }

    @Override
    public Tag getOrCreateTag(String percorso) {
        String[] sezioni = percorso.split(":");
        Tag corrente = radice;

        for (String parte : sezioni) {
            String nomePulito = parte.trim();
            Tag successivo = new Tag(nomePulito);
            boolean trovato = false;

            for (Tag figlio : getChildren(corrente)) {
                if (figlio.equals(successivo)) {
                    successivo = figlio;
                    trovato = true;
                    break;
                }
            }

            if (!trovato) {
                addSubTag(corrente, successivo);
            }

            corrente = successivo;
        }

        return corrente;
    }
}
