package com.dalianpai.gulimall.seckill.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author WGR
 * @create 2020/7/28 -- 16:01
 */
@Configuration
public class GuliFeignConfig {

    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if(attributes !=null){
                    HttpServletRequest request = attributes.getRequest();
                    if(request !=null){
                        requestTemplate.header("Cookie", request.getHeader("Cookie"));
                    }

                }
            }
        };
    }
}
