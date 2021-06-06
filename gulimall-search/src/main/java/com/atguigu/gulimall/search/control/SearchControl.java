package com.atguigu.gulimall.search.control;


import com.atguigu.gulimall.search.service.MallService;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@Slf4j
@RequestMapping("search/search")
public class SearchControl {

    @Autowired
    private MallService mallService;


    @GetMapping(value = {"/search.html","/"})
    public String getSearchPage(SearchParam searchParam, // 检索参数，
                                Model model, HttpServletRequest request) {
        searchParam.set_queryString(request.getQueryString());//_queryString是个字段
        SearchResult result= mallService.getSearchResult(searchParam);
        model.addAttribute("result", result);
        return "search";
    }


}
