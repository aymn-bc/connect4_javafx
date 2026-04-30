package com.example.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnexionDB {
    private static final Logger LOGGER = Logger.getLogger(ConnexionDB.class.getName());
    private static ConnexionDB instance;
    private Connection connexion;

    private static final String URL  = "jdbc:mysql://localhost:3306/puissance4?useSSL=false&serverTimezone=UTC";
    private static final String USER = "admin";
    private static final String PASS = "admin";

    private ConnexionDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connexion = DriverManager.getConnection(URL, USER, PASS);
            LOGGER.log(Level.INFO, "Connexion réussie à la base de données.");
        } catch (SQLException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Erreur de connexion : " + e.getMessage(), e);
        }
    }

    public static ConnexionDB getInstance() {
        if (instance == null) {
            instance = new ConnexionDB();
        }
        return instance;
    }

    public Connection getConnexion() {
        return connexion;
    }
}
