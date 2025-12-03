package com.sportsdb;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnectionUtil {

    private static String URL;
    private static String USER;
    private static String PASS;

    static {
        try {
            // 1) Load MySQL JDBC driver (from the JAR in your lib/ folder)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 2) Load DB settings from db.properties (in project root)
            loadProperties();

        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot load MySQL JDBC driver", e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load db.properties", e);
        }
    }

    private static void loadProperties() throws IOException {
        Properties props = new Properties();

        // Looks for db.properties in the working directory (your project root if you run from there)
        try (InputStream in = new FileInputStream("db.properties")) {
            props.load(in);
        }

        URL  = props.getProperty("db.url");
        USER = props.getProperty("db.user");
        PASS = props.getProperty("db.password");

        if (URL == null || URL.isBlank()
                || USER == null || USER.isBlank()
                || PASS == null) {
            throw new RuntimeException(
                "db.properties is missing db.url, db.user, or db.password"
            );
        }

        System.out.println("DBConnectionUtil: loaded config from db.properties");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
