package com.blogWeb.service.impl;

import com.blogWeb.dao.UserDao;
import com.blogWeb.model.User;
import com.blogWeb.service.UserService;
import com.blogWeb.util.JsonUtil;
import com.blogWeb.util.MD5Util;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service(value = "userService")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;//这里会报错，idea的纠错机制在解析spring通过命名约定的方式进行配置时,支持的并不是太友好,但是并不会影响

    /*
     * @CachePut 应用到写数据的方法上，如新增/修改方法，调用方法时会自动把相应的数据放入缓存
     */
    @Override
    @CachePut(value = "user", key = "#user.userId")
    public String addUser(User user) {
        userDao.insert(user);
        //int userId=user.getUserId();
        return JsonUtil.toJSON(user);
    }


    /*
     * @CachePut 这个注释可以确保方法被执行，同时方法的返回值也被记录到缓存中，实现缓存与数据库的同步更新。
     */
    @Override
    @CachePut(value = "user", key = "#user.userId")
    public String updateUser(User user) {
            userDao.updateUser(user);
            return JsonUtil.toJSON(user);

    }


    /*
     * @CacheEvict  清空 userName 缓存
     */
    @Override
    @CacheEvict(value = "user", key = "#userId")
    public String removeUser(int userId) {
        String back="200";
        try {
            Map<String,Object> map=new HashMap<String, Object>();
            map.put("userId",userId);
            userDao.removeUser(map);
        }catch (Exception e){
            back="404";
        }
        return back;
    }


    /*
     * 这个方法中用到了我们开头配置依赖的分页插件pagehelper
     * 只需要在service层传入参数，然后将参数传递给一个插件的一个静态方法即可；
     * pageNum 开始页数
     * pageSize 每页显示的数据条数
     * */
    @Override
    public PageInfo<User> findAllUser(int pageNum, int pageSize) {
        //将参数传给这个方法就可以实现物理分页了。
        PageHelper.startPage(pageNum, pageSize);
        List<User> userDomains = userDao.selectUsers();
        PageInfo result = new PageInfo(userDomains);
        return result;
    }

    @Override
    public String getToken(String username) {
        StringBuilder token = new StringBuilder();
        //加token:
        token.append("");
        //加加密的用户名
        token.append(MD5Util.convertMD5(MD5Util.string2MD5(username)) + "-");
        //加时间
        token.append(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + "-");
        //加六位随机数111111-999999
        token.append(new Random().nextInt((999999 - 111111 + 1)) + 111111);
        return token.toString();
    }

    @Override
    public User getUser(String username, String password) {
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("username",username);
        map.put("password",password);
        return userDao.getUserByName(map);

    }

    @Override
    @Cacheable(value = "user",key = "#userId")
    public String getUser(int userId) {
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("userId",userId);
        User user=userDao.getUserById(map);
        return JsonUtil.toJSON(user);

    }

}

