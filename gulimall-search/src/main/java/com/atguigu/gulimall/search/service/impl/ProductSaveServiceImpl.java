package com.atguigu.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.common.vo.SkuEsModel;
import com.atguigu.gulimall.search.Constant.EsConstant;
import com.atguigu.gulimall.search.config.MallElasticSearchConfig;
import com.atguigu.gulimall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {
    @Resource
    private RestHighLevelClient client;

    @Override
    public boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException {
        //建立索引
        BulkRequest bulkRequest = new BulkRequest();
        for (SkuEsModel esModel : skuEsModels){
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            //设置索引id
            indexRequest.id(esModel.getSkuId().toString());
            indexRequest.type();
            indexRequest.source(JSON.toJSON(esModel),XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        BulkResponse bulk = client.bulk(bulkRequest, MallElasticSearchConfig.COMMON_OPTIONS);
        if (bulk.hasFailures()){
            /**
             * Arrays.stream  将数组转换成流
             */
            List<String> collect = Arrays.stream(bulk.getItems()).map(item -> item.getId()).collect(Collectors.toList());
            log.error("商品上架错误: {}",collect);
        }
        return bulk.hasFailures();


    }
}
