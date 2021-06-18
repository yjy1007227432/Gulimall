package com.atguigu.gulimal.cart.vo;//整个购物车
//整个购物车存放的商品信息。需要计算的属性需要重写get方法，保证每次获取属性都会进行计算

import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

public class Cart {
    //购物车的购物项
    private List<CartItem> items;
    //商品数量
    private Integer countNum;
    //商品类型数量,几种不同的商品
    private Integer countType;
    //商品总价
    private BigDecimal totalAmount;
    //商品减免价格,优惠多少
    private BigDecimal reduce = new BigDecimal("0.00");

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Integer getCountNum() {
        int count = 0;
        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                count += item.getCount();
            }
        }
        return count;
    }

    public Integer getCountType() {
        int count = 0;
        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                count += 1;
            }
        }
        return count;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0");
        // 1.计算购物项总价
        if (!CollectionUtils.isEmpty(items)) {
            for (CartItem item : items) {
                if (item.getCheck()) {
                    amount = amount.add(item.getTotalPrice());
                }
            }
        }
        // 2.计算优惠后的价格
        return amount.subtract(getReduce());
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}

