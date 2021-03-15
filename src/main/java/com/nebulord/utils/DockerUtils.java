package com.nebulord.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.core.DockerClientBuilder;

import java.util.ArrayList;
import java.util.List;

public class DockerUtils {
    public String info = "{}";
    private DockerClient client = null;
    private List<String> containerId;

    /**
     *
     * @throws JsonProcessingException
     */
    public DockerUtils() throws JsonProcessingException {
        //获得DockerClient实例
        DockerClient _client = DockerClientBuilder.getInstance("tcp://localhost:2375").build();
        client = _client;

        //得到docker信息Info
        Info _info = client.infoCmd().exec();
        ObjectMapper mapper = new ObjectMapper();
        info = mapper.writeValueAsString(_info);

        //初始化containerId
        containerId = new ArrayList<String>();

    }

    /**
     *
     * @return
     */
    public DockerClient getDockerClient() {
        return client;
    }

    /**
     * @param client
     * @param containerName
     * @return
     * delete container by name
     */
    public Boolean stopContainer(DockerClient client, String containerName){
        //获得所有容器
        List<Container> list = client.listContainersCmd().exec();
        //遍历每一个容器
        for(Container container : list){
            //遍历每一个容器下的容器名
            for(String name : container.getNames()){
                //如果容器的容器名==给出的名，则删除该容器
                if(name.equals("/" + containerName)){
                    client.stopContainerCmd(container.getId()).exec();
                    return true;
                }
            }
        }
        //找不到容器
        return false;
    }

    /**
     *
     * @param client
     * @param containerName
     * @return
     */
    @Deprecated
    public Boolean removeContainer(DockerClient client, String containerName){
        //获得所有容器
        List<Container> list = client.listContainersCmd().exec();
        //遍历每一个容器
        for(Container container : list){
            //遍历每一个容器下的容器名
            for(String name : container.getNames()){
                //如果容器的容器名==给出的名，则删除该容器
                if(name.equals("/" + containerName)){
                    client.removeContainerCmd(container.getId()).exec();
                    return true;
                }
            }
        }
        //找不到容器
        return false;
    }

    /**
     *
     * @param client
     * @param containerName
     * @return
     */
    public Boolean stopAndRemoveContainer(DockerClient client, String containerName){
        //获得所有容器
        List<Container> list = client.listContainersCmd().exec();
        //遍历每一个容器
        for(Container container : list){
            //遍历每一个容器下的容器名
            for(String name : container.getNames()){
                //如果容器的容器名==给出的名，则删除该容器
                if(name.equals("/" + containerName)){
                    client.stopContainerCmd(container.getId()).exec();
                    client.removeContainerCmd(container.getId()).exec();
                    return true;
                }
            }
        }
        //找不到容器
        return false;
    }

    /**
     *
     * @param client
     * @param containerName
     * @return
     * 如果找不到容器则返回空字符串
     */
    public String getContainerIdByName(DockerClient client, String containerName){
        //获得所有容器
        List<Container> list = client.listContainersCmd().exec();
        //遍历每一个容器
        for(Container container : list){
            //遍历每一个容器下的容器名
            for(String name : container.getNames()){
                //如果容器的容器名==给出的名，则删除该容器
                if(name.equals("/" + containerName)){
                    return container.getId();
                }
            }
        }
        //找不到容器
        return "";
    }


}
