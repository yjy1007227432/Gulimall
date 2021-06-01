package com.dalianpai.gulimall.seckill.interceptor;

import com.dalianpai.common.constant.AuthServerConstant;
import com.dalianpai.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author WGR
 * @create 2020/7/27 -- 21:52
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberRespVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        boolean match = new AntPathMatcher().match("/kill", uri);
        if(match ){
            MemberRespVo attribute = (MemberRespVo)request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
            if(attribute !=null){
                loginUser.set(attribute);
                return true;
            }else{
                request.getSession().setAttribute("msg","请先进行登录");
                response.sendRedirect("http://auth.gulimall.com/login.html");
                return false;
            }
        }

        return true;

    }
}
