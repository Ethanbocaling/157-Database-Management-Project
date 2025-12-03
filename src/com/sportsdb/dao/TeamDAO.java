package com.sportsdb.dao;

import com.sportsdb.model.Team;
import java.util.List;

public interface TeamDAO {
    List<Team> findAll() throws Exception;
}
