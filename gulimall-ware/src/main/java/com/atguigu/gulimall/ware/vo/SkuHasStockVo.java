package com.atguigu.gulimall.ware.vo;


import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SkuHasStockVo {
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * 库存数
     */
    private Boolean hasStock;
}
