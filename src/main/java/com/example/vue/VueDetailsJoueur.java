package com.example.vue;

import com.example.model.Joueur;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class VueDetailsJoueur {

    public void afficher(Joueur joueur) {
        Stage stage = new Stage();
        stage.setTitle("Détails du Joueur");

        // Create GridPane
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));

        // Row 0: ID
        Label lblIdLabel = new Label("ID :");
        Label lblId = new Label(String.valueOf(joueur.getId()));
        gridPane.add(lblIdLabel, 0, 0);
        gridPane.add(lblId, 1, 0);

        // Row 1: Nom
        Label lblNomLabel = new Label("Nom :");
        Label lblNom = new Label(joueur.getNom());
        gridPane.add(lblNomLabel, 0, 1);
        gridPane.add(lblNom, 1, 1);

        // Row 2: Score
        Label lblScoreLabel = new Label("Score :");
        Label lblScore = new Label(String.valueOf(joueur.getScore()));
        gridPane.add(lblScoreLabel, 0, 2);
        gridPane.add(lblScore, 1, 2);

        // Scene and Stage
        Scene scene = new Scene(gridPane, 300, 200);
        stage.setScene(scene);
        stage.show();
    }
}
