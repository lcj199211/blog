package com.blogWeb.dao;

import com.blogWeb.model.User;

import java.util.List;
import java.util.Map;

public interface  UserDao {

    int insert(User record);

    List<User> selectUsers();

    User getUserByName(Map<String,Object> map);

    void updateUser(User user);

    void removeUser(Map<String,Object> map);

    User getUserById(Map<String,Object> map);

}
