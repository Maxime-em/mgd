package org.mgd.jab.source;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mgd.jab.JabSingletons;
import org.mgd.jab.objet.Adresse;
import org.mgd.jab.objet.Voie;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.UUID;
import java.util.stream.Stream;

class AdTest {
    private static Path ressourcesCommun;
    private static Path ressourcesSupprimable;

    @BeforeEach
    void setUp() throws IOException {
        JabSingletons.reinitialiser();
        JabSingletons.sauvegarde().setAsynchrone(false);
        JabSingletons.sauvegarde().setDelai(0L);

        ressourcesCommun = Paths.get("src/test/resources/base/commun");
        ressourcesSupprimable = Paths.get("src/test/resources/base/supprimable");

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

    @Test
    void creationSurFichierInexistant() throws IOException, JaoExecutionException, JaoParseException {
        VoieAd voieAd = new VoieAd(ressourcesSupprimable);
        Assertions.assertThrows(NullPointerException.class, () -> voieAd.access((String) null));

        VoieAf voieAf = voieAd.access("voie");
        Path fichier = ressourcesSupprimable.resolve("voie.json");
        Assertions.assertTrue(Files.isRegularFile(fichier));

        Voie voie = voieAf.jo();
        Assertions.assertNotNull(voie.getIdentifiant());

        JsonElement elementActuel = JsonParser.parseReader(Files.newBufferedReader(fichier));
        Assertions.assertTrue(elementActuel.isJsonObject());

        JsonObject voieActuel = elementActuel.getAsJsonObject();
        Assertions.assertAll(
                () -> Assertions.assertEquals(1, voieActuel.keySet().size()),
                () -> Assertions.assertTrue(voieActuel.has("identifiant")),
                () -> Assertions.assertTrue(voieActuel.getAsJsonPrimitive("identifiant").isString()),
                () -> Assertions.assertEquals(voie.getIdentifiant(), UUID.fromString(voieActuel.getAsJsonPrimitive("identifiant").getAsString()))
        );

        AdresseAd adresseAd = new AdresseAd(ressourcesSupprimable);

        AdresseAf adresseAf = adresseAd.access("adresse_prorietaires_null");
        Assertions.assertThrows(JaoParseException.class, adresseAf::jo);

        adresseAf = adresseAd.access("adresse.json", "{\"proprietaires\":[],\"voie\":{}}");
        Adresse adresse = adresseAf.jo();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(adresse.getProprietaires()),
                () -> Assertions.assertEquals(0, adresse.getProprietaires().size())
        );
    }

    @Test
    void creationSurFichierExistant() throws IOException, JaoParseException, JaoExecutionException {
        AdresseAd adresseAd = new AdresseAd(ressourcesCommun);
        Adresse adresse = adresseAd.access("adresse").jo();

        Path fichier = ressourcesCommun.resolve("adresse.json");
        Assertions.assertTrue(Files.isRegularFile(fichier));

        JsonElement elementActuel = JsonParser.parseReader(Files.newBufferedReader(fichier));
        Assertions.assertTrue(elementActuel.isJsonObject());

        JsonObject adresseActuel = elementActuel.getAsJsonObject();
        Assertions.assertAll(
                () -> Assertions.assertEquals(3, adresseActuel.keySet().size()),
                () -> Assertions.assertTrue(adresseActuel.has("identifiant")),
                () -> Assertions.assertTrue(adresseActuel.getAsJsonPrimitive("identifiant").isString()),
                () -> Assertions.assertTrue(adresseActuel.has("proprietaires")),
                () -> Assertions.assertTrue(adresseActuel.getAsJsonArray("proprietaires").isJsonArray()),
                () -> Assertions.assertTrue(adresseActuel.has("voie")),
                () -> Assertions.assertTrue(adresseActuel.getAsJsonObject("voie").isJsonObject())
        );

        JsonObject voieActuel = adresseActuel.getAsJsonObject("voie");
        Assertions.assertAll(
                () -> Assertions.assertEquals(3, voieActuel.keySet().size()),
                () -> Assertions.assertTrue(voieActuel.has("identifiant")),
                () -> Assertions.assertTrue(voieActuel.getAsJsonPrimitive("identifiant").isString()),
                () -> Assertions.assertTrue(voieActuel.has("numero")),
                () -> Assertions.assertTrue(voieActuel.getAsJsonPrimitive("numero").isNumber()),
                () -> Assertions.assertEquals(voieActuel.getAsJsonPrimitive("numero").getAsInt(), adresse.getVoie().getNumero()),
                () -> Assertions.assertTrue(voieActuel.has("libelle")),
                () -> Assertions.assertTrue(voieActuel.getAsJsonPrimitive("libelle").isString()),
                () -> Assertions.assertEquals(voieActuel.getAsJsonPrimitive("libelle").getAsString(), adresse.getVoie().getLibelle())
        );
    }
}
