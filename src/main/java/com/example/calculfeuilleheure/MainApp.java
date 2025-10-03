package com.example.calculfeuilleheure;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Classe principale de l'application JavaFX.
 * Elle initialise la fenêtre principale et charge la vue FXML.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Chargement du fichier FXML pour la vue principale
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
        Parent root = loader.load();

        // Configuration de la scène principale
        Scene scene = new Scene(root);
        primaryStage.setTitle("Calcul Feuille d'Heure");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
