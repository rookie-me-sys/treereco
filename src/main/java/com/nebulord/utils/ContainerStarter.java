package com.nebulord.utils;

import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

/**
 * odm容器启动器
 */
public class ContainerStarter {

    private Runtime runtime;

    private String outputOrtho;

    private String outputTexure;

    private String repo;

    public ContainerStarter(){

        repo = PropertiesUtil.getProperty("upload.root-pahth");
        outputOrtho = PropertiesUtil.getProperty("output.orthophoto");
        outputTexure = PropertiesUtil.getProperty("output.texuring");

        runtime = Runtime.getRuntime();

    }

    /**
     * 启动容器
     * @param uuid
     */
    public void runContainer(String uuid) throws IOException {

        /**
         * 是否要加--rm有待争议:
         *  存在--rm调用stop立即删除容器
         *  但是处理完毕后也删除容器，无法获取容器状态
         *  不过可以通过查询数据库中，容器id存在但是不存在status的情况
         *
         *  不存在--rm时，需要手动判断状态并清除容器
         *
         * 我认为不加上
         */
        String exec = "docker run " +
                "-v " + repo + "/" + uuid + ":" + "/code/images " +
                "-v " + outputOrtho + "/" + uuid + ":" + "/code/odm_orthophoto " +
                "-v " + outputTexure + "/" + uuid + ":" + "/code/odm_texturing " +
                "--name " + uuid +
                " opendronemap/odm";

        System.out.println(exec);

        runtime.exec(exec);
    }


}
