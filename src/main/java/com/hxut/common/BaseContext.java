package com.hxut.common;

/**
 * description: BaseContext
 * date: 2022/6/24 13:30
 * author: MR.孙
 */
public class BaseContext {
    private static final ThreadLocal<Long> threadLocal=new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
    public static Long  getCurreantId(){
        return threadLocal.get();
    }
}
