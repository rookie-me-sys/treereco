package com.nebulord;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nebulord.mapper.OdmContainerMapper;
import com.nebulord.mapper.TreerepoMapper;
import com.nebulord.mapper.UserMapper;
import com.nebulord.pojo.Treerepo;
import com.nebulord.pojo.User;
import com.nebulord.utils.ContainerStarter;
import com.nebulord.utils.DockerUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;


@SpringBootTest
class Version01ApplicationTests {

    @Autowired
    DataSource dataSource;

    @Autowired
    UserMapper usermapper;

    @Autowired
    TreerepoMapper treerepomapper;

    @Autowired
    OdmContainerMapper containermapper;

    @Test
    void getAllUser() throws SQLException {
        System.out.println(usermapper.getAllUser());
    }

    /**
     * 测试用户操作
     */
    @Test
    void addUser(){
        String maxid = usermapper.getMaxId();
        int id = Integer.parseInt(maxid);
        ++id;
        String newid = "" + id;
        while(newid.length() < 10){
            newid = "0" + newid;
        }
        Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        User newUser = new User(newid, "zyy", "123456", ft.format(dNow));
        usermapper.addUser(newUser);
        System.out.println(usermapper.getAllUser());
    }

    @Test
    void changePassword(){
        Map<String, Object> map = new HashMap();
        map.put("id", "0000000001");
        map.put("password", "654321");
        usermapper.changePassword(map);
    }

    /**
     * 测试上传文件目录操作
     */
    @Test
    void addTreerepo(){
        String uuid = UUID.randomUUID().toString();

        Treerepo repo = new Treerepo("0000000001", uuid);

        treerepomapper.addTreerepo(repo);

        System.out.println(treerepomapper.getUUIDByUserid("0000000001"));
        System.out.println(treerepomapper.getUseridByUUID(uuid));
    }

    /**
     * 测试添加容器操作
     */
    @Test
    void addContainer(){
        List<String> uuidList = treerepomapper.getUUIDByUserid("0000000001");

        System.out.println(containermapper.getAllContainers());
        System.out.println(containermapper.getContainerByUserid("0000000001"));
        System.out.println(containermapper.getContainerByUsername("zyy"));
        System.out.println(containermapper.getUserByContainerName(uuidList.get(0)));
        System.out.println(containermapper.getUserByContainerId("notknow"));

    }

    /**
     * 测试dockerUtils
     *
     * test passed
     */
    @Test
    @Deprecated
    void deleteContainer() throws JsonProcessingException {

        DockerUtils docker = new DockerUtils();

        docker.removeContainerById("a28bfd31818f");

    }

    /**
     * ContainerStarter test
     */
    @Test
    void startContainer() throws IOException {
        ContainerStarter starter = new ContainerStarter();
        DockerUtils docker = new DockerUtils();

        starter.runContainer("cdd4e3ed-b56d-41fa-aa53-ce46537fb342");

        String id = docker.getContainerIdByName("cdd4e3ed-b56d-41fa-aa53-ce46537fb342");

        System.out.println("id: " + id);

    }


}
