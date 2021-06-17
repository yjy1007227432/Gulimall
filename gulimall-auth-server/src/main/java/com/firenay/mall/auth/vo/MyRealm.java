package com.firenay.mall.auth.vo;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * 自定义Realm
 * 需要继承AuthorizingRealm类
 * 会实现两个方法
 * @author jojo
 *
 */
public class MyRealm extends AuthorizingRealm{
    /**
     * 执行授权
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // TODO Auto-generated method stub
        return null;
    }
    /**
     * 执行认证逻辑
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken arg) throws AuthenticationException {
        System.out.println("执行认证"+arg);
        //假设获取数据库用户名与密码
        String name = "wuhui";;
        String password = "123";
        //编写Shiro判断逻辑，判断用户名与密码
        //判断用户名
        UsernamePasswordToken token = (UsernamePasswordToken)arg;
        //如果返回的值与数据库中是用户名不匹配
        if(!token.getUsername().equals(name)) {
            //shiro底层会抛出一个异常
            return null;
        }
        //判断密码
        return new SimpleAuthenticationInfo("",password,"");
    }
}



