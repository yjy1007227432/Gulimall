package com.atguigu.gulimall.product.controller;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("product/html")
public class TestControl {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping("/item")
    public String test(){
        return "item";
    }

    @GetMapping({"/", "index.html"})
    public String getIndex(Model model) {
        //获取所有的一级分类
        List<CategoryEntity> catagories = categoryService.getLevel1Categorys();
        model.addAttribute("catagories", catagories);
        return "index";
    }



}
