package com.dalianpai.gulimall.seckill.service.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.dalianpai.gulimall.seckill.feign.CouponFeignService;
import com.dalianpai.gulimall.seckill.feign.ProductFeignService;
import com.dalianpai.gulimall.seckill.interceptor.LoginUserInterceptor;
import com.dalianpai.gulimall.seckill.service.SeckillService;
import com.dalianpai.gulimall.seckill.to.SecKillSkuRedisTo;
import com.dalianpai.gulimall.seckill.vo.SeckillSessionsWithSkus;
import com.dalianpai.gulimall.seckill.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author WGR
 * @create 2020/8/17 -- 22:09
 */
@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {

   @Autowired
    CouponFeignService couponFeignService;

   @Autowired
    StringRedisTemplate redisTemplate;

   @Autowired
    ProductFeignService productFeignService;

   @Autowired
    RabbitTemplate rabbitTemplate;

   @Autowired
    RedissonClient redissonClient;

   private final String SESSIONS_CACHE_PREFIX ="seckill:sessions:";
   private final String SKUKILL_CACHE_PREDIX ="seckill:skus";

   private final String SKU_STOCK_SEMPHORE ="seckill:stock:";

    @Override
    public void uploadSeckillSkuLatest3Days() {
        R session = couponFeignService.lates3DaySession();
        if(session.getCode()==0){
            List<SeckillSessionsWithSkus> sessionData = session.getData(new TypeReference<List<SeckillSessionsWithSkus>>(){});
            System.out.println(sessionData);
            saveSessionInfos(sessionData);
            saveSessionSkuInfos(sessionData);
        }
    }

    public List<SecKillSkuRedisTo> blockHandler(BlockException e){
        log.error("getCurrentSeckillSkusResource被限流了。。。");
        return  null;

    }

    @SentinelResource(value = "getCurrentSeckillSkusResource",blockHandler = "blockHandler")
    @Override
    public List<SecKillSkuRedisTo> getCurrentSeckillSkus() {
        // 1.确定当前时间属于那个秒杀场次
        long time = new Date().getTime();
        try (Entry entry = SphU.entry("seckillSkus")){

            Set<String> keys = redisTemplate.keys(SESSIONS_CACHE_PREFIX + "*");
            for (String key : keys) {

                String replace = key.replace("seckill:sessions:", "");
                String[] split = replace.split("_");
                long start = Long.parseLong(split[0]);
                long end = Long.parseLong(split[1]);
                if(time >= start && time <= end){
                    // 2.获取这个秒杀场次的所有商品信息
                    List<String> range = redisTemplate.opsForList().range(key, 0, 100);
                    BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREDIX);
                    List<String> list = hashOps.multiGet(range);
                    if(list != null){
                        return list.stream().map(item -> {
                            SecKillSkuRedisTo redisTo = JSON.parseObject(item, SecKillSkuRedisTo.class);
                            return redisTo;
                        }).collect(Collectors.toList());
                    }
                    break;
                }
        }
        }catch (BlockException e){
            log.warn("资源被限流：" + e.getMessage());
        }
        return null;
    }

    @Override
    public SecKillSkuRedisTo getSkuSeckillInfo(Long skuId) {
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREDIX);
        Set<String> keys = hashOps.keys();
        if(keys != null && keys.size() > 0){
            String regx = "\\d+_" + skuId;
            for (String key : keys) {
                if(Pattern.matches(regx, key)){
                    String json = hashOps.get(key);
                    SecKillSkuRedisTo to = JSON.parseObject(json, SecKillSkuRedisTo.class);
                    // 处理一下随机码
                    long current = new Date().getTime();

                    if(current <= to.getStartTime() || current >= to.getEndTime()){
                        to.setRandomCode(null);
                    }
                    System.out.println(to);
                    return to;
                }
            }
        }
        return null;
    }

    @Override
    public String kill(String killId, String key, Integer num) {
        MemberRespVo memberRsepVo = LoginUserInterceptor.loginUser.get();

        // 1.获取当前秒杀商品的详细信息
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SKUKILL_CACHE_PREDIX);
        String json = hashOps.get(killId);
        System.out.println(killId);
        System.out.println(json);
        if(StringUtils.isEmpty(json)){
            return null;
        }else{
            SecKillSkuRedisTo redisTo = JSON.parseObject(json, SecKillSkuRedisTo.class);
            // 校验合法性
            long time = new Date().getTime();
            if(time >= redisTo.getStartTime() && time <= redisTo.getEndTime()){
                // 1.校验随机码跟商品id是否匹配
                String randomCode = redisTo.getRandomCode();
                String skuId = redisTo.getPromotionSessionId() + "_" + redisTo.getSkuId();

                if(randomCode.equals(key) && killId.equals(skuId)){
                    // 2.说明数据合法
                    Integer limit = redisTo.getSeckillLimit();
                    if(num <= limit){
                        // 3.验证这个人是否已经购买过了
                        String redisKey = memberRsepVo.getId() + "_" + skuId;
                        // 让数据自动过期
                        long ttl = redisTo.getEndTime() - redisTo.getStartTime();

                        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), ttl<0?0:ttl, TimeUnit.MILLISECONDS);
                        if(aBoolean){
                            // 占位成功 说明从来没买过
                            RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMPHORE + randomCode);

                            boolean  acquire = semaphore.tryAcquire(num);
                            if(acquire){
                                // 秒杀成功
                                // 快速下单 发送MQ
                                String orderSn = IdWorker.getTimeId() + UUID.randomUUID().toString().replace("-","").substring(7,8);
                                SecKillOrderTo orderTo = new SecKillOrderTo();
                                orderTo.setOrderSn(orderSn);
                                orderTo.setMemberId(memberRsepVo.getId());
                                orderTo.setNum(num);
                                orderTo.setSkuId(redisTo.getSkuId());
                                orderTo.setSeckillPrice(redisTo.getSeckillPrice());
                                orderTo.setPromotionSessionId(redisTo.getPromotionSessionId());
                                rabbitTemplate.convertAndSend("order-event-exchange","order.seckill.order", orderTo);
                                return orderSn;
                            }
                        }else {
                            return null;
                        }
                    }
                }else{
                    return null;
                }
            }else{
                return null;
            }
        }
        return null;
}

    private void saveSessionInfos(List<SeckillSessionsWithSkus> sessions){
        sessions.stream().forEach(session ->{
            long startTime = session.getStartTime().getTime();
            long endTime = session.getEndTime().getTime();
            String key = SESSIONS_CACHE_PREFIX + startTime + "_" + endTime;
            Boolean hasKey = redisTemplate.hasKey(key);
            if(!hasKey){
                List<String> collect = session.getRelationSkus().stream().map(item -> item.getPromotionSessionId().toString()+"_"+item.getSkuId().toString()).collect(Collectors.toList());
                //缓存活动信息
                redisTemplate.opsForList().leftPushAll(key,collect);
            }
        });


    }

  private void saveSessionSkuInfos(List<SeckillSessionsWithSkus> sessions){
      sessions.stream().forEach(session ->{
          BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREDIX);
          session.getRelationSkus().stream().forEach(seckillSkuVo -> {
              String token = UUID.randomUUID().toString().replace("-", "");
              if(!ops.hasKey(seckillSkuVo.getPromotionSessionId().toString()+"_"+seckillSkuVo.getSkuId().toString())){
                  SecKillSkuRedisTo redisTo = new SecKillSkuRedisTo();
                  R skuInfo = productFeignService.info(seckillSkuVo.getSkuId());

                  if(skuInfo.getCode() ==0){
                      SkuInfoVo info = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                      });
                      redisTo.setSkuInfo(info);
                  }

                  //2.sku的秒杀信息
                  BeanUtils.copyProperties(seckillSkuVo,redisTo);

                  //3.设置上当前商品的秒杀时间信息
                  redisTo.setStartTime(session.getStartTime().getTime());
                  redisTo.setEndTime(session.getEndTime().getTime());


                  redisTo.setRandomCode(token);

                  String jsonString = JSON.toJSONString(redisTo);
                  ops.put( seckillSkuVo.getPromotionSessionId().toString()+"_"+seckillSkuVo.getSkuId().toString(),jsonString);

                  RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMPHORE + token);
                  semaphore.trySetPermits(seckillSkuVo.getSeckillCount());
              }

          });
      });
  }
























}
