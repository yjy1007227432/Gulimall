package com.atguigu.gulimall.order.feign;


import com.atguigu.gulimall.order.entity.OrderEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("gulimall-payment")
public interface PaymentFeignService {

    @GetMapping("queryOrderByOrderToken")
    OrderEntity queryOrderByOrderToken(@RequestParam("orderToken") String orderToken);

    Long save(OrderEntity orderEntity, int i);
}
