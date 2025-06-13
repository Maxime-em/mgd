package org.mgd.jab.source;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mgd.jab.JabSingletons;
import org.mgd.jab.objet.Adresse;
import org.mgd.jab.objet.Commune;
import org.mgd.jab.objet.Voie;
import org.mgd.jab.persistence.CommunauteJao;
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

        AdresseAf adresseAf = adresseAd.access("adresse_vide");
        Assertions.assertThrows(JaoParseException.class, adresseAf::jo);

        String referenceFrance = "\"pays\": {\"identifiant\": \"6377eaef-bd5c-4bb1-b4f4-2d93e8040a52\",\"chemin\": \"E:\\\\IdeaProjects\\\\mgd\\\\jab\\\\src\\\\test\\\\resources\\\\base\\\\commun\\\\monde1.json\",\"classeFournisseur\": \"org.mgd.jab.persistence.MondeJao\"}";

        adresseAf = adresseAd.access("adresse_proprietaires_null", "{\"voie\":{}," + referenceFrance + "}");
        Assertions.assertThrows(JaoParseException.class, adresseAf::jo);

        adresseAf = adresseAd.access("adresse_voie_null", "{\"proprietaires\":[]," + referenceFrance + "}");
        Assertions.assertThrows(JaoParseException.class, adresseAf::jo);

        adresseAf = adresseAd.access("adresse_pays_null", "{\"proprietaires\":[]," + referenceFrance + "}");
        Assertions.assertThrows(JaoParseException.class, adresseAf::jo);

        adresseAf = adresseAd.access("adresse", "{\"proprietaires\":[],\"voie\":{}," + referenceFrance + "}");
        Adresse adresse = adresseAf.jo();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(adresse.getProprietaires()),
                () -> Assertions.assertEquals(0, adresse.getProprietaires().size()),
                () -> Assertions.assertNotNull(adresse.getVoie()),
                () -> Assertions.assertNull(adresse.getCommune()),
                () -> Assertions.assertNotNull(adresse.getPays())
        );
    }

    @Test
    void creationSurFichierExistant() throws IOException, JaoParseException, JaoExecutionException {
        Path fichierAdresse = ressourcesCommun.resolve("adresse1.json");
        Path fichierCommunaute = ressourcesCommun.resolve("communaute1.json");
        Assertions.assertAll(
                () -> Assertions.assertTrue(Files.isRegularFile(fichierAdresse)),
                () -> Assertions.assertTrue(Files.isRegularFile(fichierCommunaute))
        );

        AdresseAd adresseAd = new AdresseAd(ressourcesCommun);
        Adresse adresse = adresseAd.access("adresse1").jo();
        Voie voie = adresse.getVoie();
        Commune commune = adresse.getCommune();

        JsonElement elementActuel = JsonParser.parseReader(Files.newBufferedReader(fichierAdresse));
        JsonObject adresseActuel = elementActuel.getAsJsonObject();
        JsonObject voieActuel = adresseActuel.getAsJsonObject("voie");
        JsonObject referenceCommuneActuel = adresseActuel.getAsJsonObject("commune");
        Assertions.assertAll(
                () -> Assertions.assertEquals(adresseActuel.getAsJsonPrimitive("identifiant").getAsString(), adresse.getIdentifiant().toString()),
                () -> Assertions.assertEquals(voieActuel.getAsJsonPrimitive("identifiant").getAsString(), voie.getIdentifiant().toString()),
                () -> Assertions.assertEquals(voieActuel.getAsJsonPrimitive("numero").getAsInt(), voie.getNumero()),
                () -> Assertions.assertEquals(voieActuel.getAsJsonPrimitive("libelle").getAsString(), voie.getLibelle()),
                () -> Assertions.assertEquals(referenceCommuneActuel.getAsJsonPrimitive("identifiant").getAsString(), commune.getIdentifiant().toString()),
                () -> Assertions.assertEquals(referenceCommuneActuel.getAsJsonPrimitive("chemin").getAsString(), fichierCommunaute.toAbsolutePath().toString()),
                () -> Assertions.assertEquals(referenceCommuneActuel.getAsJsonPrimitive("classeFournisseur").getAsString(), CommunauteJao.class.getName())
        );

        JsonObject communauteActuel = JsonParser.parseReader(Files.newBufferedReader(fichierCommunaute)).getAsJsonObject();
        JsonArray communesActuel = communauteActuel.getAsJsonArray("communes");
        for (JsonElement element : communesActuel) {
            JsonObject communeActuel = element.getAsJsonObject();
            if (communeActuel.getAsJsonPrimitive("identifiant").equals(referenceCommuneActuel.getAsJsonPrimitive("identifiant"))) {
                Assertions.assertAll(
                        () -> Assertions.assertEquals(communeActuel.getAsJsonPrimitive("identifiant").getAsString(), commune.getIdentifiant().toString()),
                        () -> Assertions.assertEquals(communeActuel.getAsJsonPrimitive("nom").getAsString(), commune.getNom()),
                        () -> Assertions.assertEquals(communeActuel.getAsJsonPrimitive("code").getAsString(), commune.getCode())
                );
                break;
            }
        }
    }
}
