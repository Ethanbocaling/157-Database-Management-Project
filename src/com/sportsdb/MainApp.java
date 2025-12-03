package com.sportsdb;

import com.sportsdb.dao.*;
import com.sportsdb.model.Player;
import com.sportsdb.model.Stat;
import com.sportsdb.model.Team;
import com.sportsdb.service.PlayerService;
import com.sportsdb.service.TeamService;

import java.util.List;
import java.util.Scanner;

public class MainApp {

    public static void main(String[] args) {
        PlayerDAO playerDAO = new PlayerDAOImpl();
        StatDAO statDAO = new StatDAOImpl();
        TeamDAO teamDAO = new TeamDAOImpl();

        PlayerService playerService = new PlayerService(playerDAO, statDAO);
        TeamService teamService = new TeamService(teamDAO);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("=== NBA Player Management ===");
            System.out.println("1. List all players");
            System.out.println("2. Show player details");
            System.out.println("3. Search players by position");
            System.out.println("4. List teams");
            System.out.println("0. Exit");
            System.out.print("Choose option: ");

            String line = scanner.nextLine();
            int choice;
            try {
                choice = Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.\n");
                continue;
            }

            try {
                switch (choice) {
                    case 1:
                        listPlayers(playerService);
                        break;
                    case 2:
                        showPlayerDetails(playerService, scanner);
                        break;
                    case 3:
                        searchByPosition(playerService, scanner);
                        break;
                    case 4:
                        listTeams(teamService);
                        break;
                    case 0:
                        System.out.println("Goodbye!");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice.\n");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println();
        }
    }

    private static void listPlayers(PlayerService playerService) throws Exception {
        List<Player> players = playerService.getAllPlayers();
        if (players.isEmpty()) {
            System.out.println("No players found.");
            return;
        }
        for (Player p : players) {
            System.out.println(p.getPlayerId() + " - " + p.getName()
                    + " (" + p.getPosition() + ", age " + p.getAge() + ")");
        }
    }

    private static void showPlayerDetails(PlayerService playerService, Scanner scanner) throws Exception {
        System.out.print("Enter player ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        Player p = playerService.getPlayerWithStats(id);
        if (p == null) {
            System.out.println("Player not found.");
            return;
        }
        System.out.println("Name: " + p.getName());
        System.out.println("Age: " + p.getAge());
        System.out.println("Position: " + p.getPosition());
        System.out.println("Team ID: " + p.getTeamId());

        Stat s = p.getStat();
        if (s != null) {
            System.out.println("Points per game: " + s.getGoals());
            System.out.println("Assists per game: " + s.getAssists());
            System.out.println("Games played: " + s.getMatches());
        } else {
            System.out.println("No stats recorded.");
        }
    }

    private static void searchByPosition(PlayerService playerService, Scanner scanner) throws Exception {
        System.out.print("Enter position (e.g., PG, SG, SF, PF, C): ");
        String pos = scanner.nextLine();
        List<Player> players = playerService.searchByPosition(pos);
        if (players.isEmpty()) {
            System.out.println("No players found for position: " + pos);
            return;
        }
        for (Player p : players) {
            System.out.println(p.getPlayerId() + " - " + p.getName()
                    + " (" + p.getPosition() + ")");
        }
    }

    private static void listTeams(TeamService teamService) throws Exception {
        List<Team> teams = teamService.getAllTeams();
        if (teams.isEmpty()) {
            System.out.println("No teams found.");
            return;
        }
        for (Team t : teams) {
            System.out.println(t.getTeamId() + " - " + t.getName());
        }
    }
}

