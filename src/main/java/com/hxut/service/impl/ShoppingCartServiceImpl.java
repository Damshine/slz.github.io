package com.hxut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxut.entity.ShoppingCart;
import com.hxut.mapper.ShoppingCartMapper;
import com.hxut.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * description: ShoppingCartServiceImpl
 * date: 2022/6/27 17:44
 * author: MR.孙
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
