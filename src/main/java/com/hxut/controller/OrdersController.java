package com.hxut.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxut.common.Result;
import com.hxut.entity.Orders;
import com.hxut.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * description: OrdersController
 * date: 2022/6/27 20:32
 * author: MR.孙
 */
@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    /**
     * @description:  用户下单
     * @param orders
     * @return: com.hxut.common.Result<java.lang.String>
     * @author: MR.孙
     * @date: 2022/6/27 20:41
    */
    @PostMapping("/submit")
    public Result<String> order(@RequestBody Orders orders){
        log.info("用户下单信息->:{}",orders);
        ordersService.submit(orders);
        return Result.success("用户下单成功");
    }

    /**
     * @description:  订单分页查询
     * @param page
     * @param pageSize
     * @return: com.hxut.common.Result<com.hxut.entity.Orders>
     * @author: MR.孙
     * @date: 2022/6/28 9:23
    */
    @GetMapping("/userPage")
    public Result<Page> page(int page,int pageSize){
        log.info("页面传递的页数->:{},页面传递的记录数->:{}",page,pageSize);

        //添加分页构造器
        Page<Orders> pageInfo=new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Orders::getOrderTime);
        ordersService.page(pageInfo,queryWrapper);
        return Result.success(pageInfo);
    }

    /**
     * @description:  订单分页查询
     * @param page
     * @param pageSize
     * @param beginTime
     * @param endTime
     * @return: com.hxut.common.Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
     * @author: MR.孙
     * @date: 2022/6/28 17:20
    */
    @GetMapping("/page")
    public Result<Page> page(Integer page, Integer pageSize,String number,String beginTime, String endTime){
        log.info("页面传递的页数:{},记录数:{},订单号:{},下单时间:{},结束时间:{}",page,pageSize,number,beginTime,endTime);
        Page<Orders> pageInfo=new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(number!=null,Orders::getId,number)
                .gt(Strings.isNotEmpty(beginTime),Orders::getOrderTime,beginTime)
                .lt(Strings.isNotEmpty(endTime),Orders::getCheckoutTime,endTime);
        ordersService.page(pageInfo,queryWrapper);
        return Result.success(pageInfo);
    }


    /**
     * @description: 更新订单状态
     * @param orders
     * @return: com.hxut.common.Result<java.lang.String>
     * @author: MR.孙
     * @date: 2022/6/28 19:13
    */
    @PutMapping
    public Result<String> updateOrderStatus(@RequestBody Orders orders){
        log.info("更新订单状态,id->:{},status->:{}",orders.getId(),orders.getStatus());

        //根据订单id查询订单信息
        Orders order = ordersService.getById(orders.getId());
        //设置订单状态
        if(order!=null){
            order.setStatus(4);
        }else{
            return Result.error("没有此条订单信息");
        }
        //更新订单状态
         ordersService.updateById(order);
         return Result.success("更新订单状态成功");
    }

}
