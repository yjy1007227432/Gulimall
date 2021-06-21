package com.dalianpai.gulimall.seckill.controller;

import com.atguigu.common.utils.R;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.dalianpai.gulimall.seckill.interceptor.LoginUserInterceptor;
import com.dalianpai.gulimall.seckill.service.SeckillService;
import com.dalianpai.gulimall.seckill.to.SecKillSkuRedisTo;
import com.dalianpai.gulimall.seckill.vo.OrderItemVo;
import com.dalianpai.gulimall.seckill.vo.WareSkuLockVo;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author WGR
 * @create 2020/8/18 -- 19:51
 */
@Controller
public class SeckillController {

    @Autowired
    private SeckillService seckillService;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @ResponseBody
    @GetMapping("/currentSeckillSkus")
    public R getCurrentSeckillSkus(){
        List<SecKillSkuRedisTo> vos = seckillService.getCurrentSeckillSkus();
        return R.ok().setData(vos);
    }

    @GetMapping("/miaosha/{skuId}")
    public R kill(@PathVariable("skuId") Long skuId){
        Long userId = LoginUserInterceptor.loginUser.get().getId();
        if(userId!=null){
            // 查询库存
            String stock = this.redisTemplate.opsForValue().get("sec:stock:" + skuId);
            if (StringUtils.isEmpty(stock)){
                return R.error("秒杀结束");
            }
            // 通过信号量，获取秒杀库存
            RSemaphore semaphore = this.redissonClient.getSemaphore("sec:semaphore:" + skuId);
            semaphore.trySetPermits(Integer.valueOf(stock));
            //0.1s
            boolean b = semaphore.tryAcquire();
            if(b){
                //创建订单
                String orderSn = IdWorker.getTimeId();

                WareSkuLockVo lockVO = new WareSkuLockVo();
                lockVO.setOrderSn(orderSn);
                OrderItemVo orderItem = new OrderItemVo();
                orderItem.setCount(1);
                orderItem.setSkuId(skuId);
                lockVO.setLocks(Arrays.asList(orderItem));

                //准备闭锁信息
                RCountDownLatch latch = this.redissonClient.getCountDownLatch("sec:countdown:" + orderSn);
                latch.trySetCount(1);
                this.kafkaTemplate.send("ORDER-EXCHANGE", "sec.kill", lockVO);
                return R.ok("秒杀成功，订单号：" + orderSn);
            }else {
                return R.error("秒杀失败，欢迎再次秒杀！");
            }
    }

//    @ResponseBody
//    @GetMapping("/sku/seckill/{skuId}")
//    public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId){
//        SecKillSkuRedisTo to = seckillService.getSkuSeckillInfo(skuId);
//        return R.ok().setData(to);
//    }
//
//    @GetMapping("/kill")
//    public String secKill(@RequestParam("killId") String killId, @RequestParam("key") String key, @RequestParam("num") Integer num, Model model){
//        String orderSn = seckillService.kill(killId,key,num);
//        System.out.println(orderSn);
//        // 1.判断是否登录
//        model.addAttribute("orderSn", orderSn);
//        return "success";
//    }
}
