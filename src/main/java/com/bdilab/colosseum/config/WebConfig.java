package com.bdilab.colosseum.config;

import com.bdilab.colosseum.interceptor.AdminCheckInterceptor;
import com.bdilab.colosseum.interceptor.LoginCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @ClassName WebConfig
 * @Author wx
 * @Date 2020/12/16 0016 14:35
 **/
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Autowired
    LoginCheckInterceptor loginCheckInterceptor;

    @Autowired
    AdminCheckInterceptor adminCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginCheckInterceptor).excludePathPatterns("/user/login/**")
                .excludePathPatterns("/user/register/**")
                // 排除调优实验Restful调用接口
                .excludePathPatterns("/experiment/getPerformance")
                .excludePathPatterns("/mysql80/**")
                .excludePathPatterns("/swagger-ui.html","/swagger-resources/**","/images/**","/webjars/**","/v2/api-docs")
                .excludePathPatterns("/admin/**")
                .excludePathPatterns("/**");

        //新增针对管理员的过滤器
        registry.addInterceptor(adminCheckInterceptor).addPathPatterns("/admin/**");
    }
}
