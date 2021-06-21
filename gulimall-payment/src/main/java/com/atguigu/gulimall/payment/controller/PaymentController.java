package com.atguigu.gulimall.payment.controller;

import com.atguigu.common.config.AlipayTemplate;
import com.atguigu.common.utils.R;
import com.atguigu.common.vo.OrderEntity;
import com.atguigu.common.vo.PayAsyncVo;
import com.atguigu.gulimall.payment.entity.PaymentInfoEntity;
import com.atguigu.gulimall.payment.servcie.PaymentService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AlipayTemplate alipayTemplate;

    @Autowired
    private KafkaTemplate kafkaTemplate;


    @GetMapping("pay.html")
    public String toPay(@RequestParam("orderToken") String orderToken, Model model){

        OrderEntity orderEntity = this.paymentService.queryOrderByOrderToken(orderToken);
        model.addAttribute("orderEntity", orderEntity);
        return "pay";
    }

    @GetMapping("queryOrderByOrderToken")
    public OrderEntity queryOrderByOrderToken(@RequestParam("orderToken") String orderToken){
        return this.paymentService.queryOrderByOrderToken(orderToken);
    }

    @PostMapping("pay/success")
    @ResponseBody
    public String paySuccess(PayAsyncVo payAsyncVo){

        // 1.验签
        Boolean flag = this.alipayTemplate.verifySignature(payAsyncVo);
        if (!flag) {
            //TODO：验签失败则记录异常日志
            return "failure"; // 支付失败
        }

        // 2.验签成功后，按照支付结果异步通知中的描述，对支付结果中的业务内容进行二次校验
        String payId = payAsyncVo.getNotify_id();
        if (StringUtils.isBlank(payId)){
            return "failure";
        }
        PaymentInfoEntity paymentInfoEntity = this.paymentService.queryPayMentById(Long.valueOf(payId));
        if (paymentInfoEntity == null
                || !StringUtils.equals(payAsyncVo.getApp_id(), this.alipayTemplate.getApp_id())
                || !StringUtils.equals(payAsyncVo.getOut_trade_no(), paymentInfoEntity.getOutTradeNo())
                || paymentInfoEntity.getTotalAmount().compareTo(new BigDecimal(payAsyncVo.getBuyer_pay_amount())) != 0){
            return "failure";
        }

        // 3.校验支付状态。根据 trade_status 进行后续业务处理  TRADE_SUCCESS
        if (!StringUtils.equals("TRADE_SUCCESS", payAsyncVo.getTrade_status())) {
            return "failure";
        }

        // 4.正常的支付成功，记录支付记录方便对账
        paymentService.paySuccess(payAsyncVo);

        // 5.发送消息更新订单状态，并减库存
        this.kafkaTemplate.send("order-exchange","order.pay",payAsyncVo.getOut_trade_no());

        // 6.给支付宝成功回执
        return "success";
    }


}
