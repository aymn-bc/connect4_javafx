package com.example.vue;

import java.io.File;

import com.example.model.Game;
import com.example.model.GestionFichier;
import com.example.model.Partie;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class VueImporterPartie {
    private Stage stage;
    private BorderPane mainLayout;
    private Scene scene;
    
    public void afficher() {
        stage = new Stage();
        stage.setTitle("Importer une partie");
        
        mainLayout = new BorderPane();
        
        // Create import panel
        VBox importPanel = createImportPanel();
        mainLayout.setCenter(importPanel);
        
        scene = new Scene(mainLayout, 700, 550);
        stage.setScene(scene);
        stage.show();
    }
    
    private VBox createImportPanel() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(12));
        root.setStyle("-fx-alignment: center;");

        TextField pathField = new TextField();
        pathField.setPrefWidth(300);
        pathField.setPromptText("Sélectionnez un fichier de partie...");

        Button btnBrowse = new Button("Parcourir...");
        btnBrowse.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choisir un fichier de partie");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers texte", "*.txt"));
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                pathField.setText(selectedFile.getAbsolutePath());
            }
        });

        Button btnImporter = new Button("Importer");
        btnImporter.setPrefWidth(100);
        btnImporter.setOnAction(event -> {
            if (pathField.getText().isBlank()) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Chemin requis");
                alert.setHeaderText("Veuillez choisir un fichier");
                alert.showAndWait();
                return;
            }
            
            try {
                GestionFichier gf = new GestionFichier();
                Partie partie = gf.importerPartie(pathField.getText());
                
                // Display the imported game in the same stage
                displayImportedGame(partie);
                
            } catch (Exception e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Impossible d'importer la partie");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        });

        HBox browseBox = new HBox(10);
        browseBox.setStyle("-fx-alignment: center;");
        browseBox.getChildren().addAll(pathField, btnBrowse);
        
        Label titleLabel = new Label("Importer une partie");
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        root.getChildren().addAll(
            titleLabel,
            new Label("Fichier de partie:"),
            browseBox,
            btnImporter
        );

        return root;
    }
    
    private void displayImportedGame(Partie partie) {
        // Clear the main layout and show the game
        mainLayout.setCenter(null);
        mainLayout.setRight(null);
        
        stage.setTitle("Partie importée - " + partie.getJ1().getNom() + " vs " + partie.getJ2().getNom());
        
        // Create the game interface
        Game game = partie.getPuissance();
        InterfaceJeuPuissance interfaceJeu = new InterfaceJeuPuissance(game.getNbLigne(), game.getNbColonne());
        interfaceJeu.dessiner();
        
        // Update board colors based on game state
        for (int i = 0; i < game.getNbLigne(); i++) {
            for (int j = 0; j < game.getNbColonne(); j++) {
                int valeur = game.getValeurPosition(i, j);
                if (valeur == partie.getJ1().getId()) {
                    interfaceJeu.setCouleurButton(game.getNbLigne() - i - 1, j, "#FF6B6B"); // Red for player 1
                } else if (valeur == partie.getJ2().getId()) {
                    interfaceJeu.setCouleurButton(game.getNbLigne() - i - 1, j, "#FFD93D"); // Yellow for player 2
                } else {
                    interfaceJeu.setCouleurButton(game.getNbLigne() - i - 1, j, "#aaaaaa"); // Gray for empty
                }
            }
        }
        
        // Create info panel
        VBox infoPanel = new VBox(10);
        infoPanel.setPadding(new Insets(12));
        infoPanel.setStyle("-fx-border-color: #cccccc;");
        
        Label title = new Label("Résumé de la partie");
        title.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
        
        Label j1Info = new Label("Joueur 1: " + partie.getJ1().getNom() + " (Score: " + partie.getScoreJ1() + ")");
        Label j2Info = new Label("Joueur 2: " + partie.getJ2().getNom() + " (Score: " + partie.getScoreJ2() + ")");
        Label coupInfo = new Label("Nombre de coups: " + partie.getCoups().size());
        
        String gagnantText = "Résultat: ";
        if (partie.getGagnant() != null) {
            gagnantText += partie.getGagnant().getNom() + " a gagné!";
        } else {
            gagnantText += "Aucun gagnant";
        }
        Label resultatInfo = new Label(gagnantText);
        resultatInfo.setStyle("-fx-font-weight: bold;");
        
        // Create back button
        Button btnBack = new Button("← Retour");
        btnBack.setOnAction(event -> {
            mainLayout.setCenter(createImportPanel());
            mainLayout.setRight(null);
            stage.setTitle("Importer une partie");
        });
        
        infoPanel.getChildren().addAll(title, j1Info, j2Info, coupInfo, resultatInfo, new Label(""), btnBack);
        
        // Set the game board and info in the main layout
        mainLayout.setCenter(interfaceJeu.getGrilleJeu());
        mainLayout.setRight(infoPanel);
    }
}
