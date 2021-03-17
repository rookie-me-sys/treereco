package com.nebulord.mapper;

import com.nebulord.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * User pojo mapper
 */
@Mapper
@Repository
public interface UserMapper {

    /**
     * 获得所有用户
     * @return
     */
    List<User> getAllUser();

}
