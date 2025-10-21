package com.example.calculfeuilleheure.controller;

import com.example.calculfeuilleheure.model.User;
import com.example.calculfeuilleheure.PasswordUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Contrôleur pour l'écran de connexion.
 * Gère la validation des identifiants utilisateur.
 */
public class LoginController {

    @FXML
    private TextField loginField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    private Stage primaryStage;
    private Runnable onLoginSuccess;
    private String authenticatedLogin; // Pour stocker le login après authentification

    // Instance Gson pour la sérialisation JSON
    private final Gson gson = new Gson();

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setOnLoginSuccess(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    /**
     * Gère l'événement de connexion.
     */
    @FXML
    public void handleLogin() {
        String login = loginField.getText().trim();
        String password = passwordField.getText();

        if (login.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez saisir login et mot de passe.");
            return;
        }

        // Charger les utilisateurs depuis users.json
        List<User> users = loadUsers();
        if (users == null) {
            errorLabel.setText("Erreur lors du chargement des utilisateurs.");
            return;
        }

        // Vérifier les identifiants
        boolean authenticated = users.stream()
                .anyMatch(user -> user.getLogin().equals(login) && PasswordUtils.verifyPassword(password, user.getPassword()));

        if (authenticated) {
            // Connexion réussie
            authenticatedLogin = login;
            errorLabel.setText("");
            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }
        } else {
            errorLabel.setText("Login ou mot de passe incorrect.");
        }
    }

    /**
     * Gère l'événement de création de compte.
     */
    @FXML
    public void handleCreateAccount() {
        String login = loginField.getText().trim();
        String password = passwordField.getText();

        if (login.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Veuillez saisir login et mot de passe.");
            return;
        }

        // Charger les utilisateurs existants
        List<User> users = loadUsers();

        // Vérifier si le login existe déjà
        boolean exists = users.stream().anyMatch(user -> user.getLogin().equals(login));
        if (exists) {
            errorLabel.setText("Ce login existe déjà.");
            return;
        }

        // Créer le nouvel utilisateur avec mot de passe hashé
        String hashedPassword = PasswordUtils.hashPassword(password);
        User newUser = new User(login, hashedPassword);
        users.add(newUser);

        // Sauvegarder la liste mise à jour
        saveUsers(users);

        errorLabel.setText("Compte créé avec succès ! Vous pouvez maintenant vous connecter.");
    }

    /**
     * Gère l'événement de sortie de l'application.
     */
    @FXML
    public void handleExit() {
        if (primaryStage != null) {
            primaryStage.close();
        }
    }

    /**
     * Charge la liste des utilisateurs depuis le fichier users.json.
     * @return liste des utilisateurs ou null en cas d'erreur
     */
    private List<User> loadUsers() {
        try (FileReader reader = new FileReader("users.json")) {
            Type listType = new TypeToken<List<User>>(){}.getType();
            List<User> users = gson.fromJson(reader, listType);
            return users != null ? users : new java.util.ArrayList<>();
        } catch (IOException e) {
            // Si le fichier n'existe pas ou erreur, retourner une liste vide
            return new java.util.ArrayList<>();
        }
    }

    /**
     * Sauvegarde la liste des utilisateurs dans le fichier users.json.
     * @param users liste des utilisateurs à sauvegarder
     */
    private void saveUsers(List<User> users) {
        try (java.io.FileWriter writer = new java.io.FileWriter("users.json")) {
            gson.toJson(users, writer);
        } catch (java.io.IOException e) {
            errorLabel.setText("Erreur lors de la sauvegarde du compte.");
        }
    }

    /**
     * Retourne le login authentifié.
     * @return login de l'utilisateur connecté
     */
    public String getAuthenticatedLogin() {
        return authenticatedLogin;
    }
}
