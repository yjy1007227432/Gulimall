package com.atguigu.gulimall.product.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRedisCongfig {

    @Value("${ipAddr}")
    private String ipAddr;

    // redission通过redissonClient对象使用 // 如果是多个redis集群，可以配置
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() {
        Config config = new Config();
        // 创建单例模式的配置
        config.useSingleServer().setAddress("redis://" + ipAddr + ":6379");
        return Redisson.create(config);
    }
}

