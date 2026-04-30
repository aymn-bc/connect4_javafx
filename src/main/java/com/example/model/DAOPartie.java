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
        // Not fully implemented for this MVP
        return new ArrayList<>();
    }

    @Override
    public Partie findById(int id) {
        // Not fully implemented for this MVP
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
