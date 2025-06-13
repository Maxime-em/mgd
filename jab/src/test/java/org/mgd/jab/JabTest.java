package org.mgd.jab;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mgd.jab.exception.JabException;
import org.mgd.jab.objet.Pegi;
import org.mgd.jab.objet.Voie;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.source.VoieAd;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.UUID;
import java.util.stream.Stream;

class JabTest {
    private static Path ressourcesJabs;
    private static Path ressourcesSupprimable;
    private final long delai = 2000L;
    private Jabt jab;
    private JabSauvegarde sauvegarde;

    @BeforeEach
    void setUp() throws IOException, JabException {
        JabSingletons.reinitialiser();

        ressourcesJabs = Path.of("src/test/resources/jabs");
        ressourcesSupprimable = Path.of("src/test/resources/base/supprimable");

        try (Stream<Path> arborescence = Files.walk(ressourcesSupprimable)) {
            arborescence.filter(Files::isRegularFile).forEach(fichier -> {
                try {
                    Files.deleteIfExists(fichier);
                } catch (IOException e) {
                    System.err.println(MessageFormat.format("Impossible de supprimer le fichier \"{0}\" : {1}", fichier, e));
                }
            });
        }

        jab = new Jabt(ressourcesJabs.resolve("jab_complet.properties"));

        sauvegarde = JabSingletons.sauvegarde();
    }

    @ParameterizedTest
    @ArgumentsSource(JabTest.propertiesExceptionArgumentsProvider.class)
    void chargerException(String chemin, String message) {
        Assertions.assertThrows(JabException.class, () -> new Jabt(chemin != null ? ressourcesJabs.resolve(chemin) : null), message);
    }

    @Test
    void chargerFichierMinimal() throws JabException {
        Jabt jabMinimal = new Jabt(ressourcesJabs.resolve("jab_minimal.properties"));
        Assertions.assertAll(
                () -> Assertions.assertNotNull(jabMinimal.proprietes),
                () -> Assertions.assertTrue(jabMinimal.proprietes.containsKey("jab.base")),
                () -> Assertions.assertEquals(Path.of(jabMinimal.proprietes.getProperty("jab.base")), jab.base),
                () -> Assertions.assertNotNull(jabMinimal.ads),
                () -> Assertions.assertTrue(jabMinimal.ads.isEmpty()),
                () -> Assertions.assertNotNull(jabMinimal.jaos),
                () -> Assertions.assertTrue(jabMinimal.jaos.isEmpty()),
                () -> Assertions.assertNotNull(sauvegarde),
                () -> Assertions.assertSame(sauvegarde, JabSingletons.sauvegarde())
        );
    }

    @Test
    void chargerFichierComplet() {
        Assertions.assertAll(
                () -> Assertions.assertNotNull(jab.proprietes),
                () -> Assertions.assertTrue(jab.proprietes.containsKey("jab.base")),
                () -> Assertions.assertTrue(jab.proprietes.containsKey("jab.sauvegarde.nombre.threads")),
                () -> Assertions.assertTrue(jab.proprietes.containsKey("jab.sauvegarde.asynchrone")),
                () -> Assertions.assertTrue(jab.proprietes.containsKey("jab.sauvegarde.delai")),
                () -> Assertions.assertTrue(jab.proprietes.containsKey("jab.sources")),
                () -> Assertions.assertTrue(jab.proprietes.containsKey("jab.persistences")),
                () -> Assertions.assertEquals("E:\\IdeaProjects\\mgd\\jab\\src\\test\\resources\\base", jab.proprietes.getProperty("jab.base")),
                () -> Assertions.assertEquals("10", jab.proprietes.getProperty("jab.sauvegarde.nombre.threads")),
                () -> Assertions.assertEquals("false", jab.proprietes.getProperty("jab.sauvegarde.asynchrone")),
                () -> Assertions.assertEquals("5000", jab.proprietes.getProperty("jab.sauvegarde.delai")),
                () -> Assertions.assertNotNull(jab.ads),
                () -> Assertions.assertEquals(2, jab.ads.size()),
                () -> Assertions.assertTrue(jab.ads.containsKey("adresse")),
                () -> Assertions.assertTrue(jab.ads.containsKey("voie")),
                () -> Assertions.assertNotNull(jab.ads.get("adresse")),
                () -> Assertions.assertNotNull(jab.ads.get("voie")),
                () -> Assertions.assertNotNull(jab.jaos),
                () -> Assertions.assertEquals(2, jab.jaos.size()),
                () -> Assertions.assertDoesNotThrow(jab::disposer)
        );
    }

    @Test
    void sauvegarder() throws JaoParseException, JaoExecutionException, IOException {
        sauvegarde.setAsynchrone(true);

        Pegi pegi = new Pegi();
        pegi.setIdentifiant(UUID.randomUUID());
        pegi.setAge(18);

        VoieAd voieAd = new VoieAd(ressourcesSupprimable);
        Voie voie = voieAd.access("voie.json").jo();

        Assertions.assertAll(
                () -> Assertions.assertDoesNotThrow(() -> sauvegarde.demarrer(pegi)),
                () -> {
                    long debut = System.currentTimeMillis();
                    sauvegarde.demarrer(voie);
                    sauvegarde.attendre();
                    long temps = System.currentTimeMillis() - debut;
                    Assertions.assertTrue(temps >= delai);
                },
                () -> {
                    long debut = System.currentTimeMillis();
                    sauvegarde.demarrer(voie);
                    sauvegarde.reinitialiser();
                    long temps = System.currentTimeMillis() - debut;
                    Assertions.assertTrue(temps >= delai);
                }
        );

        Files.deleteIfExists(ressourcesSupprimable.resolve("voie.json"));
        sauvegarde.setAsynchrone(false);
        sauvegarde.demarrer(voie);
        Assertions.assertDoesNotThrow(() -> sauvegarde.demarrer(voie));
    }

    private static class propertiesExceptionArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(null, "Le chemin vers le fichier de configuration du Jab ne doit pas être nul."),
                    Arguments.of("inexistant.properties", "Impossible de lire le fichier de configuration du Jab avec le chemin src\\test\\resources\\jabs\\inexistant.properties."),
                    Arguments.of("jab_vide.properties", "Le fichier de configuration du Jab doit contenir le chemin vers la base via la propriété jab.base."),
                    Arguments.of("jab_source_inconnu.properties", "java.lang.ClassNotFoundException: org.mgd.jab.source.InconnuAd"),
                    Arguments.of("jab_source_nom_vide.properties", "Les noms des sources ne doivent pas être vide."),
                    Arguments.of("jab_jao_inconnu.properties", "java.lang.ClassNotFoundException: org.mgd.jab.persistence.InconnuJao")
            );
        }
    }
}
