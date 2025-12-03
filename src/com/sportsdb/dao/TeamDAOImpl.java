package com.sportsdb.dao;

import com.sportsdb.DBConnectionUtil;
import com.sportsdb.model.Team;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeamDAOImpl implements TeamDAO {

    private Team mapRow(ResultSet rs) throws SQLException {
        Team t = new Team();
        t.setTeamId(rs.getInt("TeamID"));
        t.setName(rs.getString("Name"));
        return t;
    }

    @Override
    public List<Team> findAll() throws Exception {
        List<Team> list = new ArrayList<>();
        String sql = "SELECT TeamID, Name FROM Teams";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }
}
