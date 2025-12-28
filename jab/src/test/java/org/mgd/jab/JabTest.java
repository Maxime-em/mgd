package org.mgd.jab;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.support.ParameterDeclarations;
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
    private static Path ressourcesBase;
    private static Path ressourcesJabs;
    private static Path ressourcesSupprimable;

    @BeforeEach
    void setUp() throws IOException {
        JabSingletons.reinitialiser();

        ressourcesBase = Path.of("src/test/resources/base");
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
    }

    @ParameterizedTest
    @ArgumentsSource(JabTest.propertiesExceptionArgumentsProvider.class)
    void chargerException(String chemin) {
        Assertions.assertThrows(JabException.class, () -> new Jabt(chemin != null ? ressourcesJabs.resolve(chemin) : null));
    }

    @Test
    void chargerFichierMinimal() throws JabException {
        Jabt jabMinimal = new Jabt(ressourcesJabs.resolve("jab_minimal.properties"));
        Assertions.assertAll(
                () -> Assertions.assertNotNull(jabMinimal.proprietes),
                () -> Assertions.assertTrue(jabMinimal.proprietes.containsKey("jab.base")),
                () -> Assertions.assertEquals(ressourcesBase.toAbsolutePath(), Path.of(jabMinimal.proprietes.getProperty("jab.base"))),
                () -> Assertions.assertEquals(100, JabSingletons.sauvegarde().getNombreThreads()),
                () -> Assertions.assertTrue(JabSingletons.sauvegarde().isAsynchrone()),
                () -> Assertions.assertEquals(60_000L, JabSingletons.sauvegarde().getDelai()),
                () -> Assertions.assertNotNull(jabMinimal.ads),
                () -> Assertions.assertTrue(jabMinimal.ads.isEmpty()),
                () -> Assertions.assertNotNull(jabMinimal.jaos),
                () -> Assertions.assertTrue(jabMinimal.jaos.isEmpty())
        );
    }

    @Test
    void chargerFichierComplet() throws JabException {
        Jabt jab = new Jabt(ressourcesJabs.resolve("jab_complet.properties"));
        Assertions.assertAll(
                () -> Assertions.assertNotNull(jab.proprietes),
                () -> Assertions.assertTrue(jab.proprietes.containsKey("jab.base")),
                () -> Assertions.assertTrue(jab.proprietes.containsKey("jab.sauvegarde.nombre.threads")),
                () -> Assertions.assertTrue(jab.proprietes.containsKey("jab.sauvegarde.asynchrone")),
                () -> Assertions.assertTrue(jab.proprietes.containsKey("jab.sauvegarde.delai")),
                () -> Assertions.assertTrue(jab.proprietes.containsKey("jab.sources")),
                () -> Assertions.assertTrue(jab.proprietes.containsKey("jab.persistences")),
                () -> Assertions.assertEquals(ressourcesBase.toAbsolutePath(), Path.of(jab.proprietes.getProperty("jab.base"))),
                () -> Assertions.assertEquals("10", jab.proprietes.getProperty("jab.sauvegarde.nombre.threads")),
                () -> Assertions.assertEquals("false", jab.proprietes.getProperty("jab.sauvegarde.asynchrone")),
                () -> Assertions.assertEquals("5000", jab.proprietes.getProperty("jab.sauvegarde.delai")),
                () -> Assertions.assertEquals(10, JabSingletons.sauvegarde().getNombreThreads()),
                () -> Assertions.assertFalse(JabSingletons.sauvegarde().isAsynchrone()),
                () -> Assertions.assertEquals(5_000L, JabSingletons.sauvegarde().getDelai()),
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
    void chargerFichierSourceInconnu() throws JabException {
        Jabt jab = new Jabt(ressourcesJabs.resolve("jab_source_inconnu.properties"));
        Assertions.assertAll(
                () -> Assertions.assertNotNull(jab.proprietes),
                () -> Assertions.assertTrue(jab.proprietes.containsKey("jab.base")),
                () -> Assertions.assertTrue(jab.proprietes.containsKey("jab.sources")),
                () -> Assertions.assertEquals(ressourcesBase.toAbsolutePath(), Path.of(jab.proprietes.getProperty("jab.base"))),
                () -> Assertions.assertNotNull(jab.ads),
                () -> Assertions.assertEquals(0, jab.ads.size()),
                () -> Assertions.assertNotNull(jab.jaos),
                () -> Assertions.assertEquals(0, jab.jaos.size()),
                () -> Assertions.assertDoesNotThrow(jab::disposer)
        );
    }

    @Test
    void sauvegarder() throws JaoParseException, JaoExecutionException, IOException {
        JabSauvegarde sauvegarde = JabSingletons.sauvegarde();
        sauvegarde.setAsynchrone(true);
        sauvegarde.setDelai(3_000L);

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
                    Assertions.assertTrue(temps >= sauvegarde.getDelai());
                },
                () -> {
                    long debut = System.currentTimeMillis();
                    sauvegarde.demarrer(voie);
                    sauvegarde.reinitialiser();
                    long temps = System.currentTimeMillis() - debut;
                    Assertions.assertTrue(temps >= sauvegarde.getDelai());
                }
        );

        Files.deleteIfExists(ressourcesSupprimable.resolve("voie.json"));
        sauvegarde.setAsynchrone(false);
        sauvegarde.demarrer(voie);
        Assertions.assertDoesNotThrow(() -> sauvegarde.demarrer(voie));
    }

    private static class propertiesExceptionArgumentsProvider implements ArgumentsProvider {
        @Override
        public @NonNull Stream<? extends Arguments> provideArguments(@NonNull ParameterDeclarations parameters, @NonNull ExtensionContext context) {
            return Stream.of(
                    Arguments.of(new Object[]{null}),
                    Arguments.of("inexistant.properties"),
                    Arguments.of("jab_vide.properties"),
                    Arguments.of("jab_source_nom_vide.properties"),
                    Arguments.of("jab_jao_inconnu.properties")
            );
        }
    }
}
