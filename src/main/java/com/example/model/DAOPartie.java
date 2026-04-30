package com.example.model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DAOPartie implements DAO<Partie> {
    private static final Logger LOGGER = Logger.getLogger(DAOPartie.class.getName());
    private Connection connexion = ConnexionDB.getInstance().getConnexion();

    @Override
    public List<Partie> findAll() {
        List<Partie> liste = new ArrayList<>();

        if (connexion == null) {
            LOGGER.log(Level.WARNING, "Database connection is null - returning empty list.");
            return liste;
        }

        String sql = "SELECT * FROM partie";

        try (PreparedStatement ps = connexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            // use DAOJoueur to load Joueur objects (fetches from DB)
            DAOJoueur daoJoueur = new DAOJoueur();

            while (rs.next()) {
                Partie p = new Partie();

                int j1Id = rs.getInt("id_joueur1");
                int j2Id = rs.getInt("id_joueur2");
                int gagnantId = rs.getInt("id_gagnant"); // may be NULL
                Joueur j1 = daoJoueur.findById(j1Id);
                Joueur j2 = daoJoueur.findById(j2Id);
                // defensive: if DAO couldn't load players (DB fallback), create lightweight placeholders
                if (j1 == null) j1 = new Joueur(j1Id, "Joueur " + j1Id, 0);
                if (j2 == null) j2 = new Joueur(j2Id, "Joueur " + j2Id, 0);
                Joueur gagnant = null;

                if (!rs.wasNull()) {
                    gagnant = daoJoueur.findById(gagnantId);
                    if (gagnant == null) gagnant = new Joueur(gagnantId, "Joueur " + gagnantId, 0);
                }
    
                // set database id if the model supports it
                p.setId(rs.getInt("id"));
                p.setJ1(j1);
                p.setJ2(j2);
                p.setGagnant(gagnant);

                Date sqlDate = rs.getDate("date_partie");
                if (sqlDate != null) {
                    p.setLocalDate(sqlDate.toLocalDate());
                }

                // Optional: recreate game
                p.setGame(new Game(j1Id, j2Id));

                liste.add(p);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "findAll error: " + e.getMessage(), e);
        }

        return liste;
    }

    @Override
    public Partie findById(int id) {
        if (connexion == null) {
            LOGGER.log(Level.WARNING, "Database connection is null - findById returning null.");
            return null;
        }

        String sql = "SELECT * FROM partie WHERE id = ?";
        try (PreparedStatement ps = connexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                DAOJoueur daoJoueur = new DAOJoueur();

                int j1Id = rs.getInt("id_joueur1");
                int j2Id = rs.getInt("id_joueur2");
                int gagnantId = rs.getInt("id_gagnant"); // may be NULL

                Joueur j1 = daoJoueur.findById(j1Id);
                Joueur j2 = daoJoueur.findById(j2Id);
                if (j1 == null) j1 = new Joueur(j1Id, "Joueur " + j1Id, 0);
                if (j2 == null) j2 = new Joueur(j2Id, "Joueur " + j2Id, 0);

                Joueur gagnant = null;
                if (!rs.wasNull()) {
                    gagnant = daoJoueur.findById(gagnantId);
                    if (gagnant == null) gagnant = new Joueur(gagnantId, "Joueur " + gagnantId, 0);
                }

                Partie p = new Partie();
                p.setId(rs.getInt("id"));
                p.setJ1(j1);
                p.setJ2(j2);
                p.setGagnant(gagnant);

                Date sqlDate = rs.getDate("date_partie");
                if (sqlDate != null) {
                    p.setLocalDate(sqlDate.toLocalDate());
                }

                // Recreate the game context
                p.setGame(new Game(j1Id, j2Id));

                // Load coups for this partie via DAOCoup
                DAOCoup daoCoup = new DAOCoup();
                List<Coup> coups = daoCoup.findByPartieId(id);
                for (Coup c : coups) {
                    p.ajouterCoup(c);
                }

                return p;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "findById error: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public boolean insert(Partie p) {
        return insertAndGetId(p) != -1;
    }

    @Override
    public boolean update(Partie p) {
        // Not fully implemented for this MVP
        return false;
    }

    @Override
    public boolean delete(int id) {
        // Not fully implemented for this MVP
        return false;
    }

    public int insertAndGetId(Partie p) {
        if (connexion == null) {
            LOGGER.log(Level.WARNING, "Database connection is null - insertAndGetId returning -1.");
            return -1;
        }

        String sql = "INSERT INTO partie (id_joueur1, id_joueur2, id_gagnant, date_partie) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getJ1().getId());
            ps.setInt(2, p.getJ2().getId());

            if (p.getGagnant() != null) {
                ps.setInt(3, p.getGagnant().getId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            ps.setDate(4, Date.valueOf(LocalDate.now()));

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "insertAndGetId error: " + e.getMessage(), e);
        }

        return -1;
    }
}
