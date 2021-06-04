package com.atguigu.gulimall.product.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-seckill")
public interface SeckillFeignService {
    // 远程服务的url

    /**
     *@PathVariable("xxx")
     通过 @PathVariable 可以将URL中占位符参数{xxx}绑定到处理器类的方法形参中@PathVariable(“xxx“)
     * @param skuId
     * @return
     */
    @RequestMapping("/sku/seckill/{skuId}")//注意写全优惠券类上还有映射//注意我们这个地方不是控制层，所以这个请求映射请求的不是我们服务器上的东西，而是nacos注册中心的
    public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId);//得到一个R对象

}
