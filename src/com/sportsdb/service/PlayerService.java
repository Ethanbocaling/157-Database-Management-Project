package com.sportsdb.service;

import com.sportsdb.dao.PlayerDAO;
import com.sportsdb.dao.StatDAO;
import com.sportsdb.model.Player;
import com.sportsdb.model.Stat;
import java.util.List;

public class PlayerService {

    private final PlayerDAO playerDAO;
    private final StatDAO statDAO;

    public PlayerService(PlayerDAO playerDAO, StatDAO statDAO) {
        this.playerDAO = playerDAO;
        this.statDAO = statDAO;
    }

    public List<Player> getAllPlayers() throws Exception {
        return playerDAO.findAll();
    }

    public Player getPlayerWithStats(int playerId) throws Exception {
        Player p = playerDAO.findById(playerId);
        if (p != null) {
            Stat s = statDAO.findByPlayerId(playerId);
            p.setStat(s);
        }
        return p;
    }

    public List<Player> searchByPosition(String position) throws Exception {
        return playerDAO.findByPosition(position);
    }

    public List<Player> searchByNameOrTeam(String query) throws Exception {
        if (query == null || query.isBlank()) {
            return playerDAO.findAll();
        }
        return playerDAO.searchByNameOrTeam(query);
    }

    public Stat getStatsForPlayer(int playerId) throws Exception {
        return statDAO.findByPlayerId(playerId);
    }

    public void addPlayer(Player player) throws Exception {
        // simple validation
        if (player.getName() == null || player.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        playerDAO.create(player);
    }

    public void updatePlayer(Player player) throws Exception {
        playerDAO.update(player);
    }

    public void deletePlayer(int playerId) throws Exception {
        playerDAO.delete(playerId);
    }


}

