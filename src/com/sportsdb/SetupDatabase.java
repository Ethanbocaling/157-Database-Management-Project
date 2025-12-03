package com.sportsdb;

import java.sql.Connection;
import java.sql.Statement;

public class SetupDatabase {

    public static void main(String[] args) throws Exception {
        try (Connection conn = DBConnectionUtil.getConnection();
             Statement stmt = conn.createStatement()) {

            String createTeams = """
                CREATE TABLE IF NOT EXISTS Teams (
                    TeamID INT AUTO_INCREMENT PRIMARY KEY,
                    Name   VARCHAR(50) NOT NULL UNIQUE
                );
                """;

            String createPlayers = """
                CREATE TABLE IF NOT EXISTS Players (
                    PlayerID INT AUTO_INCREMENT PRIMARY KEY,
                    Name     VARCHAR(100) NOT NULL,
                    Age      INT NOT NULL,
                    Position VARCHAR(10) NOT NULL,
                    TeamID   INT NOT NULL,
                    FaceCode VARCHAR(50) NOT NULL,
                    CONSTRAINT fk_player_team
                        FOREIGN KEY (TeamID) REFERENCES Teams(TeamID)
                );
                """;

            String createStats = """
                CREATE TABLE IF NOT EXISTS Stats (
                    StatID   INT AUTO_INCREMENT PRIMARY KEY,
                    PlayerID INT NOT NULL UNIQUE,
                    Goals    DECIMAL(5,2) NOT NULL,
                    Assists  DECIMAL(5,2) NOT NULL,
                    Matches  INT NOT NULL,
                    CONSTRAINT fk_stats_player
                        FOREIGN KEY (PlayerID) REFERENCES Players(PlayerID)
                );
                """;

            String createUsers = """
                CREATE TABLE IF NOT EXISTS Users (
                    UserID INT AUTO_INCREMENT PRIMARY KEY,
                    Username VARCHAR(50) NOT NULL UNIQUE,
                    PasswordHash VARCHAR(255) NOT NULL,
                    Role VARCHAR(20) NOT NULL
                );
                """;

            stmt.execute(createTeams);
            stmt.execute(createPlayers);
            stmt.execute(createStats);
            stmt.execute(createUsers);

            System.out.println("Tables created (if they did not exist).");
        }
    }
}

