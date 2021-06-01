package com.dalianpai.gulimall.seckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author WGR
 * @create 2020/6/22 -- 21:58
 */
@Configuration
public class MyRedissonConfig {

    @Bean(destroyMethod="shutdown")
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://gulimall-redis.gulimall:6379");
        RedissonClient redisson = Redisson.create(config);
        return  redisson;
    }
}
