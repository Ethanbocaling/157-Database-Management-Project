package com.sportsdb.dao;

import com.sportsdb.model.Stat;

public interface StatDAO {
    Stat findByPlayerId(int playerId) throws Exception;
}

