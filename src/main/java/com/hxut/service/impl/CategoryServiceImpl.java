package com.hxut.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxut.common.exception.CustomException;
import com.hxut.entity.Category;
import com.hxut.entity.Dish;
import com.hxut.entity.Setmeal;
import com.hxut.mapper.CategoryMapper;
import com.hxut.service.CategoryService;
import com.hxut.service.DishService;
import com.hxut.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;


    /**
     * @description:  根据id删除分类信息
     * @param id
     * @return: void
     * @author: MR.孙
     * @date: 2022/6/24 19:43
    */
    @Override
    public void remove(Long id) {
        //添加查询条件，根据分类id进行查询
        LambdaQueryWrapper<Dish> lbdDish=new LambdaQueryWrapper<>();
        lbdDish.eq(Dish::getCategoryId,id);
        int relationCount1 = dishService.count(lbdDish);
        //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        if(relationCount1>0){
            //已经关联菜品，抛出一个业务异常
            throw  new CustomException("当前分类下关联了菜品，不能删除");
        }

        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> lbdMeal=new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        lbdMeal.eq(Setmeal::getCategoryId,id);
        int relationCount2 = setmealService.count(lbdMeal);
        if(relationCount2>0){
            //已经关联套餐，抛出一个业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        //正常删除分类
        super.removeById(id);

    }
}
