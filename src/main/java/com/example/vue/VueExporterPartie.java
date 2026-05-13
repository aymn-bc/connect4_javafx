package com.example.vue;

import java.io.File;
import java.util.List;

import com.example.model.DAOPartie;
import com.example.model.GestionFichier;
import com.example.model.Partie;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class VueExporterPartie {
    public void afficher() {
        Stage stage = new Stage();
        stage.setTitle("Exporter une partie");

        DAOPartie daoPartie = new DAOPartie();
        List<Partie> parties = daoPartie.findAll();

        ListView<Partie> listView = new ListView<>(FXCollections.observableArrayList(parties));
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Partie item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String j1 = item.getJ1() == null ? "?" : item.getJ1().getNom();
                    String j2 = item.getJ2() == null ? "?" : item.getJ2().getNom();
                    setText("Partie #" + item.getId() + " - " + j1 + " vs " + j2);
                }
            }
        });

        TextField pathField = new TextField();
        pathField.setPrefWidth(300); // preferred width

        Button btnBrowse = new Button("Choisir...");
        btnBrowse.setOnAction(event -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Choisir le fichier d'export");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers texte", "*.txt")); // if not .txt it dont use it
            File file = chooser.showSaveDialog(stage);
            if (file != null) {
                pathField.setText(file.getAbsolutePath());
            }
        });

        Button btnExporter = new Button("Exporter");
        btnExporter.setOnAction(event -> {
            Partie selected = listView.getSelectionModel().getSelectedItem();
            if (selected == null) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Sélection requise");
                alert.setHeaderText("Veuillez sélectionner une partie");
                alert.showAndWait();
                return;
            }
            if (pathField.getText().isBlank()) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Chemin requis");
                alert.setHeaderText("Veuillez choisir un fichier");
                alert.showAndWait();
                return;
            }
            
            try {
                // Get full partie with coups from DB
                DAOPartie dao = new DAOPartie();
                Partie fullPartie = dao.findById(selected.getId());
                
                // Export to chosen path
                GestionFichier gf = new GestionFichier();
                
                // Determine output path (use selected path or default name if directory)
                String outputPath = pathField.getText();
                File outputFile = new File(outputPath);
                if (outputFile.isDirectory() || !outputPath.endsWith(".txt")) {
                    outputPath = outputPath + "/partie_" + fullPartie.getId() + ".txt";
                }
                
                // Write to actual file
                File targetFile = new File(outputPath);
                java.io.FileWriter fw = new java.io.FileWriter(targetFile);
                try (fw) {
                    fw.write(fullPartie.getJ1().getId() + "\n");
                    fw.write(fullPartie.getJ2().getId() + "\n");
                    fw.write(fullPartie.getScoreJ1() + "\n");
                    fw.write(fullPartie.getScoreJ2() + "\n");
                    
                    if (fullPartie.getGagnant() != null) {
                        fw.write(fullPartie.getGagnant().getId() + "\n");
                    } else {
                        fw.write("-1\n");
                    }
                    
                    fw.write(fullPartie.getLocalDate() + "\n");
                    
                    for (com.example.model.Coup c : fullPartie.getCoups()) {
                        fw.write(c.getIdPartie() + "," + c.getNumLigne() + "," + c.getNumCol() + "," + c.getIdJoueur() + "\n");
                    }
                }
                
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText("Partie exportée avec succès");
                alert.setContentText("Fichier: " + outputPath);
                alert.showAndWait();
                stage.close();
            } catch (Exception e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Impossible d'exporter la partie");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        });

        VBox root = new VBox(10);
        root.setPadding(new Insets(12));
        root.getChildren().addAll(
            new Label("Sélectionner une partie"),
            listView,
            new Label("Fichier de destination"),
            new HBox(10, pathField, btnBrowse),
            btnExporter
        );

        stage.setScene(new Scene(root, 560, 380));
        stage.show();
    }
}
