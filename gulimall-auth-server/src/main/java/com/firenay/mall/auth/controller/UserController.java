package com.firenay.mall.auth.controller;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserController {

    /**
     * 测试方法
     * @return
     */
    @RequestMapping("/add")
    public String add() {
        System.out.println("usercontroller.add");
        return "/user/add";
    }

    @RequestMapping("/update")
    public String update() {
        System.out.println("usercontroller.update");
        return "/user/update";
    }

    /**
     * 设置登录跳转页面
     * @return
     */
    @RequestMapping("/toLogin")
    public String toLogin() {
        System.out.println("usercontroller.toLogin");
        return "login";
    }

    /**
     * 测试thymeleaf页面是否可用
     */
    @GetMapping("/test")
    public String testThymeleaf(Model model) {

        model.addAttribute("name","huihui");

        return "test";

    }

    /**
     * 登录逻辑处理
     * @return
     */
    @RequestMapping("/login")
    public String login(String name,String password,Model model) {
        /**
         * 获取Shiro编写认证操作
         */
        //1、获取Subject
        Subject subject = SecurityUtils.getSubject();
        //2、封装用户数据
        UsernamePasswordToken token = new UsernamePasswordToken(name,password);
        //执行登录方法
        try {
            //登录成功
            subject.login(token);
            //跳转到test.html
            return "redirect:test";
        } catch (UnknownAccountException e) {
            // TODO Auto-generated catch block
//			e.printStackTrace();
            //登录失败: 用户名不存在
            model.addAttribute("msg","用户名不存在");
            return "login";
        }catch (IncorrectCredentialsException e) {
            //登录失败: 用户名不存在
            model.addAttribute("msg","密码错误");
            return "login";
        }
    }
}


