package com.nebulord.mapper;

import com.nebulord.pojo.Treerepo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TreerepoMapper {

    /**
     * 根据用户id获得全部uuid
     * @param id
     * @return
     */
    List<String> getUUIDByUserid(String id);

    /**
     * 根据uuid查询用户id
     * @param uuid
     * @return
     */
    String getUseridByUUID(String uuid);

    /**
     * 上传后添加一条数据
     * @param repo
     */
    void addTreerepo(Treerepo repo);

}
