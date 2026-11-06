package org.mgd.jab;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mgd.jab.exception.JabException;
import org.mgd.jab.persistence.HerosJao;
import org.mgd.jab.source.LigueAd;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Tests de la configuration du Jab de la Ligue de Justice.
 */
public class JabTest extends AbstractJabTest {

    @Test
    void configurationValideChargeJaosEtSources() throws Exception {
        JabJustice jab = new JabJustice(configuration());

        Assertions.assertAll(
                () -> Assertions.assertEquals(1, jab.jaos().size()),
                () -> Assertions.assertInstanceOf(HerosJao.class, jab.jaos().get("heros")),
                () -> Assertions.assertEquals(1, jab.ads().size()),
                () -> Assertions.assertInstanceOf(LigueAd.class, jab.ads().get("ligue")),
                () -> Assertions.assertTrue(Files.isDirectory(jab.base().resolve("ligue")))
        );
        jab.disposer();
    }

    @Test
    void dossierDeConfigurationEstResolu() throws Exception {
        Path fichier = configuration();

        JabJustice jab = new JabJustice(fichier.getParent());

        Assertions.assertNotNull(jab.proprietes().getProperty("jab.base"));
        jab.disposer();
    }

    @Test
    void cheminNulEstRefuse() {
        JabException exception = Assertions.assertThrows(JabException.class, () -> new JabJustice(null));

        Assertions.assertEquals("Le chemin vers le fichier de configuration du Jab ne doit pas être nul.",
                exception.getMessage());
    }

    @Test
    void fichierAbsentEstRefuse() {
        Path inconnu = dossier.resolve("Quartier-general-inconnu");

        Assertions.assertThrows(JabException.class, () -> new JabJustice(inconnu));
    }

    @Test
    void baseAbsenteEstRefusee() throws IOException {
        Path conf = Files.createDirectories(dossier.resolve("conf"));
        Path fichier = Files.writeString(conf.resolve("jab.properties"), "jab.sauvegarde.asynchrone=false\n");

        Assertions.assertThrows(JabException.class, () -> new JabJustice(fichier));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "jab.persistences=heros:org.gotham.ClasseInconnue",
            "jab.sources=ligue:org.gotham.ClasseInconnue"
    })
    void classeInconnueEstRefusee(String ligne) throws IOException {
        Path conf = Files.createDirectories(dossier.resolve("conf"));
        String base = dossier.resolve("base").toString().replace('\\', '/');
        Path fichier = Files.writeString(conf.resolve("jab.properties"),
                "jab.base=" + base + "/\n" + ligne + "\n");

        Assertions.assertThrows(JabException.class, () -> new JabJustice(fichier));
    }

    @Test
    void proprietesDeSauvegardeSontAppliquees() throws Exception {
        JabJustice jab = new JabJustice(configuration(
                "jab.sauvegarde.nombre.threads=7", "jab.sauvegarde.delai=1234"));

        Assertions.assertAll(
                () -> Assertions.assertEquals(7, JabSingletons.sauvegarde().getNombreThreads()),
                () -> Assertions.assertEquals(1234, JabSingletons.sauvegarde().getDelai()),
                () -> Assertions.assertFalse(JabSingletons.sauvegarde().isAsynchrone())
        );
        jab.disposer();
    }
}
