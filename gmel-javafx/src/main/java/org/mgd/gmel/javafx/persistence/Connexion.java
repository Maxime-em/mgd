package org.mgd.gmel.javafx.persistence;

import org.mgd.gmel.coeur.Jabm;
import org.mgd.gmel.javafx.persistence.exception.ConnectionException;
import org.mgd.gmel.pdf.Pabm;
import org.mgd.jab.exception.JabException;
import org.mgd.pam.exception.PabException;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

public class Connexion {
    public static final String APPLICATION_JAB = "application.jab";
    public static final String APPLICATION_PAB = "application.pab";
    private static final String APPLICATION_CONFIGURATION = "application.configuration";
    private final Properties proprietes;
    private Jabm jabm;
    private Pabm pabm;

    public Connexion() throws ConnectionException {
        Path configuration = Paths.get(System.getProperty(APPLICATION_CONFIGURATION, "./"));
        Path fichier = Files.isRegularFile(configuration) ? configuration : configuration.resolve("configuration.properties");
        try (BufferedReader lecteur = Files.newBufferedReader(fichier, StandardCharsets.UTF_8)) {
            this.proprietes = new Properties();
            this.proprietes.load(lecteur);
        } catch (IOException e) {
            throw new ConnectionException(e);
        }
    }

    public void ouvrir() throws ConnectionException {
        if (!isOuverte()) {
            try {
                jabm = new Jabm(Paths.get(proprietes.getProperty(APPLICATION_JAB, "./")));
                pabm = new Pabm(Paths.get(proprietes.getProperty(APPLICATION_PAB, "./")));
            } catch (JabException | PabException e) {
                throw new ConnectionException(e);
            }
        }
    }

    public void fermer() {
        if (isOuverte()) {
            jabm.disposer();
        }
    }

    public boolean isOuverte() {
        return Objects.nonNull(jabm) && Objects.nonNull(pabm);
    }

    public Jabm getJabm() {
        return jabm;
    }

    public Pabm getPabm() {
        return pabm;
    }
}
