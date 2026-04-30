package com.example.vue;

import java.util.Comparator;
import java.util.List;

import com.example.model.DAOJoueur;
import com.example.model.Joueur;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VueListeJoueursParParties {

    public void afficher() {
        Stage stage = new Stage();
        stage.setTitle("Classement par parties jouées");

        // Load players from database
        DAOJoueur dao = new DAOJoueur();
        List<Joueur> joueurs = dao.findAll();

        // Sort by score descending
        joueurs.sort(Comparator.comparingInt(Joueur::getScore).reversed());
        ObservableList<Joueur> observableList = FXCollections.observableArrayList(joueurs);

        // Create TableView
        TableView<Joueur> tableView = new TableView<>(observableList);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Column: ID
        TableColumn<Joueur, Number> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()));

        // Column: Nom
        TableColumn<Joueur, String> colNom = new TableColumn<>("Nom");
        colNom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));

        // Column: Score
        TableColumn<Joueur, Number> colScore = new TableColumn<>("Score");
        colScore.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getScore()));

        tableView.getColumns().addAll(colId, colNom, colScore);

        // Layout
        VBox vbox = new VBox(10);
        vbox.setPadding(new javafx.geometry.Insets(10));
        vbox.getChildren().add(tableView);

        // Scene and Stage
        Scene scene = new Scene(vbox, 400, 300);
        stage.setScene(scene);
        stage.show();
    }
}
