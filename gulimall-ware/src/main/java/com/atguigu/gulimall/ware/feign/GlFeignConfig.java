package com.atguigu.gulimall.ware.feign;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class GlFeignConfig {

    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor(){



        return null;
    }

}
