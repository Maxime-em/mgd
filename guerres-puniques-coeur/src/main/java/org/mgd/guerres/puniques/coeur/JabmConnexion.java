package org.mgd.guerres.puniques.coeur;

import org.mgd.connexion.Connexion;
import org.mgd.connexion.exception.ConnexionException;
import org.mgd.jab.exception.JabException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class JabmConnexion extends Connexion<Jabm> {
    public static final String APPLICATION_JAB = "application.jab";

    public JabmConnexion(Properties proprietes) {
        super(Paths.get(proprietes.getProperty(APPLICATION_JAB, "./")));
    }

    @Override
    protected Jabm construire(Path chemin) throws ConnexionException {
        try {
            return new Jabm(chemin);
        } catch (JabException e) {
            throw new ConnexionException(e);
        }
    }
}
