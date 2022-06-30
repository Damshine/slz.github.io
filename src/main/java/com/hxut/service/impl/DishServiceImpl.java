package com.hxut.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxut.dto.DishDto;
import com.hxut.entity.Dish;
import com.hxut.entity.DishFlavor;
import com.hxut.mapper.DishMapper;
import com.hxut.service.DishFlavorService;
import com.hxut.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * description: DishServiceImpl
 * date: 2022/6/24 18:16
 * author: MR.孙
 */


@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    /**
     * @description:  新增菜品,并且保存对应的菜品口味
     * @param null
     * @return:
     * @author: MR.孙
     * @date: 2022/6/25 14:11
    */


   @Autowired
   private DishFlavorService dishFlavorService;

   /**
    * @description:  新增菜品。同时插入菜品对应的口味，也就是要插入dish表和dish_flavor表。需要操作两张表dish,dish_flavor
    * @param dishDto
    * @return: void
    * @author: MR.孙
    * @date: 2022/6/26 0:29
   */
   @Transactional
    @Override
    public void setWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到dish表
       this.save(dishDto);

       //经过调试发现dishDto中封装的List<DishFlavor>只有name和value属性，没有dish_id这是不行的
        //dish_id是关联字段,所以我们要给dish_id设置值

        //拿到dish表的id然后对DishFlavor的dish_id进行赋值
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors=flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据到口味表dish_flavor
        dishFlavorService.saveBatch(flavors);

    }


    /**
     * @description:  根据id查询菜品信息和对应的口味信息
     * @param id
     * @return: com.hxut.dto.DishDto
     * @author: MR.孙
     * @date: 2022/6/25 19:44
    */
    @Transactional
    @Override
    public DishDto getDishAndFlavor(Long id) {
        DishDto dishDto = new DishDto();
        //查询id查询菜品信息
        Dish dish = this.getById(id);

        //将菜品信息拷贝到dishDto
        BeanUtils.copyProperties(dish,dishDto);

        //根据id查询List<Flavor>口味信息
        //添加条件构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();

        //添加添加
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(list);


        return dishDto;
    }

    /**
     * @description:  更新菜品和口味信息
     * @param dishDto
     * @return: void
     * @author: MR.孙
     * @date: 2022/6/25 23:31
    */
    @Transactional
    @Override
    public void updateDishAndFlavor(DishDto dishDto) {
        //更新dish表的信息
        this.updateById(dishDto);

        //更新口味表,如果传过来了多个口味，那么更新一条一条的十分麻烦，所以这里有个方案就是先删除口味
        //然后在插入多条口味，这样就会简单很多

        //清除当前菜品对应的口味数据--dlete操作
        //添加条件构造器
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();

        //添加条件
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());

        //执行删除操作
        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据--insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors=flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        //添加操作
        dishFlavorService.saveBatch(flavors);

    }
}
