package com.example.calculfeuilleheure;

import com.example.calculfeuilleheure.controller.LoginController;
import com.example.calculfeuilleheure.controller.MainController;
import com.example.calculfeuilleheure.model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principale de l'application JavaFX.
 * Elle gère la navigation entre l'écran de connexion et l'écran principal.
 */
public class MainApp extends Application {

    private Stage primaryStage;
    private User currentUser;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Calcul Feuille d'Heure");

        // Démarrer avec l'écran de connexion
        showLoginScreen();
    }

    /**
     * Affiche l'écran de connexion.
     */
    private void showLoginScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
        Parent root = loader.load();

        LoginController loginController = loader.getController();
        loginController.setPrimaryStage(primaryStage);
        loginController.setOnLoginSuccess(() -> {
            try {
                // Récupérer le login authentifié
                String login = loginController.getAuthenticatedLogin();
                currentUser = new User(login, ""); // Mot de passe non stocké ici
                showMainScreen();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Scene scene = new Scene(root, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen(); // Centrer la fenêtre sur l'écran
        primaryStage.show();
    }

    /**
     * Affiche l'écran principal après connexion.
     */
    private void showMainScreen() throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
        Parent root = loader.load();

        MainController mainController = loader.getController();
        mainController.setCurrentUser(currentUser); // Passer l'utilisateur au contrôleur principal

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen(); // Centrer la fenêtre sur l'écran
    }

    public static void main(String[] args) {
        launch(args);
    }
}
