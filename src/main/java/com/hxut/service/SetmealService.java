package com.hxut.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hxut.dto.SetmealDto;
import com.hxut.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    //新增套餐，同时需要保存菜品和套餐的关联关系
    void saveMealAndMealDish(SetmealDto setmealDto);

    //删除套餐。同时删除setmeal表和它的关联表的数据
    void delSetmealAndMealDish(List<Long> ids);

    //查询套餐信息
    SetmealDto getSetmeal(Long id);
    //更改套餐信息
    void updateSetmealInfo(SetmealDto setmealDto);
}
