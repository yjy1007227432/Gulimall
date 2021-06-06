package com.atguigu.gulimall.search.service;


import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;
import org.springframework.stereotype.Service;


@Service
public interface MallService {
    SearchResult getSearchResult(SearchParam searchParam);
}
