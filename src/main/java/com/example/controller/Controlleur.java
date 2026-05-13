package com.example.controller;
import java.io.IOException;
import java.util.List;

import com.example.model.Coup;
import com.example.model.CoupException;
import com.example.model.DAOCoup;
import com.example.model.DAOJoueur;
import com.example.model.DAOPartie;
import com.example.model.Game;
import com.example.model.GestionFichier;
import com.example.model.Joueur;
import com.example.model.Partie;
import com.example.model.Position;
import com.example.vue.InterfaceJeuPuissance;
import com.example.vue.VueAjouterJoueur;
import com.example.vue.VueDetailsJoueur;
import com.example.vue.VueExporterPartie;
import com.example.vue.VueHistoriqueJoueur;
import com.example.vue.VueListeJoueurs;
import com.example.vue.VueListeJoueursParParties;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;

public class Controlleur {
    // ============ Fields ============
    private BorderPane fenetre = new BorderPane();
    private Partie partieJeu;
    private Button[][] tabButton;
    private InterfaceJeuPuissance interfaceJeuPuissance;
    private CheckMenuItem mnisauvPartie;
    private int gameMode = 0; // 0=PvP, 1=PvIA, 2=AIvsAI
    private boolean gameInProgress = false;

    // ============ Constructor ============
    public Controlleur() {
        fenetre.setTop(getMenu());
    }

    // ============ Getters ============
    public BorderPane getFenetre() {
        return this.fenetre;
    }

    public void gameControlleur() {
        int nbLigne = 6, nbColonne = 7;
        
        // Load players from database
        List<Joueur> joueurs = loadPlayers();
        if (joueurs == null || joueurs.size() < 2) {
            showPlayerErrorAlert();
            return;
        }
        
        // Initialize game
        Joueur jouer1 = joueurs.get(0);
        Joueur jouer2 = joueurs.get(1);
        partieJeu = new Partie(jouer1, jouer2);
        gameMode = 0; // PvP
        gameInProgress = true;
        
        // Setup UI
        setupGameUI(nbLigne, nbColonne);
    }

    /**
     * Lancer une partie Joueur vs Joueur
     */
    public void lancerPartieJoueurVsJoueur() {
        int nbLigne = 6, nbColonne = 7;
        
        List<Joueur> joueurs = loadPlayers();
        if (joueurs == null || joueurs.size() < 2) {
            showPlayerErrorAlert();
            return;
        }
        
        Joueur jouer1 = joueurs.get(0);
        Joueur jouer2 = joueurs.get(1);
        partieJeu = new Partie(jouer1, jouer2);
        gameMode = 0; // PvP
        gameInProgress = true;
        
        setupGameUI(nbLigne, nbColonne);
    }

    /**
     * Lancer une partie Joueur vs IA
     */
    public void lancerPartieJoueurVsIA() {
        int nbLigne = 6, nbColonne = 7;
        
        List<Joueur> joueurs = loadPlayers();
        if (joueurs == null || joueurs.isEmpty()) {
            showPlayerErrorAlert();
            return;
        }
        
        Joueur joueur = joueurs.get(0);
        Joueur ai = new com.example.model.JoueurAI(999, "IA - Medium", 0, 2);
        partieJeu = new Partie(joueur, ai);
        gameMode = 1; // PvIA
        gameInProgress = true;
        
        setupGameUI(nbLigne, nbColonne);
    }

    /**
     * Lancer une simulation IA vs IA
     */
    public void lancerSimulationAIvsAI() {
        int nbLigne = 6, nbColonne = 7;
        
        com.example.model.JoueurAI ai1 = new com.example.model.JoueurAI(998, "IA - Easy", 0, 1);
        com.example.model.JoueurAI ai2 = new com.example.model.JoueurAI(999, "IA - Medium", 0, 2);
        partieJeu = new Partie(ai1, ai2);
        gameMode = 2; // AIvsAI
        gameInProgress = true;
        
        setupGameUI(nbLigne, nbColonne);
        
        // Start AI simulation automatically
        startAISimulation();
    }

    // ============ Game Logic ============
    private List<Joueur> loadPlayers() {
        DAOJoueur daoJoueur = new DAOJoueur();
        return daoJoueur.findAll();
    }

    private void showPlayerErrorAlert() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Données insuffisantes");
        alert.setContentText("Au moins 2 joueurs doivent exister dans la base de données.");
        alert.showAndWait();
    }

    private void setupGameUI(int nbLigne, int nbColonne) {
        interfaceJeuPuissance = new InterfaceJeuPuissance(nbLigne, nbColonne);
        interfaceJeuPuissance.dessiner();
        fenetre.setLeft(interfaceJeuPuissance.getJoueur(partieJeu.getJ1()));
        fenetre.setRight(interfaceJeuPuissance.getJoueur(partieJeu.getJ2()));
        fenetre.setCenter(interfaceJeuPuissance.getGrilleJeu());
        tabButton = interfaceJeuPuissance.getTabButton();
        
        for (int i = 0; i < nbLigne; i++)
            for (int j = 0; j < nbColonne; j++) {
                final int numCol = j;
                tabButton[i][j].setOnAction(event -> gestionAction(numCol));
            }
    }

    private void gestionAction(int numCol) {
        // If it's AI's turn in PvIA mode, ignore player input
        if (gameMode == 1 && partieJeu.getJoueurCourant() instanceof com.example.model.JoueurAI) {
            return;
        }
        
        Game puissance = this.partieJeu.getPuissance();
        int numligne;
        try {
            numligne = puissance.getLigneVideByColonne(numCol);
            puissance.setCoup(numligne, numCol, this.partieJeu.getJoueurCourant().getId());
            
            // Record every move
            Coup coup = new Coup(0, numligne, numCol, this.partieJeu.getJoueurCourant().getId());
            this.partieJeu.ajouterCoup(coup);
            
            updateButtonColor(numligne, numCol);
            
            if (this.partieJeu.estGagnant(new Position(numligne, numCol))) {
                handleGameWin();
            } else if (this.partieJeu.estRemplie()) {
                handleGameTie();
            } else {
                this.partieJeu.modifieRole();
                
                // If next player is AI in PvIA mode, let AI play
                if (gameMode == 1 && partieJeu.getJoueurCourant() instanceof com.example.model.JoueurAI) {
                    new Thread(() -> {
                        try {
                            Thread.sleep(800);
                            javafx.application.Platform.runLater(this::playAIMove);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }).start();
                }
            }
        } catch (CoupException e) {
            showColumnFullAlert(numCol);
        }
    }

    /**
     * Play one move for AI
     */
    private void playAIMove() {
        Game puissance = this.partieJeu.getPuissance();
        com.example.model.JoueurAI ai = (com.example.model.JoueurAI) this.partieJeu.getJoueurCourant();
        
        try {
            int col = ai.choisirCoup(puissance);
            int numligne = puissance.getLigneVideByColonne(col);
            
            puissance.setCoup(numligne, col, ai.getId());
            Coup coup = new Coup(0, numligne, col, ai.getId());
            this.partieJeu.ajouterCoup(coup);
            
            updateButtonColor(numligne, col);
            
            if (this.partieJeu.estGagnant(new Position(numligne, col))) {
                handleGameWin();
            } else if (this.partieJeu.estRemplie()) {
                handleGameTie();
            } else {
                this.partieJeu.modifieRole();
            }
        } catch (CoupException e) {
            // AI stuck, try another move
            this.partieJeu.modifieRole();
        }
    }

    private void updateButtonColor(int numligne, int numCol) {
        if (this.partieJeu.getJoueurCourant().getId() == this.partieJeu.getJ1().getId())
            interfaceJeuPuissance.setCouleurButton(5 - numligne, numCol, "#FF0000");
        else
            interfaceJeuPuissance.setCouleurButton(5 - numligne, numCol, "#00FF00");
    }

    private void handleGameWin() {
        showWinAlert();
        this.partieJeu.setGagnant(this.partieJeu.getJoueurCourant());
        updateScores();
        persistGameData();
        saveGameToFileIfChecked();
        resetAndRestart();
    }

    private void showWinAlert() {
        Alert iBox = new Alert(AlertType.INFORMATION);
        iBox.setHeaderText("PARTIE FINIE");
        iBox.setContentText("Le joueur " + this.partieJeu.getJoueurCourant().getNom() + " est le gagnant");
        iBox.showAndWait();
    }

    private void updateScores() {
        if (this.partieJeu.getRolejoueur() == this.partieJeu.getJ1().getId()) {
            this.partieJeu.getJ1().incrementerScore();
            this.partieJeu.getJ2().decrementerScore();
            this.partieJeu.setScoreJ1(1);
            this.partieJeu.setScoreJ2(-1);
        } else {
            this.partieJeu.getJ2().incrementerScore();
            this.partieJeu.getJ1().decrementerScore();
            this.partieJeu.setScoreJ1(-1);
            this.partieJeu.setScoreJ2(1);
        }
    }

    private void persistGameData() {
        DAOPartie daoPartie = new DAOPartie();
        int idPartie = daoPartie.insertAndGetId(this.partieJeu);
        
        DAOCoup daoCoup = new DAOCoup();
        for (Coup c : this.partieJeu.getCoups()) {
            c.setIdPartie(idPartie);
            daoCoup.insert(c);
        }
        
        DAOJoueur daoJoueur = new DAOJoueur();
        daoJoueur.update(this.partieJeu.getJ1());
        daoJoueur.update(this.partieJeu.getJ2());
    }

    private void saveGameToFileIfChecked() {
        if (mnisauvPartie != null && mnisauvPartie.isSelected()) {
            try {
                new GestionFichier().enregistrePartie(this.partieJeu);
                showSaveSuccessAlert();
            } catch (IOException e) {
                showSaveErrorAlert(e);
            }
        }
    }

    private void showSaveSuccessAlert() {
        Alert saved = new Alert(AlertType.INFORMATION);
        saved.setTitle("Enregistrement");
        saved.setHeaderText("Partie enregistrée");
        saved.setContentText("La partie a été enregistrée dans un fichier.");
        saved.showAndWait();
    }

    private void showSaveErrorAlert(IOException e) {
        Alert err = new Alert(AlertType.ERROR);
        err.setTitle("Erreur d'enregistrement");
        err.setHeaderText("Impossible d'enregistrer la partie");
        err.setContentText(e.getMessage());
        err.showAndWait();
    }

    private void handleGameTie() {
        Alert cBox = new Alert(AlertType.CONFIRMATION, "Rejouer ?", ButtonType.YES, ButtonType.CANCEL);
        cBox.setHeaderText("PARTIE NULL");
        cBox.setContentText("La grille est remplie \n Voulez-vous rejouer?");
        cBox.showAndWait();
        
        if (cBox.getResult() == ButtonType.YES) {
            this.partieJeu.getPuissance().initialiseGrille();
            gameControlleur();
        }
    }

    /**
     * Start automatic AI vs AI simulation in the main interface
     */
    private void startAISimulation() {
        new Thread(() -> {
            try {
                Game game = partieJeu.getPuissance();
                game.initialiseGrille();
                
                com.example.model.JoueurAI ai1 = (com.example.model.JoueurAI) partieJeu.getJ1();
                com.example.model.JoueurAI ai2 = (com.example.model.JoueurAI) partieJeu.getJ2();
                
                int moveCount = 0;
                while (gameInProgress && moveCount < 100) {
                    // AI 1 plays
                    int col1 = ai1.choisirCoup(game);
                    try {
                        int ligne1 = game.getLigneVideByColonne(col1);
                        game.setCoup(ligne1, col1, ai1.getId());
                        
                        Coup coup1 = new Coup(0, ligne1, col1, ai1.getId());
                        partieJeu.ajouterCoup(coup1);
                        moveCount++;
                        
                        final int move = moveCount;
                        javafx.application.Platform.runLater(() -> updateButtonColor(ligne1, col1));
                        
                        Thread.sleep(800);
                        
                        if (game.estGagnant(new Position(ligne1, col1), ai1.getId())) {
                            javafx.application.Platform.runLater(this::handleGameWin);
                            return;
                        }
                        
                        if (game.estRemplie()) {
                            javafx.application.Platform.runLater(this::handleGameTie);
                            return;
                        }
                    } catch (CoupException e) {
                        // AI 1 stuck
                    }
                    
                    if (!gameInProgress) return;
                    
                    // AI 2 plays
                    int col2 = ai2.choisirCoup(game);
                    try {
                        int ligne2 = game.getLigneVideByColonne(col2);
                        game.setCoup(ligne2, col2, ai2.getId());
                        
                        Coup coup2 = new Coup(0, ligne2, col2, ai2.getId());
                        partieJeu.ajouterCoup(coup2);
                        moveCount++;
                        
                        final int move = moveCount;
                        javafx.application.Platform.runLater(() -> updateButtonColor(ligne2, col2));
                        
                        Thread.sleep(800);
                        
                        if (game.estGagnant(new Position(ligne2, col2), ai2.getId())) {
                            javafx.application.Platform.runLater(this::handleGameWin);
                            return;
                        }
                        
                        if (game.estRemplie()) {
                            javafx.application.Platform.runLater(this::handleGameTie);
                            return;
                        }
                    } catch (CoupException e) {
                        // AI 2 stuck
                    }
                }
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
    private void resetAndRestart() {
        this.partieJeu.initialiseGrille();
        gameControlleur();
    }

    private void showColumnFullAlert(int numCol) {
        Alert xBox = new Alert(AlertType.ERROR);
        xBox.setHeaderText("La colonne " + numCol + " est remplie");
        xBox.showAndWait();
    }

    // ============ Menus ============
    public MenuBar getMenu() {
        MenuBar mBar = new MenuBar();

        Menu mnuPartie = new Menu("Partie");
        Menu mnuClassement = new Menu("Classement");
        Menu mnuJoueur = new Menu("Joueur");
        Menu lancerPartie  = new Menu("Lancer Partie");

        MenuItem mniQuitter = new MenuItem("Quitter");
        MenuItem mniLancerPartie = new MenuItem("Joueur vs Joueur");
        MenuItem mniLancerPartieIA = new MenuItem("Joueur vs IA");
        MenuItem mniSimulerAIvsAI = new MenuItem("IA vs IA (Simulation)");
        Menu mnuImportExport = new Menu("Importer/Exporter une partie");
        MenuItem mniImporter = new MenuItem("Importer une partie");
        MenuItem mniExporter = new MenuItem("Exporter une partie");
        mnisauvPartie = new CheckMenuItem("Enregistrer la partie");

        lancerPartie.getItems().addAll(mniLancerPartie, mniLancerPartieIA, mniSimulerAIvsAI);
        mnuImportExport.getItems().addAll(mniImporter, mniExporter);

        mnuPartie.getItems().addAll(lancerPartie, mnuImportExport);
        mnuPartie.getItems().add(new javafx.scene.control.SeparatorMenuItem());
        mnuPartie.getItems().addAll(mnisauvPartie, mniQuitter);

        mniLancerPartie.setOnAction(event -> lancerPartieJoueurVsJoueur());
        mniLancerPartieIA.setOnAction(event -> lancerPartieJoueurVsIA());
        mniSimulerAIvsAI.setOnAction(event -> lancerSimulationAIvsAI());
        mniImporter.setOnAction(event -> new com.example.vue.VueImporterPartie().afficher());
        mniExporter.setOnAction(event -> new VueExporterPartie().afficher());
        mniQuitter.setOnAction(event -> Platform.exit());

        MenuItem mniListeJoueurs = new MenuItem("Liste des joueurs");
        MenuItem mniListeParNbParties = new MenuItem("Liste par nombre de parties jouées");
        mnuClassement.getItems().addAll(mniListeJoueurs, mniListeParNbParties);

        mniListeJoueurs.setOnAction(event -> new VueListeJoueurs().afficher());
        mniListeParNbParties.setOnAction(event -> new VueListeJoueursParParties().afficher());

        MenuItem mniAjouterJoueur = new MenuItem("Ajouter Joueur");
        MenuItem mniDetailsJoueur = new MenuItem("Détails joueurs");
        MenuItem mniHistoriqueJoueur = new MenuItem("Historique du joueur");

        mnuJoueur.getItems().addAll(mniAjouterJoueur, mniDetailsJoueur, mniHistoriqueJoueur);

        mniAjouterJoueur.setOnAction(event -> new VueAjouterJoueur().afficher());

        mniDetailsJoueur.setOnAction(event -> {
            VueListeJoueurs vue = new VueListeJoueurs();
            vue.afficher();
            vue.getTableView().setOnMouseClicked(e -> {
                Joueur selected = vue.getTableView().getSelectionModel().getSelectedItem();
                if (selected != null) new VueDetailsJoueur().afficher(selected);
            });
        });

        mniHistoriqueJoueur.setOnAction(event -> {
            VueListeJoueurs vue = new VueListeJoueurs();
            vue.afficher();
            vue.getTableView().setOnMouseClicked(e -> {
                Joueur selected = vue.getTableView().getSelectionModel().getSelectedItem();
                if (selected != null) new VueHistoriqueJoueur().afficher(selected);
            });
        });

        mBar.getMenus().addAll(mnuPartie, mnuClassement, mnuJoueur);
        return mBar;
    }

    // ============ Utility Methods ============
}