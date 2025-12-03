package com.sportsdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionUtil {

    private static final String URL =
            "jdbc:mysql://127.0.0.1:3306/sportsdb?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";          //  MySQL username
    private static final String PASS = "Bocan625"; //  MySQL password

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Cannot load MySQL JDBC driver", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}


