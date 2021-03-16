package com.nebulord.controller;

import com.nebulord.utils.FileuploadUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class FileuploadController {

    @PostMapping("/upload")
    @ResponseBody
    public void upload(HttpServletRequest request){
        //F:/treedata_repo/
        /*指定上传根路径以及临时文件根目录*/
        String uploadPath = "F:/treedata_repo";
        String tempPath = "F:/tmp";
        FileuploadUtils fileUploadr = new FileuploadUtils(request, uploadPath, tempPath);

        //上传
        request.getSession().setAttribute("process", "0");
        String uuid = fileUploadr.upload(request.getSession());
        //如果返回null字符串，则说明不带文件，在session中放置uploadStatus=null
        if(uuid.equals("null")){
            request.getSession().setAttribute("uploadStatus", "null");
            return;
        }
        else{
            request.getSession().setAttribute("uploadStatus", "ok");
        }
        System.out.println(uuid);
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

}
