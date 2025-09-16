package org.mgd.lwjgl.affichage.tetehaute;

import org.mgd.lwjgl.affichage.tetehaute.composant.Entite;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Permet le paramétrage d'une barre.
 *
 */
public final class Options {
    private final Entite suivante;
    private final Entite precedente;
    private final Supplier<Entite> secondaire;
    private final boolean sousEntites;
    private final Position positionSousEntites;
    private final Position positionInfobulle;

    /**
     * @param suivante            Entité permettant de passer à la page suivante de la pagination.
     * @param precedente          Entité permettant de passer à la page précédente de la pagination.
     * @param secondaire          Constructeur d'une entité permettant de faire apparaître le panneau des sous-entités
     * @param sousentites         Booléen indiquant s'il faut activer l'affichage des sous-entités.
     * @param positionSousEntites Position par rapport à la barre du panneau des sous-entités.
     * @param positionInfobulle   Position par rapport à la barre des infobulles.
     */
    public Options(Entite suivante,
                   Entite precedente,
                   Supplier<Entite> secondaire,
                   boolean sousentites,
                   Position positionSousEntites,
                   Position positionInfobulle) {
        this.suivante = suivante;
        this.precedente = precedente;
        this.secondaire = secondaire;
        this.sousEntites = sousentites;
        this.positionSousEntites = positionSousEntites;
        this.positionInfobulle = positionInfobulle;
    }

    public Options(Entite suivante, Entite precedente, Position positionInfobulle) {
        this(suivante, precedente, null, false, null, positionInfobulle);
    }

    public Options() {
        this(null, null, null, false, null, null);
    }

    public Optional<Entite> suivante() {
        return Optional.ofNullable(suivante);
    }

    public Optional<Entite> precedente() {
        return Optional.ofNullable(precedente);
    }

    public Optional<Supplier<Entite>> secondaire() {
        return Optional.ofNullable(secondaire);
    }

    public boolean sousentites() {
        return sousEntites;
    }

    public Optional<Position> positionSousEntites() {
        return Optional.ofNullable(positionSousEntites);
    }

    public Optional<Position> positionInfobulle() {
        return Optional.ofNullable(positionInfobulle);
    }
}
