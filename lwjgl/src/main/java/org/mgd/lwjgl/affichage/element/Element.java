package org.mgd.lwjgl.affichage.element;

import org.mgd.commun.Matrice;
import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Fenetre.EvenementSouris;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.Primitif;
import org.mgd.lwjgl.affichage.element.forme.Forme;
import org.mgd.lwjgl.commun.Animateur;
import org.mgd.lwjgl.commun.Identifiable;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.interne.Ombreur;
import org.mgd.lwjgl.interne.Tisseur;

import java.nio.file.Path;
import java.util.*;

public abstract class Element<G> extends Primitif implements Animateur {
    protected final Map<G, List<Forme>> groupes;
    private final UUID uuid;
    private final int priorite;
    private final Matrice<Float> transformation;
    private final LinkedList<G> ordre;
    private final String nom;
    protected List<Forme> formesSurvoles;

    protected Element(Fenetre parent,
                      String nom,
                      int priorite,
                      float[] translation,
                      float[] agrandissement,
                      float[] rotation,
                      Map<String, Path> textures) throws LwjglException {
        super(parent, true);
        this.uuid = UUID.randomUUID();
        this.nom = nom;
        this.priorite = priorite;
        this.transformation = Matrice.transformation(translation, agrandissement, rotation);
        this.ordre = new LinkedList<>();
        this.groupes = new HashMap<>();
        this.formesSurvoles = new ArrayList<>();

        if (!textures.isEmpty()) {
            Tisseur.compiler(nom, textures);
        }
        parent.enfants().add(this);
    }

    @Override
    public UUID uuid() {
        return uuid;
    }

    @Override
    public Fenetre parent() {
        return parent;
    }

    @Override
    public boolean survoler(Vision vision, EvenementSouris evenementSouris) {
        formesSurvoles = Collections.emptyList();
        if (visible) {
            for (G groupe : ordre) {
                List<Forme> formes = groupes.get(groupe);
                for (Forme forme : formes) {
                    if (formesSurvoles.isEmpty() && forme.survoler(vision, evenementSouris)) {
                        formesSurvoles = Collections.singletonList(forme);
                    } else {
                        forme.desurvoler();
                    }
                }
            }
        }
        return !formesSurvoles.isEmpty();
    }

    @Override
    public void retirer(Vision vision, EvenementSouris evenementSouris) {
        formesSurvoles.forEach(Forme::desurvoler);
        formesSurvoles = Collections.emptyList();
    }

    @Override
    public Collection<Identifiable> amorcer(boolean droite) {
        return formesSurvoles.stream().map(Identifiable.class::cast).toList();
    }

    @Override
    public void maj(long accumulateur, Vision vision, EvenementSouris evenementSouris, Fenetre.EvenementAmorcages evenementAmorcagesCourant) throws LwjglException {
        Animateur.super.maj(accumulateur, vision, evenementSouris, evenementAmorcagesCourant);
        ordre.forEach(groupe -> groupes.get(groupe).forEach(forme -> forme.maj(accumulateur, vision, evenementSouris, evenementAmorcagesCourant)));
    }

    @Override
    public boolean visible() {
        return visible;
    }

    @Override
    public void jouer(long ellipse, Vision vision) {
        Ombreur.configurer("transformation", transformation);
        ordre.forEach(groupe -> groupes.get(groupe).forEach(forme -> forme.produire(ellipse, vision)));
    }

    public void nettoyer() {
        ordre.forEach(groupe -> groupes.get(groupe).forEach(Forme::nettoyer));
    }

    public List<Forme> ajouter(G groupe, Forme forme) {
        List<Forme> formes = groupes.computeIfAbsent(groupe, cle -> {
            ordre.addFirst(cle);
            return new LinkedList<>();
        });
        formes.add(forme);
        return formes;
    }

    public List<Forme> supprimer(G groupe, Forme forme) {
        return groupes.computeIfPresent(groupe, (_, formes) -> {
            formes.remove(forme);
            return formes;
        });
    }

    public void ajouter(G groupe, Collection<Forme> formes) {
        this.groupes.computeIfAbsent(groupe, cle -> {
            this.ordre.addFirst(cle);
            return new LinkedList<>();
        }).addAll(formes);
    }

    public String identifiant() {
        return nom;
    }

    public int priorite() {
        return priorite;
    }

    public Matrice<Float> transformation() {
        return transformation;
    }

    public void preparer(Vision vision) {
        ordre.forEach(groupe -> groupes.get(groupe).forEach(forme -> forme.preparer(vision)));
    }
}
