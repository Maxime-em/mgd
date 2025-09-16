# Lwjgl

## Objectif

`lwjgl` est le module de rendu graphique de MGD. Il fournit aux applications Java une fenêtre GLFW et un contexte
OpenGL, puis des abstractions pour afficher et animer des formes, gérer les interactions souris/clavier et composer une
interface 2D avec NanoVG. Il est notamment destiné aux jeux et applications interactives du dépôt.

Le module sépare le moteur graphique de la logique métier : l'application consommatrice fournit les objets à afficher
et leur comportement, tandis que `lwjgl` gère la boucle d'exécution, le rendu et la distribution des événements.

## Fonctions principales

- Création et gestion d'une fenêtre GLFW avec contexte OpenGL.
- Boucle d'application avec mises à jour, rendu et événements de fenêtre.
- Affichage de formes OpenGL, transformations, textures et transitions de déplacement.
- Caméra (`Vue`), projection perspective et conversion des coordonnées entre écran et scène (`TransformationEcran`).
- Détection du survol et sélection de formes, ainsi que distribution des événements clavier et souris.
- Interface utilisateur NanoVG : menus paginés, barres/listes, actions, panneaux contextuels et infobulles.
- Chargement des images de texture par STB et des images/polices d'interface par NanoVG.

## Architecture

| Composant                                             | Rôle                                                                                                                                              |
|-------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|
| `Application`                                         | Classe de base de l'application : configure les ressources, initialise OpenGL, appelle `peupler()`, démarre la boucle puis libère les ressources. |
| `Fenetre`                                             | Fenêtre GLFW, contexte de rendu, objets affichés, caméra et événements d'interaction.                                                             |
| `GroupeFormes<G>`                                     | Groupe de maillages (`Maillage`), avec transformation, visibilité, ordre d'affichage et gestion du survol.                                        |
| `Maillage` / `Quadrilatere`                           | Géométrie OpenGL, texture, sélection et transitions de déplacement.                                                                               |
| `Quadrillage`                                         | Composant spécialisé pour construire une grille et y placer des jetons.                                                                           |
| `AffichageTeteHaute`                                  | Base des composants d'interface dessinés par NanoVG.                                                                                              |
| `Menu`, `Barre`, `MenuContextuel`, `Informations`     | Composants d'interface réutilisables.                                                                                                             |
| `Entite`, `Action`, `Liste`, `Disposition`, `Options` | Briques de contenu, d'interaction, de pagination et de mise en page de l'interface.                                                               |
| `GestionnaireOmbrages`                                | Enregistrement, compilation et utilisation des programmes d'ombrage OpenGL et de leurs uniformes.                                                 |
| `ChargeurTextures`                                    | Chargement et libération des textures OpenGL à partir de fichiers image.                                                                          |
| `GestionnaireEvenementsGlfw`                          | Adaptation des callbacks GLFW aux détecteurs enregistrés par fenêtre.                                                                             |
| `Transition`                                          | Interpolation temporelle de valeurs, utilisée notamment pour animer les formes.                                                                   |

## Utilisation

Une application consommatrice étend `Application`, fournit le titre et les dimensions de la fenêtre, enregistre ses
programmes d'ombrage puis construit sa scène dans `peupler()`.

```java
import java.nio.file.Path;

import org.mgd.lwjgl.Application;
import org.mgd.lwjgl.Programme;
import org.mgd.lwjgl.EtatVisuel;
import org.mgd.lwjgl.exception.LwjglException;

public final class ExempleApplication extends Application {
    public ExempleApplication(Path dossierRessources) throws LwjglException {
        super("Exemple", 720, 16, 9);
        configurer(dossierRessources);

        Programme.nouveau(
                "objet",
                EtatVisuel.PSEUDO_BASE,
                dossierRessources,
                new String[]{"vecteur", "fragment"}
        );
    }

    @Override
    protected void peupler() throws LwjglException {
        fenetre.creerContexteNvg();
        // Créer ici les éléments et les composants d'interface.
    }

    static void main(String[] args) throws LwjglException {
        new ExempleApplication(Path.of("ressources")).demarrer();
    }
}
```

Le constructeur de `Fenetre` est appelé par `Application`. `demarrer()` initialise OpenGL, crée les programmes
d'ombrage enregistrés, appelle `peupler()`, affiche la fenêtre et lance la boucle. La boucle continue jusqu'à la
fermeture de la fenêtre ou une demande d'arrêt. Le code de rendu et les appels OpenGL doivent rester sur le thread qui
possède le contexte courant.

`configurer(Path)` enregistre le programme d'ombrage standard. Les autres programmes se déclarent avec
`Programme.nouveau(nom, pseudo, dossier, types)`. Les types actuellement reconnus sont `vecteur` (vertex shader) et
`fragment`. Pour chaque type, les fichiers sont recherchés selon la convention :

```text
<dossier>/ombrages/<nom>/<pseudo>/<pseudo>.vert
<dossier>/ombrages/<nom>/<pseudo>/<pseudo>.frag
```

Le programme par défaut utilise `defaut/base/base.vert` et `defaut/base/base.frag`. Les pseudo-états intégrés sont
`base`, `survoler` et `activer`. Les textures d'un `Element` sont fournies sous forme de chemins associés à ces
pseudo-états.

Le contexte NanoVG doit être créé avec `fenetre.creerContexteNvg()` avant la création des composants d'interface et
avant l'enregistrement de leurs ressources. Les polices et images d'interface se déclarent ensuite avec
`AffichageTeteHaute.creerPolice(...)` et `AffichageTeteHaute.creerImage(...)`.

## Dépendances et exécution

- Java 25 et Maven.
- Dépend du module `commun`.
- Utilise LWJGL 3.4.1, GLFW, OpenGL, STB et NanoVG.
- Le module déclarant les bindings Java LWJGL ne fournit pas, à lui seul, les bibliothèques natives spécifiques à la
  plateforme. L'application exécutable doit ajouter au chemin d'exécution les natives LWJGL correspondant au système et
  à l'architecture ciblés.

Pour compiler le module et ses dépendances depuis la racine du dépôt :

```shell
mvn test -pl lwjgl -am
```

Les appels système, ressources graphiques et comportement de fenêtre nécessitent un environnement graphique pour être
exercés. Le répertoire `lwjgl/src/test/java` ne contient actuellement pas de tests automatisés du module.

## Contrats et limites d'utilisation

- Les dimensions et le ratio passés au constructeur doivent être valides et positifs.
- Enregistrer les programmes et charger les ressources avant leur utilisation dans la boucle de rendu.
- Créer le contexte NanoVG avant tout composant qui en dépend.
- Conserver les opérations OpenGL/NanoVG sur le thread du contexte.
- Appeler `demarrer()` pour utiliser le cycle de vie prévu par le module; à la fermeture normale, il libère les
  ressources du moteur.

## Robustesse et cycle de vie

- `Application.demarrer()` libère ses ressources même si l'initialisation, `peupler()` ou la boucle échoue.
  La boucle utilise une horloge monotone, des pas d'animation fixes et des fréquences exprimées en nanosecondes.
- Les programmes OpenGL et les shaders partiellement compilés, les textures chargées par STB ainsi que les images et
  polices NanoVG sont supprimés au moment de la fermeture. Les pixels décodés par STB sont libérés même si la création
  de texture échoue.
- Les callbacks GLFW sont détachés et libérés, les inscriptions sont vidées et le gestionnaire singleton peut être
  recréé pour une autre application.
- Les dimensions de fenêtre et de grille, les coordonnées des cases/jetons, l'appartenance d'un jeton à la grille et la
  géométrie d'un maillage sont validées avant les opérations concernées. Le centre du maillage est calculé à partir de
  tous ses sommets.
- `EvenementSouris.coordonneesVision()` renvoie une copie; `Quadrillage.casesParIndex()` renvoie une map non modifiable
  indexée par le record immuable `CoordonneeCase`. `Contour` vérifie la forme des entrées et traite les cas dégénérés
  avec une tolérance numérique.
