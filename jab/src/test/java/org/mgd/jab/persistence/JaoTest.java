package org.mgd.jab.persistence;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mgd.jab.JabSingletons;
import org.mgd.jab.dto.Dto;
import org.mgd.jab.dto.PersonneDto;
import org.mgd.jab.dto.VoieDto;
import org.mgd.jab.objet.*;
import org.mgd.jab.persistence.exception.JaoExecutionException;
import org.mgd.jab.persistence.exception.JaoParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO tester mise à jour des parents
// TODO tester plus précisément les collections et tableaux associatifs
// TODO tester LocalDateAdapter
// TODO tester nonInclus
class JaoTest {
    private static Path ressourcesObjets;
    private static Path ressourcesSupprimable;
    private static Path fichier1;
    private static Path fichier2;
    private static Path fichier3;
    private static Path fichier4;
    private static Path fichier5;
    private static Path fichier6;
    private static Path fichier7;
    private static Path fichier8;
    private static Path fichier9;
    private static Path fichier10;
    private static Path fichier11;
    private static Path fichier12;
    private static Personne personne1;
    private static Personne personne2;
    private static Jeu jeu1;
    private static Livre livre1;
    private static Livre livre2;
    private static Chapitre chapitre11;
    private static Chapitre chapitre21;
    private static Chapitre chapitre22;
    private static Jeu jeu;
    private static PersonneJao personneJao;
    private static JeuJao jeuJao;
    private static PegiJao pegiJao;
    private static Pegi pegi12;
    private static Pegi pegi18;
    private static Adresse adresse1;

    @BeforeEach
    void setUp() throws IOException, JaoExecutionException, JaoParseException {
        JabSingletons.reinitialiser();
        JabSingletons.sauvegarde().setAsynchrone(false);

        ressourcesObjets = Path.of("src/test/resources/base/commun");
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

        fichier1 = ressourcesObjets.resolve("personne1.json");
        fichier2 = ressourcesObjets.resolve("personne2.json");
        fichier3 = ressourcesObjets.resolve("jeu.json");
        fichier4 = ressourcesObjets.resolve("jeu1.json");
        fichier5 = ressourcesObjets.resolve("pegi12.json");
        fichier6 = ressourcesObjets.resolve("pegi18.json");
        fichier7 = ressourcesObjets.resolve("adresse.json");
        fichier8 = ressourcesObjets.resolve("livre1.json");
        fichier9 = ressourcesObjets.resolve("livre2.json");
        fichier10 = ressourcesObjets.resolve("chapitre11.json");
        fichier11 = ressourcesObjets.resolve("chapitre21.json");
        fichier12 = ressourcesObjets.resolve("chapitre22.json");

        personneJao = new PersonneJao();
        personne1 = personneJao.charger(fichier1);
        personne2 = personneJao.charger(fichier2);

        jeuJao = new JeuJao();
        jeu = jeuJao.charger(fichier3);
        jeu1 = jeuJao.charger(fichier4);

        pegiJao = new PegiJao();
        pegi12 = pegiJao.charger(fichier5);
        pegi18 = pegiJao.charger(fichier6);

        AdresseJao adresseJao = new AdresseJao();
        adresse1 = adresseJao.charger(fichier7);

        LivreJao livreJao = new LivreJao();
        livre1 = livreJao.charger(fichier8);
        livre2 = livreJao.charger(fichier9);

        ChapitreJao chapitreJao = new ChapitreJao();
        chapitre11 = chapitreJao.charger(fichier10);
        chapitre21 = chapitreJao.charger(fichier11);
        chapitre22 = chapitreJao.charger(fichier12);
    }

    @ParameterizedTest
    @ArgumentsSource(verifierChargementArgumentsProvider.class)
    <D extends Dto> void verifierChargement(Jo<D> objet, String identifiant, List<Jo<?>> parents, List<Jo<?>> enfants) {
        Assertions.assertAll(
                () -> Assertions.assertEquals(UUID.fromString(identifiant), objet.getIdentifiant()),
                () -> Assertions.assertTrue(parents.stream().allMatch(objet::estEnfantDe)),
                () -> Assertions.assertTrue(enfants.stream().allMatch(objet::estParentDe)),
                () -> Assertions.assertFalse(objet.estDetache())
        );
    }

    @ParameterizedTest
    @ArgumentsSource(chargerExceptionArgumentsProvider.class)
    <T extends Throwable> void chargerException(Class<T> classe, String chemin, String message, Supplier<Jao<Dto, Jo<Dto>>> jaoSupplier) {
        Assertions.assertThrows(classe, () -> jaoSupplier.get().charger(ressourcesObjets.resolve(chemin)), message);
    }

    @Test
    void charger() {
        Jeu jeu1Personne1 = personne1.getJeux().stream().filter(element -> element.getNom().equals("jeu1")).findFirst().orElseThrow();
        Jeu jeu1Personne2 = personne2.getJeux().stream().filter(element -> element.getNom().equals("jeu1")).findFirst().orElseThrow();

        Assertions.assertAll(
                () -> Assertions.assertSame(personne1, new PersonneJao().charger(fichier1)),
                () -> Assertions.assertEquals(personne1, new PersonneJao().charger(fichier1)),
                () -> Assertions.assertTrue(personne1.idem(new PersonneJao().charger(fichier1))),
                () -> Assertions.assertSame(personne1, new PersonneJao().charger(fichier1)),
                () -> Assertions.assertSame(jeu1Personne1, jeu1Personne2),
                () -> Assertions.assertThrows(JaoParseException.class, () -> new PersonneJao().charger((PersonneDto) null, null)),
                () -> Assertions.assertThrows(JaoParseException.class, () -> new PersonneJao().charger(new PersonneDto(), null)),
                () -> Assertions.assertDoesNotThrow(() -> new VoieJao().charger(new VoieDto(), null))
        );
    }

    @Test
    void creer() {
        Map<Path, UUID> germes = Map.ofEntries(
                new AbstractMap.SimpleEntry<>(fichier1, personne1.getIdentifiant()),
                new AbstractMap.SimpleEntry<>(fichier2, personne2.getIdentifiant()),
                new AbstractMap.SimpleEntry<>(fichier3, jeu.getIdentifiant()),
                new AbstractMap.SimpleEntry<>(fichier4, jeu1.getIdentifiant()),
                new AbstractMap.SimpleEntry<>(fichier5, pegi12.getIdentifiant()),
                new AbstractMap.SimpleEntry<>(fichier6, pegi18.getIdentifiant()),
                new AbstractMap.SimpleEntry<>(fichier7, adresse1.getIdentifiant()),
                new AbstractMap.SimpleEntry<>(fichier8, livre1.getIdentifiant()),
                new AbstractMap.SimpleEntry<>(fichier9, livre2.getIdentifiant()),
                new AbstractMap.SimpleEntry<>(fichier10, chapitre11.getIdentifiant()),
                new AbstractMap.SimpleEntry<>(fichier11, chapitre21.getIdentifiant()),
                new AbstractMap.SimpleEntry<>(fichier12, chapitre22.getIdentifiant())
        );
        Map<UUID, Path> fichiers = Map.ofEntries(
                new AbstractMap.SimpleEntry<>(personne1.getIdentifiant(), fichier1),
                new AbstractMap.SimpleEntry<>(personne2.getIdentifiant(), fichier2),
                new AbstractMap.SimpleEntry<>(jeu.getIdentifiant(), fichier3),
                new AbstractMap.SimpleEntry<>(jeu1.getIdentifiant(), fichier4),
                new AbstractMap.SimpleEntry<>(pegi12.getIdentifiant(), fichier5),
                new AbstractMap.SimpleEntry<>(pegi18.getIdentifiant(), fichier6),
                new AbstractMap.SimpleEntry<>(adresse1.getIdentifiant(), fichier7),
                new AbstractMap.SimpleEntry<>(livre1.getIdentifiant(), fichier8),
                new AbstractMap.SimpleEntry<>(livre2.getIdentifiant(), fichier9),
                new AbstractMap.SimpleEntry<>(chapitre11.getIdentifiant(), fichier10),
                new AbstractMap.SimpleEntry<>(chapitre21.getIdentifiant(), fichier11),
                new AbstractMap.SimpleEntry<>(chapitre22.getIdentifiant(), fichier12)
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(germes.size(), JabSingletons.creation().getGermes().size()),
                () -> Assertions.assertTrue(JabSingletons.creation().getGermes().keySet().containsAll(germes.keySet())),
                () -> JabSingletons.creation().getGermes().keySet().forEach(fichier ->
                        Assertions.assertEquals(germes.get(fichier), JabSingletons.creation().getGermes().get(fichier))
                ),
                () -> Assertions.assertEquals(fichiers.size(), JabSingletons.creation().getFichiers().size()),
                () -> Assertions.assertTrue(JabSingletons.creation().getFichiers().keySet().containsAll(fichiers.keySet())),
                () -> JabSingletons.creation().getFichiers().keySet().forEach(identifiant ->
                        Assertions.assertSame(fichiers.get(identifiant), JabSingletons.creation().getFichiers().get(identifiant))
                )
        );
    }

    @Test
    void nouveau() throws JaoExecutionException {
        Pegi nouveauPegi = pegiJao.nouveau();
        Jeu nouveauJeu = jeuJao.nouveau(objet -> objet.setPegi(nouveauPegi));

        Assertions.assertAll(
                () -> Assertions.assertNotNull(nouveauPegi),
                () -> Assertions.assertNotNull(nouveauPegi.getIdentifiant()),
                () -> Assertions.assertFalse(nouveauPegi.estDetache()),
                () -> Assertions.assertNotNull(nouveauJeu),
                () -> Assertions.assertNotNull(nouveauJeu.getIdentifiant()),
                () -> Assertions.assertFalse(nouveauJeu.estDetache()),
                () -> Assertions.assertSame(nouveauPegi, nouveauJeu.getPegi())
        );
    }

    @ParameterizedTest
    @ArgumentsSource(jsonTableArgumentsProvider.class)
    void table(Supplier<Set<Jo<Dto>>> josSupplier, Supplier<Jao<Dto, Jo<Dto>>> jaoSupplier, Class<? extends Jo<Dto>> classeJo) {
        Set<Jo<Dto>> jos = josSupplier.get();
        Jao<Dto, Jo<Dto>> jao = jaoSupplier.get();
        Assertions.assertAll(
                () -> Assertions.assertNotNull(jao.table()),
                () -> Assertions.assertSame(jao.table(), JabSingletons.table(classeJo)),

                () -> Assertions.assertNotNull(jao.table().selectionner()),
                () -> Assertions.assertEquals(jos.size(), jao.table().selectionner().size()),
                () -> Assertions.assertTrue(jao.table().selectionner().containsAll(jos)),

                () -> Assertions.assertTrue(jos.stream().allMatch(jo -> jao.table().selectionner(jo.getIdentifiant()).equals(jo)))
        );
    }

    @Test
    void sauvegarder() throws IOException, JaoParseException, JaoExecutionException {
        Path defautPersonne = ressourcesObjets.resolve("personne_sauvegarde_attendu.json");
        Path sauvegardePersonne = ressourcesSupprimable.resolve("personne_sauvegarde.json");
        Files.writeString(sauvegardePersonne, Files.readString(defautPersonne), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Personne chargementPersonne = personneJao.charger(sauvegardePersonne);

        Files.writeString(sauvegardePersonne, "{}");
        chargementPersonne.setScore(chargementPersonne.getScore());
        JsonElement actuelPersonneAucuneModificationScore = JsonParser.parseReader(Files.newBufferedReader(sauvegardePersonne));
        chargementPersonne.setScore(chargementPersonne.getScore() + 100);
        JsonElement actuelPersonneModificationScore = JsonParser.parseReader(Files.newBufferedReader(sauvegardePersonne));

        Assertions.assertAll(
                () -> Assertions.assertTrue(actuelPersonneAucuneModificationScore.isJsonObject()),
                () -> Assertions.assertTrue(actuelPersonneAucuneModificationScore.getAsJsonObject().isEmpty()),
                () -> Assertions.assertTrue(actuelPersonneModificationScore.isJsonObject()),
                () -> Assertions.assertFalse(actuelPersonneModificationScore.getAsJsonObject().isEmpty())
        );

        Jeu premierJeu = chargementPersonne.getJeux().stream().findFirst().orElseThrow();

        Files.writeString(sauvegardePersonne, "{}");
        premierJeu.setType(premierJeu.getType());
        JsonElement actuelPersonneAucuneModificationJeux = JsonParser.parseReader(Files.newBufferedReader(sauvegardePersonne));
        premierJeu.setType(JeuType.values()[(premierJeu.getType().ordinal() + 1) % JeuType.values().length]);
        JsonElement actuelPersonneModificationJeux = JsonParser.parseReader(Files.newBufferedReader(sauvegardePersonne));

        Assertions.assertAll(
                () -> Assertions.assertTrue(actuelPersonneAucuneModificationJeux.isJsonObject()),
                () -> Assertions.assertTrue(actuelPersonneAucuneModificationJeux.getAsJsonObject().isEmpty()),
                () -> Assertions.assertTrue(actuelPersonneModificationJeux.isJsonObject()),
                () -> Assertions.assertFalse(actuelPersonneModificationJeux.getAsJsonObject().isEmpty())
        );

        Path defautJeu = ressourcesObjets.resolve("jeu_sauvegarde_attendu.json");
        Path sauvegardeJeu = ressourcesSupprimable.resolve("jeu_sauvegarde.json");
        Files.writeString(sauvegardeJeu, Files.readString(defautJeu), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Jeu chargementJeu = jeuJao.charger(sauvegardeJeu);

        Files.writeString(sauvegardePersonne, "{}");
        Files.writeString(sauvegardeJeu, "{}");
        chargementJeu.setNom(chargementJeu.getNom());
        JsonElement actuelPersonneAucuneModificationJeuNom = JsonParser.parseReader(Files.newBufferedReader(sauvegardePersonne));
        JsonElement actuelJeuAucuneModificationJeuNom = JsonParser.parseReader(Files.newBufferedReader(sauvegardeJeu));
        chargementJeu.setNom(chargementJeu.getNom() + " (nouveau)");
        JsonElement actuelPersonneModificationJeuNom = JsonParser.parseReader(Files.newBufferedReader(sauvegardePersonne));
        JsonElement actuelJeuModificationJeuNom = JsonParser.parseReader(Files.newBufferedReader(sauvegardeJeu));

        Assertions.assertAll(
                () -> Assertions.assertTrue(actuelPersonneAucuneModificationJeuNom.isJsonObject()),
                () -> Assertions.assertTrue(actuelPersonneAucuneModificationJeuNom.getAsJsonObject().isEmpty()),
                () -> Assertions.assertTrue(actuelJeuAucuneModificationJeuNom.isJsonObject()),
                () -> Assertions.assertTrue(actuelJeuAucuneModificationJeuNom.getAsJsonObject().isEmpty()),
                () -> Assertions.assertTrue(actuelPersonneModificationJeuNom.isJsonObject()),
                () -> Assertions.assertFalse(actuelPersonneModificationJeuNom.getAsJsonObject().isEmpty()),
                () -> Assertions.assertTrue(actuelJeuModificationJeuNom.isJsonObject()),
                () -> Assertions.assertFalse(actuelJeuModificationJeuNom.getAsJsonObject().isEmpty())
        );
    }

    @Test
    void modificationJoc() throws IOException, JaoParseException, JaoExecutionException {
        Path defautPersonne = ressourcesObjets.resolve("personne_modification_attendu.json");
        Path modificationPersonne = ressourcesSupprimable.resolve("personne_sauvegarde.json");
        Files.writeString(modificationPersonne, Files.readString(defautPersonne), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Personne chargementPersonne = personneJao.charger(modificationPersonne);
        Jeu premierJeu = chargementPersonne.getJeux().stream().findFirst().orElseThrow();

        Set<Jeu> jeux = Set.of(premierJeu);
        Assertions.assertAll(
                () -> Assertions.assertEquals(600, chargementPersonne.getScore()),
                () -> Assertions.assertEquals(jeux.size(), chargementPersonne.getJeux().size()),
                () -> Assertions.assertTrue(chargementPersonne.getJeux().containsAll(jeux)),
                () -> Assertions.assertNull(premierJeu.getPegi())
        );

        chargementPersonne.setScore(700L);
        Assertions.assertEquals(700, chargementPersonne.getScore());

        premierJeu.setPegi(pegi12);
        Assertions.assertSame(pegi12, premierJeu.getPegi());

        premierJeu.setPegi(pegi18);
        Assertions.assertSame(pegi18, premierJeu.getPegi());
    }

    @Test
    void modificationJocArrayList() throws IOException, JaoParseException, JaoExecutionException {
        Path defautPersonne = ressourcesObjets.resolve("personne_modification_attendu.json");
        Path modificationPersonne = ressourcesSupprimable.resolve("personne_sauvegarde.json");
        Files.writeString(modificationPersonne, Files.readString(defautPersonne), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Personne chargementPersonne = personneJao.charger(modificationPersonne);
        Jeu premierJeu = chargementPersonne.getJeux().stream().findFirst().orElseThrow();
        chargementPersonne.getJeux().add(JaoTest.jeu);

        Set<Jeu> jeux1 = Set.of(premierJeu, JaoTest.jeu);
        Assertions.assertAll(
                () -> Assertions.assertEquals(jeux1.size(), chargementPersonne.getJeux().size()),
                () -> Assertions.assertTrue(chargementPersonne.getJeux().containsAll(jeux1))
        );

        chargementPersonne.getJeux().remove(premierJeu);
        Set<Jeu> jeux2 = Set.of(JaoTest.jeu);
        Assertions.assertAll(
                () -> Assertions.assertEquals(jeux2.size(), chargementPersonne.getJeux().size()),
                () -> Assertions.assertTrue(chargementPersonne.getJeux().containsAll(jeux2))
        );
    }

    @Test
    void dupliquer() throws JaoExecutionException {
        Personne duplicationPersonne = new PersonneJao().dupliquer(personne1);
        Pegi duplicationPegi = new PegiJao().dupliquer(pegi12);
        Map<Chapitre, Pegi> duplicationChapitres = new ChapitreJao().dupliquer(new PegiJao(), livre1.getChapitres());
        Chapitre repliquesChapitre11 = duplicationChapitres.keySet().stream().filter(chapitre -> chapitre.getNom().equals("chapitre11")).findFirst().orElseThrow();
        Pegi repliquesPegi = duplicationChapitres.get(repliquesChapitre11);
        Assertions.assertAll(
                () -> Assertions.assertNotEquals(pegi12, duplicationPegi),
                () -> Assertions.assertTrue(pegi12.idem(duplicationPegi)),
                () -> Assertions.assertEquals(livre1.getChapitres().size(), duplicationChapitres.size()),
                () -> Assertions.assertNotEquals(chapitre11, repliquesChapitre11),
                () -> Assertions.assertTrue(chapitre11.idem(repliquesChapitre11)),
                () -> Assertions.assertNotEquals(pegi12, repliquesPegi),
                () -> Assertions.assertTrue(pegi12.idem(repliquesPegi)),
                () -> Assertions.assertNotEquals(personne1, duplicationPersonne),
                () -> Assertions.assertTrue(personne1.idem(duplicationPersonne)),
                () -> assertIdentifiantsNonEgales(personne1.getJeux(), duplicationPersonne.getJeux()),
                () -> assertIdentifiantsNonEgales(personne1.getJeux().stream().map(Jeu::getPegi).filter(Objects::nonNull).toList(), duplicationPersonne.getJeux().stream().map(Jeu::getPegi).filter(Objects::nonNull).toList()),
                () -> assertIdentifiantsNonEgales(personne1.getLivres(), duplicationPersonne.getLivres()),
                () -> assertIdentifiantsNonEgales(
                        personne1.getLivres().values().stream().flatMap(livre -> livre.getChapitres().keySet().stream()).toList(),
                        duplicationPersonne.getLivres().values().stream().flatMap(livre -> livre.getChapitres().keySet().stream()).toList()
                )
        );
    }

    private <D extends Dto, O extends Jo<D>> void assertIdentifiantsNonEgales(Collection<O> objets1, Collection<O> objets2) {
        Assertions.assertTrue(objets1.stream().allMatch(objet1 -> objets2.stream().noneMatch(objet1::equals)));
    }

    private <K, D extends Dto, O extends Jo<D>> void assertIdentifiantsNonEgales(Map<K, O> objets1, Map<K, O> objets2) {
        Assertions.assertTrue(objets1.values().stream().allMatch(objet1 -> objets2.values().stream().noneMatch(objet1::equals)));
    }

    private static class verifierChargementArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of(personne1, "5896fac0-c48d-11ed-afa1-0242ac120002", Collections.emptyList(), Arrays.asList(jeu1, livre1, livre2)),
                    Arguments.of(jeu1, "7b056a2a-c48c-11ed-afa1-0242ac120002", Collections.singletonList(personne1), Collections.emptyList()),
                    Arguments.of(livre1, "856b9132-c48d-11ed-afa1-0242ac120002", Collections.singletonList(personne1), Collections.singletonList(chapitre11)),
                    Arguments.of(livre2, "97583a9e-c48d-11ed-afa1-0242ac120002", Collections.singletonList(personne1), Arrays.asList(chapitre21, chapitre22)),
                    Arguments.of(chapitre11, "e3b1aa1f-c48d-11ed-afa1-0242ac120002", Collections.singletonList(livre1), Collections.emptyList()),
                    Arguments.of(chapitre21, "e6428919-c48d-11ed-afa1-0242ac120002", Collections.singletonList(livre2), Collections.emptyList()),
                    Arguments.of(chapitre22, "60d4a435-c48d-11ed-afa1-0242ac120002", Collections.singletonList(livre2), Collections.emptyList())
            );
        }
    }

    private static class chargerExceptionArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(JaoParseException.class, "inconnu.json", MessageFormat.format("Impossible de charger le fichier {0}\\inconnu.json", ressourcesObjets), (Supplier<JeuJao>) JeuJao::new),
                    Arguments.of(JaoParseException.class, "jeu_avec_nom_null.json", "Le nom du jeu est obligatoire", (Supplier<JeuJao>) JeuJao::new),
                    Arguments.of(JaoParseException.class, "jeu_avec_nom_vide.json", "Le nom du jeu est obligatoire", (Supplier<JeuJao>) JeuJao::new),
                    Arguments.of(JaoParseException.class, "jeu_avec_type_incorrect.json", "Le type d'un jeu devrait être une des valeurs JEU_DE_ROLES, CARTES", (Supplier<JeuJao>) JeuJao::new),
                    Arguments.of(JaoParseException.class, "jeu_avec_annee_incorrect.json", "L'année du jeu est en dehors de la plage acceptée", (Supplier<JeuJao>) JeuJao::new),
                    Arguments.of(JaoParseException.class, "jeu_avec_semaine_incorrect.json", "Le numéro de la semaine annuel du jeu est en dehors de la plage acceptée", (Supplier<JeuJao>) JeuJao::new),
                    Arguments.of(JaoParseException.class, "pegi_avec_age_trop_grand.json", "L'age du système PEGI doit être compris entre 0 et 99 ans.", (Supplier<PegiJao>) PegiJao::new),
                    Arguments.of(JaoParseException.class, "pegi_avec_age_trop_petit.json", "L'age du système PEGI doit être compris entre 0 et 99 ans.", (Supplier<PegiJao>) PegiJao::new),
                    Arguments.of(JaoParseException.class, "personne_avec_jeux_null.json", "La liste des jeux d'une personne devrait être une liste éventuellement vide", (Supplier<PersonneJao>) PersonneJao::new),
                    Arguments.of(JaoParseException.class, "personne_avec_jeu_sans_nom.json", "Impossible de charger la liste d'objets. Le nom du jeu est obligatoire", (Supplier<PersonneJao>) PersonneJao::new),
                    Arguments.of(JaoParseException.class, "personne_avec_noms_jeux_multiple.json", "La liste des jeux ne doit pas contenir plusieurs jeux avec le même nom", (Supplier<PersonneJao>) PersonneJao::new),
                    Arguments.of(JaoParseException.class, "personne_avec_score_negatif.json", "Le score d'une personne doit être strictement positif", (Supplier<PersonneJao>) PersonneJao::new)
            );
        }
    }

    private static class jsonTableArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(
                            (Supplier<Set<Personne>>) () -> Set.of(personne1, personne2),
                            (Supplier<PersonneJao>) PersonneJao::new,
                            Personne.class
                    ),
                    Arguments.of(
                            (Supplier<Set<Jeu>>) () -> Stream.concat(Stream.concat(personne1.getJeux().stream(), personne2.getJeux().stream()), Stream.of(jeu)).collect(Collectors.toSet()),
                            (Supplier<JeuJao>) JeuJao::new,
                            Jeu.class
                    ),
                    Arguments.of(
                            (Supplier<Set<Livre>>) () -> Stream.concat(personne1.getLivres().values().stream(), personne2.getLivres().values().stream()).collect(Collectors.toSet()),
                            (Supplier<LivreJao>) LivreJao::new,
                            Livre.class
                    ),
                    Arguments.of(
                            (Supplier<Set<Adresse>>) () -> Set.of(adresse1),
                            (Supplier<AdresseJao>) AdresseJao::new,
                            Adresse.class
                    )
            );
        }
    }
}
