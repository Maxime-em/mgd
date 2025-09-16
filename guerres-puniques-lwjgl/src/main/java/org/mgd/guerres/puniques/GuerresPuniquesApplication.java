package org.mgd.guerres.puniques;

import org.mgd.lwjgl.Application;
import org.mgd.lwjgl.Fenetre;
import org.mgd.lwjgl.ITisseur;
import org.mgd.lwjgl.element.Actions;
import org.mgd.lwjgl.element.Cadrillage;
import org.mgd.lwjgl.element.Informations;
import org.mgd.lwjgl.exception.LwjglException;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static java.awt.Font.BOLD;
import static java.awt.Font.MONOSPACED;

public class GuerresPuniquesApplication extends Application {
    protected GuerresPuniquesApplication() throws LwjglException {
        super("Guerres puniques");
    }

    public static void main(String[] args) throws LwjglException, IOException, URISyntaxException {
        new GuerresPuniquesApplication().demarrer();
    }

    @Override
    protected void peupler(Fenetre fenetre) throws LwjglException {
        Font police = Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()).contains("Calibri")
                ? new Font("Calibri", BOLD, 16)
                : new Font(MONOSPACED, BOLD, 16);

        Informations informations = new Informations(fenetre, "informations", new float[]{0.05f, 0.03f, 0f, 0.03f}, police);

        new Actions(fenetre, "actions", 10, 0.16f, 0.09f, new float[]{0.05f, 0.3f, 0.05f, 0.3f}, Collections.emptyMap())
                .souscrire(index -> System.out.println(index));

        new Cadrillage(fenetre, "carte", 32, 18, Map.of(
                ITisseur.PSEUDO_BASE, "E:\\IdeaProjects\\mgd\\guerres-puniques-lwjgl\\src\\main\\resources\\carte.png",
                ITisseur.PSEUDO_SURVOLE, "E:\\IdeaProjects\\mgd\\guerres-puniques-lwjgl\\src\\main\\resources\\carte_survoler.png",
                ITisseur.PSEUDO_ACTIVER, "E:\\IdeaProjects\\mgd\\guerres-puniques-lwjgl\\src\\main\\resources\\carte_activer.png"
        ))
                .souscrire((ligne, colonne) -> informations.modifierTexte(MessageFormat.format("""
                        ligne {0}
                        colonne {1}
                        """, ligne, colonne)))
                .souscrire(informations::effacer);
    }
}