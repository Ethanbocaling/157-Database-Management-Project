package com.sportsdb.dao;

import com.sportsdb.model.Player;
import java.util.List;

public interface PlayerDAO {
    List<Player> findAll() throws Exception;
    Player findById(int id) throws Exception;
    List<Player> findByPosition(String position) throws Exception;
    List<Player> searchByNameOrTeam(String query) throws Exception;

    void create(Player player) throws Exception;
    void update(Player player) throws Exception;
    void delete(int playerId) throws Exception;
}

