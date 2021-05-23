package com.atguigu.gulimall.product.service.impl;

import com.atguigu.gulimall.product.controller.SkuInfoController;
import com.atguigu.gulimall.product.entity.*;
import com.atguigu.gulimall.product.feign.CouponFeignService;
import com.atguigu.gulimall.product.feign.SearchFeignService;
import com.atguigu.gulimall.product.feign.WareFeignService;
import com.atguigu.gulimall.product.service.*;
import com.atguigu.gulimall.product.vo.SkuEsModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.product.dao.SpuInfoDao;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {
        return null;
    }

    @Override
    public void up(Long spuId) {

    }

    // SpuInfoServiceImpl
//    public void upSpuForSearch(Long spuId) {
//        //1、查出当前spuId对应的所有sku信息,品牌的名字
//        List<SkuInfoEntity> skuInfoEntities=skuInfoService.getSkusBySpuId(spuId);
//        //TODO 4、根据spu查出当前sku的所有可以被用来检索的规格属性
//        List<ProductAttrValueEntity> productAttrValueEntities = productAttrValueService.list(new QueryWrapper<ProductAttrValueEntity>().eq("spu_id", spuId));
//        List<Long> attrIds = productAttrValueEntities.stream().map(attr -> {
//            return attr.getAttrId();
//        }).collect(Collectors.toList());
//        List<Long> searchIds=attrService.selectSearchAttrIds(attrIds);
//        Set<Long> ids = new HashSet<>(searchIds);
//        List<SkuEsModel.Attr> searchAttrs = productAttrValueEntities.stream().filter(entity -> {
//            return ids.contains(entity.getAttrId());
//        }).map(entity -> {
//            SkuEsModel.Attr attr = new SkuEsModel.Attr();
//            BeanUtils.copyProperties(entity, attr);
//            return attr;
//        }).collect(Collectors.toList());
//
//
//        //TODO 1、发送远程调用，库存系统查询是否有库存
//        Map<Long, Boolean> stockMap = null;
//        try {
//            List<Long> longList = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
//            List<SkuHasStockVo> skuHasStocks = wareFeignService.getSkuHasStocks(longList);
//            stockMap = skuHasStocks.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
//        }catch (Exception e){
//            log.error("远程调用库存服务失败,原因{}",e);
//        }
//
//        //2、封装每个sku的信息
//        Map<Long, Boolean> finalStockMap = stockMap;
//        List<SkuEsModel> skuEsModels = skuInfoEntities.stream().map(sku -> {
//            SkuEsModel skuEsModel = new SkuEsModel();
//            BeanUtils.copyProperties(sku, skuEsModel);
//            skuEsModel.setSkuPrice(sku.getPrice());
//            skuEsModel.setSkuImg(sku.getSkuDefaultImg());
//            //TODO 2、热度评分。0
//            skuEsModel.setHotScore(0L);
//            //TODO 3、查询品牌和分类的名字信息
//            BrandEntity brandEntity = brandService.getById(sku.getBrandId());
//            skuEsModel.setBrandName(brandEntity.getName());
//            skuEsModel.setBrandImg(brandEntity.getLogo());
//            CategoryEntity categoryEntity = categoryService.getById(sku.getCatalogId());
//            skuEsModel.setCatalogName(categoryEntity.getName());
//            //设置可搜索属性
//            skuEsModel.setAttrs(searchAttrs);
//            //设置是否有库存
//            skuEsModel.setHasStock(finalStockMap==null?false:finalStockMap.get(sku.getSkuId()));
//            return skuEsModel;
//        }).collect(Collectors.toList());
//
//
//        //TODO 5、将数据发给es进行保存：gulimall-search
//        R r = searchFeignService.saveProductAsIndices(skuEsModels);
//        if (r.getCode()==0){
//            this.baseMapper.upSpuStatus(spuId, ProductConstant.ProductStatusEnum.SPU_UP.getCode());
//        }else {
//            log.error("商品远程es保存失败");
//        }
//    }


}