package com.atguigu.gulimal.cart.vo;

import java.math.BigDecimal;
import java.util.List;

//购物车的购物项内容
public class CartItem {
    //商品Id
    private Long skuId;
    //是否选中商品
    private Boolean check = true;
    //商品名称
    private String title;
    //商品图片
    private String image;
    //商品属性
    private List<String> skuAttr;
    //价格
    private BigDecimal price;
    //商品数量
    private Integer count;
    //总价格
    private BigDecimal totalPrice;
    //商品销售属性



    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getSkuAttr() {
        return skuAttr;
    }

    public void setSkuAttr(List<String> skuAttr) {
        this.skuAttr = skuAttr;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * 计算总价格:价格*数量
     */
    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal(""+this.count));
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}

