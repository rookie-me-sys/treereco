package com.nebulord;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nebulord.mapper.UserMapper;
import com.nebulord.utils.DockerUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
@SpringBootTest
class Version01ApplicationTests {

    @Autowired
    DataSource dataSource;

    @Autowired
    UserMapper usermapper;

    @Test
    void contextLoads() throws SQLException {
        System.out.println(usermapper.getAllUser().toString());
    }

}
