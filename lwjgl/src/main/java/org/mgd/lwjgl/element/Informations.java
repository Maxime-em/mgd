package org.mgd.lwjgl.element;

import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.exception.LwjglException;
import org.mgd.lwjgl.forme.Case;
import org.mgd.lwjgl.forme.Colorisation;
import org.mgd.lwjgl.interne.Tisseur;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;

// TODO ElementMarger enfant de Element
public class Informations extends Element {
    private final Font police;

    public Informations(Fenetre parent, String identifiant, String texte, float[] marges, Font police) throws LwjglException {
        super(parent, identifiant, new float[]{0f, 0f, 0f}, new float[]{1f, 1f, 1f}, new float[]{0f, 0f, 0f}, Collections.emptyMap());
        this.police = police;
        Tisseur.obtenir(identifiant, police, Tisseur.PSEUDO_BASE).orElseThrow(IllegalArgumentException::new);
        marger(marges);
        modifierTexte(texte);
    }

    public Informations(Fenetre parent, String identifiant, float[] marges, Font police) throws LwjglException {
        this(parent, identifiant, "", marges, police);
    }

    public void modifierTexte(String texte) {
        formes = new LinkedList<>();

        Tisseur.Scribe scribe = new Tisseur.Scribe(police, texte);
        // TODO Interface (haut, droite, bas, gauche, glyphe) -> ...
        float decalagex = -scribe.largeur() * parent.ratioDenominateur() / 2;
        float decalagey = 1f - margeHaut() - scribe.hauteur() * parent.ratioNumerateur();
        scribe.glyphes().forEach((valeurs, glyphe) ->
                formes.add(new Case(this,
                        valeurs[0] * parent.ratioNumerateur() + decalagey,
                        valeurs[1] * parent.ratioDenominateur() + decalagex,
                        valeurs[2] * parent.ratioNumerateur() + decalagey,
                        valeurs[3] * parent.ratioDenominateur() + decalagex,
                        0,
                        new float[]{
                                glyphe.positionHomogene() + glyphe.largeurHomogene(), 0f, 0f, 0f,
                                glyphe.positionHomogene(), 0f, 0f, 0f,
                                glyphe.positionHomogene(), 1f, 0f, 0f,
                                glyphe.positionHomogene() + glyphe.largeurHomogene(), 1f, 0f, 0f
                        },
                        Colorisation.TEXTURE)));
        formes.addFirst(new Case(this,
                1f - margeHaut(),
                1f - margeDroite(),
                1f - margeHaut() - scribe.hauteur() * parent.ratioNumerateur(),
                margeGauche() - 1f,
                1, new float[]{
                0f, 0.4f, 0.4f, 1f,
                0f, 0.4f, 0.4f, 1f,
                0f, 0.4f, 0.4f, 1f,
                0f, 0.4f, 0.4f, 1f
        }, Colorisation.COULEUR));
    }

    public void effacer() {
        formes = Collections.emptyList();
    }
}
