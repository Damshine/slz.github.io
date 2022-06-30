package com.hxut.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxut.entity.Employee;
import com.hxut.mapper.EmployeeMapper;
import com.hxut.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * description: EmployeeServiceImpl
 * date: 2022/6/23 11:15
 * author: MR.孙
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
