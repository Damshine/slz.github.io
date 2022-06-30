package com.hxut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxut.entity.User;
import com.hxut.mapper.UserMapper;
import com.hxut.service.UserService;
import org.springframework.stereotype.Service;

/**
 * description: UserServiceImpl
 * date: 2022/6/26 22:40
 * author: MR.孙
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
