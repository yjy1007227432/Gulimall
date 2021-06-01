package com.atguigu.gulimall.search.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("product/html")
public class TestControl {

    @RequestMapping("/index")
    public String index(){
        return "index"; //当浏览器输入/index时，会返回 /templates/home.html页面
    }

    @RequestMapping("/list")
    public String test(){
        return "list";
    }

}
