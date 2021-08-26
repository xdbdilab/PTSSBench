package com.bdilab.colosseum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {

    @Bean
    public RestTemplate restTemplate(){
        //默认底层执行HttpURLConnection
        return new RestTemplate();
    }
}
