package org.mgd.lwjgl.affichage.tetehaute;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.Primitif;
import org.mgd.lwjgl.exception.LwjglException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.lwjgl.nanovg.NanoVG.*;

public class Informations extends AffichageTeteHaute {
    private final Map<Primitif, Ecrit> ecrits;
    private final float taille;
    private final NVGPolice police;
    private final NVGCouleur couleur;
    private final float[] marges;
    private final float[] margesTexte;

    public Informations(Fenetre parent, float taille, NVGPolice police, NVGCouleur couleur, float[] margesTexte) throws LwjglException {
        super(parent, false);
        this.taille = taille;
        this.police = police;
        this.couleur = couleur;
        this.marges = new float[]{0.01f * parent.hauteur(), 0.1f * parent.largeur(), 0f, 0.1f * parent.largeur()};
        this.margesTexte = new float[]{0f, 0f, 0f, 0f};
        this.ecrits = new HashMap<>();

        System.arraycopy(margesTexte, 0, this.margesTexte, 0, margesTexte.length);
    }

    public void afficher(Primitif primitif, String texte) {
        ecrits.put(primitif, new Ecrit(taille, police, couleur, texte, margesTexte));
    }

    public void effacer(Primitif primitif) {
        ecrits.remove(primitif);
    }

    public void effacer() {
        ecrits.clear();
    }

    @Override
    protected void dessiner() {
        if (!ecrits.isEmpty()) {
            nvgTextAlign(contexte, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
            AtomicReference<Float> largeur = new AtomicReference<>(0f);
            AtomicReference<Float> hauteur = new AtomicReference<>(margesTexte[0]);
            ecrits.values().forEach(ecrit -> {
                nvgFontSize(contexte, ecrit.taille);
                nvgFontFace(contexte, ecrit.police.identifiant());
                nvgTextBounds(contexte, 0f, 0f, ecrit.texte, ecrit.dimensions);
                largeur.accumulateAndGet(ecrit.dimensions[2], Math::max);
                ecrit.position[0] = marges[3] + (parent.largeur() - marges[1] - marges[3] - ecrit.dimensions[2]) / 2f;
                ecrit.position[1] = marges[0] + hauteur.getAndAccumulate(ecrit.dimensions[3], Float::sum);
            });

            nvgBeginPath(contexte);
            nvgRect(contexte, marges[3], marges[0], parent.largeur() - marges[1] - marges[3], hauteur.get() + margesTexte[2]);
            nvgFillColor(contexte, EMERAUDE.nvg());
            nvgFill(contexte);
            nvgClosePath(contexte);

            ecrits.values().forEach(ecrit -> {
                nvgFillColor(contexte, ecrit.couleur.nvg());
                nvgText(contexte, ecrit.position[0], ecrit.position[1], ecrit.texte);
            });
        }
    }

    @Override
    public void maj(Vision vision, Fenetre.EvenementSouris evenementSouris) {
        // Rien à faire
    }
}
