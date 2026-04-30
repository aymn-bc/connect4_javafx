package com.example.model;

public class Coup {
	private int idPartie;
	private int numLigne;
	private int numCol;
	private int idJoueur;

	public Coup(int idPartie, int numLigne, int numCol, int idJoueur) {
		this.idPartie = idPartie;
		this.numLigne = numLigne;
		this.numCol = numCol;
		this.idJoueur = idJoueur;
	}

	public int getIdPartie() {
		return idPartie;
	}

	public void setIdPartie(int idPartie) {
		this.idPartie = idPartie;
	}

	public int getNumLigne() {
		return numLigne;
	}

	public void setNumLigne(int numLigne) {
		this.numLigne = numLigne;
	}

	public int getNumCol() {
		return numCol;
	}

	public void setNumCol(int numCol) {
		this.numCol = numCol;
	}

	public int getIdJoueur() {
		return idJoueur;
	}

	public void setIdJoueur(int idJoueur) {
		this.idJoueur = idJoueur;
	}

	@Override
	public String toString() {
		return "Coup [idPartie=" + idPartie + ", numLigne=" + numLigne + 
		       ", numCol=" + numCol + ", idJoueur=" + idJoueur + "]";
	}
}
