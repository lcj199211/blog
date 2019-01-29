package com.blogWeb.controller;

import com.blogWeb.model.User;
import com.blogWeb.service.UserService;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @ResponseBody
    @PostMapping("/add")
    public String addUser(User user){
        String json="200";
        try {
            userService.addUser(user);
        }catch (Exception e){
            json="404";
        }
        return json;
    }

    @ResponseBody
    @PostMapping("/getUser")
    public String getUser(int userId){
        String json="";
        try {
            json=userService.getUser(userId);
        }catch (Exception e){
            json="404";
        }
        return json;
    }

    @ResponseBody
    @PostMapping("/updateUser")
    public String updateUser(User user){
        String json="200";
        try {
            userService.updateUser(user);
        }catch (Exception e){
            json="404";
        }
        return json;
    }

    @ResponseBody
    @PostMapping("/removeUser")
    public String removeUser(int userId){
        return userService.removeUser(userId);
    }


    @ResponseBody
    @GetMapping("/all")
    public Object findAllUser(
            @RequestParam(name = "pageNum", required = false, defaultValue = "1")
                    int pageNum,
            @RequestParam(name = "pageSize", required = false, defaultValue = "10")
                    int pageSize){
        return userService.findAllUser(pageNum,pageSize);
    }

    /*
    *  登录验证，用户名作为缓存键值
    *  @Cacheable 应用到读取数据的方法上，先从缓存中读取，如果没有再从DB获取数据，然后把数据添加到缓存中
    *  unless 表示条件表达式成立的话不放入缓存
    */
    @ResponseBody
    @Cacheable(value = "login",key = "#username")
    @PostMapping("/login")
    public String login(String username, String password){
        try {
            JSONObject jsonObject=new JSONObject();
            User user=userService.getUser(username,password);
            Integer userId = user.getUserId();
            if (user != null) {
                String token = userService.getToken(username);
                jsonObject.put("token",token);
                jsonObject.put("isLogin",true);
                jsonObject.put("tokenCreatTime",System.currentTimeMillis());
                jsonObject.put("tokenExpiryTime",System.currentTimeMillis());
            }else {
                jsonObject.put("isLogin",false);
            }

            return jsonObject.toString();

        }catch (Exception e){

            return "404";

        }

    }


    /*
     *  退出登录
     */
    @ResponseBody
    @CacheEvict(value = "login",key = "#username")
    @PostMapping("/outLogin")
    public String outLogin(String username){
        try {
            System.out.print("清除缓存");
            return "200";
        }catch (Exception e){

            return "404";

        }

    }

}

