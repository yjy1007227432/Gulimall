package com.atguigu.gulimal.cart.to;

import lombok.Data;
import lombok.ToString;

//用户信息
@Data
@ToString
public class UserInfoTo {
    private Long userId;//如果没有用户id，说明为登录，一定封装临时用户
    //临时用户
    private String userKey;//一定封装
    //是否为临时用户
    private Boolean tempUser=false;
}
