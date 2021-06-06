package com.atguigu.gulimall.search.service;

import com.atguigu.common.vo.SkuEsModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface ProductSaveService {

    boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;

}
