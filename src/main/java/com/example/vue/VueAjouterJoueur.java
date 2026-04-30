package com.example.vue;

import com.example.model.DAOJoueur;
import com.example.model.Joueur;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VueAjouterJoueur {

    public void afficher() {
        Stage stage = new Stage();
        stage.setTitle("Ajouter un Joueur");

        // Create GridPane
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));

        // Row 0: Nom label and text field
        Label lblNom = new Label("Nom :");
        TextField txtNom = new TextField();
        gridPane.add(lblNom, 0, 0);
        gridPane.add(txtNom, 1, 0);

        // Row 1: Message label
        Label lblMessage = new Label();
        gridPane.add(lblMessage, 0, 1);

        // Row 2: Button
        Button btnAjouter = new Button("Ajouter");
        gridPane.add(btnAjouter, 1, 2);

        // Button action
        btnAjouter.setOnAction(event -> {
            String nom = txtNom.getText().trim();
            if (nom.isEmpty()) {
                lblMessage.setText("Le nom ne peut pas être vide.");
                lblMessage.setStyle("-fx-text-fill: red;");
            } else {
                Joueur newJoueur = new Joueur(0, nom, 0);
                DAOJoueur dao = new DAOJoueur();
                if (dao.insert(newJoueur)) {
                    lblMessage.setText("Joueur ajouté avec succès !");
                    lblMessage.setStyle("-fx-text-fill: green;");
                    txtNom.clear();
                } else {
                    lblMessage.setText("Erreur lors de l'ajout.");
                    lblMessage.setStyle("-fx-text-fill: red;");
                }
            }
        });

        // Layout
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().add(gridPane);

        // Scene and Stage
        Scene scene = new Scene(vbox, 300, 200);
        stage.setScene(scene);
        stage.show();
    }
}
