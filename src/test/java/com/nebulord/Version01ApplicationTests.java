package com.nebulord;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nebulord.utils.DockerUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Version01ApplicationTests {

    @Test
    void contextLoads() throws JsonProcessingException {
        DockerUtils client = new DockerUtils();
        client.stopAndRemoveContainer(client.getDockerClient(), "angry_nobel");
    }

    @Test
    void remove() throws JsonProcessingException {
        System.out.println();
    }

}
