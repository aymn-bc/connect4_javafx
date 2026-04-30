// package com.example.vue;

// import javafx.geometry.Insets;
// import javafx.scene.Scene;
// import javafx.scene.control.Alert;
// import javafx.scene.control.Alert.AlertType;
// import javafx.scene.control.Button;
// import javafx.scene.control.Label;
// import javafx.scene.control.TextField;
// import javafx.scene.layout.HBox;
// import javafx.scene.layout.VBox;
// import javafx.stage.Stage;


// public class VueImporterPartie {
//     public void afficher() {
//         Stage stage = new Stage();
//         stage.setTitle("Importer une partie");

//         TextField pathField = new TextField();
//         pathField.setPrefWidth(300);

//         Button btnImporter = new Button("Importer");
//         btnImporter.setOnAction(event -> {
//             if (pathField.getText().isBlank()) {
//                 Alert alert = new Alert(AlertType.WARNING);
//                 alert.setTitle("Chemin requis");
//                 alert.setHeaderText("Veuillez choisir un fichier");
//                 alert.showAndWait();
//                 return;
//             }
//             Alert alert = new Alert(AlertType.INFORMATION);
//             alert.setTitle("Import");
//             alert.setHeaderText("Import non implémenté");
//             alert.setContentText("Fichier: " + pathField.getText());
//             alert.showAndWait();
//         });

//         VBox root = new VBox(10);
//         root.setPadding(new Insets(12));
//         root.getChildren().addAll(
//             new Label("Fichier de partie"),
//             new HBox(10, pathField),
//             btnImporter
//         );

//         stage.setScene(new Scene(root, 520, 160));
//         stage.show();
//     }
// }
