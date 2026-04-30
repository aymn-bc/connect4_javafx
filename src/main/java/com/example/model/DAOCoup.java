package com.example.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
        // Not fully implemented for this MVP
        return new ArrayList<>();
    }

    @Override
    public Coup findById(int id) {
        // Not fully implemented for this MVP
        return null;
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
