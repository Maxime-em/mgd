package org.mgd.lwjgl.affichage.element;

import org.mgd.commun.Matrice;
import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Fenetre.EvenementSouris;
import org.mgd.lwjgl.Vue;
import org.mgd.lwjgl.affichage.ObjetAffichable;
import org.mgd.lwjgl.affichage.element.forme.Maillage;
import org.mgd.lwjgl.commun.Animateur;
import org.mgd.lwjgl.commun.Identifiable;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.interne.ChargeurTextures;
import org.mgd.lwjgl.interne.GestionnaireOmbrages;

import java.nio.file.Path;
import java.util.*;

public abstract class GroupeFormes<G> extends ObjetAffichable implements Animateur {
    protected final Map<G, List<Maillage>> groupes;
    private final UUID uuid;
    private final Matrice<Float> transformation;
    private final LinkedList<G> ordre;
    private final String nom;
    protected List<Maillage> formesSurvoles;

    protected GroupeFormes(Fenetre parent,
                           String nom,
                           int priorite,
                           float[] translation,
                           float[] agrandissement,
                           float[] rotation,
                           Map<String, Path> textures) throws LwjglException {
        super(parent, priorite);
        this.uuid = UUID.randomUUID();
        this.nom = nom;
        this.transformation = Matrice.transformation(translation, agrandissement, rotation);
        this.ordre = new LinkedList<>();
        this.groupes = new HashMap<>();
        this.formesSurvoles = new ArrayList<>();

        if (!textures.isEmpty()) {
            ChargeurTextures.compiler(nom, textures);
        }
        parent.ajouterEnfant(this);
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
    public boolean survoler(Vue vue, EvenementSouris evenementSouris) {
        formesSurvoles = Collections.emptyList();
        if (visible) {
            for (G groupe : ordre) {
                List<Maillage> maillages = groupes.get(groupe);
                for (Maillage maillage : maillages) {
                    if (formesSurvoles.isEmpty() && maillage.survoler(vue, evenementSouris)) {
                        formesSurvoles = Collections.singletonList(maillage);
                    } else {
                        maillage.desurvoler();
                    }
                }
            }
        }
        return !formesSurvoles.isEmpty();
    }

    @Override
    public void retirer(Vue vue, EvenementSouris evenementSouris) {
        formesSurvoles.forEach(Maillage::desurvoler);
        formesSurvoles = Collections.emptyList();
    }

    @Override
    public Collection<Identifiable> amorcer(boolean droite) {
        return formesSurvoles.stream().map(Identifiable.class::cast).toList();
    }

    @Override
    public void maj(long accumulateur, Vue vue, EvenementSouris evenementSouris, Fenetre.EvenementAmorcages evenementAmorcagesCourant) throws LwjglException {
        Animateur.super.maj(accumulateur, vue, evenementSouris, evenementAmorcagesCourant);
        ordre.forEach(groupe -> groupes.get(groupe).forEach(forme -> forme.maj(accumulateur, vue, evenementSouris, evenementAmorcagesCourant)));
    }

    @Override
    public boolean visible() {
        return visible;
    }

    @Override
    public void jouer(long tempsEcouleMs, Vue vue) {
        GestionnaireOmbrages.configurer("transformation", transformation);
        ordre.forEach(groupe -> groupes.get(groupe).forEach(forme -> forme.produire(tempsEcouleMs, vue)));
    }

    public void nettoyer() {
        ordre.forEach(groupe -> groupes.get(groupe).forEach(Maillage::nettoyer));
    }

    public void ajouter(G groupe, Maillage maillage) {
        groupes.computeIfAbsent(groupe, cle -> {
            ordre.addFirst(cle);
            return new LinkedList<>();
        }).add(maillage);
    }

    public void supprimer(G groupe, Maillage maillage) {
        groupes.computeIfPresent(groupe, (_, formes) -> {
            formes.remove(maillage);
            return formes;
        });
    }

    public void ajouter(G groupe, Collection<Maillage> maillages) {
        this.groupes.computeIfAbsent(groupe, cle -> {
            this.ordre.addFirst(cle);
            return new LinkedList<>();
        }).addAll(maillages);
    }

    public String identifiant() {
        return nom;
    }

    public Matrice<Float> transformation() {
        return transformation;
    }

    public void preparer(Vue vue) {
        ordre.forEach(groupe -> groupes.get(groupe).forEach(forme -> forme.preparer(vue)));
    }
}
