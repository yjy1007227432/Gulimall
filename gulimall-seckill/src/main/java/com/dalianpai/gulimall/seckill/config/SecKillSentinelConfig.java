package com.dalianpai.gulimall.seckill.config;

import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.fastjson.JSON;
import com.dalianpai.common.exception.BizCodeEnume;
import com.dalianpai.common.utils.R;
import org.springframework.context.annotation.Configuration;

/**
 * @author WGR
 * @create 2020/8/20 -- 21:59
 */
@Configuration
public class SecKillSentinelConfig {

    public SecKillSentinelConfig(){
        WebCallbackManager.setUrlBlockHandler((request, response, exception) -> {
            R error = R.error(BizCodeEnume.TO_MANY_REQUEST.getCode(), BizCodeEnume.TO_MANY_REQUEST.getMessage());
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.getWriter().write(JSON.toJSONString(error));
        });
    }
}
