package org.mgd.lwjgl.affichage.tetehaute;

import org.lwjgl.nanovg.NVGColor;
import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.Vision;
import org.mgd.lwjgl.affichage.Acteur;
import org.mgd.lwjgl.affichage.Primitif;
import org.mgd.lwjgl.affichage.tetehaute.nvg.NVGCouleur;
import org.mgd.lwjgl.affichage.tetehaute.nvg.NVGImage;
import org.mgd.lwjgl.affichage.tetehaute.nvg.NVGPolice;
import org.mgd.lwjgl.exception.LwjglException;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.lwjgl.nanovg.NanoVG.*;

public abstract class AffichageTeteHaute extends Primitif implements Acteur {
    public static final NVGCouleur BLANC;
    public static final NVGCouleur EMERAUDE;
    public static final NVGCouleur ROUGE_COQUELICOT_A50;
    public static final NVGCouleur INDIGO_A50;
    public static final NVGCouleur AUBURN;
    public static final NVGCouleur NOIR_A50;
    private static final Map<String, NVGPolice> POLICES;
    private static final Map<String, NVGImage> IMAGES;

    static {
        NVGColor nvg = NVGColor.create();
        nvg.r(1f);
        nvg.g(1f);
        nvg.b(1f);
        nvg.a(1f);
        BLANC = new NVGCouleur("Blanc", nvg);

        nvg = NVGColor.create();
        nvg.r(0f);
        nvg.g(102f / 255);
        nvg.b(0f);
        nvg.a(1f);
        EMERAUDE = new NVGCouleur("Émeraude", nvg);

        nvg = NVGColor.create();
        nvg.r(198f / 255);
        nvg.g(8f / 255);
        nvg.b(0f);
        nvg.a(0.5f);
        ROUGE_COQUELICOT_A50 = new NVGCouleur("Rouge coquelicot 50% transparent", nvg);

        nvg = NVGColor.create();
        nvg.r(121f / 255);
        nvg.g(28f / 255);
        nvg.b(248f / 255);
        nvg.a(0.5f);
        INDIGO_A50 = new NVGCouleur("Indigo 50% transparent", nvg);

        nvg = NVGColor.create();
        nvg.r(157f / 255);
        nvg.g(62f / 255);
        nvg.b(12f / 255);
        nvg.a(1f);
        AUBURN = new NVGCouleur("Auburn", nvg);

        nvg = NVGColor.create();
        nvg.r(0f);
        nvg.g(0f);
        nvg.b(0f);
        nvg.a(0.5f);
        NOIR_A50 = new NVGCouleur("Noir 50% transparent", nvg);

        POLICES = new HashMap<>();

        IMAGES = new HashMap<>();
    }

    protected final long contexte;

    protected AffichageTeteHaute(Fenetre parent, boolean estMenu, boolean apparaitreParDefaut) throws LwjglException {
        super(parent, apparaitreParDefaut);
        this.contexte = parent.contexteNvg();
        if (this.contexte == 0L) {
            throw new LwjglException("Il faut créer un contexte NVG avant d'instancier un affichage.");
        }

        if (estMenu) {
            parent.ajouterMenu(this);
        } else {
            parent.affichages().add(this);
        }
    }

    private static String contextualiser(long contexte, String identifiant) {
        return MessageFormat.format("{0}:{1}", contexte, identifiant);
    }

    public static void creerPolice(long contexte, String identifiant, Path fichier) {
        POLICES.computeIfAbsent(contextualiser(contexte, identifiant), _ -> new NVGPolice(identifiant, fichier, nvgCreateFont(contexte, identifiant, fichier.toString())));
    }

    public static void creerImage(long contexte, String identifiant, Path fichier) {
        IMAGES.computeIfAbsent(contextualiser(contexte, identifiant), _ -> {
            int nvg = nvgCreateImage(contexte, fichier.toString(), NVG_IMAGE_NEAREST);
            int[] largeurImage = new int[1];
            int[] hauteurImage = new int[1];
            nvgImageSize(contexte, nvg, largeurImage, hauteurImage);
            return new NVGImage(identifiant, fichier, largeurImage[0], hauteurImage[0], nvg);
        });
    }

    public static NVGImage obtenirImage(long contexte, String identifiant) {
        String cle = contextualiser(contexte, identifiant);
        if (!IMAGES.containsKey(cle)) {
            throw new NoSuchElementException(MessageFormat.format("L''image \"{0}\" est introuvable.", identifiant));
        }
        return IMAGES.get(cle);
    }

    public static NVGPolice obtenirPolice(long contexte, String identifiant) {
        String cle = contextualiser(contexte, identifiant);
        if (!POLICES.containsKey(cle)) {
            throw new NoSuchElementException(MessageFormat.format("La police \"{0}\" est introuvable.", identifiant));
        }
        return POLICES.get(cle);
    }

    protected abstract void dessiner(long ellipse);

    @Override
    public boolean visible() {
        return visible;
    }

    @Override
    public void jouer(long ellipse, Vision vision) {
        nvgBeginFrame(contexte, parent.largeur(), parent.hauteur(), 1f);
        dessiner(ellipse);
        nvgEndFrame(contexte);
    }

    public void liberer() {
        // Rien à faire
    }
}
