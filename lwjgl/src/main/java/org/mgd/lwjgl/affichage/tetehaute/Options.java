package org.mgd.lwjgl.affichage.tetehaute;

import org.mgd.lwjgl.affichage.tetehaute.composant.Entite;

import java.util.Optional;

/**
 * Permet le paramétrage d'une barre.
 *
 */
public final class Options {
    private final Entite suivante;
    private final Entite precedente;
    private final Position positionInfobulle;
    private final boolean sousEntites;
    private final Position positionSousEntites;
    private final Entite secondaire;

    /**
     * @param suivante            Entité permettant de passer à la page suivante de la pagination.
     * @param precedente          Entité permettant de passer à la page précédente de la pagination.
     * @param positionInfobulle   Position par rapport à la barre des infobulles.
     * @param sousentites         Booléen indiquant s'il faut activer l'affichage des sous-entités.
     * @param positionSousEntites Position par rapport à la barre du panneau des sous-entités.
     * @param secondaire          Entité permettant de faire apparaître le panneau des sous-entités
     */
    public Options(Entite suivante,
                   Entite precedente,
                   Position positionInfobulle,
                   boolean sousentites,
                   Position positionSousEntites,
                   Entite secondaire) {
        this.suivante = suivante;
        this.precedente = precedente;
        this.positionInfobulle = positionInfobulle;
        this.sousEntites = sousentites;
        this.positionSousEntites = positionSousEntites;
        this.secondaire = secondaire;
    }

    public Options(Entite suivante, Entite precedente, Position positionInfobulle) {
        this(suivante, precedente, positionInfobulle, false, null, null);
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

    public Optional<Position> positionInfobulle() {
        return Optional.ofNullable(positionInfobulle);
    }

    public boolean sousentites() {
        return sousEntites;
    }

    public Optional<Position> positionSousEntites() {
        return Optional.ofNullable(positionSousEntites);
    }

    public Optional<Entite> secondaire() {
        return Optional.ofNullable(secondaire);
    }
}
