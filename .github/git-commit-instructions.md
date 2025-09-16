## Messages de commit

- Rédiger les messages de commit en français.
- Conserver les noms techniques et les identifiants de code dans leur langue d'origine.
- Analyser l'intégralité des changements concernés, hors fichiers exclus ci-dessus.
- Respecter la structure suivante :
    - une première ligne courte (72 caractères maximum) résumant le changement principal ;
    - une ligne vide ;
    - une liste à puces décrivant chaque modification, regroupée par fichier ou par composant.
- Mentionner explicitement les suppressions de code ayant un impact fonctionnel (appels de méthode, navigation, routage,
  souscriptions, logique métier),
  en précisant la méthode concernée (ex. `Composant.methode`).
- Prioriser les changements de comportement ; regrouper les changements purement
  cosmétiques (imports réordonnés, guillemets, accolades, formatage) en une seule
  puce en fin de liste.
- Indiquer la raison du changement lorsqu'elle est identifiable (ex. correction d'un problème Sonar).
- Fournir le message dans un bloc de code, prêt à être copié-collé dans le champ
  d'IntelliJ dédié aux messages de commit.