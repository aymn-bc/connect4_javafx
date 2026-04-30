package com.example.controller;
import java.util.ArrayList;
import java.util.List;

import com.example.model.Coup;
import com.example.model.CoupException;
import com.example.model.CritereSelection;
import com.example.model.DAOCoup;
import com.example.model.DAOJoueur;
import com.example.model.DAOPartie;
import com.example.model.Game;
import com.example.model.Joueur;
import com.example.model.Partie;
import com.example.model.Position;
import com.example.vue.InterfaceJeuPuissance;
import com.example.vue.VueAjouterJoueur;
import com.example.vue.VueDetailsJoueur;
import com.example.vue.VueExporterPartie;
import com.example.vue.VueHistoriqueJoueur;
import com.example.vue.VueImporterPartie;
import com.example.vue.VueListeJoueurs;
import com.example.vue.VueListeJoueursParParties;
import com.example.vue.VueSimulerPartie;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;

public class Controlleur {
    BorderPane fenetre = new BorderPane();
    Partie partieJeu;
    Button[][] tabButton;
    InterfaceJeuPuissance interfaceJeuPuissance;

    public Controlleur() {
        fenetre.setTop(getMenu());
    }

    public void gameControlleur() {
        int nbLigne = 6, nbColonne = 7;
        
        // Load players from database
        DAOJoueur daoJoueur = new DAOJoueur();
        List<Joueur> joueurs = daoJoueur.findAll();
        
        if (joueurs.size() < 2) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Données insuffisantes");
            alert.setContentText("Au moins 2 joueurs doivent exister dans la base de données.");
            alert.showAndWait();
            return;
        }
        
        Joueur jouer1 = joueurs.get(0);
        Joueur jouer2 = joueurs.get(1);
        partieJeu = new Partie(jouer1, jouer2);
        interfaceJeuPuissance = new InterfaceJeuPuissance(nbLigne, nbColonne);
        interfaceJeuPuissance.dessiner();
        fenetre.setLeft(interfaceJeuPuissance.getJoueur(jouer1));
        fenetre.setRight(interfaceJeuPuissance.getJoueur(jouer2));
        fenetre.setCenter(interfaceJeuPuissance.getGrilleJeu());
		fenetre.setTop(getMenu());
        tabButton = interfaceJeuPuissance.getTabButton();
        for (int i = 0; i < nbLigne; i++)
            for (int j = 0; j < nbColonne; j++) {
                final int numCol = j;
                tabButton[i][j].setOnAction(event -> {
                    gestionAction(numCol);
                });
            }
    }

    private void gestionAction(int numCol) {
        Game puissance = this.partieJeu.getPuissance();
        int numligne;
        try {
            numligne = puissance.getLigneVideByColonne(numCol);
            puissance.setCoup(numligne, numCol, this.partieJeu.getJoueurCourant().getId());
            
            // Record every move
            Coup coup = new Coup(0, numligne, numCol,
                this.partieJeu.getJoueurCourant().getId());
            this.partieJeu.ajouterCoup(coup);
            
            if (this.partieJeu.getJoueurCourant().getId() == this.partieJeu.getJ1().getId())
                interfaceJeuPuissance.setCouleurButton(5 - numligne, numCol, "#FF0000");
            else
                interfaceJeuPuissance.setCouleurButton(5 - numligne, numCol, "#00FF00");
            if (this.partieJeu.estGagnant(new Position(numligne, numCol))) {
                Alert iBox = new Alert(AlertType.INFORMATION);
                iBox.setHeaderText("PARTIE FINIE");
                iBox.setContentText("Le joueur " + this.partieJeu.getJoueurCourant().getNom() + " est le gagnant");
                iBox.showAndWait();
                
                // Set winner
                this.partieJeu.setGagnant(this.partieJeu.getJoueurCourant());
                
                if (this.partieJeu.getRolejoueur() == this.partieJeu.getJ1().getId()) {
                    this.partieJeu.getJ1().incrementerScore();
                    this.partieJeu.getJ2().decrementerScore();
                    this.partieJeu.setScoreJ1(10);
                    this.partieJeu.setScoreJ2(-10);
                } else {
                    this.partieJeu.getJ2().incrementerScore();
                    this.partieJeu.getJ1().decrementerScore();
                    this.partieJeu.setScoreJ1(-10);
                    this.partieJeu.setScoreJ2(10);
                }
                
                // Persist partie and coups
                DAOPartie daoPartie = new DAOPartie();
                int idPartie = daoPartie.insertAndGetId(this.partieJeu);
                DAOCoup daoCoup = new DAOCoup();
                for (Coup c : this.partieJeu.getCoups()) {
                    c.setIdPartie(idPartie);
                    daoCoup.insert(c);
                }
                
                // Persist updated scores
                DAOJoueur daoJoueur = new DAOJoueur();
                daoJoueur.update(this.partieJeu.getJ1());
                daoJoueur.update(this.partieJeu.getJ2());
                
                this.partieJeu.initialiseGrille();
                gameControlleur();
            } else {
                if (this.partieJeu.estRemplie()) {
                    Alert cBox = new Alert(AlertType.CONFIRMATION, "Rejouer ?", ButtonType.YES, ButtonType.CANCEL);
                    cBox.setHeaderText("PARTIE NULL");
                    cBox.setContentText("La grille est remplie \n Voulez-vous rejouer?");
                    cBox.showAndWait();
                    if (cBox.getResult() == ButtonType.YES) {
                        this.partieJeu.getPuissance().initialiseGrille();
                        gameControlleur();
                    }
                } else
                    this.partieJeu.modifieRole();
            }
        } catch (CoupException e) {
            Alert xBox = new Alert(AlertType.ERROR);
            xBox.setHeaderText("La colonne " + numCol + " est remplie");
            xBox.showAndWait();
        }
    }

    public BorderPane getFenetre() {
        return this.fenetre;
    }

    public MenuBar getMenu() {
        MenuBar mBar = new MenuBar();

        Menu mnuPartie     = new Menu("Partie");
        Menu mnuClassement = new Menu("Classement");
        Menu mnuJoueur     = new Menu("Joueur");
        MenuItem mniQuitter = new MenuItem("Quitter");


        MenuItem mniLancerPartie  = new MenuItem("Lancer une partie");
        MenuItem mniSimulerPartie = new MenuItem("Simuler une partie");
        Menu     mnuImportExport  = new Menu("Importer/Exporter une partie");
        MenuItem mniImporter      = new MenuItem("Importer une partie");
        MenuItem mniExporter      = new MenuItem("Exporter une partie");

        mnuImportExport.getItems().addAll(mniImporter, mniExporter);
        mnuPartie.getItems().addAll(mniLancerPartie, mniSimulerPartie, mnuImportExport);

        mniLancerPartie.setOnAction(event -> gameControlleur());
        mniSimulerPartie.setOnAction(event -> {
            new VueSimulerPartie().afficher();
        });
        
        // mniImporter.setOnAction(event -> {
        //     new VueImporterPartie().afficher();
        // });
        mniExporter.setOnAction(event -> {
            new VueExporterPartie().afficher();
        });

        MenuItem mniListeJoueurs      = new MenuItem("Liste des joueurs");
        MenuItem mniListeParNbParties = new MenuItem("Liste par nombre de parties jouées");

        mnuClassement.getItems().addAll(mniListeJoueurs, mniListeParNbParties);

        mniListeJoueurs.setOnAction(event -> {
            new VueListeJoueurs().afficher();
        });
        mniListeParNbParties.setOnAction(event -> {
            new VueListeJoueursParParties().afficher();
        });

        MenuItem mniAjouterJoueur    = new MenuItem("Ajouter Joueur");
        MenuItem mniDetailsJoueur    = new MenuItem("Détails joueurs");
        MenuItem mniHistoriqueJoueur = new MenuItem("Historique du joueur");

        mnuJoueur.getItems().addAll(mniAjouterJoueur, mniDetailsJoueur, mniHistoriqueJoueur);

        mniAjouterJoueur.setOnAction(event -> {
            new VueAjouterJoueur().afficher();
        });
        mniDetailsJoueur.setOnAction(event -> {
            VueListeJoueurs vue = new VueListeJoueurs();
            vue.afficher();
            vue.getTableView().setOnMouseClicked(e -> {
                Joueur selected = vue.getTableView().getSelectionModel().getSelectedItem();
                if (selected != null) {
                    new VueDetailsJoueur().afficher(selected);
                }
            });
        });
        mniHistoriqueJoueur.setOnAction(event -> {
            VueListeJoueurs vue = new VueListeJoueurs();
            vue.afficher();
            vue.getTableView().setOnMouseClicked(e -> {
                Joueur selected = vue.getTableView().getSelectionModel().getSelectedItem();
                if (selected != null) {
                    new VueHistoriqueJoueur().afficher(selected);
                }
            });
        });

        mniQuitter.setOnAction(event -> Platform.exit());
        mBar.getMenus().add(mnuPartie);
        mBar.getMenus().add(mnuClassement);
        mBar.getMenus().add(mnuJoueur);
        mnuPartie.getItems().add(mniQuitter);

        return mBar;
    }

    public static <T> List<T> verifierSi(List<T> source, CritereSelection<T> critere) {
        List<T> l = new ArrayList<>();
        for (int i = 0; i < source.size(); i++) {
            if (critere.verifier(source.get(i))) {
                l.add(source.get(i));
            }
        }
        return l;
    }
}