package com.atguigu.gulimall.payment.feign;

import com.atguigu.common.vo.OrderEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("gulimall-order")
public interface OrderFeignService {
    @GetMapping("/order/order/token/{orderSn}")
    OrderEntity queryOrderByOrderToken(@PathVariable("orderSn")String orderSn);
}
