package com.atguigu.gulimall.product.controller;


import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.gulimall.product.entity.BrandEntity;
import com.atguigu.gulimall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


@SpringBootTest
class AttrControllerTest {

    @Resource
    private BrandService brandService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redission;

    @Test
    void contextLoads() {
        BrandEntity brandEntity = new BrandEntity();
        brandEntity.setDescript("哈哈1哈");
        brandEntity.setName("华为");
        brandService.save(brandEntity);
        System.out.println("保存成功");
    }

    @Test
    public void getCatalogJsonDbWithRedisLock() {
        String uuid = UUID.randomUUID().toString();
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        Boolean lock = ops.setIfAbsent("lock", uuid,500, TimeUnit.SECONDS);
        if (lock) {
            Map<String, List<SkuEsModel>> categoriesDb = null;
            String lockValue = ops.get("lock");
            // get和delete原子操作
            String script = "if redis.call(\"get\",KEYS[1]) == ARGV[1] then\n" +
                    "    return redis.call(\"del\",KEYS[1])\n" +
                    "else\n" +
                    "    return 0\n" +
                    "end";
            stringRedisTemplate.execute(
                    new DefaultRedisScript<Long>(script, Long.class), // 脚本和返回类型
                    Arrays.asList("lock"), // 参数
                    lockValue); // 参数值，锁的值
            return;
        }else {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 睡眠0.1s后，重新调用 //自旋
           getCatalogJsonDbWithRedisLock();
        }
    }


    @Test
    void redissionLoads() throws InterruptedException {
        RLock lock = redission.getLock("anyLock");
        lock.lock(10, TimeUnit.SECONDS);

// 尝试加锁，最多等待100秒，上锁以后10秒自动解锁
        boolean res = lock.tryLock(100, 100, TimeUnit.SECONDS);
        System.out.println(res);
        if (res) {
            try {
            } finally {
                lock.unlock();
            }
        }
    }

    @Test
    void redissionReadWrite() throws InterruptedException {

        RReadWriteLock rwlock = redission.getReadWriteLock("anyRWLock");
        rwlock.readLock().lock(100, TimeUnit.SECONDS);

    }

    @Test
    void redissionSemaphore() throws InterruptedException {
        RSemaphore semaphore = redission.getSemaphore("semaphore2");
        RCountDownLatch latch = redission.getCountDownLatch("anyCountDownLatch");
        latch.trySetCount(10);
        latch.await();


    }
}