package com.atguigu.gulimall.order.controller;


import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrderWebController {
    @Autowired
    OrderService orderService;
    //结算
    @GetMapping("/toTrade")
    public String toTrade(Model model){
        //展示订单确认的数据
        OrderConfirmVo orderConfirmVo= orderService.confirmOrder();
        System.out.println("订单确认的数据: "+ orderConfirmVo);
        model.addAttribute("orderConfirmData",orderConfirmVo);
        return "confirm";
    }


}
