package com.atguigu.gulimall.payment.servcie;

import com.atguigu.common.vo.OrderEntity;
import com.atguigu.common.vo.PayAsyncVo;
import com.atguigu.gulimall.payment.entity.PaymentInfoEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


public interface PaymentService {
    OrderEntity queryOrderByOrderToken(String orderToken);
    Long save(OrderEntity orderEntity, Integer payType);

    PaymentInfoEntity queryPayMentById(Long valueOf);

    void paySuccess(PayAsyncVo payAsyncVo);
}
