package com.hxut.common.exception;

/**
 * description: CustomException自定义异常处理
 * date: 2022/6/24 19:03
 * author: MR.孙
 */
public class CustomException extends RuntimeException{
    public CustomException(String msg) {
       super(msg);
    }
}
