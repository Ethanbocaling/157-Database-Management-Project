package com.sportsdb.dao;

import com.sportsdb.DBConnectionUtil;
import com.sportsdb.model.Stat;
import java.sql.*;

public class StatDAOImpl implements StatDAO {

    @Override
    public Stat findByPlayerId(int playerId) throws Exception {
        String sql = "SELECT StatID, PlayerID, Goals, Assists, Matches FROM Stats WHERE PlayerID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, playerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Stat s = new Stat();
                    s.setStatId(rs.getInt("StatID"));
                    s.setPlayerId(rs.getInt("PlayerID"));
                    s.setGoals(rs.getDouble("Goals"));
                    s.setAssists(rs.getDouble("Assists"));
                    s.setMatches(rs.getInt("Matches"));
                    return s;
                }
            }
        }
        return null;
    }
}
