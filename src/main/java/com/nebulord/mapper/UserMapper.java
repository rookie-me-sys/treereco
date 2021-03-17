package com.nebulord.mapper;

import com.nebulord.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/**
 * User pojo mapper
 */
@Mapper
@Repository
public interface UserMapper {

    /**
     * 获得所有用户信息
     * @return
     */
    List<User> getAllUser();

    /**
     * 根据用户id获得用户信息
     * @param id
     * @return
     */
    User getUserById(String id);

    /**
     * 根据用户昵称获得信息
     * @param nickname
     * @return
     */
    User getUserByNickname(String nickname);

    /**
     * 添加新的用户
     * @param user
     */
    void addUser(User user);

    /**
     * 根据id找到用户，使用map中的密码字段修改
     * @need id
     * @need password
     * @param map
     */
    void changePassword(Map<String, Object> map);

    /**
     * 获取最大的id号
     * 在插入新用户时需要将id递增
     * 在此查询结果基础上加一
     * @return
     */
    String getMaxId();

}
