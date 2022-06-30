package com.hxut.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hxut.common.Result;
import com.hxut.entity.User;
import com.hxut.service.UserService;
import com.hxut.utils.SMSUtils;
import com.hxut.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * description: UserController
 * date: 2022/6/26 22:40
 * author: MR.孙
 */
@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * @description: 发送短信验证码
     * @param user
     * @param request
     * @return: com.hxut.common.Result<java.lang.String>
     * @author: MR.孙
     * @date: 2022/6/26 23:11
    */
    @PostMapping("/sendMsg")
    public Result<String> sendMsg(@RequestBody User user, HttpServletRequest request){
        log.info("当前登录的手机号为->{}",user.getPhone());
        //获取登录用户手机号
        String phone = user.getPhone();

        if(Strings.isNotEmpty(phone)){
            //生成随机4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("生成的短信验证码为->{}",code);
            try {
                //调用阿里云提供的短信服务API完成发送短信
                //由于是测试服务，请指定绑定的手机号
//                SMSUtils.sendMessage("阿里云短信测试","SMS_154950909",phone,code);
                //将生成的验证码保存到Session中
                request.getSession().setAttribute(phone,code);
                return Result.success("手机验证码短信发送成功");
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return Result.error("短信发送失败");
    }


    /**
     * @description:  移动端用户登录
     * @param map
     * @param request
     * @return: com.hxut.common.Result<java.lang.String>
     * @author: MR.孙
     * @date: 2022/6/26 23:55
    */
    @PostMapping("/login")
    public Result<User> login(@RequestBody Map map, HttpServletRequest request){
        log.info("当前用户提交的登录数据为:{}",map.toString());

        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从session中获取验证码
        Object codeInSession = request.getSession().getAttribute(phone);
        //将页面提交的验证码和session中的验证码进行比对
        //验证码一致则通过
        if(codeInSession!=null && codeInSession.equals(code)){
            //判断当前手机号是否为新用户
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            //如果是新用户则自动完成注册
            if(user==null){
                 user = new User();
                 user.setPhone(phone);
                 user.setStatus(1);
                 userService.save(user);
            }

            //注册完之后要将userId存入session中，因为过滤器会判断userid是不是null,如果不存session,将被拦截
            request.getSession().setAttribute("user",user.getId());
            //如果是老用户,直接返回一个User对象,如果是新用户自动注册后也是直接返回的
            return Result.success(user);
        }



        return Result.error("登录失败");
    }

    /**
     * @description:  退出登录
     * @param request
     * @return: com.hxut.common.Result<java.lang.String>
     * @author: MR.孙
     * @date: 2022/6/28 9:35
    */
    @PostMapping("/loginout")
    public Result<String> logout(HttpServletRequest request){
        log.info("退出登录...");
        request.getSession().removeAttribute("user");
        return Result.success("退出登录成功");
    }

}
