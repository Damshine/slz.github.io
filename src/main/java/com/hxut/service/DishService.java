package com.hxut.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxut.dto.DishDto;
import com.hxut.entity.Dish;

public interface DishService extends IService<Dish> {
    //新增菜品。同时插入菜品对应的口味，也就是要插入dish表和dish_flavor表。需要操作两张表dish,dish_flavor
    void setWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和对应的口味信息
    DishDto getDishAndFlavor(Long id);

    //根据提交的信息更新菜品和口味信息
    void updateDishAndFlavor(DishDto dishDto);
}
