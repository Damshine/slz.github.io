package com.hxut.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hxut.common.Result;
import com.hxut.entity.Employee;
import com.hxut.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;



/**
 * description: EmployeeController
 * date: 2022/6/23 11:19
 * author: MR.孙
 */
@SuppressWarnings("all")
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * @description:  登录接口
     * @param request 存储session
     * @param employee 员工实体类
     * @return: com.hxut.common.Result<com.hxut.entity.Employee>
     * @author: MR.孙
     * @date: 2022/6/23 17:12
    */
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee){

        //获取密码并进行md5加密
        String password = employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());

        //难道表单的用户名和数据库进行比对
        String username = employee.getUsername();
        LambdaQueryWrapper<Employee> lamqw = new LambdaQueryWrapper<Employee>();
        lamqw.eq(Employee::getUsername, username);
        Employee emp = employeeService.getOne(lamqw);

        //如果没有此用户则，给出错误提示
        if(emp==null){
           return  Result.error("登录失败");
        }

        //密码进行比对,错误则给出提示
        if(!emp.getPassword().equals(password)){
            return Result.error("登录失败");
        }

        //账户是否禁用状态,如果是禁用状态则给出提示
        if(emp.getStatus()==0){
            return Result.error("账户已被禁用");
        }

        //以上都不存在的情况，就把员工id信息存到session中
        request.getSession().setAttribute("employee",emp.getId());
        return Result.success(emp);
    }

    /**
     * @description: 退出功能
     * @param request
     * @return: com.hxut.common.Result<java.lang.String>
     * @author: MR.孙
     * @date: 2022/6/23 19:42
    */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request){
        //清空session中的员工id信息
        request.getSession().removeAttribute("employee");
        return Result.success("退出成功");
    }

    /**
     * @description:  新增员工
     * @param employee 提交的新增员工信息
     * @return: com.hxut.common.Result<java.lang.String>
     * @author: MR.孙
     * @date: 2022/6/23 19:49
    */
    @PostMapping
    public Result<String> add(@RequestBody Employee employee,HttpServletRequest request){
        log.info("新增员工信息:{}",employee.toString());
        //初始化密码123456，进行MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //创建时间
//        employee.setCreateTime(LocalDateTime.now());
        //更新时间
//        employee.setUpdateTime(LocalDateTime.now());
        //获取当前登录用户的id
//        Long empId = (Long) request.getSession().getAttribute("employee");


        //创建人   创建人就是当前登录的用户，所以我们在session中取出id即可
//        employee.setCreateUser(empId);
        //更新创建人
//        employee.setUpdateUser(empId);

        employeeService.save(employee);
        return Result.success("添加员工成功");
    }

    /**
     * @description:  分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return: com.hxut.common.Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page>
     * @author: MR.孙
     * @date: 2022/6/24 10:34
    */
    @GetMapping("/page")
    public Result<Page> page(int page,int pageSize,String name){
        log.info("page= =>{},pageSize= =>{},name= =>{}",page,pageSize,name);
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> lmbdqw = new LambdaQueryWrapper<>();
        //添加过滤条件
        lmbdqw.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        lmbdqw.orderByDesc(Employee::getUpdateTime);
        //执行分页查询
        employeeService.page(pageInfo);

        return Result.success(pageInfo);
    }
    /**
     * @description: 更新员工信息
     * @param employee
     * @param request
     * @return: com.hxut.common.Result<java.lang.String>
     * @author: MR.孙
     * @date: 2022/6/24 10:35
    */
    @PutMapping
    public Result<String> update(@RequestBody Employee employee,HttpServletRequest request){
        log.info("更新员工信息:{}",employee.toString());
        //更新员工信息之前要同步更新时间和更新创建人
//        employee.setUpdateTime(LocalDateTime.now());

//        Long empId = (Long)request.getSession().getAttribute("employee");

//        employee.setUpdateUser(empId);//创建人由当前登录的用户获取，也就是session中
        log.info("当前线程id:{}",Thread.currentThread().getId());

        //判断页面传递的用户id是否为管理员
        if(employee.getId()==1){
            return Result.error("管理员无法修改状态");
        }

        employeeService.updateById(employee);//把前端传递过来的数据更新到数据库
        return Result.success("员工信息更新成功");
    }

    /**
     * @description:  根据员工id查询员工信息
     * @param id
     * @return: com.hxut.common.Result<com.hxut.entity.Employee>
     * @author: MR.孙
     * @date: 2022/6/24 9:59
    */
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id){
        log.info("根据员工id查询员工信息...");
        Employee employee = employeeService.getById(id);
        if(employee!=null) {
            return Result.success(employee);
        }
        return Result.error("没有查询到对应员工信息");
    }

}
