package com.nebulord.controller;

import com.nebulord.mapper.TreerepoMapper;
import com.nebulord.pojo.Treerepo;
import com.nebulord.pojo.User;
import com.nebulord.utils.FileuploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class FileuploadController {

    @Autowired
    private TreerepoMapper treerepoMapper;

    @Value("${upload.root-pahth}")
    String uploadPath;

    @Value("${upload.tmp-path}")
    String tempPath;

    @PostMapping("/upload")
    @ResponseBody
    public void upload(HttpServletRequest request){

        FileuploadUtils fileUploadr = new FileuploadUtils(request, uploadPath, tempPath);

        //上传
        request.getSession().setAttribute("process", "0");
        String uuid = fileUploadr.upload(request.getSession());

        //如果返回null字符串，则说明不带文件，在session中放置uploadStatus=null
        if(uuid.equals("null")){
            request.getSession().setAttribute("uploadStatus", "上传文件中存在非图片文件");
            return;
        }
        else{
            request.getSession().setAttribute("uploadStatus", "上传成功");
        }

        //向数据库写入上传信息
        User user = (User)request.getSession().getAttribute("user");
        treerepoMapper.addTreerepo(new Treerepo(user.getId(), uuid));

    }

    @RequestMapping("/process")
    @ResponseBody
    public String process(HttpServletRequest request){
        return (String)request.getSession().getAttribute("process");

    }

    @RequestMapping("/status")
    @ResponseBody
    public String status(HttpServletRequest request){
        return (String)request.getSession().getAttribute("uploadStatus");
    }

    @RequestMapping("/uploadtest")
    @ResponseBody
    public String test(HttpServletRequest request){
        return request.getServletContext().getRealPath("upload");

    }

}
