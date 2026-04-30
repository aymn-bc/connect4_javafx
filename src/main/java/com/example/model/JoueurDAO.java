package com.example.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class JoueurDAO {
    private static final String URL   = "jdbc:mysql://127.0.0.1:3306/puissance4?useSSL=false&serverTimezone=UTC";
    private static final String LOGIN = "admin";
    private static final String PASS  = "admin";

    public List<Joueur> getTousLesJoueurs() {
        List<Joueur> joueurs = new ArrayList<>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(URL, LOGIN, PASS);
                 Statement ps = conn.createStatement();
                 ResultSet rs = ps.executeQuery("SELECT * FROM joueur")) {
                while (rs.next()) {
                    joueurs.add(new Joueur(
                        (int) rs.getLong("id"),
                        rs.getString("nom"),
                        rs.getInt("score")
                    ));
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println(Level.SEVERE + ": Error fetching joueurs from database " + e);
        }
        return joueurs;
    }
}