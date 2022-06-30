package com.hxut.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hxut.common.BaseContext;
import com.hxut.common.Result;
import com.hxut.entity.ShoppingCart;
import com.hxut.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * description: ShoppingCartController
 * date: 2022/6/27 17:45
 * author: MR.孙
 */
@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * @description:  新增购物车
     * @param shoppingCart
     * @return: com.hxut.common.Result<com.hxut.entity.ShoppingCart>
     * @author: MR.孙
     * @date: 2022/6/27 19:25
    */
    @PostMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("页面传递的购物车信息为->:{}",shoppingCart.toString());
        Long userId = BaseContext.getCurreantId();
        //指定当前用户id,提前知道目前是哪个用户再下单
        shoppingCart.setUserId(userId);

        Long dishId = shoppingCart.getDishId();
        //判断当前购物车的商品是菜品还是套餐
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);

        if(dishId!=null){
            //如果页面传递的是dish_id,那么就是菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            //如果页面传递的是setmeal_id,那么就是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        //判断新增的商品是否已添加到购物车
        //slect * from shopping_cart where user_id=? and setmeal_id/dish_id =?
        ShoppingCart shopcart = shoppingCartService.getOne(queryWrapper);

        //如果购物车已有商品那么新增的商品数量+1
        if(shopcart!=null){
            Integer number = shopcart.getNumber();
            shopcart.setNumber(number+1);
            shoppingCartService.updateById(shopcart);
        }else{
            //如果购物车没有商品那么新增的的商品数量为1
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            shopcart=shoppingCart;
        }

        return Result.success(shopcart);
    }
    
    /**
     * @description:  查看购物车数据
     * @return: java.util.List<com.hxut.entity.ShoppingCart>
     * @author: MR.孙
     * @date: 2022/6/27 19:34
    */

    @GetMapping("/list")
    public Result<List<ShoppingCart>> list(){
        log.info("查看购物车");
        Long curreantId = BaseContext.getCurreantId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,curreantId).orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return Result.success(list);
    }

    /**
     * @description: 清空购物车
     * @return: com.hxut.common.Result<java.lang.String>
     * @author: MR.孙
     * @date: 2022/6/27 19:48
    */
    @DeleteMapping("/clean")
    public Result<String> clean(){
        log.info("清空购物车...");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurreantId());
        shoppingCartService.remove(queryWrapper);
        return Result.success("清空购物车成功");
    }

    /**
     * @description:  删除购物车中的菜品或者套餐
     * @param shoppingCart
     * @return: com.hxut.common.Result<java.lang.String>
     * @author: MR.孙
     * @date: 2022/6/28 8:01
    */
    @PostMapping("/sub")
    public Result<String> sub(@RequestBody ShoppingCart shoppingCart){
        log.info("页面传递删除购物车某条商品的数据为:->{}",shoppingCart.toString());

        Long dishId = shoppingCart.getDishId();//菜品id
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //判断页面传参是dish_id还是setmeal_id
        if(dishId!=null){
            //如果是dish_id说明是菜品,那么根据dish_id进行删除number-1
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            //如果是setmeal_id说明是套餐,那么根据setmeal_id进行更新number-1

            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());

        }
        ShoppingCart cart = shoppingCartService.getOne(queryWrapper);
        //如果number<0则直接删除这条记录
        if(cart.getNumber()>0){
            shoppingCartService.remove(queryWrapper);
        }else{
            cart.setNumber(cart.getNumber()-1);
            shoppingCartService.updateById(cart);
        }



        return Result.success("商品删除成功");
    }

}
