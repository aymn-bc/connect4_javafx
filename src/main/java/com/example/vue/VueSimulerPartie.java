package com.example.vue;

import java.util.List;

import com.example.model.DAOJoueur;
import com.example.model.Joueur;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class VueSimulerPartie {
    public void afficher() {
        Stage stage = new Stage();
        stage.setTitle("Simuler une partie");

        DAOJoueur dao = new DAOJoueur();
        List<Joueur> joueurs = dao.findAll();
        ObservableList<Joueur> data = FXCollections.observableArrayList(joueurs);

        ComboBox<Joueur> cbJ1 = new ComboBox<>(data);
        ComboBox<Joueur> cbJ2 = new ComboBox<>(data); //drop down
        cbJ1.setPromptText("Joueur 1");
        cbJ2.setPromptText("Joueur 2");

        StringConverter<Joueur> converter = new StringConverter<>() {
            @Override
            public String toString(Joueur joueur) {
                return joueur == null ? "" : joueur.getNom();
            }

            @Override
            public Joueur fromString(String string) {
                return null;
            }
        };
        cbJ1.setConverter(converter);
        cbJ2.setConverter(converter);

        Button btnSimuler = new Button("Simuler");
        btnSimuler.setOnAction(event -> {
            Joueur j1 = cbJ1.getValue();
            Joueur j2 = cbJ2.getValue();
            if (j1 == null || j2 == null) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Sélection requise");
                alert.setHeaderText("Veuillez sélectionner deux joueurs");
                alert.showAndWait();
                return;
            }
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Simulation");
            alert.setHeaderText("Simulation non implémentée");
            alert.setContentText("Vous avez sélectionné: " + j1.getNom() + " vs " + j2.getNom());
            alert.showAndWait();
        });

        VBox root = new VBox(10);
        root.setPadding(new Insets(12));
        root.getChildren().addAll(
            new Label("Choisir les joueurs"),
            new HBox(10, cbJ1, cbJ2),
            btnSimuler
        );

        stage.setScene(new Scene(root, 420, 180));
        stage.show();
    }
}
