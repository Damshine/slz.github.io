package com.hxut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxut.entity.AddressBook;
import com.hxut.mapper.AddressBookMapper;
import com.hxut.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * description: AddressBookServiceImpl
 * date: 2022/6/27 9:41
 * author: MR.孙
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
