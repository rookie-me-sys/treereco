package com.nebulord.mapper;

import com.nebulord.pojo.OdmContainer;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface OdmContainerMapper {

    /**
     * 获取所有容器
     * @return
     */
    List<OdmContainer> getAllContainers();

    /**
     * 根据用户id获得所有容器
     * @param userid
     * @return
     */
    List<OdmContainer> getContainerByUserid(String userid);

    /**
     * 根据用户名查询所有容器
     * @param userName
     * @return
     */
    List<OdmContainer> getContainerByUsername(String userName);

    /**
     * 根据容器id查询用户id
     * @param id
     * @return
     */
    String getUserByContainerId(String id);

    /**
     * 根据容器名查询用户id
     * @param name
     * @return
     */
    String getUserByContainerName(String name);

    /**
     * 根据容器名获得容器id
     * @param containerName
     * @return
     */
    String getContainerIdByName(String containerName);

    /**
     * 根据容器id获得容器名
     * @param containerId
     * @return
     */
    String getContainerNameById(String containerId);

    /**
     * 插入一条容器记录
     * @param container
     */
    void addContainer(OdmContainer container);

    /**
     * 查询当前容器名（即uuid）是否有容器实例
     * @param containerName
     * @return
     */
    int getContainerCount(String containerName);

    /**
     * 根据容器名删除容器数据
     * @param containerName
     */
    void deleteContainerByName(String containerName);

    /**
     * 根据容器id删除容器数据
     * @param containerId
     */
    void deleteContainerById(String containerId);
    /**
     * 修改容器状态
     * containerId-status
     * @param map
     */
    void changeContainerStatus(Map<String, Object> map);

}
