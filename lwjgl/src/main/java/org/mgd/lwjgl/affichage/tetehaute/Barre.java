package org.mgd.lwjgl.affichage.tetehaute;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Fenetre.EvenementSouris;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.tetehaute.Disposition.Alignement;
import org.mgd.lwjgl.affichage.tetehaute.Disposition.Dimensionnement;
import org.mgd.lwjgl.affichage.tetehaute.Disposition.Justification;
import org.mgd.lwjgl.affichage.tetehaute.Disposition.Orientation;
import org.mgd.lwjgl.affichage.tetehaute.composant.Entite;
import org.mgd.lwjgl.affichage.tetehaute.composant.Liste;
import org.mgd.lwjgl.commun.Animateur;
import org.mgd.lwjgl.commun.Identifiable;
import org.mgd.lwjgl.exception.LwjglException;

import java.util.*;

import static org.lwjgl.nanovg.NanoVG.*;

public class Barre<G> extends AffichageTeteHaute implements Animateur {
    private static final int MARGE_INFORMATIONS = 5;
    private static final int MARGE_TEXTES = 5;

    private final Disposition disposition;
    private final Position position;
    private final int abcisses;
    private final int ordonnee;
    private final int taille;
    private final List<Entite> persistantes;
    private final Map<G, Liste> groupes;
    private final Map<UUID, Liste> informations;
    private final List<Entite> entitesSurvolees;
    private final List<Entite> entitesLiees;
    private G groupe;

    public Barre(Fenetre parent, Disposition disposition, Position position, int abcisses, int ordonnee, int taille) throws LwjglException {
        super(parent, false, true);
        this.disposition = disposition;
        this.position = position;
        this.abcisses = abcisses;
        this.ordonnee = ordonnee;
        this.taille = taille;
        this.persistantes = new LinkedList<>();
        this.groupes = new HashMap<>();
        this.informations = new HashMap<>();
        this.entitesSurvolees = new LinkedList<>();
        this.entitesLiees = new LinkedList<>();
    }

    private Optional<Liste> liste(boolean force) {
        return Optional.ofNullable(groupes.get(groupe)).map(liste -> force || liste.visible() ? liste : null);
    }

    private Optional<Liste> information(UUID uuid, boolean force) {
        return Optional.ofNullable(informations.get(uuid)).map(liste -> force || liste.visible() ? liste : null);
    }

    private void placer() {
        liste(false).ifPresent(liste -> {
            liste.dimensionner(contexte);
            liste.placer(abcisses, ordonnee);
            liste.fluxEntitesAffichables().forEach(entite -> information(entite.uuid(), false).ifPresent(information -> {
                information.dimensionner(contexte);
                int epaisseur = switch (disposition.orientation()) {
                    case HORIZONTAL -> liste.hauteur();
                    case VERTICAL -> liste.largeur();
                };
                int abscisseInformation = switch (position) {
                    case HAUT, BAS -> entite.abscisse();
                    case DROITE -> abcisses + epaisseur + MARGE_INFORMATIONS;
                    case GAUCHE -> abcisses - information.largeur() - MARGE_INFORMATIONS;
                };
                int ordonneeInformation = switch (position) {
                    case HAUT -> ordonnee - information.hauteur() - MARGE_INFORMATIONS;
                    case BAS -> ordonnee + epaisseur + MARGE_INFORMATIONS;
                    case DROITE, GAUCHE -> entite.ordonnee();
                };

                information.placer(abscisseInformation, ordonneeInformation);
            }));
        });
    }

    @Override
    public Fenetre parent() {
        return parent;
    }

    @Override
    public void maj(long accumulateur, Vision vision, EvenementSouris evenementSouris, Fenetre.EvenementAmorcages evenementAmorcagesCourant) throws LwjglException {
        Animateur.super.maj(accumulateur, vision, evenementSouris, evenementAmorcagesCourant);
        entitesLiees.clear();
        liste(false).ifPresent(liste ->
                entitesLiees.addAll(liste
                        .fluxEntitesAffichables()
                        .filter(entite -> entite.liaisons().stream().anyMatch(liaison -> liaison.visible() && liaison.survoler(vision, evenementSouris)))
                        .toList()));
    }

    @Override
    public boolean survoler(Vision vision, EvenementSouris evenementSouris) {
        entitesSurvolees.clear();
        entitesLiees.clear();
        if (visible) {
            liste(false).ifPresent(liste -> {
                if (liste.suivante().survoler(vision, evenementSouris) && evenementSouris.selection()) {
                    liste.pagination().suivant();
                    placer();
                } else if (liste.precedente().survoler(vision, evenementSouris) && evenementSouris.selection()) {
                    liste.pagination().precedent();
                    placer();
                } else {
                    entitesSurvolees.addAll(liste
                            .fluxEntitesAffichables()
                            .filter(entite -> entite.survoler(vision, evenementSouris))
                            .toList());
                }
            });
        }
        return !entitesSurvolees.isEmpty();
    }

    @Override
    public void retirer(Vision vision, EvenementSouris evenementSouris) {
        entitesSurvolees.clear();
        entitesLiees.clear();
    }

    @Override
    public Collection<Identifiable> amorcer(boolean droite) {
        return entitesSurvolees.stream().map(Identifiable.class::cast).toList();
    }

    @Override
    protected void dessiner() {
        nvgTextAlign(contexte, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        liste(false).ifPresent(liste -> {
            liste.colorier(contexte, AUBURN);
            liste.fluxEntitesAffichables().forEach(entite -> entite.colorier(contexte, NOIR_A50));
            liste.dessiner(contexte);
            liste.fluxEntitesAffichables()
                    .filter(Entite::active)
                    .forEach(entite -> entite.colorier(contexte, INDIGO_A50));

            entitesSurvolees.forEach(entite -> {
                entite.colorier(contexte, ROUGE_COQUELICOT_A50);
                dessinerInfobulle(entite);
            });

            entitesLiees.forEach(this::dessinerInfobulle);
        });
    }

    private void dessinerInfobulle(Entite entite) {
        information(entite.uuid(), false).ifPresent(liste -> {
            liste.colorier(contexte, EMERAUDE);
            liste.dessiner(contexte);
        });
    }

    public void ajouter(Entite entite) {
        persistantes.add(entite);
    }

    public final void ajouter(G groupe, Entite... entites) {
        groupes.computeIfAbsent(groupe, _ -> {
            Liste liste = new Liste(parent.contexteNvg(), disposition, taille);
            liste.persistantes().addAll(persistantes);
            return liste;
        }).entites().addAll(List.of(entites));
    }

    public void informer(UUID uuid, Entite entite) {
        informations.computeIfAbsent(uuid, _ -> new Liste(parent.contexteNvg(), new Disposition(Orientation.VERTICAL, Justification.DEBUT, Alignement.DEBUT, Dimensionnement.VARIABLE, 0, MARGE_TEXTES, 0)))
                .entites()
                .add(entite);
    }

    public void afficher(G groupe) {
        afficher(groupe, false);
    }

    public void afficher(G groupe, boolean force) {
        if (force || !Objects.equals(this.groupe, groupe)) {
            liste(true).ifPresent(liste -> {
                liste.masquer();
                liste.entites().forEach(entite -> {
                    entite.masquer();
                    information(entite.uuid(), true).ifPresent(information -> {
                        information.masquer();
                        information.entites().forEach(Entite::masquer);
                    });
                });
                liste.persistantes().forEach(entite -> {
                    entite.masquer();
                    information(entite.uuid(), true).ifPresent(information -> {
                        information.masquer();
                        information.entites().forEach(Entite::masquer);
                    });
                });
            });
            this.groupe = groupe;
            liste(true).ifPresent(liste -> {
                liste.afficher();
                liste.entites().forEach(entite -> {
                    entite.afficher();
                    information(entite.uuid(), true).ifPresent(information -> {
                        information.afficher();
                        information.entites().forEach(Entite::afficher);
                    });
                });
                liste.persistantes().forEach(entite -> {
                    entite.afficher();
                    information(entite.uuid(), true).ifPresent(information -> {
                        information.afficher();
                        information.entites().forEach(Entite::afficher);
                    });
                });
            });
            placer();
        }
    }

    public void desactiverEntites() {
        groupes.values().stream().flatMap(liste -> liste.entites().stream()).forEach(Entite::desactiver);
    }
}
