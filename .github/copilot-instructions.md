## Conventions du projet

- Le projet est écrit en français autant que possible : commentaires, Javadoc, noms de méthodes de test, messages de
  commit et réponses.
- Les mots-clés, les API et les noms issus de bibliothèques restent en anglais.
- Stack : Java 25, Maven multi-modules (`commun`, `jab`, `pab`, `gmel-coeur`, `gmel-pdf`, `gmel-javafx`), JavaFX, Gson,
  Log4j 2, annotations JetBrains.
- Ne pas modifier le code de production lors de la génération de tests, sauf demande explicite.

## Génération de tests unitaires

Analysez le code demandé et générez des tests unitaires permettant de valider rigoureusement son comportement.

### Outils et structure

- Utiliser JUnit 6 (`org.junit.jupiter`) avec `junit-jupiter-params` ; ne pas ajouter d'autre bibliothèque de test sans
  demande.
- Placer les tests dans `src/test/java` du module concerné, dans le même paquet que la classe testée, avec le suffixe
  `Test` (ex. `QuantiteJaoTest`).
- Nommer les méthodes de test en français, en camelCase, selon le comportement vérifié (ex. `depuisFichierInexistant`).
- Réutiliser les classes de base existantes (ex. `AbstractMetierTest`) quand elles s'appliquent.
- Regrouper les assertions liées avec `Assertions.assertAll`.
- Isoler les tests : pas de réseau ni de dépendance à l'environnement ; utiliser des fichiers temporaires ou des
  ressources de test.
- Lancer les tests du module avec `mvn test -pl <module> -am`.

### Couverture et scénarios

- Viser la plus grande couverture possible, en privilégiant la pertinence des assertions plutôt que le seul pourcentage.
- Tester avec des scénarios de données réalistes.
- Tester avec des entrées invalides.
- Tester avec des valeurs `null`.
- Tester avec des chaînes, des collections ou des objets vides.
- Tester les valeurs limites (min/max, zéro, nombres négatifs).
- Tester les cas particuliers (caractères accentués, dates et fuseaux horaires, grands volumes, fichiers absents ou
  corrompus).
- Vérifier que les exceptions attendues sont levées, avec le bon type et un message explicite.
- Utiliser autant que possible des tests paramétrés (`@ParameterizedTest`).
