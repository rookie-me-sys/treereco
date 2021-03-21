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
     * @param containerName
     * @return
     * delete container by name
     */
    public Boolean stopContainer(String containerName) throws InterruptedException {
        //获得所有容器
        List<Container> list = client.listContainersCmd().exec();
        //遍历每一个容器
        for(Container container : list){
            //遍历每一个容器下的容器名
            for(String name : container.getNames()){
                //如果容器的容器名==给出的名，则删除该容器
                if(name.equals("/" + containerName)){
                    client.stopContainerCmd(container.getId()).exec();
                    //若不为空字符串，则说明还未停止，应该循环到停止
                    while(HasActiveContainer(container.getId())){
                        System.out.println("stopContainer");
                        Thread.sleep(1000);
                    }
                    return true;
                }
            }
        }
        //找不到容器
        return false;
    }

    /**
     * @param containerName
     * @return
     */
    @Deprecated
    public Boolean removeContainer(String containerName){
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
     * @param containerName
     * @return
     */
    public Boolean stopAndRemoveContainer(String containerName) throws InterruptedException {
        //获得所有容器
        List<Container> list = client.listContainersCmd().exec();
        //遍历每一个容器
        for(Container container : list){
            //遍历每一个容器下的容器名
            for(String name : container.getNames()){
                //如果容器的容器名==给出的名，则删除该容器
                if(name.equals("/" + containerName)){
                    client.stopContainerCmd(container.getId()).exec();
                    //若不为空字符串，则说明还未停止，应该循环到停止
                    while(HasActiveContainer(container.getId())){
                        System.out.println("stopAndRemoveContainer");
                        Thread.sleep(1000);
                    }
                    client.removeContainerCmd(container.getId()).exec();
                    return true;
                }
            }
        }
        //找不到容器
        return false;
    }

    /**
     * @param containerName
     * @return
     * 如果找不到容器则返回空字符串
     */
    public String getContainerIdByName(String containerName){
        //获得所有容器
        List<Container> list = client.listContainersCmd().exec();
        //遍历每一个容器
        for(Container container : list){
            //遍历每一个容器下的容器名
            for(String name : container.getNames()){
                //如果容器的容器名==给出的名，返回id
                if(name.equals("/" + containerName)){
                    return container.getId();
                }
            }
        }
        //找不到容器
        return "";
    }

    /**
     *
     * @param containerName
     * @return
     */
    public String getContainerStatusByName(String containerName){
        //获得所有容器
        List<Container> list = client.listContainersCmd().exec();
        //遍历每一个容器
        for(Container container : list){
            //遍历每一个容器下的容器名
            for(String name : container.getNames()){
                //如果容器的容器名==给出的名，则获得容器status
                if(name.equals("/" + containerName)){
                    return container.getStatus();
                }
            }
        }
        //找不到容器
        return "";
    }

    /**
     * 通过停止的容器无法获得，得到是否存在id为id的活容器
     * @param id
     * @return
     */
    public Boolean HasActiveContainer(String id){
        List<Container> list = client.listContainersCmd().exec();

        for(Container container : list){
            if(container.getId().equals(id)){
                return true;
            }
        }

        return false;
    }

    /**
     * 万能删除
     * 它甚至能够删除不再list中的容器
     * @param id
     * @return
     */
    public Boolean removeContainerById(String id){
        try{
            client.removeContainerCmd(id).exec();
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 万能停止
     * @param id
     * @return
     */
    public Boolean stopContainerById(String id){
        try{
            client.stopContainerCmd(id).exec();
            while(HasActiveContainer(id)){
                System.out.println("stopContainerById");
                Thread.sleep(1000);
            }
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }




}
