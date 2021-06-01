package com.dalianpai.gulimall.seckill.service;

import com.dalianpai.gulimall.seckill.to.SecKillSkuRedisTo;

import java.util.List;

/**
 * @author WGR
 * @create 2020/8/17 -- 22:09
 */
public interface SeckillService {

    public void uploadSeckillSkuLatest3Days();

    List<SecKillSkuRedisTo> getCurrentSeckillSkus();

    SecKillSkuRedisTo getSkuSeckillInfo(Long skuId);

    String kill(String killId, String key, Integer num);
}
