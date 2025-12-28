package org.mgd.jab.persistence;

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
import org.mgd.jab.AbstractTest;
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
class JaoTest extends AbstractTest {
    public static final String UUID_PERSONNE_1 = "5896fac0-c48d-11ed-afa1-0242ac120002";
    public static final String UUID_PERSONNE_2 = "5f2a5aa8-c48d-11ed-afa1-0242ac120002";
    public static final String UUID_JEU_1 = "7b056a2a-c48c-11ed-afa1-0242ac120002";
    public static final String UUID_JEU_2 = "105b6dea-c48d-11ed-afa1-0242ac120002";
    public static final String UUID_LIVRE_1 = "856b9132-c48d-11ed-afa1-0242ac120002";
    public static final String UUID_LIVRE_2 = "97583a9e-c48d-11ed-afa1-0242ac120002";
    public static final String UUID_LIVRE_3 = "ef53d929-c48d-11ed-afa1-0242ac120002";
    public static final String UUID_CHAPITRE_11 = "e3b1aa1f-c48d-11ed-afa1-0242ac120002";
    public static final String UUID_CHAPITRE_21 = "e6428919-c48d-11ed-afa1-0242ac120002";
    public static final String UUID_CHAPITRE_22 = "60d4a435-c48d-11ed-afa1-0242ac120002";
    public static final String UUID_CHAPITRE_31 = "6a7d6fbc-c48d-11ed-afa1-0242ac120002";
    public static final String UUID_CHAPITRE_32 = "93fa7649-c48d-11ed-afa1-0242ac120002";

    private static Path ressourcesCommun;
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
    private static Path fichier13;
    private static Path fichier14;
    private static Personne personne1;
    private static Personne personne2;
    private static Jeu jeu1;
    private static Jeu jeu2;
    private static Livre livre1;
    private static Livre livre2;
    private static Livre livre3;
    private static Chapitre chapitre11;
    private static Chapitre chapitre21;
    private static Chapitre chapitre22;
    private static Chapitre chapitre31;
    private static Chapitre chapitre32;
    private static Jeu jeu;
    private static PersonneJao personneJao;
    private static JeuJao jeuJao;
    private static PegiJao pegiJao;
    private static AdresseJao adresseJao;
    private static Pegi pegi12;
    private static Pegi pegi18;
    private static Adresse adresse1;
    private static Communaute communaute1;
    private static Monde monde1;

    @BeforeEach
    void setUp() throws IOException, JaoExecutionException, JaoParseException {
        JabSingletons.reinitialiser();
        JabSingletons.sauvegarde().setAsynchrone(false);

        ressourcesCommun = Path.of("src/test/resources/base/commun");
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

        fichier1 = ressourcesCommun.resolve("personne1.json").toAbsolutePath();
        fichier2 = ressourcesCommun.resolve("personne2.json").toAbsolutePath();
        fichier3 = ressourcesCommun.resolve("jeu.json").toAbsolutePath();
        fichier4 = ressourcesCommun.resolve("jeu1.json").toAbsolutePath();
        fichier5 = ressourcesCommun.resolve("pegi12.json").toAbsolutePath();
        fichier6 = ressourcesCommun.resolve("pegi18.json").toAbsolutePath();
        fichier7 = ressourcesCommun.resolve("adresse1.json").toAbsolutePath();
        fichier8 = ressourcesCommun.resolve("livre1.json").toAbsolutePath();
        fichier9 = ressourcesCommun.resolve("livre2.json").toAbsolutePath();
        fichier10 = ressourcesCommun.resolve("chapitre11.json").toAbsolutePath();
        fichier11 = ressourcesCommun.resolve("chapitre21.json").toAbsolutePath();
        fichier12 = ressourcesCommun.resolve("chapitre22.json").toAbsolutePath();
        fichier13 = ressourcesCommun.resolve("communaute1.json").toAbsolutePath();
        fichier14 = ressourcesCommun.resolve("monde1.json").toAbsolutePath();

        CommunauteJao communauteJao = new CommunauteJao();
        communaute1 = communauteJao.charger(fichier13);

        MondeJao mondeJao = new MondeJao();
        monde1 = mondeJao.charger(fichier14);

        adresseJao = new AdresseJao();
        adresse1 = adresseJao.charger(fichier7);

        personneJao = new PersonneJao();
        personne1 = personneJao.charger(fichier1);
        personne2 = adresse1.getProprietaires().stream().filter(element -> element.getIdentifiant().equals(UUID.fromString(UUID_PERSONNE_2))).findFirst().orElseThrow();

        jeuJao = new JeuJao();
        jeu = jeuJao.charger(fichier3);
        jeu1 = jeuJao.charger(fichier4);
        jeu2 = personne2.getJeux().stream().filter(element -> element.getIdentifiant().equals(UUID.fromString(UUID_JEU_2))).findFirst().orElseThrow();

        pegiJao = new PegiJao();
        pegi12 = pegiJao.charger(fichier5);
        pegi18 = pegiJao.charger(fichier6);

        LivreJao livreJao = new LivreJao();
        livre1 = livreJao.charger(fichier8);
        livre2 = livreJao.charger(fichier9);
        livre3 = personne2.getLivres().values().stream().filter(element -> element.getIdentifiant().equals(UUID.fromString(UUID_LIVRE_3))).findFirst().orElseThrow();

        ChapitreJao chapitreJao = new ChapitreJao();
        chapitre11 = chapitreJao.charger(fichier10);
        chapitre21 = chapitreJao.charger(fichier11);
        chapitre22 = chapitreJao.charger(fichier12);
        chapitre31 = livre3.getChapitres().keySet().stream().filter(element -> element.getIdentifiant().equals(UUID.fromString(UUID_CHAPITRE_31))).findFirst().orElseThrow();
        chapitre32 = livre3.getChapitres().keySet().stream().filter(element -> element.getIdentifiant().equals(UUID.fromString(UUID_CHAPITRE_32))).findFirst().orElseThrow();
    }

    @ParameterizedTest
    @ArgumentsSource(verifierChargementArgumentsProvider.class)
    void verifierChargement(Jo objet, String identifiant, List<Jo> parents, List<Jo> enfants) {
        Assertions.assertAll(
                () -> Assertions.assertEquals(UUID.fromString(identifiant), objet.getIdentifiant()),
                () -> Assertions.assertTrue(parents.stream().allMatch(objet::estEnfantDe)),
                () -> Assertions.assertTrue(enfants.stream().allMatch(objet::estParentDe)),
                () -> Assertions.assertFalse(objet.estDetache())
        );
    }

    @ParameterizedTest
    @ArgumentsSource(chargerExceptionArgumentsProvider.class)
    <T extends Throwable> void chargerException(Class<T> classe, String chemin, Supplier<Jao<Dto, Jo>> jaoSupplier) {
        Jao<Dto, Jo> jao = jaoSupplier.get();
        Assertions.assertThrows(classe, () -> jao.charger(ressourcesCommun.resolve(chemin)), MessageFormat.format("Chargement de {0} avec {1}", chemin, jao.getClass().getName()));
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
                new AbstractMap.SimpleEntry<>(fichier12, chapitre22.getIdentifiant()),
                new AbstractMap.SimpleEntry<>(fichier13, communaute1.getIdentifiant()),
                new AbstractMap.SimpleEntry<>(fichier14, monde1.getIdentifiant())
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
                new AbstractMap.SimpleEntry<>(chapitre22.getIdentifiant(), fichier12),
                new AbstractMap.SimpleEntry<>(communaute1.getIdentifiant(), fichier13),
                new AbstractMap.SimpleEntry<>(monde1.getIdentifiant(), fichier14)
        );

        Assertions.assertAll(
                () -> Assertions.assertEquals(germes.size(), JabSingletons.creation().getGermes().size()),
                () -> Assertions.assertTrue(JabSingletons.creation().getGermes().keySet().containsAll(germes.keySet())),
                () -> JabSingletons.creation().getGermes().keySet().forEach(fichier -> Assertions.assertEquals(germes.get(fichier), JabSingletons.creation().getGermes().get(fichier))),
                () -> Assertions.assertEquals(fichiers.size(), JabSingletons.creation().getFichiers().size()),
                () -> Assertions.assertTrue(JabSingletons.creation().getFichiers().keySet().containsAll(fichiers.keySet())),
                () -> JabSingletons.creation().getFichiers().keySet().forEach(identifiant -> Assertions.assertEquals(fichiers.get(identifiant), JabSingletons.creation().getFichiers().get(identifiant)))
        );
    }

    @Test
    void nouveau() throws JaoExecutionException, JaoParseException {
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

        Voie voie = new VoieJao().nouveau(objet -> {
            objet.setNumero(0);
            objet.setLibelle("Voie");
        });
        Commune commune = new CommuneJao().nouveau(objet -> {
            objet.setNom("Commune");
            objet.setCode("00000");
        });
        Adresse nouvelleAdresse = new AdresseJao().nouveau((objet, jos) -> {
                    objet.setVoie((Voie) jos[0]);
                    objet.setCommune((Commune) jos[1]);
                },
                voie,
                commune
        );

        Assertions.assertAll(
                () -> Assertions.assertNotNull(nouvelleAdresse),
                () -> Assertions.assertNotNull(nouvelleAdresse.getIdentifiant()),
                () -> Assertions.assertFalse(nouvelleAdresse.estDetache()),
                () -> Assertions.assertNotNull(nouvelleAdresse.getVoie()),
                () -> Assertions.assertSame(voie, nouvelleAdresse.getVoie()),
                () -> Assertions.assertNotNull(nouvelleAdresse.getCommune()),
                () -> Assertions.assertSame(commune, nouvelleAdresse.getCommune())
        );
    }

    @ParameterizedTest
    @ArgumentsSource(jsonTableArgumentsProvider.class)
    void table(Supplier<Set<Jo>> josSupplier, Supplier<Jao<Dto, Jo>> jaoSupplier, Class<? extends Jo> classeJo) {
        Set<Jo> jos = josSupplier.get();
        Jao<Dto, Jo> jao = jaoSupplier.get();
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
        Path attenduPersonne = ressourcesCommun.resolve("personne_sauvegarde_attendu.json");
        Path attenduPersonneScore = ressourcesCommun.resolve("personne_sauvegarde_attendu_score.json");
        Path attenduPersonnePremierJeuType = ressourcesCommun.resolve("personne_sauvegarde_attendu_premier_jeu_type.json");
        Path attenduPersonneJeuNom = ressourcesCommun.resolve("personne_sauvegarde_attendu_jeu_nom.json");
        Path sauvegardePersonne = ressourcesSupprimable.resolve("personne_sauvegarde.json");
        Files.writeString(sauvegardePersonne, Files.readString(attenduPersonne), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        Personne chargementPersonne = personneJao.charger(sauvegardePersonne);
        Files.writeString(sauvegardePersonne, "{}");
        chargementPersonne.setScore(chargementPersonne.getScore());
        assertFichierVide(sauvegardePersonne);

        chargementPersonne.sauvegarder();
        assertFichier(attenduPersonne, sauvegardePersonne);

        chargementPersonne.setScore(chargementPersonne.getScore() + 100);
        assertFichier(attenduPersonneScore, sauvegardePersonne);

        Files.writeString(sauvegardePersonne, "{}");
        Jeu premierJeu = chargementPersonne.getJeux().stream().findFirst().orElseThrow();
        premierJeu.setType(premierJeu.getType());
        assertFichierVide(sauvegardePersonne);

        premierJeu.setType(JeuType.CARTES);
        assertFichier(attenduPersonnePremierJeuType, sauvegardePersonne);

        Path attenduJeu = ressourcesCommun.resolve("jeu_sauvegarde_attendu.json");
        Path attenduJeuNom = ressourcesCommun.resolve("jeu_sauvegarde_attendu_jeu_nom.json");
        Path sauvegardeJeu = ressourcesSupprimable.resolve("jeu_sauvegarde.json");
        Files.writeString(sauvegardeJeu, Files.readString(attenduJeu), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Jeu chargementJeu = jeuJao.charger(sauvegardeJeu);

        Files.writeString(sauvegardePersonne, "{}");
        Files.writeString(sauvegardeJeu, "{}");

        chargementJeu.setNom(chargementJeu.getNom() + " (nouveau)");
        assertFichier(attenduPersonneJeuNom, sauvegardePersonne);
        assertFichier(attenduJeuNom, sauvegardeJeu);

        Path attenduAdresse = ressourcesCommun.resolve("adresse_sauvegarde_attendu.json");
        Path sauvegardeAdresse = ressourcesSupprimable.resolve("adresse_sauvegarde.json");
        Files.writeString(sauvegardeAdresse, Files.readString(attenduAdresse), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        Adresse chargementAdresse = adresseJao.charger(sauvegardeAdresse);
        Files.writeString(sauvegardeAdresse, "{}");
        chargementAdresse.sauvegarder();

        assertFichier(attenduAdresse, sauvegardeAdresse);
    }

    @Test
    void modificationJoc() throws IOException, JaoParseException, JaoExecutionException {
        Path defautPersonne = ressourcesCommun.resolve("personne_modification_attendu.json");
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
        Path defautPersonne = ressourcesCommun.resolve("personne_modification_attendu.json");
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
    void dupliquer() throws JaoExecutionException, JaoParseException {
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

    private static class verifierChargementArgumentsProvider implements ArgumentsProvider {
        @Override
        public @NonNull Stream<? extends Arguments> provideArguments(@NonNull ParameterDeclarations parameters, @NonNull ExtensionContext extensionContext) {
            return Stream.of(
                    Arguments.of(personne1, UUID_PERSONNE_1, Collections.emptyList(), Arrays.asList(jeu1, livre1, livre2)),
                    Arguments.of(personne2, UUID_PERSONNE_2, Collections.emptyList(), Arrays.asList(jeu1, jeu2, livre3)),
                    Arguments.of(jeu1, UUID_JEU_1, Arrays.asList(personne1, personne2), Collections.emptyList()),
                    Arguments.of(jeu2, UUID_JEU_2, Collections.singletonList(personne2), Collections.emptyList()),
                    Arguments.of(livre1, UUID_LIVRE_1, Collections.singletonList(personne1), Collections.singletonList(chapitre11)),
                    Arguments.of(livre2, UUID_LIVRE_2, Collections.singletonList(personne1), Arrays.asList(chapitre21, chapitre22)),
                    Arguments.of(livre3, UUID_LIVRE_3, Collections.singletonList(personne2), Arrays.asList(chapitre31, chapitre32)),
                    Arguments.of(chapitre11, UUID_CHAPITRE_11, Collections.singletonList(livre1), Collections.emptyList()),
                    Arguments.of(chapitre21, UUID_CHAPITRE_21, Collections.singletonList(livre2), Collections.emptyList()),
                    Arguments.of(chapitre22, UUID_CHAPITRE_22, Collections.singletonList(livre2), Collections.emptyList()),
                    Arguments.of(chapitre31, UUID_CHAPITRE_31, Collections.singletonList(livre3), Collections.emptyList()),
                    Arguments.of(chapitre32, UUID_CHAPITRE_32, Collections.singletonList(livre3), Collections.emptyList())
            );
        }
    }

    private static class chargerExceptionArgumentsProvider implements ArgumentsProvider {
        @Override
        public @NonNull Stream<? extends Arguments> provideArguments(@NonNull ParameterDeclarations parameters, @NonNull ExtensionContext context) {
            return Stream.of(
                    Arguments.of(JaoParseException.class, "inconnu.json", (Supplier<JeuJao>) JeuJao::new),
                    Arguments.of(JaoParseException.class, "jeu_avec_nom_null.json", (Supplier<JeuJao>) JeuJao::new),
                    Arguments.of(JaoParseException.class, "jeu_avec_nom_vide.json", (Supplier<JeuJao>) JeuJao::new),
                    Arguments.of(JaoParseException.class, "jeu_avec_type_incorrect.json", (Supplier<JeuJao>) JeuJao::new),
                    Arguments.of(JaoParseException.class, "jeu_avec_annee_incorrect.json", (Supplier<JeuJao>) JeuJao::new),
                    Arguments.of(JaoParseException.class, "jeu_avec_semaine_incorrect.json", (Supplier<JeuJao>) JeuJao::new),
                    Arguments.of(JaoParseException.class, "pegi_avec_age_trop_grand.json", (Supplier<PegiJao>) PegiJao::new),
                    Arguments.of(JaoParseException.class, "pegi_avec_age_trop_petit.json", (Supplier<PegiJao>) PegiJao::new),
                    Arguments.of(JaoParseException.class, "personne_avec_jeux_null.json", (Supplier<PersonneJao>) PersonneJao::new),
                    Arguments.of(JaoParseException.class, "personne_avec_jeu_sans_nom.json", (Supplier<PersonneJao>) PersonneJao::new),
                    Arguments.of(JaoParseException.class, "personne_avec_noms_jeux_multiple.json", (Supplier<PersonneJao>) PersonneJao::new),
                    Arguments.of(JaoParseException.class, "personne_avec_score_negatif.json", (Supplier<PersonneJao>) PersonneJao::new),
                    Arguments.of(JaoParseException.class, "personne_avec_score_negatif.json", (Supplier<PersonneJao>) PersonneJao::new),
                    Arguments.of(JaoParseException.class, "personne_avec_score_negatif.json", (Supplier<PersonneJao>) PersonneJao::new),
                    Arguments.of(JaoExecutionException.class, "adresse_avec_reference_commune_inconnu.json", (Supplier<AdresseJao>) AdresseJao::new),
                    Arguments.of(JaoParseException.class, "adresse_avec_reference_commune_identifiant_inconnu.json", (Supplier<AdresseJao>) AdresseJao::new),
                    Arguments.of(JaoParseException.class, "adresse_avec_reference_commune_sans_chemin.json", (Supplier<AdresseJao>) AdresseJao::new),
                    Arguments.of(JaoParseException.class, "adresse_avec_reference_commune_mauvais_chemin.json", (Supplier<AdresseJao>) AdresseJao::new),
                    Arguments.of(JaoParseException.class, "adresse_avec_reference_commune_sans_classe.json", (Supplier<AdresseJao>) AdresseJao::new),
                    Arguments.of(JaoParseException.class, "adresse_avec_reference_commune_mauvaise_classe.json", (Supplier<AdresseJao>) AdresseJao::new),
                    Arguments.of(JaoParseException.class, "adresse_avec_reference_sans_pays.json", (Supplier<AdresseJao>) AdresseJao::new)
            );
        }
    }

    private static class jsonTableArgumentsProvider implements ArgumentsProvider {
        @Override
        public @NonNull Stream<? extends Arguments> provideArguments(@NonNull ParameterDeclarations parameters, @NonNull ExtensionContext context) {
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
