package com.dalianpai.gulimall.seckill.to;

import com.dalianpai.gulimall.seckill.vo.SkuInfoVo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author WGR
 * @create 2020/8/17 -- 23:35
 */
@Data
public class SecKillSkuRedisTo {

    /**
     * 活动id
     */
    private Long promotionId;
    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;

    private String randomCode;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;
    /**
     * 秒杀总量
     */
    private Integer seckillCount;
    /**
     * 每人限购数量
     */
    private Integer seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;
    //Sku信息
    private SkuInfoVo skuInfo;

    private Long startTime;

    private Long endTime;
}
