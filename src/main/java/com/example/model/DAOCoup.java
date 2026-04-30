package com.example.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DAOCoup implements DAO<Coup> {
    private static final Logger LOGGER = Logger.getLogger(DAOCoup.class.getName());
    private Connection connexion = ConnexionDB.getInstance().getConnexion();

    @Override
    public List<Coup> findAll() {
        List<Coup> liste = new ArrayList<>();
        if (connexion == null) {
            // DB unavailable: return fallback sample players so UI can start
            LOGGER.log(Level.WARNING, "Database connection is null - returning in-memory fallback players.");
            return liste;
        }
        try (PreparedStatement ps = connexion.prepareStatement("SELECT * FROM coup");
            ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Coup j = new Coup(rs.getInt("id_partie"), rs.getInt("num_ligne"), rs.getInt("num_col"), rs.getInt("id_joueur"));
                liste.add(j);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "findAll error: " + e.getMessage(), e);
        }
        return liste;
    }

    @Override
    public Coup findById(int id) {
        if (connexion == null) {
            LOGGER.log(Level.WARNING, "Database connection is null - findById returning null.");
            return null;
        }
        try (PreparedStatement ps = connexion.prepareStatement("SELECT * FROM Coup WHERE id_partie = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Coup(rs.getInt("id_partie"), rs.getInt("num_ligne"), rs.getInt("num_col"), rs.getInt("id_joueur"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "findById error: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Return all coups for a given partie id.
     */
    public List<Coup> findByPartieId(int idPartie) {
        List<Coup> liste = new ArrayList<>();
        if (connexion == null) {
            LOGGER.log(Level.WARNING, "Database connection is null - findByPartieId returning empty list.");
            return liste;
        }
        String sql = "SELECT * FROM coup WHERE id_partie = ? ORDER BY id";
        try (PreparedStatement ps = connexion.prepareStatement(sql)) {
            ps.setInt(1, idPartie);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Coup j = new Coup(rs.getInt("id_partie"), rs.getInt("num_ligne"), rs.getInt("num_col"), rs.getInt("id_joueur"));
                    liste.add(j);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "findByPartieId error: " + e.getMessage(), e);
        }
        return liste;
    }

    @Override
    public boolean insert(Coup c) {
        if (connexion == null) {
            LOGGER.log(Level.WARNING, "Database connection is null - insert skipped.");
            return false;
        }

        String sql = "INSERT INTO coup (id_partie, num_ligne, num_col, id_joueur) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connexion.prepareStatement(sql)) {
            ps.setInt(1, c.getIdPartie());
            ps.setInt(2, c.getNumLigne());
            ps.setInt(3, c.getNumCol());
            ps.setInt(4, c.getIdJoueur());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "insert error: " + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean update(Coup c) {
        // Not fully implemented for this MVP
        return false;
    }

    @Override
    public boolean delete(int id) {
        // Not fully implemented for this MVP
        return false;
    }
}
