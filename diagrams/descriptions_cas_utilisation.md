# Descriptions textuelles des cas d'utilisation

## Cas d'utilisation 1: S'inscrire

**Nom:** S'inscrire
**Acteur principal:** Utilisateur (futur Joueur Inscrit)
**Portée:** Application d'échecs en ligne
**Niveau:** But utilisateur
**Parties prenantes et intérêts:**
- Utilisateur: Souhaite devenir un joueur inscrit pour accéder à toutes les fonctionnalités
- Système: Doit valider et enregistrer les informations de l'utilisateur

**Préconditions:**
- L'utilisateur n'est pas encore inscrit
- L'utilisateur a une adresse email valide

**Postconditions (succès):**
- Un compte joueur est créé dans le système
- L'email est validé
- L'utilisateur peut se connecter en tant que joueur inscrit

**Scénario nominal:**
1. L'utilisateur accède à la page d'inscription
2. Le système affiche le formulaire d'inscription
3. L'utilisateur saisit son adresse email
4. L'utilisateur valide le formulaire
5. Le système vérifie que l'email n'est pas déjà utilisé
6. Le système envoie un email de validation
7. L'utilisateur reçoit l'email et clique sur le lien de validation
8. Le système valide l'inscription et active le compte
9. Le système confirme l'inscription à l'utilisateur

**Scénarios alternatifs:**
- 5a. L'email est déjà utilisé: Le système affiche un message d'erreur et retourne à l'étape 3
- 7a. Le lien de validation expire: L'utilisateur doit recommencer l'inscription
- 7b. L'utilisateur ne reçoit pas l'email: Possibilité de renvoyer l'email de validation

---

## Cas d'utilisation 2: Jouer une partie contre un autre joueur

**Nom:** Jouer une partie contre un autre joueur
**Acteur principal:** Joueur (Inscrit ou Invité)
**Portée:** Application d'échecs en ligne
**Niveau:** But utilisateur
**Parties prenantes et intérêts:**
- Joueur 1: Souhaite jouer une partie d'échecs
- Joueur 2: Souhaite jouer une partie d'échecs
- Système: Doit gérer la partie et enregistrer les mouvements

**Préconditions:**
- Le joueur est connecté (inscrit ou invité)
- Le joueur se trouve dans une salle (entrainement ou tournois)

**Postconditions (succès):**
- La partie est terminée avec un gagnant ou un match nul
- Pour les joueurs inscrits: la partie est enregistrée dans l'historique
- Les résultats sont transmis à l'application externe pour le calcul du coefficient

**Scénario nominal:**
1. Le Joueur 1 entre dans une salle de jeu
2. Le Joueur 1 demande à jouer contre un adversaire
3. Le système recherche un adversaire disponible
4. Le système trouve le Joueur 2 qui accepte de jouer
5. Le système crée une nouvelle partie et attribue les couleurs
6. Le système affiche l'échiquier aux deux joueurs
7. Le joueur avec les pièces blanches effectue son mouvement
8. Le système vérifie la validité du mouvement
9. Le système met à jour l'échiquier pour les deux joueurs
10. Le joueur adverse effectue son mouvement
11. Les étapes 7-10 se répètent jusqu'à la fin de la partie
12. Le système détermine le résultat (victoire, défaite, match nul)
13. Le système enregistre la partie (si joueurs inscrits)
14. Le système affiche le résultat aux deux joueurs

**Scénarios alternatifs:**
- 3a. Aucun adversaire disponible: Le joueur peut attendre ou jouer contre le logiciel
- 8a. Mouvement invalide: Le système demande au joueur de refaire son mouvement
- 11a. Un joueur abandonne: L'adversaire gagne par forfait
- 11b. Un joueur se déconnecte: L'adversaire gagne par forfait après un délai
- 11c. Proposition de match nul: Les deux joueurs peuvent accepter un match nul
