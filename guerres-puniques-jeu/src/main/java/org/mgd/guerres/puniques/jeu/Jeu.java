package org.mgd.guerres.puniques.jeu;

import org.mgd.connexion.exception.ConnexionException;
import org.mgd.guerres.puniques.coeur.Jabm;
import org.mgd.guerres.puniques.coeur.JabmConnexion;

import java.util.Properties;

public class Jeu {
    private final Jabm jabm;

    public Jeu(Properties proprietes) throws ConnexionException {
        this.jabm = new JabmConnexion(proprietes).ouvrir().getInstance();
    }
}
