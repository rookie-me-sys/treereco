package com.nebulord.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nebulord.mapper.TreerepoMapper;
import com.nebulord.pojo.User;
import com.nebulord.utils.DockerUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ContainerController {

    @Autowired
    TreerepoMapper treerepoMapper;

    @RequestMapping("/repoinfo")
    @ResponseBody
    public String getRepoInfo(HttpServletRequest request) throws JsonProcessingException {

        //需要返回的odm容器信息
        /*
        * 用户id
        * uuid
        * containerid
        * 状态：status/是否创建
        *
        * */
        String infoJson = "";

        User user = (User)request.getSession().getAttribute("user");
        ContainerInfo infoObject;
        ObjectMapper mapper = new ObjectMapper();

        //获得用户的uuid列表
        List<String> uuidList = treerepoMapper.getUUIDByUserid(user.getId());

        //获得uuid对应容器id
        DockerUtils dockerUtils = new DockerUtils();
        List<String> containerId = new ArrayList();
        for(int i = 0; i < uuidList.size(); i++){
            containerId.add(dockerUtils.getContainerIdByName(uuidList.get(i)));
        }

        //获得容器状态
        List<String> status = new ArrayList();
        for(int i = 0; i < uuidList.size(); i++){
            status.add(dockerUtils.getContainerStatusByName(uuidList.get(i)));
        }

        infoObject = new ContainerInfo(uuidList, user.getId(), containerId, status);

        infoJson = mapper.writeValueAsString(infoObject);

        return infoJson;
    }

    /**
     * 3/19做这个
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping("/startstop")
    @ResponseBody
    public String startstop(HttpServletRequest request) throws JsonProcessingException {
        DockerUtils dockerUtils = new DockerUtils();
        String status = dockerUtils.getContainerStatusByName((String)request.getParameter("containerName"));
        if(status.equals("")){

            return "启动";
        }
        return "停止";
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class ContainerInfo{
        private List<String> uuid;
        private String userId;
        private List<String> containerId;
        private List<String> status;
    }

}
