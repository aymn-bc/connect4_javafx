package com.example.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DAOJoueur implements DAO<Joueur> {
    private static final Logger LOGGER = Logger.getLogger(DAOJoueur.class.getName());
    private Connection connexion = ConnexionDB.getInstance().getConnexion();

    @Override
    public List<Joueur> findAll() {
        List<Joueur> liste = new ArrayList<>();
        if (connexion == null) {
            // DB unavailable: return fallback sample players so UI can start
            LOGGER.log(Level.WARNING, "Database connection is null - returning in-memory fallback players.");
            liste.add(new Joueur(1, "Alice", 0));
            liste.add(new Joueur(2, "Bob", 0));
            return liste;
        }
        try (PreparedStatement ps = connexion.prepareStatement("SELECT * FROM joueur");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Joueur j = new Joueur(rs.getInt("id"), rs.getString("nom"), rs.getInt("score"));
                liste.add(j);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "findAll error: " + e.getMessage(), e);
        }
        return liste;
    }

    @Override
    public Joueur findById(int id) {
        if (connexion == null) {
            LOGGER.log(Level.WARNING, "Database connection is null - findById returning null.");
            return null;
        }
        try (PreparedStatement ps = connexion.prepareStatement("SELECT * FROM joueur WHERE id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Joueur(rs.getInt("id"), rs.getString("nom"), rs.getInt("score"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "findById error: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean insert(Joueur j) {
        if (connexion == null) {
            LOGGER.log(Level.WARNING, "Database connection is null - insert skipped.");
            return false;
        }
        try (PreparedStatement ps = connexion.prepareStatement(
                "INSERT INTO joueur (nom, score) VALUES (?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, j.getNom());
            ps.setInt(2, j.getScore());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        j.setId(keys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "insert error: " + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean update(Joueur j) {
        if (connexion == null) {
            LOGGER.log(Level.WARNING, "Database connection is null - update skipped.");
            return false;
        }
        try (PreparedStatement ps = connexion.prepareStatement(
                "UPDATE joueur SET nom = ?, score = ? WHERE id = ?")) {
            ps.setString(1, j.getNom());
            ps.setInt(2, j.getScore());
            ps.setInt(3, j.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "update error: " + e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        if (connexion == null) {
            LOGGER.log(Level.WARNING, "Database connection is null - delete skipped.");
            return false;
        }
        try (PreparedStatement ps = connexion.prepareStatement(
                "DELETE FROM joueur WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "delete error: " + e.getMessage(), e);
        }
        return false;
    }
}
