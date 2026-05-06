package org.mgd.lwjgl.affichage.tetehaute.nvg;

import java.nio.file.Path;

public record NVGImage(String identifiant, Path fichier, int largeur, int hauteur, int nvg) {
}
