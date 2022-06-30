package com.hxut.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxut.common.Result;
import com.hxut.common.exception.CustomException;
import com.hxut.dto.SetmealDto;
import com.hxut.entity.Setmeal;
import com.hxut.entity.SetmealDish;
import com.hxut.mapper.SetmealMapper;
import com.hxut.service.SetMealDIshService;
import com.hxut.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * description: SetmealServiceImpl
 * date: 2022/6/24 18:17
 * author: MR.孙
 */
@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetMealDIshService setMealDIshService;

    /**
     * @description:   新增套餐，同时需要保存菜品和套餐的关联关系
     * @param setmealDto
     * @return: void
     * @author: MR.孙
     * @date: 2022/6/26 11:34
    */
    @Transactional
    @Override
    public void saveMealAndMealDish(SetmealDto setmealDto) {
        //保存套餐信息到setmeal表
        this.save(setmealDto);

        //保存信息到setmealdish表
        List<SetmealDish> mealDishesList = setmealDto.getSetmealDishes();
        //由于传递过来的setmealDto没有封装setmeal_dish的setmealId,所以需要遍历赋值
        //由于dishDto没有setmealId,但是它继承了setmeal表,通过它的id可以给setmealId赋值
        mealDishesList = mealDishesList.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setMealDIshService.saveBatch(mealDishesList);


    }
    /**
     * @description:  删除套餐。同时删除setmeal表和它的关联表的数据
     * @param ids
     * @return: void
     * @author: MR.孙
     * @date: 2022/6/26 19:04
    */
    @Transactional
    @Override
    public void delSetmealAndMealDish(List<Long> ids) {
        //查询套餐状态,确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //select count(*) from setmeal where id in (1,2,3) and status = 1
        //status=1为起售状态不可删除,为0可删除
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        //不可以删除套餐的数量
        int reusltCount = this.count(queryWrapper);
        if(reusltCount>0){
            //如果是不可删除，那么就抛出异常
            throw new CustomException("当前套餐为起售状态,不可删除");
        }

        //如果状态是可以删除,先删除setmeal表
        this.removeByIds(ids);

        //然后删除套餐关系表中的数据setmeal_dish
        //delete from setmeal_dish where setmeal_id in (1,2,3)
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getDishId,ids);
        setMealDIshService.remove(lambdaQueryWrapper);

    }

    /**
     * @description:  查询套餐信息
     * @param id
     * @return: com.hxut.dto.SetmealDto
     * @author: MR.孙
     * @date: 2022/6/28 13:18
    */
    @Override
    public SetmealDto getSetmeal(Long id) {
        //根据id获取套餐信息
        Setmeal setmeal = this.getById(id);
        //套餐中又包含了菜品信息

        SetmealDto setmealDto = new SetmealDto();
        //将setmeal表属性拷贝到setmealDto中
        BeanUtils.copyProperties(setmeal,setmealDto);
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        //根据id查询关联表的套餐中的菜品
        queryWrapper.eq(id!=null,SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setMealDIshService.list(queryWrapper);
        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }

    /**
     * @description:  更改套餐信息
     * @param setmealDto
     * @return: void
     * @author: MR.孙
     * @date: 2022/6/28 13:18
    */
    @Override
    public void updateSetmealInfo(SetmealDto setmealDto) {

       if(setmealDto.getSetmealDishes()==null){
            Result.error("该套餐没有菜品,请添加菜品后重试");
       }
        //因为setmealDto包含了setmeal表和其关联表setmeal_dish的信息
        //向setmeal表更新信息
//        Setmeal setmeal = new Setmeal();
//        setmeal.setId(setmealDto.getId());
//        setmeal.setCategoryId(setmealDto.getCategoryId());
//        setmeal.setName(setmealDto.getName());
//        setmeal.setPrice(setmealDto.getPrice());
//        setmeal.setStatus(setmealDto.getStatus());
//        setmeal.setDescription(setmealDto.getDescription());
//        setmeal.setImage(setmealDto.getImage());
        this.updateById(setmealDto);

        //向setmeal_dish表更新信息
        //为了方便,可以先清空在插入,这样不会导致有的有数据，有的没有
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        Long setmealId = setmealDto.getId();

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealId);
        setMealDIshService.remove(queryWrapper);

        //为setmeal_dish表填充相关的属性
        for(SetmealDish setmealDish:setmealDishes){
            setmealDish.setSetmealId(setmealId);
        }

        //保存到setmeal_dish表
        setMealDIshService.saveBatch(setmealDishes);

    }
}
