package com.kahvia.teacher.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestTemplateUtil {
    @Bean
    public static RestTemplate getRestTemplate(){
        return new RestTemplate();
    }
}
