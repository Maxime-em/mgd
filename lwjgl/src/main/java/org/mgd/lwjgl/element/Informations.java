package org.mgd.lwjgl.element;

import org.mgd.lwjgl.AffichageTeteHaute;
import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.exception.LwjglException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.lwjgl.nanovg.NanoVG.*;

public class Informations extends AffichageTeteHaute {
    private final Map<Element, Ecrit> ecrits;
    private final float taille;
    private final NVGPolice police;
    private final NVGCouleur couleur;
    private final float[] marges;

    public Informations(Fenetre parent, float taille, NVGPolice police, NVGCouleur couleur, float[] marges) throws LwjglException {
        super(parent, false);
        this.taille = taille;
        this.police = police;
        this.couleur = couleur;
        this.marges = new float[]{0f, 0f, 0f, 0f};
        this.ecrits = new HashMap<>();

        System.arraycopy(marges, 0, this.marges, 0, marges.length);
    }

    public void afficher(Element element, String texte) {
        ecrits.put(element, new Ecrit(taille, police, couleur, texte, marges));
    }

    public void effacer(Element element) {
        ecrits.remove(element);
    }

    @Override
    protected void dessiner() {
        nvgTextAlign(contexte, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
        AtomicReference<Float> y = new AtomicReference<>(0f);
        ecrits.values().forEach(ecrit -> {
            dessiner(ecrit, 0f, y.get());
            y.accumulateAndGet(ecrit.dimensions[3], Float::sum);
        });
    }

    @Override
    public void maj(Vision vision, Fenetre.EvenementSouris evenementSouris) {
        // Rien à faire
    }
}
