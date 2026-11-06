package org.mgd.jab;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.mgd.jab.objet.Heros;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Base des tests du Jab : réinitialise les singletons et fournit des héros DC Comics.
 */
public abstract class AbstractJabTest {
    protected static final String SUPERMAN_JSON = """
            {
              "identifiant": "00000000-0000-0000-0000-000000000002",
              "nom": "Clark Kent",
              "alias": "Superman",
              "ville": "Metropolis",
              "premiereApparition": "1938-04-18",
              "pouvoirs": ["Vol", "Super-force", "Vision thermique"],
              "allies": [
                {"identifiant": "00000000-0000-0000-0000-000000000003", "nom": "Diana Prince", "alias": "Wonder Woman", "ville": "Themyscira"},
                {"identifiant": "00000000-0000-0000-0000-000000000004", "nom": "Barry Allen", "alias": "Flash", "ville": "Central City"}
              ]
            }
            """;

    @TempDir
    public Path dossier;

    protected static Heros heros(String id, String nom, String alias, String ville, LocalDate date, String... pouvoirs) {
        Heros heros = new Heros();
        heros.setIdentifiant(UUID.fromString(id));
        heros.setNom(nom);
        heros.setAlias(alias);
        heros.setVille(ville);
        heros.setPremiereApparition(date);
        heros.setPouvoirs(List.of(pouvoirs));
        return heros;
    }

    protected static Heros batman() {
        return heros("00000000-0000-0000-0000-000000000001", "Bruce Wayne", "Batman", "Gotham",
                LocalDate.of(1939, 5, 1), "Détective", "Arts martiaux");
    }

    protected static Heros superman() {
        return heros("00000000-0000-0000-0000-000000000002", "Clark Kent", "Superman", "Metropolis",
                LocalDate.of(1938, 4, 18), "Vol", "Super-force");
    }

    protected static Heros wonderWoman() {
        return heros("00000000-0000-0000-0000-000000000003", "Diana Prince", "Wonder Woman", "Themyscira",
                LocalDate.of(1941, 10, 21), "Lasso de vérité");
    }

    protected static Heros flash() {
        return heros("00000000-0000-0000-0000-000000000004", "Barry Allen", "Flash", "Central City",
                LocalDate.of(1956, 10, 1), "Super-vitesse");
    }

    @BeforeEach
    public void initialiserJab() {
        JabSingletons.sauvegarde().setNombreThreads(2);
        JabSingletons.sauvegarde().setAsynchrone(false);
        JabSingletons.reinitialiser();
    }

    @AfterEach
    public void nettoyerJab() {
        JabSingletons.reinitialiser();
    }

    protected Path configuration(String... lignesSupplementaires) throws IOException {
        Path conf = dossier.resolve("conf");
        Files.createDirectories(conf);
        StringBuilder contenu = new StringBuilder()
                .append("jab.base=").append(dossier.resolve("base").toString().replace('\\', '/')).append("/\n")
                .append("jab.sauvegarde.asynchrone=false\n")
                .append("jab.sauvegarde.nombre.threads=2\n")
                .append("jab.sources=ligue:org.mgd.jab.source.LigueAd\n")
                .append("jab.persistences=heros:org.mgd.jab.persistence.HerosJao\n");
        for (String ligne : lignesSupplementaires) {
            contenu.append(ligne).append('\n');
        }
        return Files.writeString(conf.resolve("jab.properties"), contenu.toString());
    }
}
