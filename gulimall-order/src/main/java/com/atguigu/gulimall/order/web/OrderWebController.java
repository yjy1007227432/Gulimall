package com.atguigu.gulimall.order.web;

import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.vo.OrderSubmitVo;
import com.atguigu.gulimall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

/**
 * <p>Title: OrderController</p>
 * Description：订单
 * date：2020/6/29 22:35
 */
@Controller
public class OrderWebController {

	@Autowired
	private OrderService orderService;

	@GetMapping("/toTrade")
	public String toTrade(Model model) throws ExecutionException, InterruptedException {
		OrderConfirmVo confirmVo = orderService.confirmOrder();

		model.addAttribute("orderConfirmData", confirmVo);
		return "confirm";
	}

	/**
	 * 下单功能
	 */
	//2.提交订单
	@PostMapping("/submitOrder")
	public String submitOrder(OrderSubmitVo vo,Model model,RedirectAttributes redirectAttributes) {
		SubmitOrderResponseVo responseVo = orderService.submitOrder(vo);
		//下单：去创建订单，验令牌，验库存...
		if (responseVo.getCode() == 0) {
			//下单成功,来到支付选择业
			System.out.println("订单提交成功");
			model.addAttribute("submitOrderResp",responseVo);
			return "pay";
		} else {
			//下单失败,回到订单确认页重新确认订单
			System.out.println("订单提交失败。。。");
			String msg="订单提交失败";
			switch (responseVo.getCode()){
				case 1: msg+="，订单信息过期,请刷新再次提交";break;
				case 2: msg+="，订单商品价格发生变化,请确认后再次提交";break;
				case 3: msg+="，库存锁定失败，商品库存不足";break;
			}
			redirectAttributes.addFlashAttribute("msg",msg);
			return "redirect:http://order.gulimall.com/toTrade";
		}
	}

}
