package org.mgd.jab.persistence;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mgd.jab.AbstractJabTest;
import org.mgd.jab.JabSingletons;
import org.mgd.jab.dto.HerosDto;
import org.mgd.jab.objet.Heros;
import org.mgd.jab.persistence.exception.JaoParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class HerosJaoTest extends AbstractJabTest {

    private Path ecrire(String nom, String json) throws IOException {
        return Files.writeString(dossier.resolve(nom + ".json"), json);
    }

    @Test
    void chargerSupermanAvecSesAllies() throws Exception {
        Heros superman = new HerosJao().charger(ecrire("superman", SUPERMAN_JSON));

        assertAll(
                () -> assertEquals("Clark Kent", superman.getNom()),
                () -> assertEquals("Superman", superman.getAlias()),
                () -> assertEquals(LocalDate.of(1938, 4, 18), superman.getPremiereApparition()),
                () -> assertEquals(List.of("Vol", "Super-force", "Vision thermique"), superman.getPouvoirs()),
                () -> assertEquals(2, superman.getAllies().size()),
                () -> assertEquals("Flash", superman.getAllies().stream().filter(h -> h.getAlias().equals("Flash")).findFirst().orElseThrow().getAlias()),
                () -> assertTrue(JabSingletons.table(Heros.class).selectionner().size() >= 3)
        );
    }

    @Test
    void chargerDeuxFoisLeMemeFichierRenvoieLeMemeObjet() throws Exception {
        Path fichier = ecrire("superman", SUPERMAN_JSON);
        HerosJao jao = new HerosJao();

        assertSame(jao.charger(fichier), jao.charger(fichier));
    }

    @ParameterizedTest
    @ValueSource(strings = {"{\"nom\": \"\"}", "{\"nom\": \"   \"}", "{\"alias\": \"Joker\"}"})
    void chargerUnNomAbsentOuVideEchoue(String json) throws IOException {
        Path fichier = ecrire("vilain", json);

        assertThrows(Exception.class, () -> new HerosJao().charger(fichier));
    }

    @ParameterizedTest
    @ValueSource(strings = {"pas du json", "{\"nom\": ", "[1, 2"})
    void chargerUnJsonCorrompuLeveUneErreurExplicite(String json) throws IOException {
        Path fichier = ecrire("corrompu", json);

        JaoParseException exception = assertThrows(JaoParseException.class, () -> new HerosJao().charger(fichier));
        assertTrue(exception.getMessage().contains("Impossible de charger le fichier"));
    }

    @Test
    void chargerUnFichierAbsentLeveUneErreur() {
        Path absent = dossier.resolve("inconnu.json");

        assertThrows(JaoParseException.class, () -> new HerosJao().charger(absent));
    }

    @Test
    void chargerUnDtoNulLeveUneErreur() {
        JaoParseException exception = assertThrows(JaoParseException.class, () -> new HerosJao().charger((HerosDto) null, null));
        assertEquals("Objet inconnu.", exception.getMessage());
    }

    @Test
    void nouveauCreeUnObjetReferenceEtModifiable() throws Exception {
        Heros heros = new HerosJao().nouveau(h -> h.setNom("Hal Jordan"));

        assertAll(
                () -> assertNotNull(heros.getIdentifiant()),
                () -> assertEquals("Hal Jordan", heros.getNom()),
                () -> assertTrue(JabSingletons.table(Heros.class).existe(heros.getIdentifiant()))
        );
    }

    @Test
    void dechargerUnObjetNulRenvoieNull() {
        assertNull(new HerosJao().decharger((Heros) null));
    }

    @Test
    void dechargerConserveLIdentifiantEtLesAllies() throws Exception {
        Heros superman = new HerosJao().charger(ecrire("superman", SUPERMAN_JSON));

        HerosDto dto = new HerosJao().decharger(superman);

        assertAll(
                () -> assertEquals("00000000-0000-0000-0000-000000000002", dto.getIdentifiant()),
                () -> assertEquals("Metropolis", dto.ville),
                () -> assertEquals(2, dto.allies.size()),
                () -> assertEquals("Wonder Woman", dto.allies.getFirst().alias)
        );
    }

    @Test
    void dechargerUneCollectionVideRenvoieUneListeVide() {
        assertTrue(new HerosJao().decharger(List.of()).isEmpty());
    }

    @Test
    void dupliquerCopieLesChampsAvecUnNouvelIdentifiant() throws Exception {
        Heros batman = batman();

        Heros copie = new HerosJao().dupliquer(batman);

        assertAll(
                () -> assertNotEquals(batman.getIdentifiant(), copie.getIdentifiant()),
                () -> assertEquals("Batman", copie.getAlias()),
                () -> assertEquals(batman.getPouvoirs(), copie.getPouvoirs()),
                () -> assertEquals(LocalDate.of(1939, 5, 1), copie.getPremiereApparition())
        );
    }

    @Test
    void selectionnerUnIdentifiantInconnuLeveUneErreur() {
        final var table = JabSingletons.table(Heros.class);
        final var identifiant = batman().getIdentifiant();
        assertThrows(NoSuchElementException.class, () -> table.selectionner(identifiant));
    }

    @Test
    void jsonSerialiseContientLesChampsAttendus() {
        HerosDto dto = new HerosJao().decharger(superman());

        JsonObject json = JsonParser.parseString(org.mgd.jab.JabSauvegarde.gsonSauvegarde.toJson(dto)).getAsJsonObject();

        assertAll(
                () -> assertEquals("Superman", json.get("alias").getAsString()),
                () -> assertEquals("1938-04-18", json.get("premiereApparition").getAsString()),
                () -> assertEquals(2, json.getAsJsonArray("pouvoirs").size())
        );
    }
}
