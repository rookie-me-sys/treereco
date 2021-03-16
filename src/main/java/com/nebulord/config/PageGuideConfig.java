package com.nebulord.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PageGuideConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        //设置index.html为主页
        /**
         * http://localhost:8080/
         */
        registry.addViewController("/").setViewName("index");

    }
}
