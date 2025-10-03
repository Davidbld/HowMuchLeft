# Calcul Feuille d'Heure

Ce projet est une application JavaFX permettant de gérer et calculer des feuilles d'heures.

## Prérequis

- Java 17
- Maven
- Connexion internet pour télécharger les dépendances lors de la première compilation

## Compilation

Pour compiler le projet, exécutez la commande suivante dans le répertoire racine du projet :

```bash
mvn clean install
```

Cette commande compile le code source, exécute les tests et génère le fichier JAR dans le dossier `target`.

## Exécution

Pour lancer l'application JavaFX, utilisez la commande Maven suivante :

```bash
mvn javafx:run
```

Cela démarre l'application avec la classe principale `com.example.calculfeuilleheure.MainApp`.

## Tests

Pour exécuter les tests unitaires JUnit, utilisez la commande :

```bash
mvn test
```

Les rapports de tests seront générés dans le dossier `target/surefire-reports`.

## Structure du projet

- `src/main/java` : code source Java
- `src/main/resources` : ressources (fichiers FXML, etc.)
- `src/test/java` : tests unitaires
- `target` : fichiers compilés et générés

---

Pour toute question ou problème, merci de contacter le développeur du projet.
