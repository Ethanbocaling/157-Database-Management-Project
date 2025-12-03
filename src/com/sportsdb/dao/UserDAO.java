package com.sportsdb.dao;

import com.sportsdb.model.User;
import java.util.List;

public interface UserDAO {
    void create(User user) throws Exception;
    User findByUsername(String username) throws Exception;
    List<User> findAll() throws Exception;
    void deleteById(int userId) throws Exception;
}

