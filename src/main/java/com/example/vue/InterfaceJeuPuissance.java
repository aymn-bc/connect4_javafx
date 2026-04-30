package com.example.vue;

import com.example.model.Joueur;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class InterfaceJeuPuissance {
	private final GridPane grilleJeu = new GridPane();
	private final int nbLigne, nbColonne;
	private final Button[][] tabButton;
	public InterfaceJeuPuissance(int nbLigne, int nbColonne) {
		this.nbLigne = nbLigne;
		this.nbColonne = nbColonne;
		tabButton=new Button[nbLigne][nbColonne];
	}

	public void dessiner() {
		for (int i = 0; i < nbLigne; i++){
			for (int j = 0; j < nbColonne; j++) {
				tabButton[i][j] = new Button("" + i + " " + j);
				grilleJeu.add(tabButton[i][j] , j, i);
				setCouleurButton(i,j, "#aaaaaa");
			}		
		}
	}
    
    public Node getJoueur(Joueur j){
    	VBox vbox= new 	VBox();
    	HBox hBoxNom= new HBox();
      	HBox hBoxScore= new HBox();
      	Label ltextNom= new Label ("Nom : ");
      	Label ltextScore= new Label ("Score : ");
    	Label ljnom= new Label (j.getNom());
       	Label lscore=new Label (""+j.getScore());
       	hBoxNom.getChildren().addAll(ltextNom,ljnom);
       	hBoxScore.getChildren().addAll(ltextScore,lscore);
       	vbox.getChildren().addAll(hBoxNom,hBoxScore);
    	return vbox;
    }
	
	public void setCouleurButton(int numLigne,int numColonne, String couleur) {
		tabButton[numLigne][numColonne].setStyle("-fx-background-radius: 150em; " +
                "-fx-min-width: 50px;" +
	                "-fx-min-height: 50px; " +
	                "-fx-max-width: 50px; " +
                "-fx-max-height: 50px;"+
             "-fx-background-color:"+couleur+";");
	}
	//
	public GridPane getGrilleJeu() {
		return grilleJeu;
	}
	 public Button[][] getTabButton(){
	    	return this.tabButton;
	}
}
