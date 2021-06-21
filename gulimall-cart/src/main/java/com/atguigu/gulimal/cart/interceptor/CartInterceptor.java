package com.atguigu.gulimal.cart.interceptor;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.constant.CartConstant;
import com.atguigu.common.vo.MemberRespVo;
import com.atguigu.gulimal.cart.to.UserInfoTo;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

//在目标方法执行之前,判断用户的登陆状态。并封包传递给controller的目标请求
//@Component
public class CartInterceptor implements HandlerInterceptor {
    //threadLocal，保证拦截器-->controller-->service-->dao是一个线程执行（同一个线程共享数据）
    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal();

    //目标方法执行前。判断是否成功
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        MemberRespVo member = (MemberRespVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        UserInfoTo userInfo = new UserInfoTo();
        if (member != null) {
            //用户已登录
            userInfo.setUserId(member.getId());
        }

        //未登录，一定分配临时用户user-key
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                //user-key,判断cookie名字是否相同，相同就是登录成功
                String name = cookie.getName();
                if (name.equals(CartConstant.TEMP_USER_COOKIE_NAME)) {
                    userInfo.setUserKey(cookie.getValue());
                    //是临时用户
                    userInfo.setTempUser(true);
                }
            }
        }

        //如果没有临时用户,一定分配一个临时用户
        if (StringUtils.isEmpty(userInfo.getUserKey())) {
            String uuid = UUID.randomUUID().toString();
            userInfo.setUserKey(uuid);
        }

        //目标方法执行之前,数据放入threadLocal。
        threadLocal.set(userInfo);
        return true;
    }

    //目标方法执行后：分配临时用户，让浏览器保存
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfo = threadLocal.get();
        //判断是否有临时用户。如果没有临时用户，一定保存一个临时用户
        if (!userInfo.getTempUser()) {
            //持续延长临时用户的过期时间
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfo.getUserKey());
            cookie.setDomain("gulimall.com");//cook域名
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIME_OUT);//cookie过期时间，一个月
            response.addCookie(cookie);
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 调用删除方法，是必须选项。因为使用的是tomcat线程池，请求结束后，线程不会结束。
        // 如果不手动删除线程变量，可能会导致内存泄漏
        threadLocal.remove();
    }
}
