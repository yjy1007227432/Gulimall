package com.atguigu.gulimall.ware.feign;


import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient
public interface MemberFeignService {


    @RequestMapping(value = "member/memberreceiveaddress/{memberId}/addresses")
    R info(@PathVariable("memberId") Long addrId);
}
