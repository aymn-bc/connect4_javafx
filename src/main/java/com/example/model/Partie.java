package com.example.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Partie {
	private Joueur j1, j2;
	private Joueur gagnant;
	private ArrayList<Coup> lisCoupJ = new ArrayList();
	private List<Coup> coups = new ArrayList<>();
	private int nbJetonJ1 = 21;
	private int nbJetonJ2 = 21;
	private int scorej1, scorej2;
	private int rolejoueur;
	private Game game;
	private LocalDate localDate;

	public Partie() {
		GestionJoueur gestionJoueur = new GestionJoueur();
		List<Joueur> listeJoueur = gestionJoueur.getList();
		j1 = listeJoueur.get(1);
		j2 = listeJoueur.get(2);
		this.rolejoueur = j1.getId();
		game = new Game(j1.getId(), j2.getId());
		localDate = LocalDate.now();

	}
	public Partie(Joueur j1, Joueur j2) {
		this.j1 = j1;
		this.j2 = j2;
		this.rolejoueur = j1.getId();
		game = new Game(j1.getId(), j2.getId());
	}

	private Position coupJoueur() throws CoupException {
		System.out.print("\nJoueur : " + this.getJoueurCourant().getNom() + "\n");
		int numColonne = getJoueurCourant().ChoisierCoup();
		int numLigne = game.getLigneVideByColonne(numColonne);
		if (numLigne == -1)
			return new Position(-1, numColonne);
		this.game.setCoup(numLigne, numColonne, rolejoueur);
		System.out.print(game);
		return new Position(numLigne, numColonne);
	}

	private void partieNull() {
		System.out
				.print("\n" + "La partie est null entre le joueur : " + j1.getNom() + " et le joueur: " + j2.getNom());
	}

	private void gagnant() {
		if (getRolejoueur() == j1.getId()) {
			System.out.print("\n" + "Gagnant : " + j1.getNom());
			j1.incrementerScore();
			j2.decrementerScore();
		} else {
			System.out.print("\n" + "Gagnant : " + j2.getNom());
			j2.incrementerScore();
			j1.decrementerScore();
		}
	}

	
	public Joueur getJoueurCourant() {
		if (this.rolejoueur == this.j1.getId())
			return j1;
		else
			return j2;
	}

	public void modifieRole() {
		if (this.rolejoueur == this.j1.getId())
			this.rolejoueur = this.j2.getId();
		else
			this.rolejoueur = this.j1.getId();
	}
	///
	public int getLigneVideByColonne(int jj) throws CoupException {	
		return this.game.getLigneVideByColonne(jj);
	}

	public boolean estGagnant(Position position) {
		return this.game.estGagnant(position, rolejoueur);
	}

	public void initialiseGrille() {
		this.game.initialiseGrille();		
	}

	public boolean estRemplie() {
		return this.game.estRemplie();
	}
	public ArrayList<Coup> getLisCoupJ() {
		return lisCoupJ;
	}
///////////////////////
	public int getRolejoueur() {
		return this.rolejoueur;
	}

	public int getScoreJ2() {
		return scorej2;
	}

	public void setScoreJ2(int scorej2) {
		this.scorej2 = scorej2;
	}

	public int getScoreJ1() {
		return scorej1;
	}

	public void setScoreJ1(int scorej1) {
		this.scorej1 = scorej1;
	}
	public int getNbJetonJ1() {
		return nbJetonJ1;
	}

	public void setNbJetonJ1(int nbPionJ1) {
		this.nbJetonJ1 = nbPionJ1;
	}

	public int getNbJetonJ2() {
		return nbJetonJ2;
	}

	public void setNbJetonJ2(int nbPionJ2) {
		this.nbJetonJ2 = nbPionJ2;
	}

	public Game getPuissance() {
		return game;
	}

	public void setPuissance(Game p) {
		this.game = p;
	}

	public Joueur getJ1() {
		return j1;
	}

	public void setJ1(Joueur j1) {
		this.j1 = j1;
	}

	public Joueur getJ2() {
		return j2;
	}

	public void setJ2(Joueur j2) {
		this.j2 = j2;
	}

    public LocalDate getLocalDate() {
        return localDate;
    }

	public void ajouterCoup(Coup c) {
		coups.add(c);
	}

	public List<Coup> getCoups() {
		return coups;
	}

	public Joueur getGagnant() {
		return gagnant;
	}

	public void setGagnant(Joueur gagnant) {
		this.gagnant = gagnant;
	}
}
