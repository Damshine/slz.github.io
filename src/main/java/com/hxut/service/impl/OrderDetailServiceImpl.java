package com.hxut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxut.entity.OrderDetail;
import com.hxut.mapper.OrderDetailMapper;
import com.hxut.service.OrderDetailService;
import org.springframework.stereotype.Service;

/**
 * description: OrderDetailServiceImpl
 * date: 2022/6/27 20:30
 * author: MR.孙
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
