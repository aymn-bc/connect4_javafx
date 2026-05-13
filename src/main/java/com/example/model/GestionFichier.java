package com.example.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestionFichier {
    
    public void enregistrePartie(Partie p) throws IOException {
        String fileName = p.getLocalDate() + "-" + p.getJ1().getId() + "_" + p.getJ2().getId() + ".txt";
        File f = new File(fileName);
        
        try (FileWriter fw = new FileWriter(f)) {
            // Write header
            fw.write(p.getJ1().getId() + "\n");
            fw.write(p.getJ2().getId() + "\n");
            fw.write(p.getScoreJ1() + "\n");
            fw.write(p.getScoreJ2() + "\n");
            
            // Write winner (or -1 if none)
            if (p.getGagnant() != null) {
                fw.write(p.getGagnant().getId() + "\n");
            } else {
                fw.write("-1\n");
            }
            
            // Write date (or NONE if null)
            if (p.getLocalDate() != null) {
                fw.write(p.getLocalDate() + "\n");
            } else {
                fw.write("NONE\n");
            }
            
            // Write coups in clean format: idPartie,numLigne,numCol,idJoueur
            for (Coup c : p.getCoups()) {
                fw.write(c.getIdPartie() + "," + c.getNumLigne() + "," + c.getNumCol() + "," + c.getIdJoueur() + "\n");
            }
        }
    }
    
    public Partie importerPartie(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            
            if (lines.size() < 6) {
                throw new IOException("Format de fichier invalide");
            }
            
            // Parse header with null/empty checks
            String line0 = lines.get(0);
            String line1 = lines.get(1);
            String line2 = lines.get(2);
            String line3 = lines.get(3);
            String line4 = lines.get(4);
            String line5 = lines.get(5);
            
            if (line0 == null || line0.trim().isEmpty() || 
                line1 == null || line1.trim().isEmpty() ||
                line2 == null || line2.trim().isEmpty() ||
                line3 == null || line3.trim().isEmpty() ||
                line4 == null || line4.trim().isEmpty() ||
                line5 == null || line5.trim().isEmpty()) {
                throw new IOException("Format de fichier invalide (lignes vides)");
            }
            
            int idJ1 = Integer.parseInt(line0.trim());
            int idJ2 = Integer.parseInt(line1.trim());
            int scoreJ1 = Integer.parseInt(line2.trim());
            int scoreJ2 = Integer.parseInt(line3.trim());
            int idGagnant = Integer.parseInt(line4.trim());
            
            // Parse date - handle null or "NONE" case
            LocalDate date = null;
            String dateStr = line5.trim();
            if (!dateStr.isEmpty() && !dateStr.equals("null") && !dateStr.equals("NONE")) {
                try {
                    date = LocalDate.parse(dateStr);
                } catch (Exception e) {
                    // If date parsing fails, just use null
                    System.err.println("Impossible de parser la date: " + dateStr);
                }
            }
            
            // Load players with fallback to placeholder if not found
            DAOJoueur daoJoueur = new DAOJoueur();
            Joueur j1 = daoJoueur.findById(idJ1);
            Joueur j2 = daoJoueur.findById(idJ2);
            
            // Fallback: create placeholder players if not found in DB
            // This allows importing games with AI players or players that don't exist in current DB
            if (j1 == null) {
                j1 = new Joueur(idJ1, "Joueur " + idJ1, 0);
            }
            if (j2 == null) {
                j2 = new Joueur(idJ2, "Joueur " + idJ2, 0);
            }
            
            // Create partie
            Partie p = new Partie(j1, j2);
            p.setScoreJ1(scoreJ1);
            p.setScoreJ2(scoreJ2);
            p.setLocalDate(date);
            
            if (idGagnant != -1) {
                Joueur gagnant = daoJoueur.findById(idGagnant);
                if (gagnant != null) {
                    p.setGagnant(gagnant);
                }
            }
            
            // Parse coups
            for (int i = 6; i < lines.size(); i++) {
                String coupLine = lines.get(i);
                if (coupLine != null) {
                    coupLine = coupLine.trim();
                    if (!coupLine.isEmpty()) {
                        String[] parts = coupLine.split(",");
                        if (parts.length == 4) {
                            try {
                                int idPartie = Integer.parseInt(parts[0].trim());
                                int numLigne = Integer.parseInt(parts[1].trim());
                                int numCol = Integer.parseInt(parts[2].trim());
                                int idJoueur = Integer.parseInt(parts[3].trim());
                                
                                Coup c = new Coup(idPartie, numLigne, numCol, idJoueur);
                                p.ajouterCoup(c);
                                
                                // Replay the move on the game board
                                p.getPuissance().setCoup(numLigne, numCol, idJoueur);
                            } catch (NumberFormatException e) {
                                // Skip malformed coup lines
                                System.err.println("Ligne de coup invalide: " + coupLine);
                            }
                        }
                    }
                }
            }
            
            return p;
        }
    }

    public List<String> listFile(String path) throws IOException {
        return new ArrayList<>();
    }
}