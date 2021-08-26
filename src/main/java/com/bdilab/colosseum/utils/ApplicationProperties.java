package com.bdilab.colosseum.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * @author SunRen
 * @version 1.0
 * @date 2021/1/4 15:36
 */
public class ApplicationProperties {
    private static Logger log = LoggerFactory.getLogger(ApplicationProperties.class);
    private Properties properties;
    private static ApplicationProperties applicationProperties = null;
    private static String path = "application.properties";

    private ApplicationProperties(){
        properties = new Properties();
        InputStream is = null;
        try {
            is = ApplicationProperties.class.getClassLoader().getResourceAsStream(path);
            properties.load(new InputStreamReader(is,"UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
            log.error("读取application.propertise文件失败");
        }finally {
            try {
                if (is!=null){
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ApplicationProperties getInstance(){
        if (applicationProperties == null){
            applicationProperties = new ApplicationProperties();
        }
        return applicationProperties;
    }

    public static ApplicationProperties getInstance(String filePath){
        path = filePath;
        if (applicationProperties == null){
            applicationProperties = new ApplicationProperties();
        }
        return applicationProperties;
    }

    public String getValue(String key){
        if (!properties.containsKey(key)){
            return null;
        }
        String value = properties.getProperty(key);
        if (value == null){
            log.error("从配置文件中无法获取传入参数的对应值");
        }
        return value;
    }
}
