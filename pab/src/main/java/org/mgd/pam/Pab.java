package org.mgd.pam;

import org.apache.fontbox.util.autodetect.FontFileFinder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.mgd.pam.exception.PabException;
import org.mgd.pam.pagination.Paginateur;
import org.mgd.pam.producteur.exception.ProducteurException;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.NoSuchElementException;
import java.util.Properties;

public abstract class Pab {
    protected final Properties proprietes = new Properties();

    private final Path cheminFichier;
    private final Path cheminPolice;

    protected Pab(Path chemin) throws PabException {
        Path fichier = Files.isRegularFile(chemin) ? chemin : chemin.resolve("pab.properties");
        try (BufferedReader lecteur = Files.newBufferedReader(fichier, StandardCharsets.UTF_8)) {
            proprietes.load(lecteur);

            Path cheminDossier = Paths.get(proprietes.getProperty("pab.racine"));
            Files.createDirectories(cheminDossier);

            if (proprietes.containsKey("pab.fichier.pdf")) {
                cheminFichier = cheminDossier.resolve(proprietes.getProperty("pab.fichier.pdf") + ".pdf");
            } else {
                throw new PabException(MessageFormat.format("La propriété pab.fichier.pdf doit être définie dans le fichier {0}.", fichier));
            }

            if (proprietes.containsKey("pab.fichier.police")) {
                Path nomFichierPolice = Path.of(proprietes.getProperty("pab.fichier.police") + ".ttf");

                cheminPolice = Path.of(new FontFileFinder()
                        .find()
                        .stream()
                        .filter(uri -> Path.of(uri).getFileName().equals(nomFichierPolice))
                        .findFirst()
                        .orElseThrow(() -> new NoSuchElementException(MessageFormat.format("Impossible de trouver la police {0}.", nomFichierPolice))));
            } else {
                throw new PabException(MessageFormat.format("La propriété pab.fichier.police doit être définie dans le fichier {0}.", fichier));
            }
        } catch (IOException e) {
            throw new PabException(e);
        }
    }

    protected <O, E> Path ecrire(Paginateur<O, E> paginateur, O objet) throws PabException {
        try (FileOutputStream fichier = new FileOutputStream(cheminFichier.toString());
             PDDocument document = new PDDocument()) {
            paginateur.paginer(document, chargerPolice(document), objet);
            document.save(fichier);
            return cheminFichier;
        } catch (IOException | ProducteurException e) {
            throw new PabException(e);
        }
    }

    private PDFont chargerPolice(PDDocument document) throws IOException {
        return PDType0Font.load(document, cheminPolice.toFile());
    }
}
