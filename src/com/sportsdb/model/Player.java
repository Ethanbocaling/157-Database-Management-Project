package com.sportsdb.model;

public class Player {
    private int playerId;
    private String name;
    private int age;
    private String position;
    private int teamId;
    private String faceCode;   // optional
    private Stat stat;         // stats for details view

    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public int getTeamId() { return teamId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }

    public String getFaceCode() { return faceCode; }
    public void setFaceCode(String faceCode) { this.faceCode = faceCode; }

    public Stat getStat() { return stat; }
    public void setStat(Stat stat) { this.stat = stat; }
}

