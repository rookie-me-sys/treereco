package com.nebulord.controller;

import com.nebulord.mapper.UserMapper;
import com.nebulord.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/login")
    @ResponseBody
    public void login(HttpServletRequest request){

        //用户id
        String id = "0000000001";

        User testUser = userMapper.getUserById(id);
        request.getSession().setAttribute("user", testUser);
    }

}
