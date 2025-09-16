package org.mgd.lwjgl.affichage.tetehaute;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.exception.LwjglException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import static org.lwjgl.nanovg.NanoVG.*;

public class Informations extends AffichageTeteHaute {
    private final Map<Object, Ecrit> ecrits;
    private final float taille;
    private final NVGPolice police;
    private final NVGCouleur couleur;
    private final int abcsisse;
    private final int ordonnee;
    private final int largeur;
    private final int hauteur;
    private final boolean force;

    public Informations(Fenetre parent,
                        float taille,
                        NVGPolice police,
                        NVGCouleur couleur,
                        int abcsisse,
                        int ordonnee,
                        int largeur,
                        int hauteur,
                        boolean force) throws LwjglException {
        super(parent, false);
        this.taille = taille;
        this.police = police;
        this.couleur = couleur;
        this.abcsisse = abcsisse;
        this.ordonnee = ordonnee;
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.force = force;
        this.ecrits = new HashMap<>();
    }

    public <T> void afficher(T objet, Supplier<String> texte) {
        ecrits.put(objet, new Ecrit(taille, police, couleur, texte));
    }

    public <T> void effacer(T objet) {
        ecrits.remove(objet);
    }

    @Override
    public void maj(Vision vision, Fenetre.EvenementSouris evenementSouris) {
        // Rien à faire
    }

    @Override
    protected void dessiner(long ellipse) {
        if (!ecrits.isEmpty() || force) {
            nvgTextAlign(contexte, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
            AtomicReference<Float> largeurCalcule = new AtomicReference<>(0f);
            AtomicReference<Float> hauteurCalcule = new AtomicReference<>(0f);
            ecrits.values().forEach(ecrit -> {
                nvgFontSize(contexte, ecrit.taille);
                nvgFontFace(contexte, ecrit.police.identifiant());
                nvgTextBounds(contexte, 0, 0, ecrit.texte.get(), ecrit.dimensions);
                largeurCalcule.accumulateAndGet(ecrit.dimensions[2], Math::max);
                ecrit.position[0] = abcsisse;
                ecrit.position[1] = ordonnee + hauteurCalcule.getAndAccumulate(ecrit.dimensions[3], Float::sum);
            });

            nvgBeginPath(contexte);
            nvgRect(contexte, abcsisse, ordonnee, this.largeur, Math.max(hauteurCalcule.get(), hauteur));
            nvgFillColor(contexte, EMERAUDE.nvg());
            nvgFill(contexte);
            nvgClosePath(contexte);

            ecrits.values().forEach(ecrit -> {
                nvgFillColor(contexte, ecrit.couleur.nvg());
                nvgText(contexte, ecrit.position[0], ecrit.position[1], ecrit.texte.get());
            });
        }
    }
}
