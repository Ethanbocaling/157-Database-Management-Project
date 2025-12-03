package com.sportsdb.service;

import com.sportsdb.dao.TeamDAO;
import com.sportsdb.model.Team;
import java.util.List;

public class TeamService {

    private final TeamDAO teamDAO;

    public TeamService(TeamDAO teamDAO) {
        this.teamDAO = teamDAO;
    }

    public List<Team> getAllTeams() throws Exception {
        return teamDAO.findAll();
    }
}

