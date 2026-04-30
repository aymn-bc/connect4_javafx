package com.example.vue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.model.ConnexionDB;
import com.example.model.Joueur;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class VueHistoriqueJoueur {
    private static final Logger LOGGER = Logger.getLogger(VueHistoriqueJoueur.class.getName());

    public void afficher(Joueur joueur) {
        Stage stage = new Stage();
        stage.setTitle("Historique de " + joueur.getNom());

        // Header label
        Label lblHeader = new Label("Parties jouées par " + joueur.getNom() + ":");

        // Load games from database
        List<String> parties = chargerHistorique(joueur.getId());

        // Create ListView
        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(parties);

        // Layout
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(lblHeader, listView);

        // Scene and Stage
        Scene scene = new Scene(vbox, 600, 400);
        stage.setScene(scene);
        stage.show();
    }

    private List<String> chargerHistorique(int idJoueur) {
        List<String> parties = new ArrayList<>();
        Connection connexion = ConnexionDB.getInstance().getConnexion();

        if (connexion == null) {
            LOGGER.log(Level.WARNING, "Database connection is null - returning empty historique.");
            return parties;
        }

        String sql = "SELECT p.id, j1.nom, j2.nom, g.nom, p.date_partie " +
                     "FROM partie p " +
                     "JOIN joueur j1 ON p.id_joueur1 = j1.id " +
                     "JOIN joueur j2 ON p.id_joueur2 = j2.id " +
                     "LEFT JOIN joueur g ON p.id_gagnant = g.id " +
                     "WHERE p.id_joueur1 = ? OR p.id_joueur2 = ?";

        try (PreparedStatement ps = connexion.prepareStatement(sql)) {
            ps.setInt(1, idJoueur);
            ps.setInt(2, idJoueur);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int partieId = rs.getInt("id");
                    String j1Nom = rs.getString("j1.nom");
                    String j2Nom = rs.getString("j2.nom");
                    String gagnantNom = rs.getString("g.nom");
                    if (gagnantNom == null) {
                        gagnantNom = "Nul";
                    }
                    String datePartie = rs.getString("date_partie");

                    String ligne = String.format("Partie #%d | %s vs %s | Gagnant: %s | Date: %s",
                            partieId, j1Nom, j2Nom, gagnantNom, datePartie);
                    parties.add(ligne);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading historique: " + e.getMessage(), e);
        }

        return parties;
    }
}
