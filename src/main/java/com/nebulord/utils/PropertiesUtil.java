package com.nebulord.utils;

import org.springframework.core.env.Environment;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class PropertiesUtil {

    private static Environment env = null;

    public static void setEnvironment(Environment env) {
        PropertiesUtil.env = env;
    }

    public static String getProperty(String key) {
        String result="";
        try {
            result= URLDecoder.decode(PropertiesUtil.env.getProperty(key), "UTF-8") ;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

}
