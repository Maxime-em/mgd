package org.mgd.jab.persistence;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mgd.jab.AbstractJabTest;
import org.mgd.jab.dto.CelluleDto;
import org.mgd.jab.dto.HerosDto;
import org.mgd.jab.dto.ReferenceDto;
import org.mgd.jab.objet.Batmobile;
import org.mgd.jab.objet.Cellule;
import org.mgd.jab.objet.Heros;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;
import org.mgd.jab.utilitaire.exception.VerificationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests du socle d'accès aux données {@link Jao}.
 */
public class JaoTest extends AbstractJabTest {
    private static final String ID_SUPERMAN = "00000000-0000-0000-0000-000000000002";

    private HerosJao jao;

    @org.junit.jupiter.api.BeforeEach
    void creerJao() {
        jao = new HerosJao();
    }

    private Path ecrire(String nom, String json) throws IOException {
        return Files.writeString(dossier.resolve(nom + ".json"), json);
    }

    private HerosDto dtoHeros(String id, String nom) {
        HerosDto dto = new HerosDto();
        dto.setIdentifiant(id);
        dto.nom = nom;
        return dto;
    }

    private ReferenceDto<HerosDto, Heros, HerosJao> reference(String id, Path chemin, Class<HerosJao> fournisseur) {
        ReferenceDto<HerosDto, Heros, HerosJao> reference = new ReferenceDto<>();
        reference.setIdentifiant(id);
        reference.setChemin(chemin);
        reference.setClasseFournisseur(fournisseur);
        return reference;
    }

    @Test
    void tableEstCelleDesHeros() throws Exception {
        Heros heros = jao.nouveau();

        assertSame(heros, jao.table().selectionner(heros.getIdentifiant()));
    }

    @Test
    void postChargementExecuteLEnrichissementApresLeChargement() throws Exception {
        Heros batman = jao.nouveau();
        AtomicBoolean execute = new AtomicBoolean();
        Jao.postChargement(batman, _ -> execute.set(true));

        jao.charger(ecrire("superman", SUPERMAN_JSON));

        assertTrue(execute.get());
    }

    @Test
    void postChargementEnErreurLeveUneErreurDExecution() throws Exception {
        Heros batman = jao.nouveau();
        AtomicBoolean premiereFois = new AtomicBoolean(true);
        Jao.postChargement(batman, _ -> {
            if (premiereFois.getAndSet(false)) {
                throw new VerificationException("Le Batcomputer est en panne.");
            }
        });
        Path fichier = ecrire("superman", SUPERMAN_JSON);
        Path autre = ecrire("autre", "{\"nom\": \"Bruce Wayne\"}");

        try {
            assertThrows(JaoExecutionException.class, () -> jao.charger(fichier));
        } finally {
            assertDoesNotThrow(() -> jao.charger(autre));
        }
    }

    @Test
    void dechargerUnObjetNullRenvoieNull() {
        assertNull(jao.decharger((Heros) null));
    }

    @Test
    void dechargerUneCollection() {
        List<HerosDto> dtos = jao.decharger(List.of(batman(), superman()));

        assertAll(
                () -> assertEquals(2, dtos.size()),
                () -> assertEquals("Batman", dtos.getFirst().alias),
                () -> assertEquals(ID_SUPERMAN, dtos.get(1).getIdentifiant())
        );
    }

    @Test
    void dechargerUneMap() {
        Map<String, HerosDto> dtos = jao.decharger(Map.of("nuit", batman(), "jour", superman()));

        assertAll(
                () -> assertEquals(2, dtos.size()),
                () -> assertEquals("Batman", dtos.get("nuit").alias),
                () -> assertEquals("Superman", dtos.get("jour").alias)
        );
    }

    @Test
    void dechargerUneMapVersUnAutreJao() {
        Map<Heros, Heros> amis = Map.of(batman(), superman());

        Map<HerosDto, HerosDto> dtos = jao.decharger(new HerosJao(), amis);

        assertAll(
                () -> assertEquals(1, dtos.size()),
                () -> assertEquals("Superman", dtos.values().iterator().next().alias)
        );
    }

    @Test
    void dechargerUnTableauRegulierEtIrregulier() {
        CelluleJao celluleJao = new CelluleJao();
        Cellule a = new Cellule();
        a.setOccupant("Batman");
        a.setIdentifiant(UUID.randomUUID());
        Cellule b = new Cellule();
        b.setOccupant("Joker");
        b.setIdentifiant(UUID.randomUUID());

        CelluleDto[][] dtos = celluleJao.decharger(new Cellule[][]{{a, null}, {b}, {}});

        assertAll(
                () -> assertEquals(3, dtos.length),
                () -> assertEquals("Batman", dtos[0][0].occupant),
                () -> assertNull(dtos[0][1]),
                () -> assertEquals(1, dtos[1].length),
                () -> assertEquals("Joker", dtos[1][0].occupant),
                () -> assertEquals(0, dtos[2].length)
        );
    }

    @Test
    void dechargerUnTableauVide() {
        assertEquals(0, new CelluleJao().decharger(new Cellule[0][0]).length);
    }

    @Test
    void dupliquerUnObjetCreeUnNouvelIdentifiant() throws Exception {
        Heros copie = jao.dupliquer(batman());

        assertAll(
                () -> assertNotEquals(batman().getIdentifiant(), copie.getIdentifiant()),
                () -> assertEquals("Batman", copie.getAlias())
        );
    }

    @Test
    void dupliquerUneCollection() throws Exception {
        List<Heros> copies = jao.dupliquer(List.of(batman(), flash()));

        assertAll(
                () -> assertEquals(2, copies.size()),
                () -> assertEquals("Flash", copies.get(1).getAlias())
        );
    }

    @Test
    void dupliquerUneMap() throws Exception {
        Map<String, Heros> copies = jao.dupliquer(Map.of("rapide", flash()));

        assertEquals("Flash", copies.get("rapide").getAlias());
    }

    @Test
    void dupliquerUneMapVersUnAutreJao() throws Exception {
        Map<Heros, Heros> copies = jao.dupliquer(new HerosJao(), Map.of(batman(), superman()));

        assertAll(
                () -> assertEquals(1, copies.size()),
                () -> assertEquals("Batman", copies.keySet().iterator().next().getAlias()),
                () -> assertEquals("Superman", copies.values().iterator().next().getAlias())
        );
    }

    @Test
    void dupliquerUnTableauIrregulier() throws Exception {
        Cellule a = new Cellule();
        a.setOccupant("Robin");

        Cellule[][] copies = new CelluleJao().dupliquer(new Cellule[][]{{a}, {}});

        assertAll(
                () -> assertEquals(2, copies.length),
                () -> assertEquals("Robin", copies[0][0].getOccupant()),
                () -> assertEquals(0, copies[1].length)
        );
    }

    @Test
    void nouveauAvecEnrichissement() throws Exception {
        Heros heros = jao.nouveau(h -> h.setAlias("Green Lantern"));

        assertEquals("Green Lantern", heros.getAlias());
    }

    @Test
    void nouveauAvecEnrichissementEtAutresObjets() throws Exception {
        Heros batman = batman();

        Heros heros = jao.nouveau((h, autres) -> h.setAlias(((Heros) autres[0]).getAlias()), batman);

        assertEquals("Batman", heros.getAlias());
    }

    @Test
    void nouveauAvecEnrichissementInvalideLeveUneErreurDAnalyse() {
        assertThrows(JaoParseException.class, () -> jao.nouveau(_ -> {
            throw new VerificationException("Aquaman est introuvable.");
        }));
    }

    @Test
    void nouveauSansConstructeurPublicLeveUneErreurDExecution() {
        BatmobileJao batmobileJao = new BatmobileJao();

        assertThrows(JaoExecutionException.class, batmobileJao::nouveau);
    }

    @Test
    void chargerUnDtoNullEchoue() {
        assertThrows(JaoParseException.class, () -> jao.charger((HerosDto) null, null));
    }

    @Test
    void chargerUnDtoSansIdentifiantGenereUnIdentifiant() throws Exception {
        Heros heros = jao.charger(dtoHeros(null, "Oliver Queen"), null);

        assertNotNull(heros.getIdentifiant());
    }

    @Test
    void chargerUnDtoDejaChargeRenvoieLeMemeObjet() throws Exception {
        HerosDto dto = dtoHeros(ID_SUPERMAN, "Clark Kent");

        assertSame(jao.charger(dto, null), jao.charger(dto, null));
    }

    @Test
    void chargerUnDtoAvecUnParent() throws Exception {
        Heros parent = jao.nouveau();

        Heros enfant = jao.charger(dtoHeros(ID_SUPERMAN, "Clark Kent"), parent);

        assertTrue(enfant.estEnfantDe(parent));
    }

    @Test
    void chargerUnDtoQuiLeveUneErreurDExecutionEnErreurDAnalyse() {
        HerosDto dto = dtoHeros(null, HerosRebelleJao.REBELLE);

        assertThrows(JaoParseException.class, () -> new HerosRebelleJao().charger(dto, null));
    }

    @Test
    void chargerUneCollection() throws Exception {
        List<Heros> chargees = jao.charger(List.of(dtoHeros(null, "Bruce Wayne"), dtoHeros(null, "Barry Allen")), null);

        assertEquals(2, chargees.size());
    }

    @Test
    void chargerUneCollectionAvecUnDtoNullEchoue() {
        Collection<HerosDto> dtos = new ArrayList<>();
        dtos.add(null);

        assertThrows(JaoParseException.class, () -> jao.charger(dtos, null));
    }

    @Test
    void chargerUneMap() throws Exception {
        Map<String, Heros> chargees = jao.charger(Map.of("nuit", dtoHeros(null, "Bruce Wayne")), null);

        assertEquals("Bruce Wayne", chargees.get("nuit").getNom());
    }

    @Test
    void chargerUneMapAvecUnDtoNullEchoue() {
        Map<String, HerosDto> dtos = new HashMap<>();
        dtos.put("vide", null);

        JaoParseException erreur = assertThrows(JaoParseException.class, () -> jao.charger(dtos, null));

        assertTrue(erreur.getMessage().contains("Impossible de charger le tableau associatif"));
    }

    @Test
    void chargerUneMapVersUnAutreJao() throws Exception {
        Map<HerosDto, HerosDto> dtos = Map.of(dtoHeros(null, "Bruce Wayne"), dtoHeros(null, "Clark Kent"));

        Map<Heros, Heros> chargees = jao.charger(new HerosJao(), dtos, null);

        assertAll(
                () -> assertEquals(1, chargees.size()),
                () -> assertEquals("Bruce Wayne", chargees.keySet().iterator().next().getNom()),
                () -> assertEquals("Clark Kent", chargees.values().iterator().next().getNom())
        );
    }

    @Test
    void chargerUneMapVersUnAutreJaoAvecUneCleNullEchoue() {
        Map<HerosDto, HerosDto> dtos = new HashMap<>();
        dtos.put(null, dtoHeros(null, "Clark Kent"));

        assertThrows(JaoParseException.class, () -> jao.charger(new HerosJao(), dtos, null));
    }

    @Test
    void chargerUneMapVersUnAutreJaoAvecUneValeurNullEchoue() {
        Map<HerosDto, HerosDto> dtos = new HashMap<>();
        dtos.put(dtoHeros(null, "Bruce Wayne"), null);

        assertThrows(JaoParseException.class, () -> jao.charger(new HerosJao(), dtos, null));
    }

    @Test
    void chargerUnTableauDeCellulesPositionneLigneEtColonne() throws Exception {
        CelluleDto a = new CelluleDto();
        a.occupant = "Batman";
        CelluleDto b = new CelluleDto();
        b.occupant = "Robin";
        CelluleDto c = new CelluleDto();
        c.occupant = "Joker";

        Cellule[][] cellules = new CelluleJao().charger(new CelluleDto[][]{{a, b}, {c}, {}}, null);

        assertAll(
                () -> assertEquals(3, cellules.length),
                () -> assertEquals("Robin", cellules[0][1].getOccupant()),
                () -> assertEquals(0, cellules[0][1].ligne()),
                () -> assertEquals(1, cellules[0][1].colonne()),
                () -> assertEquals(1, cellules[1][0].ligne()),
                () -> assertEquals(0, cellules[2].length)
        );
    }

    @Test
    void chargerUnTableauSansTabulable() throws Exception {
        Heros[][] chargees = jao.charger(new HerosDto[][]{{dtoHeros(null, "Bruce Wayne")}}, null);

        assertEquals("Bruce Wayne", chargees[0][0].getNom());
    }

    @Test
    void chargerUnTableauVide() throws Exception {
        assertEquals(0, new CelluleJao().charger(new CelluleDto[0][0], null).length);
    }

    @Test
    void dechargerVersReferenceNullRenvoieNull() {
        assertNull(jao.dechargerVersReference(null, Heros.class, HerosJao.class));
    }

    @Test
    void dechargerVersReferenceDUnObjetChargeDepuisUnFichier() throws Exception {
        Path fichier = ecrire("superman", SUPERMAN_JSON);
        Heros superman = jao.charger(fichier);

        ReferenceDto<HerosDto, Heros, HerosJao> reference = jao.dechargerVersReference(superman, Heros.class, HerosJao.class);

        assertAll(
                () -> assertEquals(ID_SUPERMAN, reference.getIdentifiant()),
                () -> assertEquals(fichier, reference.getChemin()),
                () -> assertEquals(HerosJao.class, reference.getClasseFournisseur())
        );
    }

    @Test
    void dechargerVersReferenceDUnObjetSansFichierEchoue() throws Exception {
        Heros heros = jao.nouveau();

        IllegalStateException erreur = assertThrows(IllegalStateException.class, () -> jao.dechargerVersReference(heros, Heros.class, HerosJao.class));

        assertEquals("L'objet " + heros.getIdentifiant() + " de type Heros n'a pas de racine de type Heros.", erreur.getMessage());
    }

    @Test
    void dechargerVersReferences() throws Exception {
        Heros superman = jao.charger(ecrire("superman", SUPERMAN_JSON));

        List<ReferenceDto<HerosDto, Heros, HerosJao>> references = jao.dechargerVersReferences(List.of(superman), Heros.class, HerosJao.class);

        assertEquals(1, references.size());
    }

    @Test
    void chargerParReferenceNulleEchoue() {
        assertThrows(JaoParseException.class, () -> jao.chargerParReference(null));
    }

    @Test
    void chargerParReferenceSansIdentifiantEchoue() {
        ReferenceDto<HerosDto, Heros, HerosJao> reference = reference(null, dossier, HerosJao.class);

        assertThrows(JaoParseException.class, () -> jao.chargerParReference(reference));
    }

    @Test
    void chargerParReferenceSansClasseFournisseurEchoue() {
        ReferenceDto<HerosDto, Heros, HerosJao> reference = reference(ID_SUPERMAN, dossier, null);

        assertThrows(JaoParseException.class, () -> jao.chargerParReference(reference));
    }

    @Test
    void chargerParReferenceSansCheminEchoue() {
        ReferenceDto<HerosDto, Heros, HerosJao> reference = reference(ID_SUPERMAN, null, HerosJao.class);

        assertThrows(JaoParseException.class, () -> jao.chargerParReference(reference));
    }

    @Test
    void chargerParReferenceAvecUnFournisseurNonInstanciableEchoue() {
        ReferenceDto<HerosDto, Heros, HerosFermeJao> reference = new ReferenceDto<>();
        reference.setIdentifiant(ID_SUPERMAN);
        reference.setChemin(dossier);
        reference.setClasseFournisseur(HerosFermeJao.class);

        assertThrows(JaoExecutionException.class, () -> jao.chargerParReference(reference));
    }

    @Test
    void chargerParReferenceVersUnFichierSansLIdentifiantDemandeEchoue() throws Exception {
        Path fichier = ecrire("batman", "{\"identifiant\": \"00000000-0000-0000-0000-000000000001\", \"nom\": \"Bruce Wayne\"}");
        ReferenceDto<HerosDto, Heros, HerosJao> reference = reference(ID_SUPERMAN, fichier, HerosJao.class);

        assertThrows(JaoExecutionException.class, () -> jao.chargerParReference(reference));
    }

    @Test
    void chargerParReferenceChargeLeFichier() throws Exception {
        Path fichier = ecrire("superman", SUPERMAN_JSON);

        Heros superman = jao.chargerParReference(reference(ID_SUPERMAN, fichier, HerosJao.class));

        assertEquals("Superman", superman.getAlias());
    }

    @Test
    void chargerDeuxFoisLeMemeFichierRenvoieLeMemeObjet() throws Exception {
        Path fichier = ecrire("superman", SUPERMAN_JSON);

        Heros premier = jao.charger(fichier);

        assertSame(premier, jao.charger(fichier));
    }

    @Test
    void chargerUnFichierJsonInvalideEchoue() throws Exception {
        Path fichier = ecrire("invalide", "{ ceci n'est pas du json");

        assertThrows(JaoParseException.class, () -> jao.charger(fichier));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void chargerParReferenceDejaChargeeAvecLogsDeDebug(boolean sansChemin) throws Exception {
        Path fichier = ecrire("superman", SUPERMAN_JSON);
        jao.charger(fichier);
        ReferenceDto<HerosDto, Heros, HerosJao> reference = sansChemin
                ? reference(ID_SUPERMAN, null, HerosJao.class)
                : reference(ID_SUPERMAN, fichier, null);
        Level precedent = org.apache.logging.log4j.LogManager.getLogger(Jao.class).getLevel();
        Configurator.setLevel(Jao.class.getName(), Level.DEBUG);
        try {
            assertEquals("Superman", jao.chargerParReference(reference).getAlias());
        } finally {
            Configurator.setLevel(Jao.class.getName(), precedent);
        }
    }

    @Test
    void chargerParReferences() throws Exception {
        Path fichier = ecrire("superman", SUPERMAN_JSON);

        List<Heros> chargees = jao.chargerParReferences(List.of(reference(ID_SUPERMAN, fichier, HerosJao.class)));

        assertEquals(1, chargees.size());
    }

    @Test
    void batmobileNEstPasInstanciableParLeJao() {
        assertThrows(NoSuchMethodException.class, Batmobile.class::getConstructor);
    }
}
