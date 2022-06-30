package com.hxut.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxut.common.Result;
import com.hxut.dto.DishDto;
import com.hxut.dto.SetmealDto;
import com.hxut.entity.Category;
import com.hxut.entity.Dish;
import com.hxut.entity.Setmeal;
import com.hxut.service.CategoryService;
import com.hxut.service.SetMealDIshService;
import com.hxut.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


/**
 * description: SetmealController
 * date: 2022/6/26 8:44
 * author: MR.孙
 */

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetMealDIshService setMealDIshService;

    @Autowired
    private CategoryService categoryService;

    /**
     * @description:  新增套餐
     * @param setmealDto
     * @return: com.hxut.common.Result<java.lang.String>
     * @author: MR.孙
     * @date: 2022/6/26 9:31
    */
    @PostMapping
    public Result<String> add(@RequestBody SetmealDto setmealDto){
        log.info("传递过来的套餐信息为->:{}",setmealDto.toString());
        setmealService.saveMealAndMealDish(setmealDto);
        return Result.success("新增套餐成功");
    }

    /**
     * @description:  套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return: com.hxut.common.Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
     * @author: MR.孙
     * @date: 2022/6/26 12:26
    */
    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize,String name){

        //添加分页构造器
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);

        //添加条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        //添加条件
        queryWrapper.eq(Strings.isNotEmpty(name),Setmeal::getName,name);

        //添加排序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //分页查询
        setmealService.page(pageInfo,queryWrapper);


        //但是这个只是setmeal表的信息,还有一个套餐名称在页面无法显示,所以我们需要重新创建一个对象获取分类名称
        Page<SetmealDto> setmealDtoPage = new Page<>();
        //将setmeal的信息拷贝到setmealDtoPage中
        //因为page泛型的实体类型不一致，所以要排除records
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");

        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> mealDtos=  records.stream().map((item->{
            SetmealDto setmealDto = new SetmealDto();
            Long categoryId = item.getCategoryId();
            //通过categoryId去category表查询分类名称
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                //获取分类名称
                String categoryName = category.getName();
                //填充分类名称
                setmealDto.setCategoryName(categoryName);
            }
            //上面的代码仅仅是只填充的categoryName其它的字段,还没有copy过来
            BeanUtils.copyProperties(item,setmealDto);
            return setmealDto;
        })).collect(Collectors.toList());

        setmealDtoPage.setRecords(mealDtos);

        return Result.success(setmealDtoPage);
    }


    /**
     * @description:  删除套餐
     * @param ids  可以用Long[] ids,也可以用List
     * @return: com.hxut.common.Result<java.lang.String>
     * @author: MR.孙
     * @date: 2022/6/26 18:40
    */
    @DeleteMapping
    public Result<String> del(@RequestParam List<Long> ids){
        log.info("当前接收到的id为个数为{}",ids.toString());
        setmealService.delSetmealAndMealDish(ids);
        return Result.success("删除成功");
    }


    /**
     * @description:
     * @param setmeal
     * @return: com.hxut.common.Result<java.util.List<com.hxut.dto.SetmealDto>>
     * @author: MR.孙
     * @date: 2022/6/27 14:57
    */
    @GetMapping("/list")
    public Result<List<Setmeal>> list(Setmeal setmeal){
        //添加条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        List<Setmeal> list = setmealService.list(queryWrapper);
        return Result.success(list);
    }


    /**
     * @description:  获取当前套餐的信息
     * @param id
     * @return: com.hxut.common.Result<com.hxut.dto.SetmealDto>
     * @author: MR.孙
     * @date: 2022/6/28 12:43
    */
    @GetMapping("/{id}")
    public Result<SetmealDto> get(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getSetmeal(id);
        return Result.success(setmealDto);
    }


    /**
     * @description:  更新套餐信息
     * @param setmealDto
     * @return:
     * @author: MR.孙
     * @date: 2022/6/28 12:30
     */
    @PutMapping
    public Result<String> updateSetmeal(@RequestBody SetmealDto setmealDto){
        log.info("页面传递的更新套餐信息->:{}",setmealDto.toString());
        setmealService.updateSetmealInfo(setmealDto);
        return Result.success("更新套餐信息成功");
    }


    /**
     * @description:  批量更改套餐状态
     * @param status
     * @param ids
     * @return: com.hxut.common.Result<java.lang.String>
     * @author: MR.孙
     * @date: 2022/6/28 14:01
    */
    @PostMapping("/status/{status}")
    public Result<String> updateStatus(@PathVariable Integer status,Long[] ids){
        log.info("批量起售/停售状态->:{},传递的ids数量->:{}",status,ids.length);

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids.length>0,Setmeal::getId,ids);
        //先根据传过来的ids进行查询套餐查询
        List<Setmeal> list = setmealService.list(queryWrapper);
        //遍历套餐,修改每个套餐的状态
        list.stream().map((item)->{
            item.setStatus(status);
            setmealService.updateById(item);
            return item;
        }).collect(Collectors.toList());

        return Result.success("更新起售/停售状态成功");
    }

}
