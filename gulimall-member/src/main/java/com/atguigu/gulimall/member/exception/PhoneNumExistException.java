package com.atguigu.gulimall.member.exception;

public class PhoneNumExistException extends RuntimeException{
    public PhoneNumExistException() {
        super("手机号存在");
    }
}
