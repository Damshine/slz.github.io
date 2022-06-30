package com.hxut.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.hxut.common.BaseContext;
import com.hxut.common.Result;
import com.hxut.entity.AddressBook;
import com.hxut.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * description: AddressBookController
 * date: 2022/6/27 9:43
 * author: MR.孙
 */
@RestController
@RequestMapping("/addressBook")
@Slf4j
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;


    /**
     * @description:  新增地址
     * @param addressBook
     * @return: com.hxut.common.Result<com.hxut.entity.AddressBook>
     * @author: MR.孙
     * @date: 2022/6/27 9:54
    */
    @PostMapping
    public Result<AddressBook> save(@RequestBody AddressBook addressBook){
        //一个用户可以有多个地址,所以新增时需要给id赋值
        Long userId = BaseContext.getCurreantId();
        addressBook.setUserId(userId);
        addressBookService.save(addressBook);
        return Result.success(addressBook);
    }


    /**
     * @description:  设置默认地址
     * @param addressBook
     * @return: com.hxut.common.Result<com.hxut.entity.AddressBook>
     * @author: MR.孙
     * @date: 2022/6/27 10:03
    */
    @PutMapping("/default")
    public Result<AddressBook> setDefault(@RequestBody AddressBook addressBook){
        log.info("页面传递的地址为->:{}",addressBook.toString());

        //根据用户id,将is_default全部清空.因为每个用户只有一个默认地址
        //update address_book set is_default=0 where user_id=?
        LambdaUpdateWrapper<AddressBook> queryWrapper=new LambdaUpdateWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurreantId());
        queryWrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(queryWrapper);

        //根据id,将当前地址设置为默认地址
        //update address_book set is_deafult=1 where id=?
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);

        return Result.success(addressBook);
    }


    /**
     * @description:  根据id查询地址簿
     * @param id
     * @return: com.hxut.common.Result<com.hxut.entity.AddressBook>
     * @author: MR.孙
     * @date: 2022/6/27 10:29
    */
    @GetMapping("/{id}")
    public Result<AddressBook> getById(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        if(addressBook==null){
            Result.success("没有查询到该地址");
        }
        return Result.success(addressBook);
    }


    /**
     * @description: 查询默认地址
     * @return: com.hxut.common.Result<com.hxut.entity.AddressBook>
     * @author: MR.孙
     * @date: 2022/6/27 10:41
    */
    @GetMapping("/default")
    public Result<AddressBook> getDefaultAddress(){

        //select * from address_book where user_id=? and is_default=1
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurreantId());
        queryWrapper.eq(AddressBook::getIsDefault,1);

        AddressBook addressBook = addressBookService.getOne(queryWrapper);
        if (addressBook == null) {
            Result.success("没有查询到该地址");
        }


        return Result.success(addressBook);
    }

    /**
     * @description:  查询全部用户地址
     * @param addressBook
     * @return: com.hxut.common.Result<java.util.List<com.hxut.entity.AddressBook>>
     * @author: MR.孙
     * @date: 2022/6/27 10:43
    */
    @GetMapping("/list")
    public Result<List<AddressBook>> list(AddressBook addressBook){
        //select * from address_book where user_id=?  order by update_time desc;
        addressBook.setUserId(BaseContext.getCurreantId());
        log.info("页面传递的地址簿信息为->:{}",addressBook.toString());

        //条件构造器
        LambdaQueryWrapper<AddressBook> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(addressBook.getUserId()!=null,AddressBook::getUserId,addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);

        List<AddressBook> list = addressBookService.list(queryWrapper);
        return Result.success(list);
    }


    /**
     * @description:  更新地址簿信息
     * @param addressBook
     * @return: com.hxut.common.Result<com.hxut.entity.AddressBook>
     * @author: MR.孙
     * @date: 2022/6/28 9:05
    */
    @PutMapping
    public Result<String> update(@RequestBody AddressBook addressBook){
        log.info("页面传递的更新地址簿的数据为->:{}",addressBook.toString());

        addressBookService.updateById(addressBook);

        return Result.success("地址簿更新成功");
    }

    @DeleteMapping
    public Result<String> del(@RequestParam Long ids){
        log.info("当前传递的地址簿id->:{}",ids);
        addressBookService.removeById(ids);
        return Result.success("地址簿删除成功");
    }

}
