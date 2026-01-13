# Diagrammes UML - Application d'Échecs en Ligne

Ce dossier contient tous les diagrammes UML au format XML pour draw.io, conformément au devoir UML demandé.

## Contenu

### 1. Diagramme de Cas d'Utilisation
- **Fichier**: `diagram_use_case.xml`
- **Description**: Représente tous les acteurs (Invité, Joueur Inscrit, Organisateur) et leurs interactions avec le système.

### 2. Descriptions Textuelles des Cas d'Utilisation
- **Fichier**: `descriptions_cas_utilisation.md`
- **Description**: Descriptions détaillées pour deux cas d'utilisation:
  - S'inscrire
  - Jouer une partie contre un autre joueur

### 3. Diagrammes d'Activité

| Fichier | Description |
|---------|-------------|
| `diagram_activity_inscription.xml` | Processus d'inscription d'un joueur |
| `diagram_activity_connexion.xml` | Processus de connexion d'un joueur |
| `diagram_activity_game_players.xml` | Déroulement d'une partie entre deux joueurs |
| `diagram_activity_game_software.xml` | Déroulement d'une partie contre le logiciel externe |

### 4. Diagrammes de Séquence

| Fichier | Description |
|---------|-------------|
| `diagram_sequence_inscription.xml` | Séquence d'inscription d'un joueur |
| `diagram_sequence_connexion.xml` | Séquence de connexion d'un joueur |
| `diagram_sequence_game_players.xml` | Séquence d'une partie entre deux joueurs |
| `diagram_sequence_game_software.xml` | Séquence d'une partie contre le logiciel externe |

### 5. Diagramme de Structure et Déploiement
- **Fichier**: `diagram_deployment.xml`
- **Description**: Architecture technique montrant les différents serveurs, composants et leurs connexions.

### 6. Diagramme de Classes
- **Fichier**: `diagram_class.xml`
- **Description**: Structure des classes avec analyse de la relation entre "Joueur Inscrit" et "Invité".

## Comment utiliser ces fichiers

1. Ouvrez [draw.io](https://app.diagrams.net/)
2. Cliquez sur "File" → "Open from" → "Device"
3. Sélectionnez le fichier XML souhaité
4. Le diagramme s'affichera et sera éditable

## Analyse: Relation entre Joueur Inscrit et Invité

### Hiérarchie de classes
```
Utilisateur (abstract)
├── Invite
└── JoueurInscrit
    └── Organisateur
```

### Différences principales

| Caractéristique | Invité | Joueur Inscrit |
|-----------------|--------|----------------|
| Persistance | Non | Oui |
| Nom | Temporaire (inviteXXX) | Permanent |
| Email | Non | Oui (validé) |
| Historique | Non conservé | Conservé |
| Coefficient | Non | Oui |
| Salle Tournois | Non accessible | Accessible |
| Niveau logiciel | Basique uniquement | Tous les niveaux |
| Chat-Box | Non | Oui |

### Type de relation
- **Héritage commun**: Les deux classes héritent de la classe abstraite `Utilisateur`
- **Pas de relation directe**: Aucune association ou dépendance entre `Invite` et `JoueurInscrit`
- **Transition possible**: Un `Invite` peut devenir `JoueurInscrit` par inscription (création d'un nouvel objet)

Cette architecture permet:
- Le polymorphisme pour le traitement uniforme des utilisateurs
- La séparation claire des responsabilités
- L'extensibilité facile (ajout de nouveaux types d'utilisateurs)
