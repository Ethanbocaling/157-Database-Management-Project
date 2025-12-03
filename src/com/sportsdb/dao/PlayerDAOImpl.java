package com.sportsdb.dao;

import com.sportsdb.DBConnectionUtil;
import com.sportsdb.model.Player;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerDAOImpl implements PlayerDAO {

    private Player mapRow(ResultSet rs) throws SQLException {
        Player p = new Player();
        p.setPlayerId(rs.getInt("PlayerID"));
        p.setName(rs.getString("Name"));
        p.setAge(rs.getInt("Age"));
        p.setPosition(rs.getString("Position"));
        p.setTeamId(rs.getInt("TeamID"));
        p.setFaceCode(rs.getString("FaceCode"));
        return p;
    }

    @Override
    public List<Player> findAll() throws Exception {
        List<Player> list = new ArrayList<>();
        String sql = "SELECT PlayerID, Name, Age, Position, TeamID, FaceCode FROM Players";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public Player findById(int id) throws Exception {
        String sql = "SELECT PlayerID, Name, Age, Position, TeamID, FaceCode FROM Players WHERE PlayerID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Player> findByPosition(String position) throws Exception {
        List<Player> list = new ArrayList<>();
        String sql = "SELECT PlayerID, Name, Age, Position, TeamID, FaceCode FROM Players WHERE Position = ?";

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, position);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<Player> searchByNameOrTeam(String query) throws Exception {
        List<Player> list = new ArrayList<>();
        String sql = """
            SELECT p.PlayerID, p.Name, p.Age, p.Position, p.TeamID, p.FaceCode
            FROM Players p
            JOIN Teams t ON p.TeamID = t.TeamID
            WHERE p.Name LIKE ? OR t.Name LIKE ?
            ORDER BY p.Name
            """;

        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String like = "%" + query + "%";
            ps.setString(1, like);
            ps.setString(2, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    @Override
    public void create(Player player) throws Exception {
        String sql = "INSERT INTO Players (Name, Age, Position, TeamID, FaceCode) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, player.getName());
            ps.setInt(2, player.getAge());
            ps.setString(3, player.getPosition());
            ps.setInt(4, player.getTeamId());
            ps.setString(5, player.getFaceCode());
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Player player) throws Exception {
        String sql = """
            UPDATE Players
            SET Name = ?, Age = ?, Position = ?, TeamID = ?, FaceCode = ?
            WHERE PlayerID = ?
            """;
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, player.getName());
            ps.setInt(2, player.getAge());
            ps.setString(3, player.getPosition());
            ps.setInt(4, player.getTeamId());
            ps.setString(5, player.getFaceCode());
            ps.setInt(6, player.getPlayerId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int playerId) throws Exception {
        String sql = "DELETE FROM Players WHERE PlayerID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, playerId);
            ps.executeUpdate();
        }
    }


}
