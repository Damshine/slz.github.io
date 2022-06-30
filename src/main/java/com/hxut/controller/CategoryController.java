package com.hxut.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxut.common.Result;
import com.hxut.entity.Category;
import com.hxut.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * description: CategoryController
 * date: 2022/6/24 14:32
 * author: MR.孙
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * @description:  新增菜品分类
     * @param category
     * @return: com.hxut.common.Result<java.lang.String>
     * @author: MR.孙
     * @date: 2022/6/24 14:44
    */
    @PostMapping
    public Result<String> save(@RequestBody Category category){
        log.info("分类信息category为:{}",category.toString());
        categoryService.save(category);
        return Result.success("新增菜品分类成功");
    }
    /**
     * @description:  分页查询并排序
     * @param page
     * @param pageSize
     * @return: com.hxut.common.Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
     * @author: MR.孙
     * @date: 2022/6/24 15:08
    */
    @GetMapping("/page")
    public Result<Page> page(Integer page,Integer pageSize){
        //添加分页构造器
        Page<Category> pageInfo=new Page<>(page,pageSize);
        //添加条件构造器
        LambdaQueryWrapper<Category> lambdaqw = new LambdaQueryWrapper<>();

        //添加排序条件，根据sort进行排序
        lambdaqw.orderByAsc(Category::getSort);
        //分页查询
        categoryService.page(pageInfo,lambdaqw);
        return Result.success(pageInfo);
    }

    /**
     * @description:  根据id删除分类信息
     * @param ids
     * @return: com.hxut.common.Result<java.lang.String>
     * @author: MR.孙
     * @date: 2022/6/24 18:01
    */
    @DeleteMapping
    public Result<String> del(Long ids){
        log.info("删除分类,id=>{}",ids);
//        categoryService.removeById(ids);
        categoryService.remove(ids);
        return Result.success("分类信息删除成功");
    }
    
    /**
     * @description: 修改分类信息
     * @param category
     * @return: com.hxut.common.Result<java.lang.String>
     * @author: MR.孙
     * @date: 2022/6/24 20:01
    */
    @PutMapping
    public Result<String> update(@RequestBody Category category){
        log.info("分类更新提交信息: =>{}",category.toString());
        categoryService.updateById(category);
        return Result.success("修改分类信息成功");
    }

    /**
     * @description: 根据条件查询分类数据
     * @return:
     * @author: MR.孙
     * @date: 2022/6/25 9:20
    */
    @GetMapping("/list")
    public Result<List> list(Category category){
        log.info("分类条件信息为:{}",category.toString());
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);
        return Result.success(list);
    }
}
