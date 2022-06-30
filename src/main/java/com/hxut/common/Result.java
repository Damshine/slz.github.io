package com.hxut.common;

import com.baomidou.mybatisplus.extension.api.R;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * description: Result
 * date: 2022/6/23 11:31
 * author: MR.孙
 */
@SuppressWarnings("all")
@Data
public class Result<T> implements Serializable {

    private Integer code;
    private String msg;
    private T data;
    private Map map = new HashMap(); //动态数据

    public static <T> Result<T> success(T data){
        Result<T> rs=new Result<T>();
        rs.data=data;
        rs.code=1;
        return rs;

    }

    public static <T> Result<T> error(String msg) {
        Result<T> rs = new Result<T>();
        rs.msg = msg;
        rs.code = 0;
        return rs;
    }

    public Result<T> add(String key,Object value){
        this.map.put(key,value);
        return this;
    }

}
