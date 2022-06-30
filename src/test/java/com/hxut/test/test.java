package com.hxut.test;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * description: test
 * date: 2022/6/26 0:19
 * author: MR.孙
 */
@SpringBootTest
public class test {
    public Long id;
    @Test
    void test(){
        System.out.println("使用IDWorker获取一个雪花算法生成的id:"+IdWorker.getId());
        System.out.println("使用IDWorker获取一个32位UUID:"+IdWorker.get32UUID());
    }
}
