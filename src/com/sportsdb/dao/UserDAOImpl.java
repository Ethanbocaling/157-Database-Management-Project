package com.sportsdb.dao;

import com.sportsdb.DBConnectionUtil;
import com.sportsdb.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    @Override
    public void create(User user) throws Exception {
        String sql = "INSERT INTO Users (Username, PasswordHash, Role) VALUES (?, ?, ?)";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getRole());
            ps.executeUpdate();
        }
    }

    @Override
    public User findByUsername(String username) throws Exception {
        String sql = "SELECT UserID, Username, PasswordHash, Role FROM Users WHERE Username = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("UserID"));
                    u.setUsername(rs.getString("Username"));
                    u.setPasswordHash(rs.getString("PasswordHash"));
                    u.setRole(rs.getString("Role"));
                    return u;
                }
            }
        }
        return null;
    }

    @Override
    public List<User> findAll() throws Exception {
        List<User> list = new ArrayList<>();
        String sql = "SELECT UserID, Username, PasswordHash, Role FROM Users";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("UserID"));
                u.setUsername(rs.getString("Username"));
                u.setPasswordHash(rs.getString("PasswordHash"));
                u.setRole(rs.getString("Role"));
                list.add(u);
            }
        }
        return list;
    }

    @Override
    public void deleteById(int userId) throws Exception {
        String sql = "DELETE FROM Users WHERE UserID = ?";
        try (Connection conn = DBConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }
}

