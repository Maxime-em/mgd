# Jab

Bibliothèque légère de persistance : elle permet de gérer des données **sans base de données**, directement dans des
fichiers JSON, avec une sauvegarde automatique.

## Objectifs fonctionnels

- **Stocker** les données métier dans des fichiers JSON lisibles, rangés dans une arborescence de dossiers.
- **Charger** ces fichiers sous forme d'objets Java métier, prêts à être manipulés.
- **Sauvegarder automatiquement** : il suffit de modifier un objet, Jab réécrit le fichier concerné (par défaut en
  différé, pour regrouper les modifications).
- **Relier des fichiers entre eux** par des références, pour qu'un objet puisse en utiliser un autre stocké ailleurs.
- **Créer** les fichiers manquants à la demande.

## Grandes lignes techniques

| Classe                 | Rôle                                                                                                                           |
|------------------------|--------------------------------------------------------------------------------------------------------------------------------|
| `Jab`                  | Point d'entrée, à étendre dans le module métier. Lit `jab.properties` et instancie le reste.                                   |
| `Ad` / `Af`            | Accès à un dossier (`Ad`) et à un fichier JSON (`Af`). Le fichier est créé s'il n'existe pas, l'objet est chargé à la demande. |
| `Jao`                  | Convertit un `Dto` (image JSON) en objet métier `Jo` et inversement. Gère listes, maps, tableaux et références.                |
| `Dto` / `ReferenceDto` | Objets de transfert sérialisés avec Gson ; `ReferenceDto` pointe vers un objet d'un autre fichier.                             |
| `Jo`                   | Objet métier de base : identifiant UUID, parents et enfants.                                                                   |
| `Joc*`                 | Champs et collections « observables » (`Joc`, `JocArrayList`, `JocHashMap`, `JocTreeSet`) qui déclenchent la sauvegarde.       |
| `JabSauvegarde`        | Écriture des fichiers, asynchrone ou non, via un pool de threads planifiés.                                                    |
| `JabTable`             | Registre par type d'objet : un même UUID correspond toujours à la même instance.                                               |
| `JabCreation`          | Mémorise l'association entre un objet (UUID) et son fichier.                                                                   |

**Principe de sauvegarde.** Modifier un champ `Joc*` appelle `Jo.sauvegarder()`, qui remonte aux parents jusqu'à
l'objet « racine » lié à un fichier. Celui-ci est converti en `Dto`, sérialisé en JSON puis écrit sur disque.
En mode asynchrone, l'écriture est planifiée après un délai ; `disposer()` force l'écriture des données en attente.

**Configuration (`jab.properties`)**

```properties
jab.base=data                                  # dossier racine des données (obligatoire)
jab.persistences=nom:org.exemple.MonJao        # Jao instanciés par réflexion
jab.sources=dossier:org.exemple.MonAd          # dossiers de données et leur Ad
jab.sources.pattern=false                      # true : les noms de dossiers sont des motifs
jab.sauvegarde.asynchrone=true                 # défaut : true
jab.sauvegarde.delai=60000                     # en ms, défaut : 60000
jab.sauvegarde.nombre.threads=100              # défaut : 100
```

**Stack** : Java 25 (module `org.mgd.jab`), Maven, Gson (JSON), Log4j (journalisation), dépend du module `commun`.

# Pab
Permet la production de fichier au format PDF.

# Gmel
Permet la gestion de menus et de listes de courses.
