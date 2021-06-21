package com.atguigu.gulimall.payment.servcie.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.vo.OrderEntity;
import com.atguigu.common.vo.PayAsyncVo;
import com.atguigu.gulimall.payment.dao.PaymentInfoDao;
import com.atguigu.gulimall.payment.entity.PaymentInfoEntity;
import com.atguigu.gulimall.payment.feign.OrderFeignService;
import com.atguigu.gulimall.payment.servcie.PaymentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private OrderFeignService orderService;

    @Autowired
    private PaymentInfoDao paymentInfoDao;

    public OrderEntity queryOrderByOrderToken(String orderToken) {
        OrderEntity orderEntityResponseVo = this.orderService.queryOrderByOrderToken(orderToken);
        return orderEntityResponseVo;
    }

    @Override
    public Long save(OrderEntity orderEntity, Integer payType) {
        // 查看支付记录，是否已存在。
        PaymentInfoEntity paymentInfoEntity = this.paymentInfoDao.selectOne(new QueryWrapper<PaymentInfoEntity>().eq("out_trade_no", orderEntity.getOrderSn()));
        // 如果存在，直接结束
        if (paymentInfoEntity != null) {
            return paymentInfoEntity.getId();
        }
        // 否则，新增支付记录
        paymentInfoEntity = new PaymentInfoEntity();
        paymentInfoEntity.setOutTradeNo(orderEntity.getOrderSn());
        paymentInfoEntity.setPaymentType(payType);
        paymentInfoEntity.setSubject("谷粒商城支付平台");
        // paymentInfoEntity.setTotalAmount(orderEntity.getPayAmount());
        paymentInfoEntity.setTotalAmount(new BigDecimal("0.01"));
        paymentInfoEntity.setPaymentStatus(0);
        paymentInfoEntity.setCreateTime(new Date());
        this.paymentInfoDao.insert(paymentInfoEntity);
        return paymentInfoEntity.getId();
    }

    @Override
    public PaymentInfoEntity queryPayMentById(Long valueOf) {
        return paymentInfoDao.selectById(valueOf);
    }

    @Override
    public void paySuccess(PayAsyncVo payAsyncVo) {

        PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
        paymentInfoEntity.setCallbackTime(new Date());
        paymentInfoEntity.setPaymentStatus(1);
        paymentInfoEntity.setCallbackContent(JSON.toJSONString(payAsyncVo));
        this.paymentInfoDao.update(paymentInfoEntity, new UpdateWrapper<PaymentInfoEntity>().eq("out_trade_no", payAsyncVo.getOut_trade_no()));
    }

}

