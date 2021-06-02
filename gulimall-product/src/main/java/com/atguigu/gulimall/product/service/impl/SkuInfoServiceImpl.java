package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.SkuInfoDao;
import com.atguigu.gulimall.product.entity.SkuImagesEntity;
import com.atguigu.gulimall.product.entity.SkuInfoEntity;
import com.atguigu.gulimall.product.service.SkuImagesService;
import com.atguigu.gulimall.product.service.SkuInfoService;
import com.atguigu.gulimall.product.service.SkuSaleAttrValueService;
import com.atguigu.gulimall.product.vo.ItemSaleAttrVo;
import com.atguigu.gulimall.product.vo.SkuItemVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {


    @Autowired
    private SkuImagesService imagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {

        List<SkuInfoEntity> skus = baseMapper.selectList(
                new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
        return skus;

    }

    @Override
    public SkuItemVo item(Long skuId) {
        /**
         * 所谓异步调用其实就是实现一个可无需等待被调用函数的返回值而让操作继续运行的方法。
         * 在 Java 语言中，简单的讲就是另启一个线程来完成调用中的部分计算，使调用继续运行或返回，
         * 而不需要等待计算结果,但调用者仍需要取线程的计算结果。
         * JDK5新增了Future接口，用于描述一个异步计算的结果。
         * 虽然 Future 以及相关使用方法提供了异步执行任务的能力，但是对于结果的获取却是很不方便，
         * 只能通过阻塞或者轮询的方式得到任务的结果。阻塞的方式显然和我们的异步编程的初衷相违背，
         * 轮询的方式又会耗费无谓的 CPU 资源，而且也不能及时地得到计算结果。
         */


        SkuItemVo skuItemVo = new SkuItemVo();

        //但是suppyAsync将Supplier作为参数并返回CompletableFuture<U>with结果值
        // ，这意味着它不接受任何输入参数，而是将result作为输出返回。
        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(()-> {
            //获取sku
            SkuInfoEntity sku = getById(skuId);
            skuItemVo.setInfo(sku);
            return sku;
        },executor);

        //runAsync将Runnable作为输入参数并返回CompletableFuture<Void>，这意味着它不返回任何结果。
        CompletableFuture<Void> imgageFuture = CompletableFuture.runAsync(()-> {
            //获取sku图片
            List<SkuImagesEntity> images = imagesService.getImagesBySkuId(skuId);
            skuItemVo.setImages(images);
        },executor);
        /**
         * 结论：因此，如果您希望返回结果，则选择，supplyAsync或者如果您只想运行异步操作，则选择runAsync。
         */

        CompletableFuture<Void> saleAttrFuture = infoFuture.thenAcceptAsync((res)->{
            List<ItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.
                    getSaleAttrsBySpuId(res.getSpuId());
            skuItemVo.setSaleAttr(saleAttrVos);
        },executor);



        return null;
    }

}