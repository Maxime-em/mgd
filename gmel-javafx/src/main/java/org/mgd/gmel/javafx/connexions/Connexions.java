package org.mgd.gmel.javafx.connexions;

import org.mgd.connexion.Connexion;
import org.mgd.connexion.exception.ConnexionException;
import org.mgd.gmel.coeur.Jabm;
import org.mgd.gmel.javafx.connexions.exception.ConnexionsException;
import org.mgd.gmel.pdf.Pabm;
import org.mgd.jab.exception.JabException;
import org.mgd.pam.exception.PabException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Connexions {
    private static final String APPLICATION_CONFIGURATION = "application.configuration";
    private final Connexion<Jabm> connexionJab;
    private final Connexion<Pabm> connexionPab;

    public Connexions() throws ConnexionsException {
        Path configuration = Paths.get(System.getProperty(APPLICATION_CONFIGURATION, "./"));
        Path fichier = Files.isRegularFile(configuration) ? configuration : configuration.resolve("configuration.properties");
        try (BufferedReader lecteur = Files.newBufferedReader(fichier, StandardCharsets.UTF_8)) {
            Properties proprietes = new Properties();
            proprietes.load(lecteur);
            this.connexionJab = new JabmConnexion(proprietes);
            this.connexionPab = new PabmConnexion(proprietes);
        } catch (IOException e) {
            throw new ConnexionsException(e);
        }
    }

    public void ouvrir() throws ConnexionException {
        connexionJab.ouvrir();
        connexionPab.ouvrir();
    }

    public void fermer() {
        connexionJab.fermer();
        connexionPab.fermer();
    }

    public Jabm getJabm() {
        return connexionJab.getInstance();
    }

    public Pabm getPabm() {
        return connexionPab.getInstance();
    }

    private static class JabmConnexion extends Connexion<Jabm> {
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

    private static class PabmConnexion extends Connexion<Pabm> {
        public static final String APPLICATION_PAB = "application.pab";

        public PabmConnexion(Properties proprietes) {
            super(Paths.get(proprietes.getProperty(APPLICATION_PAB, "./")));
        }

        @Override
        protected Pabm construire(Path chemin) throws ConnexionException {
            try {
                return new Pabm(chemin);
            } catch (PabException e) {
                throw new ConnexionException(e);
            }
        }
    }
}
