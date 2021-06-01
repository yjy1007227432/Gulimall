package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("index/json")
public class IndexControl {

    @Autowired
    private CategoryService categoryService;


    @ResponseBody
    @RequestMapping("/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatlogJson() {

        Map<String, List<Catelog2Vo>> map = categoryService.getCatelogJson();
        return map;
    }

    @ResponseBody
    @RequestMapping("/catalog")
    public Map<String, List<Catelog2Vo>> getCatlogJson2() {

        Map<String, List<Catelog2Vo>> map = categoryService.getCatelogJson();
        return map;
    }

}
