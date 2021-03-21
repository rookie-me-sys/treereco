package com.nebulord.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nebulord.mapper.OdmContainerMapper;
import com.nebulord.mapper.TreerepoMapper;
import com.nebulord.pojo.OdmContainer;
import com.nebulord.pojo.User;
import com.nebulord.utils.ContainerStarter;
import com.nebulord.utils.DockerUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ContainerController {

    @Autowired
    TreerepoMapper treerepoMapper;

    @Autowired
    OdmContainerMapper odmMapper;

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
        /**
         * 这里需要优化：
         * 在这里获得容器的id，是通过非数据库的方式
         * 从docker client中获取会导致当容器停止后，取不到id的情况
         */
        for(int i = 0; i < uuidList.size(); i++){
            String idByDocker = dockerUtils.getContainerIdByName(uuidList.get(i));
            String idBySql = odmMapper.getContainerIdByName(uuidList.get(i));
            String id = "error";
            if(idBySql == null){
                id = idByDocker;
            }
            else{
                id = idByDocker.equals("") ? idBySql : idByDocker;
            }
            containerId.add(id);
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
     * @param request
     * @return
     * @throws JsonProcessingException
     */
    @RequestMapping("/startstop")
    @ResponseBody
    public String startstop(HttpServletRequest request) throws IOException, InterruptedException {
        DockerUtils dockerUtils = new DockerUtils();
        ContainerStarter starter = new ContainerStarter();

        String uuid = (String)request.getParameter("containerName");

        String status = dockerUtils.getContainerStatusByName(uuid);
        System.out.println(status);
        if(status.equals("")){

            /**
             * 在run之前，查看数据库中是否已经有该容器名的容器
             * 如果有则需要删除，保证一个组UAV图像只有一个容器
             * （亟需完善）
             */
            System.out.println("start");
            int count = odmMapper.getContainerCount(uuid);
            if(count != 0){
                String id = odmMapper.getContainerIdByName(uuid);
                try{dockerUtils.removeContainerById(id);}
                catch(Exception e){
                    System.out.println("没有这个容器");
                }
                odmMapper.deleteContainerByName(uuid);
            }

            starter.runContainer(uuid);
            String id = "";
            id = dockerUtils.getContainerIdByName(uuid);

            odmMapper.addContainer(new OdmContainer(uuid, id, ((User)request.getSession().getAttribute("user")).getId(), dockerUtils.getContainerStatusByName(uuid)));

            return "start";
        }
        System.out.println("stop");
        String id = "";
        id = dockerUtils.getContainerIdByName(uuid);
        dockerUtils.stopContainerById(id);
        System.out.println(id);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("containerId", id);
        map.put("status","stoped");
        odmMapper.changeContainerStatus(map);

        return "stop";
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
