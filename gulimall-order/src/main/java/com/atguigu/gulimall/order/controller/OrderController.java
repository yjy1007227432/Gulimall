package com.atguigu.gulimall.order.controller;

import com.alipay.api.AlipayApiException;
import com.atguigu.common.excepiton.OrderException;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.order.config.AlipayTemplate;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.feign.PaymentFeignService;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.service.PaymentInfoService;
import com.atguigu.gulimall.order.vo.PayVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;



/**
 * 订单
 *
 * @author yaojunyi
 * @email yao_junyi@qq.com
 * @date 2021-05-18 16:57:07
 */
@RestController
@RequestMapping("order/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private AlipayTemplate alipayTemplate;

    @Autowired
    private PaymentInfoService paymentInfoService;


    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("order:order:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPage(params);

        return R.ok().put("page", page);
    }

    @GetMapping("token/{orderSn}")
    public R queryOrderByOrderSn(@PathVariable("orderSn")String orderSn){
        OrderEntity orderEntity = this.orderService.getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
        return R.ok().put("orderEntity",orderEntity);
    }

    @GetMapping("alipay.html")
    @ResponseBody
    public String alipay(@RequestParam("orderToken") String orderToken) {
        // 校验订单状态
        OrderEntity orderEntity = paymentInfoService.queryOrderByOrderToken(orderToken);
        if (orderEntity.getStatus() != 0){
            throw new OrderException("此订单无法支付，可能已经过期！");
        }
        // 调用支付宝接口获取支付表单
        PayVo payVo = new PayVo();
        payVo.setOut_trade_no(orderEntity.getOrderSn());
        payVo.setTotal_amount("0.01");
        payVo.setSubject("谷粒商城支付平台");
        // 把支付信息保存到数据库
        Boolean payId = orderService.save(orderEntity);
        payVo.setTotal_amount("0.01");
        String form = null;
        try {
            form = alipayTemplate.pay(payVo);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new OrderException("支付出错，请刷新后重试！");
        }
        // 跳转到支付页
        return form;


    }




    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("order:order:info")
    public R info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("order:order:save")
    public R save(@RequestBody OrderEntity order){
		orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
   // @RequiresPermissions("order:order:update")
    public R update(@RequestBody OrderEntity order){
		orderService.updateById(order);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("order:order:delete")
    public R delete(@RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
