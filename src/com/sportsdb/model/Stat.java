package com.sportsdb.model;

public class Stat {
    private int statId;
    private int playerId;
    private double goals;   // we store points per game here
    private double assists; // assists per game
    private int matches;    // games played

    public int getStatId() { return statId; }
    public void setStatId(int statId) { this.statId = statId; }

    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }

    public double getGoals() { return goals; }
    public void setGoals(double goals) { this.goals = goals; }

    public double getAssists() { return assists; }
    public void setAssists(double assists) { this.assists = assists; }

    public int getMatches() { return matches; }
    public void setMatches(int matches) { this.matches = matches; }
}

