package com.bdilab.colosseum.config;

import com.bdilab.colosseum.interceptor.LoginCheckInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    LoginCheckInterceptor loginCheckInterceptor;

    @Value("${logo.path}")
    String logoPath;
    /**
     * 配置静态资源加载
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置前端静态资源路径
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        // 记得加swagger资源路径映射，否则swagger页面无法正常显示
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/","classpath:/META-INF/resources/webjars/");

        registry.addResourceHandler("/home/colosseum/logo/**").addResourceLocations("file:"+logoPath);
    }

//    /**
//     * 配置系统登录拦截
//     * @param registry
//     */
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(loginCheckInterceptor).excludePathPatterns("/user/login/**")
////                .excludePathPatterns("/*")
//                .excludePathPatterns("/user/register/**")
//                // 排除调优实验Restful调用接口
//                .excludePathPatterns("/experiment/getPerformance")
//                .excludePathPatterns("/mysql80/**")
//                .excludePathPatterns("/swagger-ui.html","/swagger-resources/**","/images/**","/webjars/**","/v2/api-docs");
//    }
//这是一个继承自WebMvcConfigurerAdapter的配置类

}
