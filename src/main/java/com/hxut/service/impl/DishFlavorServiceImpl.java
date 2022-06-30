package com.hxut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxut.entity.Dish;
import com.hxut.entity.DishFlavor;
import com.hxut.mapper.DishFlavorMapper;
import com.hxut.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
