package com.dalianpai.gulimall.seckill.scheduled;

import com.dalianpai.gulimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author WGR
 * @create 2020/8/17 -- 22:08
 */
@Slf4j
@Service
public class SeckillSkuScheduled {

    @Autowired
    SeckillService seckillService;

    @Autowired
    RedissonClient redissonClient;

    private final String upload_lock = "seckill:upload:lock";

    @Scheduled(cron ="0 * 3 * * ?")
    public void uploadSeckillSkulatest3Days(){
        log.info("商品上架");
        RLock lock = redissonClient.getLock(upload_lock);
        lock.lock(10, TimeUnit.SECONDS);
        try{
            seckillService.uploadSeckillSkuLatest3Days();
        }finally {
            lock.unlock();
        }

    }

}
