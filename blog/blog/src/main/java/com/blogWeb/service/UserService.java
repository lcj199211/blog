package com.blogWeb.service;

import com.blogWeb.model.User;
import com.github.pagehelper.PageInfo;
import java.text.ParseException;

public interface UserService {

    String addUser (User user);

    PageInfo<User> findAllUser(int pageNum, int pageSize);

    User  getUser(String username,String password);

    String getToken(String username);

    String updateUser(User user);

    String removeUser(int userId);

    String getUser(int userId);
}
