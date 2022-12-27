package org.mgd.gmel.coeur.source;

import org.mgd.gmel.coeur.dto.BibliothequeDto;
import org.mgd.gmel.coeur.objet.Bibliotheque;
import org.mgd.gmel.coeur.persistence.BibliothequeJao;
import org.mgd.jab.source.Af;

import java.nio.file.Path;

public class BibliothequeAf extends Af<BibliothequeDto, Bibliotheque> {
    protected BibliothequeAf(Path fichier) {
        super(new BibliothequeJao(), fichier);
    }
}
