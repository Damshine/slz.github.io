package com.hxut.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxut.common.Result;
import com.hxut.common.exception.CustomException;
import com.hxut.dto.DishDto;
import com.hxut.entity.Category;
import com.hxut.entity.Dish;
import com.hxut.entity.DishFlavor;
import com.hxut.service.CategoryService;
import com.hxut.service.DishFlavorService;
import com.hxut.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * description: DishController
 * date: 2022/6/25 9:02
 * author: MR.孙
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * @description: 新增菜品
     * @param dishDto
     * @return: com.hxut.common.Result<java.lang.String>
     * @author: MR.孙
     * @date: 2022/6/25 23:25
    */
    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto){
        log.info("提交菜品的数据为:{}",dishDto.toString());
        dishService.setWithFlavor(dishDto);
        return Result.success("新增菜品成功");
    }

    /**
     * @description:  分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return: com.hxut.common.Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
     * @author: MR.孙
     * @date: 2022/6/25 18:15
    */
    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize,String name){
        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(Strings.isNotEmpty(name),Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getUpdateTime);//降序排序

        //因为，前端需要一个categoryName来显示菜品分类,而我们并没有这个字段，所以我们需要处理一下
        //我们的DishDto有categoryName这个字段并且继承所有dish的属性,
        //只需要处理page的records集合即可，因为这里面是泛型代表的实体信息
        Page<DishDto> dishDtoPage = new Page<>();


        //执行分页查询
        dishService.page(pageInfo,queryWrapper);


        //把pageInfo除了records以外所有属性拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        //我们对pageInfo的records进行处理,让它拥有pageInfo的records属性和categoryName属性
        //item代表Dish这个实体
        List<DishDto> dishDtos=records.stream().map((item)->{
            DishDto dishDto=new DishDto();
            Long categoryId = item.getCategoryId();//获取dish表的id
            Category category=categoryService.getById(categoryId);//通过id获取当前菜品数据

            if(category!=null){
                String categoryName = category.getName();//通过dish_id获取当前名称
                dishDto.setCategoryName(categoryName);//这样dishDto的categoryName就有值了
            }
            //但是其他属性还是null,需要拷贝一下dish属性
            BeanUtils.copyProperties(item,dishDto);

            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(dishDtos);

        return Result.success(dishDtoPage);
    }


    /**
     * @description:  根据id查询菜品信息和对应的口味信息
     * @param id
     * @return: com.hxut.common.Result<com.hxut.dto.DishDto>
     * @author: MR.孙
     * @date: 2022/6/25 19:41
    */
    @GetMapping("/{id}")
    public Result<DishDto> get(@PathVariable Long id){
        log.info("当前菜品id->{}",id);

        DishDto dishAndFlavor = dishService.getDishAndFlavor(id);

        return Result.success(dishAndFlavor);
    }

    /**
     * @description: 更新菜品和口味信息
     * @param dishDto
     * @return: com.hxut.common.Result<java.lang.String>
     * @author: MR.孙
     * @date: 2022/6/25 23:37
    */
    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto){
        log.info("提交的修改菜品和口味数据为->{}",dishDto.toString());
        dishService.updateDishAndFlavor(dishDto);
        return Result.success("修改菜品成功");
    }

    /**
     * @description:  根据条件查询菜品信息
     * @param dish
     * @return: com.hxut.common.Result<java.util.List<com.hxut.entity.Dish>>
     * @author: MR.孙
     * @date: 2022/6/26 9:05
     */
//    @GetMapping("/list")
//    public Result<List<Dish>> list(Dish dish){
//        log.info("当前传递的菜品id->:{}",dish.getCategoryId());
//
//        //添加条件构造器
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//
//        //添加查询条件
//        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//
//        //添加条件，菜品状态(起售状态)为1的
//        queryWrapper.eq(Dish::getStatus,1);
//
//        //添加排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        //因为菜品分类下可能有多个菜
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return Result.success(list);
//    }

    @GetMapping("/list")
    public Result<List<DishDto>> list(Dish dish){
        log.info("当前传递的菜品id->:{}",dish.getCategoryId());
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);


        List<DishDto> dishDtoList=list.stream().map((item)->{
            DishDto dishDto = new DishDto();
            //属性拷贝
            BeanUtils.copyProperties(item,dishDto);
            //获取分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询所有分类
            Category category = categoryService.getById(categoryId);

            //判断是否分类为空
            if(category!=null){
                String name = category.getName();
                dishDto.setCategoryName(name);
            }
            //当前菜品id
            Long id = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,id);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());


        return Result.success(dishDtoList);
    }

    /**
     * @description:  批量起售/停售
     * @param status
     * @param ids
     * @return: com.hxut.common.Result<java.lang.String>
     * @author: MR.孙
     * @date: 2022/6/28 9:52
    */
    @PostMapping("/status/{status}")
    public Result<String> updateStatus(@PathVariable Integer status,Long[] ids){
        log.info("批量起售/停售状态->:{},传递的ids数量->:{}",status,ids.length);

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids.length>0,Dish::getId,ids);
        //先根据传过来的ids进行查询菜品查询
        List<Dish> list = dishService.list(queryWrapper);

        //遍历菜品,修改每个菜品的状态
       list.stream().map((item)->{
            item.setStatus(status);
            dishService.updateById(item);
            return item;
        }).collect(Collectors.toList());

        return Result.success("更新起售/停售状态成功");
    }

    /**
     * @description:  单个菜品删除或批量删除
     * @param ids
     * @return: com.hxut.common.Result<java.lang.String>
     * @author: MR.孙
     * @date: 2022/6/28 10:28
    */
    @DeleteMapping
    public Result<String> del(@RequestParam List<Long> ids){
        log.info("页面传递的菜品id->{}:",ids.toString());
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids!=null,Dish::getId,ids);
        List<Dish> list = dishService.list(queryWrapper);
        for(Dish dish:list){
            Integer status = dish.getStatus();
            if(status==0){
                dishService.removeById(dish.getId());
            }else{
                throw new CustomException("删除菜品中有正在售卖的菜品，无法删除");
            }
        }

        return Result.success("菜品删除成功");
    }

}
