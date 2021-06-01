package com.dalianpai.gulimall.seckill.config;


import com.dalianpai.gulimall.seckill.interceptor.LoginUserInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author WGR
 * @create 2020/8/19 -- 20:04
 */
@Configuration
public class SeckillWebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginUserInterceptor loginUserInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        SentinelWebMvcConfig config = new SentinelWebMvcConfig();
//        config.setBlockExceptionHandler((request, response, exception) -> {
//            R error = R.error(BizCodeEnume.TO_MANY_REQUEST.getCode(), BizCodeEnume.TO_MANY_REQUEST.getMessage());
//            response.setCharacterEncoding("UTF-8");
//            response.setContentType("application/json");
//            response.getWriter().write(JSON.toJSONString(error));
//        });
//        config.setHttpMethodSpecify(true);
        registry.addInterceptor(loginUserInterceptor).addPathPatterns("/**");
 //       registry.addInterceptor(new SentinelWebInterceptor(config)).addPathPatterns("/**");
    }
}
