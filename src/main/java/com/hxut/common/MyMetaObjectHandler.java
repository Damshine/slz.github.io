package com.hxut.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * description: MyMetaObjectHandler 插入时，处理元数据的配置
 * date: 2022/6/24 10:27
 * author: MR.孙
 */
@SuppressWarnings("all")
@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * @description:  插入时，填充操作
     * @param metaObject
     * @return: void
     * @author: MR.孙
     * @date: 2022/6/24 10:32
    */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段填充[insert]...");
        log.info(metaObject.toString());
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("createUser", BaseContext.getCurreantId());
        metaObject.setValue("updateUser", BaseContext.getCurreantId());

    }
    /**
     * @description:  更新时，自动填充操作
     * @param metaObject
     * @return: void
     * @author: MR.孙
     * @date: 2022/6/24 10:32
    */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段填充[update]...");
        log.info(metaObject.toString());
        log.info("当前线程id:{}",Thread.currentThread().getId());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.getCurreantId());
    }
}
