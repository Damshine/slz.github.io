package com.hxut.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxut.entity.Orders;

public interface OrdersService extends IService<Orders> {
    void submit(Orders orders);
}
